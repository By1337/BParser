package org.by1337.bparser.text;

import java.util.function.IntPredicate;

public class StringUtil {
    public static String removeIf(String s, IntPredicate filter) {
        StringBuilder sb = new StringBuilder();
        char[] arr = s.toCharArray();
        for (char c : arr) {
            if (!filter.test(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
