package com.crazedout.ronah.service;

import com.crazedout.ronah.Repository;
import com.crazedout.ronah.RonahHttpServer;
import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.request.HttpRequest;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Objects;

import static com.crazedout.ronah.auth.BasicAuthentication.addUser;

@Parent(allowClientIP = {"127.0.0.1"})
public class AdminService extends AutoRegisterService {

    String style = """
                <style>
            
                    table {
                            width: 1400px;
                            /*border: 1px solid gray;*/
                            border-spacing: 10px;
                            border-collapse: separate;
                        }
            
            /* ===== Base ===== */
            :root {
              --bg: #0b0f14;
              --panel: #11161d;
              --border: #1f2937;
              --text: #d1d5db;
              --muted: #8b98a5;
              --accent: #22c55e;
              --radius: 10px;
            }
            
            html, body {
              background: var(--bg);
              color: var(--text);
              font-family: Inter, system-ui, sans-serif;
              line-height: 1.45;
              margin: 0;
            }
            
            /* ===== Layout ===== */
            .container {
              max-width: 860px;
              margin: 40px auto;
              padding: 0 20px;
            }
            
            /* ===== Typography ===== */
            h1, h2, h3 {
              font-weight: 600;
              letter-spacing: -0.02em;
            }
            
            h1 {
              font-size: 1.9rem;
              margin-bottom: 16px;
            }
            
            h2 {
              font-size: 1.3rem;
              margin-top: 32px;
              border-bottom: 1px solid var(--border);
              padding-bottom: 6px;
            }
            
            p {
              color: var(--muted);
            }
            
            /* ===== Panels ===== */
            .section {
              background: var(--panel);
              border: 1px solid var(--border);
              border-radius: var(--radius);
              padding: 16px 18px;
              margin: 18px 0;
            }
            
            /* ===== Code ===== */
            code {
              font-family: ui-monospace, SFMono-Regular, monospace;
              background: #0f172a;
              padding: 2px 6px;
              border-radius: 6px;
              font-size: 0.9em;
            }
            
            pre {
              background: #020617;
              border: 1px solid var(--border);
              border-radius: var(--radius);
              padding: 14px;
              overflow: auto;
              box-shadow: 0 0 0 1px #000 inset, 0 0 20px rgba(34,197,94,0.05);
            }
            
            /* ===== Links ===== */
            a {
              color: var(--accent);
              text-decoration: none;
            }
            
            a:hover {
              text-decoration: underline;
            }
            
            /* ===== Tables ===== */
            table {
              width: 100%;
              border-collapse: collapse;
              font-size: 0.9rem;
            }
            
            th, td {
              border-bottom: 1px solid var(--border);
              padding: 8px;
              text-align: left;
            }
            
                </style>
            """;
    String script2 = """
            
            <script>
            function toggleAnnot(event,path,disp){
                el = document.getElementById(path);
                if(el!=null) {
                    el.style.position = "absolute";
                    el.style.left = (event.clientX+5) + 'px';
                    el.style.top = (event.clientY+5) + 'px';
                    el.style.display=disp;
                }
            }
            
        function sendAndReceive(url){
            var xmlhttp;
                if (window.XMLHttpRequest) {
                        xmlhttp = new XMLHttpRequest();
                    } else {
                        xmlhttp = new ActiveXObject('Microsoft.XMLHTTP');
                    }
                    xmlhttp.open('GET', url, true);
                    xmlhttp.setRequestHeader('cache-control', 'no-cache');
                    xmlhttp.send();

                    xmlhttp.onreadystatechange = function() {
                        if (xmlhttp.readyState !== 4) return;
                        //if(xmlhttp.readyState != 200) alert(xmlhttp.status);
                    };
        
        }
            
            function activate(id){
                sendAndReceive("/admin?task=toggleService&value=" + id);
            }
            </script>
            """;
    String script = "<script>function activate(name){alert(name);}</script>\n";
    String head = "<!DOCTYPE html><html><head>" + style + "\n\n" + script2 + "</head><body>";
    String tail = "</body></html>";

    public AdminService(){
        super();
        String user,passwd;
        if((user=System.getProperty("ronah.admin.user"))!=null &&
                (passwd=System.getProperty("ronah.admin.passwd"))!=null){
            addUser(user,passwd);
        }else {
            RonahHttpServer.logger.warning("Using default user and passwd for AdminService.");
            addUser("admin", "admin");
        }
    }

    @API
    @GET(path="/admin", response = "text/html", enforceParams = false, useBasicAuth = true, basicAuthRealm = "admin")
    public void admin(HttpRequest request, @Param String task, @Param String value) {
        String response = getDefaultPage(task, value);
        if(task!=null && task.equals("toggleService")){
            Service serv = Objects.requireNonNull(Repository.getInstance().getServiceByName(value));
            ((AutoRegisterService)serv).setActive(!((AutoRegisterService)serv).isActive());
            request.getResponse().ok(response).send();
        }
        if(response!=null) {
            request.getResponse().ok(response).send();
        }else{
            request.getResponse().notFound().send();
        }
    }

