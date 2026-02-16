package com.crazedout.ronah;

import com.crazedout.ronah.request.ContentType;
import com.crazedout.ronah.service.DefaultService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RonahTest extends TestUtils {

    static RonahHttpServer ronahHttpServer;

    @BeforeAll
    static void initServer() throws InterruptedException{
        Thread t = new Thread(()->{
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

        String res = connect("GET /index HTTP/1.1");
        assertEquals("OK", res);

        res = connect("GET /param?name=ronah&age=1 HTTP/1.1");
        assertEquals("ronah=1", res);
    }

    @Test
    public void test2() throws IOException {

        String res = connect("GET /param?name=ronah&age=1 HTTP/1.1");
        assertEquals("ronah=1", res);
    }

    @Test
    public void test3() throws IOException {

        String json = "{\"name\":\"ringo\",\"band\":\"the beatles\"}";
        String res = connect("POST /post HTTP/1.1", ContentType.APPLICATION_JSON,json);
        assertEquals("ringo plays in the beatles",res);
    }

    @Test
    public void test4() throws IOException {
        String res = connect("POST /not_existing HTTP/1.1");
        assertEquals("<!DOCTYPE html><html><body><h3>HTTP/1.1 404 Not Found</h2>Resource was not found</h3></body></html>",res);
    }

    @Test
    void testCatchAll() throws IOException{
        new DefaultService();
        String res = connect("POST /not_existing HTTP/1.1");
        assertEquals("<!DOCTYPE html><html><body><h1>Hello from Ronah Catch all</h1></body></html>",res);
    }

    @Test
    void testJson1() throws IOException{
        String res = connect("GET /json HTTP/1.1");
    }



    // TODO: More tests...
}
