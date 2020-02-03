package com.github.danshan.asrassist.xfyun.util;

public class VersionUtil {
    private static final String VERSION = "2.0.0";
    private static final String SUB_VERSION = "1005";

    private VersionUtil() {
    }

    public static String getVersion() {
        return String.format("%s.%s", VERSION, SUB_VERSION);
    }
}
