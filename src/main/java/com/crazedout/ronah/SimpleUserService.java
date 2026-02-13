package com.crazedout.ronah;

import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.Request;

public class SimpleUserService extends AutoRegisterService {


    @POST(path="/upload")
    public void uploadFile(Request request){

    }

}
