package ch.luatan.DNSResolver.Data.Resolver;

import ch.luatan.DNSResolver.Model.DNS.DNSType;
import ch.luatan.DNSResolver.Model.DNS.Record;
import ch.luatan.DNSResolver.Model.DNS.Type;

import java.util.List;

public interface Resolvable {
    DNSType[] RECORD_TYPES = DNSType.values();

    void resolve(String domain, Type type, String dnsServer);

    List<Record> getRecords(Type type);

    String validateDNSSEC();
}
