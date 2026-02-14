package com.crazedout.ronah.handler;
/*
 * Ronah REST Server
 * Copyright (c) 2026 Fredrik Roos.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * mail: info@crazedout.com
 */

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class RawMultipartParser {

    public static List<MultipartPart> parse(
            byte[] bodyBytes,
            String contentType,
            Charset charset) throws IOException {

        List<MultipartPart> parts = new ArrayList<>();

        String boundary = extractBoundary(contentType);
        if (boundary == null) {
            throw new IllegalArgumentException("No boundary found.");
        }

        //byte[] bodyBytes = readAllBytes(inputStream, contentLength);
        String body = new String(bodyBytes, charset);

        String delimiter = "--" + boundary;
        String[] rawParts = body.split(delimiter);

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
}
