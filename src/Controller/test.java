package Controller;

import Model.DNS.Record;
import Model.DNS.Zone;

public class test {
    public static void main(String[] args) {
        DNSController controller = new DNSController("luatan.com");
        controller.request();
    }
}
