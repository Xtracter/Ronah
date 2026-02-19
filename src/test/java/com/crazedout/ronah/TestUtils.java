package com.crazedout.ronah;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TestUtils {

    protected int port = 8083;

    protected String connect(String http) throws IOException {
        return connect(http,null,null,null);
    }
    protected String connect(String http, String type, String payload) throws IOException {
        return connect(http,type,payload,null);
    }

    protected String connect(String http, String type, String payload, String origin) throws IOException {
        try(Socket s = new Socket("localhost", port)) {
            PrintStream ps = new PrintStream(s.getOutputStream());
            if(payload==null) {
                ps.println(http + "\n");
            }else{
                ps.println(http);
                if(origin!=null) ps.println("Origin: " + origin);
                ps.println("Content-Type: " + type + "; Charset=UTF-8");
                ps.println("Content-Length: " + payload.length()+"\n");
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

    protected String connectOptions(String http, String host, String origin) throws IOException {
        try(Socket s = new Socket("localhost", port)) {
            PrintStream ps = new PrintStream(s.getOutputStream());
            ps.println(http);
            ps.println("Origin: " + origin);
            ps.println("Host: " + host);
            ps.println();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                }
                return line;
            }
        }
    }


}
