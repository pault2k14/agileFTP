package agileFTP;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.io.IOException;
import java.util.HashMap;


// Class for all remote server tasks.
public class EIAClient {

    private FTPClient ftp = new FTPClient();
    private String []input = null;
    private String host = "Not connected";
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();


    // Looks up the users command in the remote hashmap
    // then runs the lambda function.
    public boolean execute(String []userInput) {
        input = userInput;
        commands.get(input[0].toLowerCase()).run();

        return true;
    }

    // Returns the hostname as a string.
    public String getHost() {

        return host;
    }


    // Hashmap of the FTPApp is added to this hashmap
    // Then the remote hashmap is setup.
    public boolean init(HashMap main) {

        commands.putAll(main);
        commands.put("connect", () -> {
            connect(input);
        });
        commands.put("disconnect", () -> {disconnect(); } );
        commands.put("ls", () -> { ls(); } );
        commands.put("upload", () -> { upload(input); });

        return true;
    }




    // Connect wrapper to determine if the user entered a password or not.
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


    // Connect to the remote server with a password.
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


    // Connect to the remote server with a blank password.
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

    // Disconnect from the remote server.
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


    // Display the contents of the current remote directory.
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

    /**
     * Upload file to remove server
     * Takes a string 'input' from the command line and uploads the specified local file
     * to the connected remote ftp server using the syntax 'upload <filename> <local filepath>
     * Example usage (Mac): 'upload upload.txt /Users/<username>/Desktop/upload.txt'
     * NOTE: if using speedtest.tele2.net the filename must include the directory name: "upload/upload.txt"
     *
     * @param input
     * @return boolean
     */
    public boolean upload (String[] input){
        try {
            if(!ftp.isConnected()){
                System.out.println("Not connected.");
                return false;
            }

            if(input.length != 3){
                System.out.println("Incorrect number of parameters for upload.  Type 'help' for command syntax.");
                return false;
            }

            // Enter Local Passive mode to switch data connection mode from server-to-client (default mode) to client-to-server
            // and to get through firewall and avoid potential connection issues
            /**
             * According to the API docs:
             * The FTPClient will stay in PASSIVE_LOCAL_DATA_CONNECTION_MODE until the mode is changed
             * by calling some other method such as enterLocalActiveMode()
             * However: currently calling any connect method will reset the mode to ACTIVE_LOCAL_DATA_CONNECTION_MODE.
             */
/*            ftp.enterLocalPassiveMode();
            try {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            File uploaded = new File (input[2]);  //create remote file

            InputStream inputStream = new FileInputStream(uploaded);
            boolean success = ftp.storeFile(input[1], inputStream);
            inputStream.close();
            if (success) {
                System.out.println("The file uploaded successfully.");
            } else {
                System.out.println("Not quite right...");
            }

        }

        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
}
