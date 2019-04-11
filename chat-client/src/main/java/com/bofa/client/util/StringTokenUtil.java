package com.bofa.client.util;

import java.util.StringTokenizer;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.util
 * @date 2019/4/11
 */
public class StringTokenUtil {

    public static String[] split(String source, String delim) {
        StringTokenizer tokenizer = new StringTokenizer(source, " \r\t\n" + delim);
//        int count = (tokenizer.countTokens() - 1) / 2;
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            result[i] = tokenizer.nextToken();
        }
//        String[] response = new String[count];
//        for (int i = 2, k = 0; count > 0; i += count, count--) {
//            response[k++] = result[i];
//        }
        return result;
    }
}
