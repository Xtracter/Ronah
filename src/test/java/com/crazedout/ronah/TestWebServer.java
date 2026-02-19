package com.crazedout.ronah;

import com.crazedout.ronah.service.WebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWebServer extends TestUtils{

    static RonahHttpServer ronahHttpServer;

    @BeforeAll
    static void initServer() throws InterruptedException{
        String basePath =  System.getProperty("user.dir") + File.separatorChar + "src"  +File.separatorChar + "test" +
                File.separatorChar + "web";

        new WebServer(basePath);
        Thread t = new Thread(()->{
            System.getProperty("ronah.debug","true");
            new WebServer(basePath);
            ronahHttpServer = new RonahHttpServer();
            ronahHttpServer.start(8083);
        });
        t.start();
        Thread.sleep(500);
    }

    @Test
    void test1() throws IOException{

        List<String> res = connectWeb("GET /web/index.html?name=ronah&age=1 HTTP/1.1");
        assertEquals("HTTP/1.1 200 OK", res.get(0));

    }

    @Test
    void test2() throws IOException{

        List<String> res = connectWeb("POST /web/index.html HTTP/1.1");
        assertEquals("HTTP/1.1 200 OK", res.get(0));

    }

    @AfterAll
    static void close() throws IOException {
        ronahHttpServer.stop();
    }



}
