package com.crazedout.ronah;

import com.crazedout.ronah.api.APIService;
import com.crazedout.ronah.service.HttpRequest;
import com.crazedout.ronah.service.RonahHttpServer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RonahTest {

    int port = 8083;
    String connect(String http) throws IOException {
        return connect(http,null,null);
    }
    String connect(String http, String type, String payload) throws IOException {
        try(Socket s = new Socket("localhost",port)) {
            PrintStream ps = new PrintStream(s.getOutputStream());
            if(payload==null) {
                ps.println(http + "\n");
            }else{
                ps.println(http);
                ps.println("Content-Type: " + type);
                ps.println("Content-Length: " + payload.length() + "\n");
                ps.print(payload);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    // read headers.
                }
                return reader.readLine();
            }
        }
    }

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
