<img src="https://c8soft.se/ronah/gitlogo.png"/>

# Ronah REST API - REST made super fast and easy

Ronah REST API is a super simple and lightning-fast REST API designed to make your development workflow seamless. Built-in web tests let you verify endpoints instantly, so you can focus on building, not debugging.

Key features include:

Fast and lightweight – minimal setup, maximum speed.

Built-in web tests – test your API directly without extra tools.

Secure by default – supports basic authentication and TLS.

Ronah REST API gives you everything you need for quick, reliable, and secure API development.

    public static void main(String[] args){
        
        AutoRegisterService.register(MyRESTService.class);
        AutoRegisterService.register(APIService.class);
    
        Ronah r = new Ronah();
        r.start();
    }


    public class MyRESTService extends AutoRegisterService {

        public MyRESTService(){
            super();
            BasicAuthentication.addUser("falcon","pencil");
        }

        @API(name="Name and age check", description="A GET Name and age check service")
        @GET(path="/index", response="text/text")
        public void getIndex(Request request, @Param String name, @Param Integer age){
            String response = String.format("Hello %s %s",name,age);
            request.getResponse().ok(response).send();
        }

        @API(name="My handler of Json call", description="Handles a Json payload and returns great things")
        @GET(path="/index", useBasicAuth="true", basicAuthRealm="cars")
        public void getIndex(Request request, @Param JSONObject json){
            String response = handleRequest(json);
            request.getResponse().ok(response).send();
        }

    }
    http://localhost:8080/api
    