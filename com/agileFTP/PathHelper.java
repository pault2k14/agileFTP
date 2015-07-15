package com.agileFTP;

public class PathHelper {
    public static String os;

    static {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().contains("windows")) {
            PathHelper.os = "windows";
        }
        if(os.toLowerCase().contains("linux")) {
            PathHelper.os = "linux";
        }
        if(os.toLowerCase().contains("mac")) {
            PathHelper.os = "mac";
        }
    }
    private static char getDelimiter() {
        if(os.equals("windows")) {
            return '\\';
        }
        return '/';
    }

    public static String getDownloadsPath() {
        char delimiter = getDelimiter();
        String path = System.getProperty("user.home")
                + delimiter
                + "Downloads"
                + delimiter;
        return path;
    }

    public static String getPathFromUserHome(String ... pathPieces) {
        char delimiter = getDelimiter();
        String path = System.getProperty("user.home");
        for (String component : pathPieces) {
            path += delimiter;
            path += component;
        }
        path += delimiter;
        return path;
    }
}
