package com.crazedout.ronah;

import com.crazedout.ronah.util.WildcardMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionTests {


    @Test
    void testWildcardMatching(){

        String str1 = "Ronah REST Server";
        String str2= "Ron*R?ST*";
        assertTrue(WildcardMatcher.matches(str1,str2));

        str1 = "/rest/api/test";
        str2= "/rest/a?i/*";
        assertTrue(WildcardMatcher.matches(str1,str2));

        str1 = "Ronah REST Server";
        str2= "Rone*R?ST*";
        assertFalse(WildcardMatcher.matches(str1,str2));
    }

}
