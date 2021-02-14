import org.junit.jupiter.api.Assertions;
import org.testng.annotations.Test;

public class DNSRequestsTest {
    DNSRequests requests = new DNSRequests();

    @Test
    void isSubdomainTest() {
        Assertions.assertEquals(true, requests.isSubdomain("api.luatan.com"));
        Assertions.assertEquals(false, requests.isSubdomain("luatan.com"));
        Assertions.assertEquals(true, requests.isSubdomain("This.is.a.sub.domain.com"));
        Assertions.assertEquals(false, requests.isSubdomain("google.com"));
        Assertions.assertEquals(false, requests.isSubdomain("netflix.com"));
    }

    @Test
    void getExtension() {
        Assertions.assertEquals("com", requests.getExtension("netflix.com"));
        Assertions.assertEquals("ch", requests.getExtension("green.ch"));
        Assertions.assertEquals("net", requests.getExtension("whois.net"));
        Assertions.assertEquals("swiss", requests.getExtension("smarties.swiss"));
        Assertions.assertEquals("ru", requests.getExtension("smarties.ru"));
        //Assertions.assertEquals("uk.com" , requests.getExtension("Greatbritian.uk.com"));
    }

    @Test
    void getMainDomain() {
        Assertions.assertEquals("domain.com", requests.getMainDomain("This.is.a.sub.domain.com"));
        Assertions.assertEquals("luatan.com", requests.getMainDomain("quiz.luatan.com"));
        Assertions.assertEquals("youtube.com", requests.getMainDomain("music.youtube.com"));
        Assertions.assertEquals("yup.com", requests.getMainDomain("lalup. yup.com"));
        Assertions.assertEquals("green.ch", requests.getMainDomain("my.green.ch"));
    }
}