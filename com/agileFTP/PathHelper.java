package com.agileFTP;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.*;

public class PathHelper {

    public static String delimiter = System.getProperty("file.separator");

    public static String getDownloadsPath() throws IOException {

        boolean success;

        String path = System.getProperty("user.home")
                + delimiter
                + "Downloads"
                + delimiter;

        File downloadPath = new File (path);

        if (!downloadPath.exists()) {

            success = downloadPath.mkdir();

            if(success) {
                System.out.println("Created local download directory.");
            }

            else {

                IOException e = new IOException("Unable to create local download directory");
                throw e;
            }
        }

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
