package com.crazedout.ronah;

import com.crazedout.ronah.service.DefaultService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest extends TestUtils {


    @Test
    public void mainTest(){
        Thread t = new Thread(()->{
                new DefaultService();
                assertThrows(NumberFormatException.class,()-> {
                    Ronah.main(new String[]{"-p:808w"});
                });
        });
        t.start();
        try {
            Thread.sleep(200);
        }catch(InterruptedException ex){
            ex.printStackTrace(System.out);
        }
    }
}
