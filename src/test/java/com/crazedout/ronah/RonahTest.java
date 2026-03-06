package com.crazedout.ronah;

import com.crazedout.ronah.request.ContentType;
import com.crazedout.ronah.service.DefaultService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RonahTest extends TestUtils {

    static RonahHttpServer ronahHttpServer;

    List<String> res;

    @BeforeAll
    static void initServer() throws InterruptedException{
        Thread t = new Thread(()->{
            System.getProperty("ronah.debug","true");
            new TestService();
            ronahHttpServer = new RonahHttpServer();
            ronahHttpServer.start(8083);
        });
        t.start();
        Thread.sleep(500);
    }

    @AfterAll
    static void close() throws IOException{
        ronahHttpServer.stop();
    }

    @Test
    public void test1() throws Exception {

        res = connect("GET /index/tests HTTP/1.1");
        assertEquals("OK", res.get(res.size()-1));

        res = connect("GET /param?name=ronah&age=1 HTTP/1.1");
        assertEquals("ronah=1", res.get(res.size()-1));
    }

    @Test
    public void test2() throws IOException {
        res = connect("GET /param?name=ronah&age=1 HTTP/1.1");
        assertEquals("ronah=1", res.get(res.size()-1));
    }

    @Test
    public void test3() throws IOException {

        String json = "{\"name\":\"ringo\",\"band\":\"the beatles\"}";
        res = connect("POST /post HTTP/1.1", ContentType.APPLICATION_JSON,json);
        assertEquals("ringo plays in the beatles",res.get(res.size()-1));
    }

    @Test
    public void test4() throws IOException {
        res = connect("POST /not_existing HTTP/1.1");
        assertEquals("<!DOCTYPE html><html><body><h3>HTTP/1.1 404 Not Found</h2>Resource was not found</h3></body></html>",res.get(res.size()-1));
    }

    @Test
    void testCatchAll() throws IOException{
        DefaultService df = new DefaultService();
        res = connect("POST /not_existing HTTP/1.1");
        assertEquals("<!DOCTYPE html><html><body><h1>Hello from Ronah Catch all</h1></body></html>",res.get(res.size()-1));
        Repository.removeService(df);
    }

    @Test
    void testJson1() throws IOException{
        res = connect("GET /json HTTP/1.1");
        assertEquals("<!DOCTYPE html><html><body><h3>HTTP/1.1 500 Internal Server</h3>wrong number of arguments</body></html>",res.get(res.size()-1));
    }

    @Test
    public void testOptions() throws IOException {
        DefaultService df = new DefaultService();
        connectOptions("OPTIONS /options?name=test HTTP/1.1", "localhost","http://localhost:8080");
        Repository.removeService(df);
    }

    @Test
    public void testApi() throws IOException {
        res = connect("GET /api HTTP/1.1");
        assertEquals("</html>",res.get(res.size()-1));
        ronahHttpServer.removeAPIService();

        res = connect("GET /api HTTP/1.1");
        assertEquals("<!DOCTYPE html><html><body><h3>HTTP/1.1 404 Not Found</h2>Resource was not found</h3></body></html>",
                res.get(res.size()-1));
    }

    @Test
    void testPath() throws IOException{
        res = connect("GET /person/Fredrik/710518/3037 HTTP/1.1");
        System.out.println(res);
    }

    @Test
    void testPath2() throws IOException{
        res = connect("GET /web/test HTTP/1.1");
        assertEquals("HTTP/1.1 200 OK",res.get(0));
    }

    @Test
    void testWebServer() throws IOException {
        TestWebServer s = new TestWebServer("src/test");
        res = connect("GET /web HTTP/1.1");
        //assertEquals("</html>",res.get(res.size()-1));
        res.forEach(System.out::println);
        res = connect("GET /web/index.html HTTP/1.1");
        res.forEach(System.out::println);
        Repository.removeService(s);
        //assertEquals("</html>",res.get(res.size()-1));
    }

    // TODO: More tests...
}