    String getDefaultPage(String task, String value){
        StringBuilder sb = new StringBuilder();
        sb.append(head);
        sb.append("<img width='400' src=\"https://c8soft.se/ronah/gitlogo.png\"/>");
        sb.append("<img src='data:image/png;base64,"+getIconAsBase64("/icons/restart.png")+"' />\n");
        sb.append("<form><table width='1200'><tr>");
        sb.append("<th>Class</th><th width='100'>Name</th><th width='300'>Purpose</th><th width='160'>Response</th><th>Active</th></tr>");
        StringBuilder divsString = new StringBuilder();
        if(task==null || (task.equals("server") && value.equals("services"))){
            String id = "ID_";
            int n=0;
            for(Service s: Repository.getServices()){
                sb.append("<tr>");
                sb.append("<td>").append(s.getClass().toString().substring(5)).append("</td><td>").
                        append(s.getName()).append("</td><td>")
                        .append(s.getPurpose()).append("</td><td></td>");
                sb.append("<td><input type='checkbox' onClick=\"activate('" + s.getName() + "')\" id='"+s.getName()+"' value='"+s.getName()+"' ").
                        append(s.isActive() ? "checked" : "").append("/></td>");
                sb.append("</tr>\n");
                for(Method m:s.getClass().getMethods()){
                    for(Annotation a : m.getDeclaredAnnotations()){
                        if(a instanceof GET){
                            String p = ((GET)a).path();
                            String res = ((GET)a).response();
                            sb.append("<tr><td width='60'>GET</td><td colspan=2>").append("<a onMouseOut=\"toggleAnnot(event,'"+(id+n)+"','none')\" onMouseOver=\"toggleAnnot(event,'"+(id+n)+"','block')\" href='/api?path="+p+"'>" + p ).append("</a></td><td>" + res + "</td><td></td>");
                            divsString.append(makeDiv(id + n++, a));
                        }
                        else if(a instanceof POST){
                            String p = ((POST)a).path();
                            String res = ((POST)a).response();
                            sb.append("<tr><td width='60'>POST</td><td colspan=2>").append("<a onMouseOut=\"toggleAnnot(event,'"+(id+n)+"','none')\" onMouseOver=\"toggleAnnot(event,'"+(id+n)+"','block')\" href='/api?path="+p+"'>" + p ).append("</a></td><td>" + res + "</td><td></td>");
                            divsString.append(makeDiv(id + n++, a));
                        }
                    }
                }
            }
        }
        sb.append("</tr></table>\n</form>\n");
        sb.append(divsString);
        sb.append(tail);
        return sb.toString();
    }

    String makeDiv(String id, Annotation a){
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='").append(id).append("' style='display: none; position: absolute; width: 400px; background: #0b0f14;'>");
        sb.append("<table>");
        if(a instanceof GET){
            sb.append("<tr><td>Method:</td><td>GET</td></tr>\n");
            sb.append("<tr><td>path:</td><td>" + ((GET)a).path() + "</td></tr>\n");
            sb.append("<tr><td>response:</td><td>" + ((GET)a).response() + "</td></tr>\n");
            sb.append("<tr><td>acceptContentType:</td><td>" + ((GET)a).acceptContentType() + "</td></tr>\n");
            sb.append("<tr><td>enforceParams:</td><td>" + ((GET)a).enforceParams() + "</td></tr>\n");
            sb.append("<tr><td>ignoreParentPath:</td><td>" + ((GET)a).ignoreParentPath() + "</td></tr>\n");
            sb.append("<tr><td>useBasicAuth:</td><td>" + ((GET)a).useBasicAuth() + "</td></tr>\n");
            sb.append("<tr><td>basicAuthRealm:</td><td>" + ((GET)a).basicAuthRealm() + "</td></tr>\n");
        }
        else if(a instanceof POST){
            sb.append("<tr><td>Method:</td><td>POST</td></tr>\n");
            sb.append("<tr><td>path:</td><td>" + ((POST)a).path() + "</td></tr>\n");
            sb.append("<tr><td>response:</td><td>" + ((POST)a).response() + "</td></tr>\n");
            sb.append("<tr><td>acceptContentType:</td><td>" + ((POST)a).acceptContentType() + "</td></tr>\n");
            sb.append("<tr><td>enforceParams:</td><td>" + ((POST)a).enforceParams() + "</td></tr>\n");
            sb.append("<tr><td>ignoreParentPath:</td><td>" + ((POST)a).ignoreParentPath() + "</td></tr>\n");
            sb.append("<tr><td>useBasicAuth:</td><td>" + ((POST)a).useBasicAuth() + "</td></tr>\n");
            sb.append("<tr><td>basicAuthRealm:</td><td>" + ((POST)a).basicAuthRealm() + "</td></tr>\n");
        }

        sb.append("</table>");
        sb.append("</div>\n");
        return sb.toString();
    }

    String getIconAsBase64(String resource){
        try(DataInputStream in = new DataInputStream(Objects.requireNonNull(getClass().getResourceAsStream(resource)))){
            byte[] buffer = in.readAllBytes();
            return new String(Base64.getEncoder().encode(buffer));
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
        return null;
    }

}
