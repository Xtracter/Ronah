package com.crazedout.ronah.util;
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

public final class Util {

    public static String getHTML(String body){
        return "<DOCTYPE html>" +
                "  <html>\n" +
                "    <body>\n" +
                "      " + body +
                "    </body>\n" +
                "  </html>";
    }

    public static String getHTML(String body, String head, String script){
        return "<DOCTYPE html>" +
                "  <html>\n" +
                "    <head>\n      " + head + "\n" +
                "      <script>\n" +
                "        " + script +
                "      </script>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "      " + body +
                "    </body>\n" +
                "  </html>";
    }


}
