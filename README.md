# Ronah REST 
Plug And Play JAVA REST framework made super easy!

- REST should be easy and fast to setup - Presenting Ronah.

    @GET(path="/api/v1", response="text/html", ignoreParentPath = true)
    public void getIndex(Request request, @Param String name, @Param String age){
        System.out.println(request.getQueryString());
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello %s %s</h3></body></html>",name,age);
        request.getResponse().ok(html).send();
    }
