package com.crazedout.ronah;

import java.io.IOException;
import java.util.Random;

public class StressTest implements Runnable{

    long sleep = 0;
    StressTest(long sleep){
        this.sleep=sleep;
    }

    public static void main(String[] args){

        for(int i = 0; i < 50; i++) {
            Random random = new Random();
            new Thread(new StressTest(random.nextInt(500 - 50 + 1) + 50)).start();
        }
    }

    public void run(){
        try {
            HttpURLConnectionTest.sendGET();
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
