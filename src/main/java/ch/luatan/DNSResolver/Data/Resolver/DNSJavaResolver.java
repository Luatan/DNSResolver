package ch.luatan.DNSResolver.Data.Resolver;

import ch.luatan.DNSResolver.DNSResolver;
import ch.luatan.DNSResolver.Model.DNS.DNSType;
import ch.luatan.DNSResolver.Model.DNS.Record;
import ch.luatan.DNSResolver.Model.DNS.SpecialType;
import ch.luatan.DNSResolver.Model.DNS.Type;
import ch.luatan.DNSResolver.Model.Utils.Domain;
import org.xbill.DNS.*;
import org.xbill.DNS.dnssec.ValidatingResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNSJavaResolver implements Resolvable {

    private Message answer;
    static String ROOT = ". IN DS 20326 8 2 E06D44B80B8F1D39A95C0B0D7C65D08458E880409BBC683457104237C7F8EC8D";
    @Override
    public void resolve(String domain, ch.luatan.DNSResolver.Model.DNS.Type type, String dnsServer) {
        SimpleResolver sr;
        try {
            if (!dnsServer.isEmpty()) {
                sr = new SimpleResolver(dnsServer);
            } else {
                sr = new SimpleResolver("8.8.8.8");
            }
            org.xbill.DNS.Record rc = org.xbill.DNS.Record.newRecord(Name.fromConstantString(domain + (!domain.endsWith(".") ? "." : "")), org.xbill.DNS.Type.value(type.toString()), DClass.IN);

            //run validating resolver for dnssec
            ValidatingResolver vr = new ValidatingResolver(sr);
            vr.loadTrustAnchors(new ByteArrayInputStream(ROOT.getBytes(StandardCharsets.US_ASCII)));
            answer = vr.send(Message.newQuery(rc));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Record> getRecords(Type type) {
        List<Record> records = new LinkedList<>();
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
        if (!Rcode.string(answer.getHeader().getRcode()).equals("NOERROR") && type.equals(SpecialType.MSG)) {
            addMessage(records, answer.getHeader().toString());
        }
        return records;
    }

    @Override
    public String validateDNSSEC() {
        for (RRset set : answer.getSectionRRsets(Section.ADDITIONAL)) {
            if (set.getName().equals(Name.root) && set.getType() == org.xbill.DNS.Type.TXT
                    && set.getDClass() == ValidatingResolver.VALIDATION_REASON_QCLASS) {
                return ((TXTRecord) set.first()).getStrings().get(0);
            }
        }
        return "Verified";
    }

    private void addMessage(List<Record> records, String message) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getType().equals(SpecialType.MSG)) {
                records.remove(i);
            }
        }
        DNSResolver.LOGGER.error(message);
        records.add(Record.setRecordValue(SpecialType.MSG.getRecord(), message));
    }
}
