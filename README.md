<img src="https://c8soft.se/ronah/gitlogo.png"/>

# Ronah REST API - REST made super fast and easy


    @API(name="Name and age check", description="A GET Name and age check service")
    @GET(path="/index", response="text/text")
    public void getIndex(Request request, @Param String name, @Param Integer age){
        String response = String.format("Hello %s %s",name,age);
        request.getResponse().ok(response).send();
    }

    @API
    @GET(path="/index", useBasicAuth="true", basicAuthRealm="cars")
    public void getIndex(Request request, @Param JSONObject json){
        String response = handleRequest(json);
        request.getResponse().ok(response).send();
    }

    http://localhost:8080/api
    