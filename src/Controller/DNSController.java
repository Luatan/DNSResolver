package Controller;



import DNS.Zone;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DNSController {
    private Zone zone;
    private String domain;

    DNSController(String domain) {
        this.domain = domain;
    }

    public void request() {
        List<Object> ob = new ArrayList<>();
        zone = new Zone();
        try {
            InitialDirContext iDirC = new InitialDirContext();
            // get all the DNS records for hostname
            Attributes attributes = iDirC.getAttributes("dns:/" + domain, new String[]{"*"});
            NamingEnumeration<? extends Attribute> attributeEnumeration = attributes.getAll();
            System.out.println("-- DNS INFORMATION --");

            while (attributeEnumeration.hasMore()){
                //System.out.println("" + attributeEnumeration.next());
                //Record rec = new Record("A", "admin.luatan.com", "Test");
                ob.add(attributeEnumeration.next());

            }
            attributeEnumeration.close();
        } catch (NameNotFoundException e) {


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
