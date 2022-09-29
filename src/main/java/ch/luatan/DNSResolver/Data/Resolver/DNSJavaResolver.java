package ch.luatan.DNSResolver.Data.Resolver;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Model.DNS.Record;
import ch.luatan.DNSResolver.Model.DNS.Type;
import ch.luatan.DNSResolver.Model.DNS.*;
import ch.luatan.DNSResolver.Model.Utils.Domain;
import org.xbill.DNS.*;
import org.xbill.DNS.dnssec.ValidatingResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNSJavaResolver implements Resolvable {
    static String ROOT = ". IN DS 20326 8 2 E06D44B80B8F1D39A95C0B0D7C65D08458E880409BBC683457104237C7F8EC8D";
    private boolean useDnssec = true;
    private final List<String> errors = new LinkedList<>();
    private Message answer;

    @Override
    public void resolve(String domain, Type type, String dnsServer) {
        domain = domain + (!domain.endsWith(".") ? "." : "");
        ValidatingResolver vr = null;
        try {
            org.xbill.DNS.Record query = org.xbill.DNS.Record.newRecord(Name.fromConstantString(domain), org.xbill.DNS.Type.value(type.toString()), DClass.IN);
            //run validating resolver for dnssec
            vr = new ValidatingResolver(getSimpleResolver(dnsServer));
            //Hot Fix timout
            vr.setTimeout(Duration.ofSeconds(2));
            vr.loadTrustAnchors(new ByteArrayInputStream(ROOT.getBytes(StandardCharsets.US_ASCII)));

            // query the DNS-Zone
            Message message = Message.newQuery(query);
            if (!useDnssec) {
                message.getHeader().setFlag(Flags.CD);
            }
            answer = vr.send(message);

            // Hanlde RFC8482
            if (!anyQueryAllowed()) {
                DNSResolver.LOGGER.info("ANY Request for " + domain + " was blocked");
                DNSResolver.LOGGER.info("Trying to resolve all Types manually");
                resolveInMultipleRequests(vr, domain);
            }

        } catch (UnknownHostException e) {
            errors.add("Unknown Host: " + dnsServer);
            e.printStackTrace();
        } catch (IOException e) {
            if (dnsServer.isEmpty()) {
                // Hot Fix Timeout
                resolveInMultipleRequests(vr, domain);
            } else {
                e.printStackTrace();
                errors.add(e.getMessage());
            }
        }

        if (answer == null) {
            errors.add("Could not retrieve answer from dns server");
        } else if (!Rcode.string(answer.getHeader().getRcode()).equals("NOERROR")) {
            errors.add(answer.getHeader().toString());
        }
    }

    @Override
    public List<Record> getRecords(Type type) {
        List<Record> records = new LinkedList<>();
        if (errors.size() > 0 && type.equals(SpecialType.MSG)) {
            addErrors(records);
        }

        if (answer == null) {
            return records;
        }

        for (org.xbill.DNS.Record rec : answer.getSection(Section.ANSWER)) {
            Matcher match = Pattern.compile("IN\\s(?<type>[A-Z]{0,15})\\W(?<value>.*)").matcher(rec.toString());
            if (rec.getType() == org.xbill.DNS.Type.value(type.toString()) && match.find()) {
                String value = match.group("value");
                if (type.equals(DNSType.SOA)) {
                    int max;
                    int padding = 2;

                    List<String> list = Arrays.asList(value.split(" "));
                    max = Collections.max(list).length();

                    String formatting = "%-" + (max + padding) + "." + (max + padding) + "s" + "%s";

                    list.set(1, list.get(1).replaceFirst("[.]", "@"));
                    list.set(2, String.format(formatting, list.get(2), "serialnumber"));
                    list.set(3, String.format(formatting, list.get(3), "refresh (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(3))) + ")"));
                    list.set(4, String.format(formatting, list.get(4), "retry (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(4))) + ")"));
                    list.set(5, String.format(formatting, list.get(5), "expire (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(5))) + ")"));
                    list.set(6, String.format(formatting, list.get(6), "minimum (" + Domain.getTimeFromSeconds(Integer.parseInt(list.get(6))) + ")"));

                    for (String rec2 : list) {
                        records.add(Record.setRecordValue(type.getRecord(), rec2));
                    }
                } else {
                    value = value.replaceAll("\"", "");
                    records.add(Record.setRecordValue(type.getRecord(), value));
                }
            }
        }
        return records;
    }

    @Override
    public String validateDNSSEC() {
        if (answer == null) {
            return "";
        }

        if (!useDnssec) {
            return "Validation Disabled";
        }

        for (RRset set : answer.getSectionRRsets(Section.ADDITIONAL)) {
            if (set.getName().equals(Name.root) && set.getType() == org.xbill.DNS.Type.TXT && set.getDClass() == ValidatingResolver.VALIDATION_REASON_QCLASS) {
                return ((TXTRecord) set.first()).getStrings().get(0);
            }
        }
        return "Verified";
    }

    @Override
    public void ignoreDNSSEC() {
        useDnssec = false;
    }

    private void addErrors(List<Record> records) {
        errors.forEach(error -> {
            DNSResolver.LOGGER.error(error);
            records.add(Record.setRecordValue(SpecialType.MSG.getRecord(), error));

        });
    }

    private boolean anyQueryAllowed() {
        for (org.xbill.DNS.Record rec : answer.getSection(Section.ANSWER)) {
            if (rec instanceof HINFORecord && ((HINFORecord) rec).getCPU().equals("RFC8482")) {
                return false;
            }
        }
        return true;
    }

    private void resolveInMultipleRequests(Resolver resolver, String domain) {
        List<Type> typeList = new LinkedList<>(EnumSet.allOf(DNSType.class));
        if (answer == null) {
            answer = new Message();
        }
        typeList.add(AdditionalTypes.NS);
        typeList.forEach(value -> {
            org.xbill.DNS.Record rec = org.xbill.DNS.Record.newRecord(Name.fromConstantString(domain), org.xbill.DNS.Type.value(value.toString()), DClass.IN);
            try {
                Message subanswer = resolver.send(Message.newQuery(rec));
                for (org.xbill.DNS.Record subrec : subanswer.getSection(Section.ANSWER)) {
                    answer.addRecord(subrec, Section.ANSWER);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Resolver getSimpleResolver(String dnsServer) throws UnknownHostException {
        SimpleResolver sr;
        if (!dnsServer.isEmpty()) {
            sr = new SimpleResolver(dnsServer);
        } else {
            sr = new SimpleResolver("8.8.8.8");
        }
        sr.setTimeout(Duration.ofSeconds(5));

        return sr;
    }
}
