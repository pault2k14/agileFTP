package com.agileFTP;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import sun.nio.ch.IOUtil;
import sun.util.logging.PlatformLogger;

import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// Class for all remote server tasks.
public class EIAClient implements com.agileFTP.EIA {

    //to log exceptions
    private static final Logger logger = Logger.getLogger(EIAClient.class.getName());

    private FTPClient ftp = new FTPClient();
    private String []input = null;
    private String host = "Not connected";
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();

    private ConnectionStore connectionStore = new ConnectionStore();

    // Looks up the users command in the remote hashmap
    // then runs the lambda function.
    public boolean execute(String []userInput) {
        input = userInput;

        try {
            commands.get(input[0].toLowerCase()).run();
        } catch (NullPointerException e) {
            System.out.println("Command not found, type 'help' for command syntax.\"");
            return false;
        }

        return true;
    }

    // Returns the hostname as a string.
    public String getHost() {

        return host;
    }

    // Returns the remote decorator as a string.
    public String getDecorator() {

        return host;
    }


    // Hashmap of the FTPApp is added to this hashmap
    // Then the remote hashmap is setup.
    public boolean init(HashMap main) {

        try {
            commands.putAll(main);
            commands.put("connect", () -> {
                connect(input);
            });
            commands.put("save", () -> {
                connectionStore.saveConnection(input);
            });
            commands.put("list", () -> {
                connectionStore.listConnections();
            });
            commands.put("delete", () -> {
                connectionStore.deleteConnection(input);
            });
            commands.put("disconnect", () -> {
                disconnect();
            });
            commands.put("ls", () -> {
                ls();
            });
            commands.put("download", () -> { download(input); });
            commands.put("upload", () -> { upload(input); });
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }




    // Connect wrapper to determine if the user entered a password or not.
    protected boolean connect(String []input) {

        // This case is when the user types <connect> <name of saved connection>
        if(input.length == 2) {
            input = connectionStore.retrieveConnection(input[1]);
            if(input == null) return false; // Connection name wasn't found
        }

        if(input.length == 4) {
            connectToHost(input[1], input[2], input[3], "");
        }

        else if(input.length == 5) {
            connectToHost(input[1], input[2], input[3], input[4]);
        }

        else {
            System.out.println("Incorrect number of parameters for connect. Type 'help' for command syntax.");
        }
        return true;
    }


    // Connect to the remote server with a password.
    public boolean connectWithPassword(String userHost, String port, String username, String password) {

        if (userHost.equals(null) || port.equals(null) || username.equals(null) || password.equals(null)) {
            System.out.println("Error: hostname, port, username, and password must be provided.");
            return false;
        }

        return true;
    }


    // Connect to the remote server with a blank password.
    protected boolean connectToHost(String userHost, String port, String username, String password) {

        if (userHost == null || port == null  || username == null || password == null) {
            System.out.println("Error: hostname, port, and username must be provided.");
            return false;
        }


        if (ftp.isConnected()) {
            System.out.println("Already connected, disconnect first.");
            return false;
        }

        try {
            ftp.connect(userHost, Integer.parseInt(port));

            if(ftp.getReplyCode() != 220) {
                System.out.println("Unable to connect to host.");
                return false;
            }

        } catch (IOException e) {
            System.out.println("Unable to connect, please check the host and port.");
            return false;
        }

        try {
            ftp.login(username, password);

            if(ftp.getReplyCode() != 230) {
                System.out.println("Unable to login, please check username and password.");
                return false;
            }

        } catch (IOException e) {
            System.out.println("Unable to login, please check username and password.");
            return false;
        }

        System.out.println("Connected to " + userHost);
        host = userHost;

        return true;
    }

    // Disconnect from the remote server.
    protected boolean disconnect() {

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            host = "Not connected";
            return false;
        }

        try {
            ftp.disconnect();

        } catch (IOException e) {
            System.out.println("An error occurred when trying to disconnect.");
            return false;
        }

        return true;
    }


    // Display the contents of the current remote directory.
    protected boolean ls() {

        FTPFile []directories = null;
        FTPFile []files = null;

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {

            directories = ftp.listDirectories();

            if(ftp.getReplyCode() != 226) {
                System.out.println("Unable to list current directory.");
                return false;
            }

            files = ftp.listFiles();

            if(ftp.getReplyCode() != 226) {
                System.out.println("Unable to list current directory.");
                return false;
            }

            // Used in testing for this function.
            System.out.println("Remote listing:");

            for(int i = 0; i < directories.length; ++i) {
                System.out.println(directories[i]);
            }

            for(int i = 0; i < files.length; ++i) {
                System.out.println(files[i].getRawListing() );
            }

        } catch (IOException e) {
            System.out.println("Unable to list current directory.");
            return false;
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
        //declare InputStream outside try block so it's in scope for error handling
        InputStream inputStream = null;

        try {
            if(!ftp.isConnected()){
                System.out.println("Not connected.");
                return false;
            }

            if(input.length != 3){
                System.out.println("Incorrect number of parameters for upload.  Type 'help' for command syntax.");
                return false;
            }


            File uploaded = new File (input[2]);  //create file to be uploaded

            inputStream = new FileInputStream(uploaded);
            boolean success = ftp.storeFile(input[1], inputStream);
            try{
                inputStream.close();
            } catch(IOException e){
                logger.log(Level.SEVERE, "Input stream failed to close.", e);
            }

            if (success) {
                System.out.println("The file uploaded successfully.");
                return success;
            } else {
                System.out.println("File not uploaded.");
            }

        }

        catch (IOException e){
            System.out.println("File does not exist or invalid filepath.");
        }
        //close download stream if it never closed in the try block
        finally{
            IOUtils.closeQuietly(inputStream);
        }
        return false;
    }


    /**
     * Get file from remove server
     * Takes a string 'input' from the command line and downloads the specified remote file
     * to the specified local destination using the syntax 'download <src file> <new local file>
     * Example usage: 'download 512KB.zip newlocalfile.zip'
     * @param input
     * @return
     */
    public boolean download (String[] input){
        //declare OutputStream outside try block so it's in scope for error handling
        OutputStream downloadStream = null;

        try {
            if(!ftp.isConnected()){
                System.out.println("Not connected.");
                return false;
            }

            if(input.length != 3){
                System.out.println("Incorrect number of parameters for download.  Type 'help' for command syntax.");
                return false;
            }

            long fileSize = 0;
            FTPFile[] files = ftp.listFiles(input[1]);
            if (files.length == 1 && files[0].isFile()) {
                fileSize = files[0].getSize();
            }

            WorkingIndicator progress = new WorkingIndicator(ftp, fileSize);
            progress.start();
            File downloaded = new File (PathHelper.getDownloadsPath()+input[2]);  //create local file
            downloadStream = new BufferedOutputStream(new FileOutputStream(downloaded));
            boolean success = ftp.retrieveFile(input[1], downloadStream); //pass in remote file and stream
            progress.terminate();

            try{
                downloadStream.close();
            } catch(IOException e){
                logger.log(Level.SEVERE, "Download stream failed to close.", e);
            }

            if(success){
                System.out.println("File has been successfully downloaded");
                return true;
            } else{
                System.out.println("File not downloaded.");
            }

        }
        catch (IOException e){
            System.out.println("No destination file specified.");
        }
        //close download stream if it never closed in the try block
        finally{
            IOUtils.closeQuietly(downloadStream);
        }
        return false;
    }

}
