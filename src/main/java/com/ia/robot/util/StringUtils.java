package com.ia.robot.util;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * Utilitaires String minimalistes.
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    public static String orEmpty(String s) {
        return s == null ? "" : s;
    }

    public static String join(Collection<String> items, String delimiter) {
        if (items == null || items.isEmpty()) return "";
        StringJoiner joiner = new StringJoiner(delimiter == null ? "," : delimiter);
        for (String i : items) {
            if (!isBlank(i)) joiner.add(i.trim());
        }
        return joiner.toString();
    }
}
