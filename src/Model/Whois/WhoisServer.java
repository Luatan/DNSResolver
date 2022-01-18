package Model.Whois;

import java.util.List;

public class WhoisServer {
    private final String domain;
    private final String whois;
    private List<Contact> contacts;
    private String status;
    private String remarks;
    private String created;
    private String changed;
    private String source;

    WhoisServer(String domain_ext, String whois) {
        this.domain = domain_ext;
        this.whois = whois;
    }

    public String getDomain() {
        return domain;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getWhois() {
        return whois;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChanged() {
        return changed;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}

class Organisation {
    private final String name;
    private final List<String> address;

    Organisation(String name, List<String> address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public List<String> getAddress() {
        return address;
    }

}

class Contact {
    private final String name;
    private final Organisation org;
    private final String phone;
    private final String email;
    private final Type type;

    public enum Type {
        ADMINISTRATIVE,
        TECHNICAL,
        NONE
    }

    Contact(String name, Organisation org, String phone, String email, Type type) {
        this.name = name;
        this.org = org;
        this.phone = phone;
        this.email = email;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Organisation getOrg() {
        return org;
    }

    public String getPhone() {
        return phone;
    }

    public Type getType() {
        return type;
    }

}
