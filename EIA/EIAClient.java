package com.EIA;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import java.io.IOException;
import java.util.HashMap;


public class EIAClient {

    private FTPClient ftp = new FTPClient();
    private String []input = null;
    private String host = "Not connected";
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();

    public boolean execute(String []userInput) {
        input = userInput;
        commands.get(input[0].toLowerCase()).run();

        return true;
    }

    public String getHost() {
        return host;
    }


    public boolean init(HashMap main) {

        commands.putAll(main);
        commands.put("connect", () -> { connect(input); } );
        commands.put("disconnect", () -> {disconnect(); } );
        commands.put("ls", () -> { ls(); } );

        return true;
    }

    public void connect(String []input) {

        if(input.length == 4) {
            connectNoPassword(input[1], input[2], input[3]);
        }

        else if(input.length == 5) {
            connectWithPassword(input[1], input[2], input[3], input[4]);
        }

        else {
            System.out.println("Incorrect number of parameters for connect. Type 'help' for command syntax.");
        }

    }

    public boolean connectWithPassword(String userHost, String port, String username, String password) {

        if (userHost.equals(null) || port.equals(null) || username.equals(null) || password.equals(null)) {
            System.out.println("Error: hostname, port, username, and password must be provided.");
            return false;
        }


        if (ftp.isConnected()) {
            System.out.println("Already connected, disconnect first.");
            return false;
        }

        try {
            ftp.connect(userHost, Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            ftp.login(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Connected to " + userHost);
        host = userHost;
        return true;
    }


    public boolean connectNoPassword(String userHost, String port, String username) {

        if (userHost.equals(null) || port.equals(null) || username.equals(null)) {
            System.out.println("Error: hostname, port, and username must be provided.");
            return false;
        }


        if (ftp.isConnected()) {
            System.out.println("Already connected, disconnect first.");
            return false;
        }

        try {
            ftp.connect(userHost, Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            ftp.login(username, "");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Connected to " + userHost);
        host = userHost;
        return true;
    }

    public boolean disconnect() {

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {
            ftp.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Disconnected");
        host = "Not connected";
        return true;
    }

    public boolean ls() {

        FTPFile []directories = null;
        FTPFile []files = null;

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {

            directories = ftp.listDirectories();
            files = ftp.listFiles();

            for(int i = 0; i < directories.length; ++i) {
                System.out.println(directories[i]);
            }

            for(int i = 0; i < files.length; ++i) {
                System.out.println(files[i].getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

}
