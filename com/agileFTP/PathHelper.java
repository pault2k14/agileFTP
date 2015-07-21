package com.agileFTP;


public class PathHelper {

    public static String delimiter = System.getProperty("file.separator");

    public static String getDownloadsPath() {

        String path = System.getProperty("user.home")
                + delimiter
                + "Downloads"
                + delimiter;

        return path;
    }

    public static String getPathFromUserHome(String ... pathPieces) {

        String path = System.getProperty("user.home");
        for (String component : pathPieces) {
            path += delimiter;
            path += component;
        }
        path += delimiter;
        return path;
    }
}
