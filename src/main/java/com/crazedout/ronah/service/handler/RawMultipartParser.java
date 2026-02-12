package com.crazedout.ronah.service.handler;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class RawMultipartParser {

    public static List<MultipartPart> parse(
            byte[] bodyBytes,
            String contentType,
            int contentLength, Charset charset) throws IOException {

        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            throw new IllegalArgumentException("No boundary found");
        }

        //byte[] bodyBytes = readAllBytes(inputStream, contentLength);
        String body = new String(bodyBytes, charset);

        String delimiter = "--" + boundary;
        String[] rawParts = body.split(delimiter);

        List<MultipartPart> parts = new ArrayList<>();

        for (String rawPart : rawParts) {
            if (rawPart.equals("--") || rawPart.trim().isEmpty()) {
                continue;
            }

            int headerEnd = rawPart.indexOf("\r\n\r\n");
            if (headerEnd == -1) continue;

            String headerSection = rawPart.substring(0, headerEnd);
            String bodySection = rawPart.substring(headerEnd + 4);

            Map<String, String> headers = parseHeaders(headerSection);

            // Remove trailing CRLF
            byte[] partBody = bodySection
                    .replaceFirst("\r\n$", "")
                    .getBytes(charset);

            parts.add(new MultipartPart(headers, partBody));
        }

        return parts;
    }

    private static String extractBoundary(String contentType) {
        if (contentType == null) return null;

        for (String param : contentType.split(";")) {
            param = param.trim();
            if (param.startsWith("boundary=")) {
                return param.substring("boundary=".length());
            }
        }
        return null;
    }

    private static Map<String, String> parseHeaders(String headerSection) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = headerSection.split("\r\n");

        for (String line : lines) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                String name = line.substring(0, idx).trim().toLowerCase();
                String value = line.substring(idx + 1).trim();
                headers.put(name, value);
            }
        }

        return headers;
    }

    private static byte[] readAllBytes(InputStream inputStream, int length)
            throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int totalRead = 0;

        while (totalRead < length) {
            int read = inputStream.read(data);
            if (read == -1) break;
            buffer.write(data, 0, read);
            totalRead += read;
        }

        return buffer.toByteArray();
    }
}
