package ch.luatan.DNSResolver.Model.Whois;

import ch.luatan.DNSResolver.Data.Whois.Whois;
import ch.luatan.DNSResolver.Model.Caching.WhoisExtensionCache;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WhoisServerSearch {
    private final WhoisExtensionCache cache = new WhoisExtensionCache();
    private final Map<String, List<String>> tempValues = new LinkedHashMap<>();

    public WhoisServer search(String ext) {
        //check if it is a ccTLD with multiple parts
        String[] ext_parts = ext.split("[.]");
        if (ext_parts.length > 1) {
            ext = ext_parts[ext_parts.length-1];
        }

        if (checkCache(ext)) {
            try {
                return cache.load(ext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //create dataset
        createMap(ext);

        // dont return anything, if essential values are missing
        if (tempValues.isEmpty() || !tempValues.containsKey("whois")) {
            return null;
        }
        // init and declare Server data
        WhoisServer server = new WhoisServer(tempValues.get("domain").get(0), tempValues.get("whois").get(0));

        server.setContacts(createContacts());
        server.setCreated(tempValues.get("created").get(0));
        server.setChanged(tempValues.get("changed").get(0));
        server.setStatus(tempValues.get("status").get(0));
        server.setSource(tempValues.get("source").get(0));
        server.setRemarks(tempValues.get("remarks").get(0));

        cache.write(server);
        return server;
    }

    private boolean checkCache(String ext) {
        return cache.isCached(ext);
    }

    private void createMap(String ext) {
        System.err.println("REQUEST TO whois.iana.org");
        List<String> data = Whois.getWhois(ext, "whois.iana.org").stream().filter(d -> !d.isEmpty()).collect(Collectors.toList());
        Pattern pattern = Pattern.compile("(?<key>^[\\w-]+):\\s+(?<value>.*)");
        for (String elem : data) {
            Matcher matcher = pattern.matcher(elem);
            if (matcher.find()) {
                String key = matcher.group("key");
                List<String> list = new ArrayList<>();
                if (tempValues.containsKey(key)) {
                    list = tempValues.get(key);
                }
                list.add(matcher.group("value"));
                tempValues.put(key, list);
            }
        }
    }

    private List<Contact> createContacts() {
        List<Contact> contacts = new LinkedList<>();
        for (int i = 0; i < tempValues.get("organisation").size(); i++) {
            Contact.Type type;
            String name;
            String phone;
            String email;
            if (i > 0) {
                type = Contact.Type.valueOf(tempValues.get("contact").get(i - 1).toUpperCase());
                name = tempValues.get("name").get(i - 1);
                phone = tempValues.get("phone").get(i - 1);
                email = tempValues.get("e-mail").get(i - 1);
            } else {
                name = "";
                phone = "";
                email = "";
                type = Contact.Type.NONE;
            }
            List<String> addresses = new LinkedList<>();
            for (int j = (((i + 1) * 3)) - 3; j <= ((i + 1) * 3) - 1; j++) {
                try {
                    addresses.add(tempValues.get("address").get(j));
                } catch (IndexOutOfBoundsException e) {
                    //shit happens
                    e.printStackTrace();
                }
            }

            Organisation org = new Organisation(tempValues.get("organisation").get(i), addresses);

            Contact contact = new Contact(name, org, phone, email, type);
            contacts.add(contact);
        }
        return contacts;
    }

}
