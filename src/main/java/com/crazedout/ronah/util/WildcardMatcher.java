package com.crazedout.ronah.util;

/**
 * Wildcard pattern matching using recursion
 */
public class WildcardMatcher  {

    static boolean wildCardRec(String txt,
                               String pat, int n, int m) {

        // Empty pattern can match with an empty text only
        if (m == 0)
            return (n == 0);

        // Empty text can match with a pattern consisting
        // of '*' only.
        if (n == 0) {
            for (int i = 0; i < m; i++)
                if (pat.charAt(i) != '*')
                    return false;
            return true;
        }

        // Either the characters match or pattern has '?'
        // move to the next in both text and pattern
        if (txt.charAt(n - 1) == pat.charAt(m - 1) ||
                pat.charAt(m - 1) == '?')
            return wildCardRec(txt, pat, n - 1, m - 1);

        // if the current character of pattern is '*'
        // first case: It matches with zero character
        // second case: It matches with one or more characters
        if (pat.charAt(m - 1) == '*')
            return wildCardRec(txt, pat, n, m - 1) ||
                    wildCardRec(txt, pat, n - 1, m);

        return false;
    }

    /**
     * Matches string with wildcards.
     * @param string String
     * @param pattern wildcard pattern
     * @return true/false
     */
    public static boolean matches(String string, String pattern) {
        int n = string.length();
        int m = pattern.length();
        return wildCardRec(string, pattern, n, m);
    }

    /*
    public static void main(String[] args) {
        String txt = "abcde";
        String pat = "a*de";
        System.out.println(wildCard(txt, pat) ? "true" : "false");
    }*/
}





