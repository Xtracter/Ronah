package com.crazedout.ronah;

import com.crazedout.ronah.service.RonahHttpServer;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RonahTest {

    int port = 8083;

    @Test
    public void test1() throws Exception {

        Thread t = new Thread(()->{
            new TestService();
            RonahHttpServer ronahHttpServer = new RonahHttpServer();
            ronahHttpServer.start(port);
        });
        t.start();

        Thread.sleep(500);
        try(Socket s = new Socket("localhost",port)) {
            PrintStream ps = new PrintStream(s.getOutputStream());
            ps.println("GET /index HTTP/1.1\n\n");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    // read headers.
                }
                assertEquals("OK", reader.readLine());
            }
        }
    }

    // TODO: More tests...
}
