package com.crazedout.ronah;

import com.crazedout.ronah.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RonahTest extends TestUtils {


    @Test
    public void test1() throws Exception {

        Thread t = new Thread(()->{
            new TestService();
            RonahHttpServer ronahHttpServer = new RonahHttpServer();
            ronahHttpServer.start(port);
        });
        t.start();

        Thread.sleep(500);
        String res = connect("GET /index HTTP/1.1");
        assertEquals("OK", res);

        res = connect("GET /param?name=ronah&age=1 HTTP/1.1");
        assertEquals("ronah=1", res);

    }

    @Test
    public void test2() throws IOException{
        String res = connect("GET /param?name=ronah&age=1 HTTP/1.1");
        assertEquals("ronah=1", res);
    }

    @Test
    public void test3() throws IOException{
        String json = "{\"name\":\"ringo\",\"band\":\"the beatles\"}";
        String res = connect("POST /post HTTP/1.1", HttpRequest.APPLICATION_JSON,json);
        assertEquals("ringo plays in the beatles",res);
    }
    // TODO: More tests...
}
