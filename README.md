<img src="https://c8soft.se/ronah/gitlogo.png"/>

# Ronah REST API - REST made super fast and easy

Ronah REST API is a super simple and lightning-fast REST API designed to make your development workflow seamless. Built-in web tests let you verify endpoints instantly, so you can focus on building, not debugging.

Key features include:

Fast and lightweight – minimal setup, maximum speed.

Built-in web tests – test your API directly without extra tools.

Secure by default – supports basic authentication and TLS.

Ronah REST API gives you everything you need for quick, reliable, and secure API development.

# Example:

    public class MyRESTService extends AutoRegisterService {

        public MyRESTService(){
            super();
            BasicAuthentication.addUser("falcon","pencil");
        }

        @API
        @GET(response = "text/html", path="/form")
        public void getFile(Request request) throws IOException {
            DataInputStream dis = new DataInputStream(Objects.requireNonNull(getClass().getResourceAsStream("/form.html")));
            byte[] buffer = dis.readAllBytes();
            request.getResponse().ok(new String(buffer)).send();
        }

        @API(name="Name and age check", description="A GET Name and age check service")
        @GET(path="/index", response="text/text")
        public void getIndex(Request request, @Param String name, @Param Integer age){
            String response = String.format("Hello %s %s",name,age);
            request.getResponse().ok(response).send();
        }

        @API(name="My handler of Json call", description="Handles a Json payload and returns great things")
        @GET(path="/json", acceptContentType="application/json")//, useBasicAuth=true, basicAuthRealm="cars")
        public void getIndex(Request request, @Param JSONObject json){
            try {
                String response = "Hello " + json.getString("name") + " " + json.getInt("age");
                request.getResponse().ok(response).send();
            }catch(JSONException ex){
                request.getResponse().error(ex.getMessage()).send();
            }
        }

        @API
        @POST(path="/upload", response="text/text", acceptContentType = HttpRequest.MULTIPART_FORM_DATA)
        public void getRest3(Request request)  {
            String res = "";
            for(MultipartPart part:request.getMultiParts()){
                res += part.getHeader("Content-Type") + "/" + part.getHeader("Content-Disposition") + "\n";
            }
            request.getResponse().ok(res).send();
        }

        public static void main(String[] args){

            new MyRESTService();
            new APIService();

            RonahHttpServer server = new RonahHttpServer();
            server.start(8080);
        }
    }

# Test Web API
http://localhost:8080/api 

<img src="https://c8soft.se/ronah/apiimg.png"/>

