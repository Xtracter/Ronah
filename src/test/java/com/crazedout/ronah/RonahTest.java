package com.crazedout.ronah;

import com.crazedout.ronah.service.Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RonahTest {

    private record TestMapper(String name, String value) {}

    String getPath = "/test/api/v1";

    public RonahTest() throws Exception{
        startRonah();
    }

    public static void startRonah() throws Exception {
        Logger.getLogger("").setLevel(Level.OFF);
        Thread t = new Thread(()->{
            new TestService();
            Ronah.main(new String[]{"8080"});
        });
        t.start();
        System.out.println("Waiting for server to bind..");
        Thread.sleep(500);
    }

    @Test
    public void testHtmlGet1() throws Exception {

        try (Socket s = new Socket("localhost", 8080)) {
            s.getOutputStream().write("GET / HTTP/1.1\n".getBytes());
            s.getOutputStream().write("Host: localhost\n".getBytes());
            s.getOutputStream().write("User-Agent: JUnit\n".getBytes());
            s.getOutputStream().write("Accept */*\n\n".getBytes());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String prev = null;
            while ((line = r.readLine()) != null) {
                prev = line;
            }
            assertEquals("<!DOCTYPE html><html><body><h1>Hello from Ronah</h1></body></html>", prev);
        }

        try(Socket s = new Socket("localhost",8080)){
            s.getOutputStream().write(String.format("GET %s HTTP/1.1\n",getPath).getBytes());
            s.getOutputStream().write("Host: localhost\n".getBytes());
            s.getOutputStream().write("User-Agent: JUnit\n".getBytes());
            s.getOutputStream().write("Accept */*\n\n".getBytes());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String prev=null;
            while((line=r.readLine())!=null) {
                if(line.startsWith("Content-Type:")){
                    String[] split = line.split(":");
                    assertEquals("application/json",split[1].trim());
                }
                prev=line;
            }
            ObjectMapper mapper = new ObjectMapper();
            TestMapper testMapper = mapper.readValue(prev, TestMapper.class);
            assertEquals("{\"name\":\"Ronah\",\"value\":\"server\"}", prev);
            assertEquals("Ronah", testMapper.name);
            assertEquals("server", testMapper.value);
        }

    }

    @Test
    public void testHtmlGet2() throws Exception {
        try(Socket s = new Socket("localhost",8080)){
            s.getOutputStream().write(String.format("GET %s HTTP/1.1\n",getPath).getBytes());
            s.getOutputStream().write("Host: localhost\n".getBytes());
            s.getOutputStream().write("User-Agent: JUnit\n".getBytes());
            s.getOutputStream().write("Accept */*\n\n".getBytes());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String prev=null;
            while((line=r.readLine())!=null) {
                if(line.startsWith("Content-Type:")){
                    String[] split = line.split(":");
                    assertEquals("application/json",split[1].trim());
                }
                prev=line;
            }
            ObjectMapper mapper = new ObjectMapper();
            TestMapper testMapper = mapper.readValue(prev, TestMapper.class);
            assertEquals("{\"name\":\"Ronah\",\"value\":\"server\"}", prev);
            assertEquals("Ronah", testMapper.name);
            assertEquals("server", testMapper.value);
        }
    }

    @Test
    public void testHtmlGet3() throws Exception {
        try(Socket s = new Socket("localhost",8080)){
            s.getOutputStream().write(String.format("GET %s?name=Ronah&value=server HTTP/1.1\n",getPath).getBytes());
            s.getOutputStream().write("Host: localhost\n".getBytes());
            s.getOutputStream().write("User-Agent: JUnit\n\n".getBytes());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String prev=null;
            while((line=r.readLine())!=null) {
                prev=line;
            }
            ObjectMapper mapper = new ObjectMapper();
            TestMapper testMapper = mapper.readValue(prev, TestMapper.class);
            assertEquals("{\"name\":\"Ronah\",\"value\":\"server\"}", prev);
            assertEquals("Ronah", testMapper.name);
            assertEquals("server", testMapper.value);
        }
    }

    @Test
    public void testHtmlPOST1() throws Exception {
        String json = "{\"name\":\"Ronah\",\"value\":\"server\"}";
        try(Socket s = new Socket("localhost",8080)){
            s.getOutputStream().write(String.format("POST %s/post HTTP/1.1\n",getPath).getBytes());
            s.getOutputStream().write("Host: localhost\n".getBytes());
            s.getOutputStream().write("User-Agent: JUnit\n".getBytes());
            s.getOutputStream().write("Content-Type: application/json\n".getBytes());
            s.getOutputStream().write(("Content-Length: " + json.length() + "\n\n").getBytes());
            s.getOutputStream().write(json.getBytes());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String prev=null;
            while((line=r.readLine())!=null) {
                prev=line;
            }
            ObjectMapper mapper = new ObjectMapper();
            TestMapper testMapper = mapper.readValue(prev, TestMapper.class);
            assertEquals("{\"name\":\"Ronah\",\"value\":\"server\"}", prev);
            assertEquals("Ronah", testMapper.name);
            assertEquals("server", testMapper.value);
        }
    }

    @Test
    public void testHtmlPOST2() throws Exception {
        String urlencoded = URLEncoder.encode("name=Ronah&value=server", StandardCharsets.UTF_8);
        try(Socket s = new Socket("localhost",8080)){
            s.getOutputStream().write(String.format("POST %s/post HTTP/1.1\n",getPath).getBytes());
            s.getOutputStream().write("Host: localhost\n".getBytes());
            s.getOutputStream().write("User-Agent: JUnit\n".getBytes());
            s.getOutputStream().write("Content-Type: application/json\n".getBytes());
            s.getOutputStream().write(("Content-Length: " + urlencoded.length() + "\n\n").getBytes());
            s.getOutputStream().write(urlencoded.getBytes());
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line;
            String prev=null;
            while((line=r.readLine())!=null) {
                prev=line;
            }
            ObjectMapper mapper = new ObjectMapper();
            TestMapper testMapper = mapper.readValue(prev, TestMapper.class);
            assertEquals("{\"name\":\"Ronah\",\"value\":\"server\"}", prev);
            assertEquals("Ronah", testMapper.name);
            assertEquals("server", testMapper.value);
        }
    }

}
