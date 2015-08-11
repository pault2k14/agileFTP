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
// Must be initialized before use.
public class EIAClient implements com.agileFTP.EIA {

    //to log exceptions
    private static final Logger logger = Logger.getLogger(EIAClient.class.getName());
    private FTPClient ftp = new FTPClient();
    private String []input = null;
    private String host = "Not connected";
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();
    private ConnectionStore connectionStore = new ConnectionStore();

    /**
     * Looks up the users command in the remote hashmap, then runs the command.
     * Takes a string array input that contains the paramemters that will be passed to the method
     * to be executed.
     * @param userInput
     * @return boolean
     */
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


    /**
     * Returns the current host.
     * @param
     * @return String
     */
    public String getHost() {

        return host;
    }


    /**
     * Returns the decorator for this class.
     * @param
     * @return String
     */
    public String getDecorator() {

        return host;
    }


    /**
     * Initializes the EIAClient class variables.
     * Creates a hashmap of commands that can be called.
     * Takes a hashmap from the main application, that adds all of the main application commands
     * to this hashmap.
     * @param main
     * @return boolean
     */
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
            commands.put("download", () -> { fileTransfer(input); });
            commands.put("upload", () -> { fileTransfer(input); });

            commands.put("mkdir", () -> {
                mkdir(input);
            });
            commands.put("rmdir", () -> {
                rmdir(input);
            });
            commands.put("cd", () -> {
                cd(input);
            });
            commands.put("pwd", () -> {
                pwd();
            });
            commands.put("rm", () -> {
                rm(input);
            });
            commands.put("mv", () -> {
                mv(input);
            });
            commands.put("run", () -> {
                run(input);
            });

        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }


    /**
     * Wrapper for connect method.
     * Takes a string array input that contains the paramemters that will be interpreted and
     * passed to the connect method.
     * Syntax: connect <hostname> <port> <username [password]
     *     or  connect <name of saved connection>
     * @param input
     * @return boolean
     */
    protected boolean connect(String []input) {

        if(input == null) {
            return false;
        }

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


    /**
     * Connect to a remote server.
     * Takes a string userHost, which contains the hostname of the server to connect to. Takes a string
     * port, which contains the port number of the port to connect to. Takes the string username, which contains
     * the username to provide to the remtoe server. Takes string password, which contains the password to
     * send to the remote server.
     * Syntax: No direct syntax, called through wrapper only.
     * @param userHost, port, username, password
     * @return boolean
     */
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


    /**
     * Disconnects from the remote server.
     * Syntax: disconnect
     * @param
     * @return boolean
     */
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


    /**
     * Displays the contents of the current remote directory.
     * Syntax: ls
     * @param
     * @return boolean
     */
    protected boolean ls() {

        FTPFile []directories = null;
        FTPFile []files = null;

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {

            ftp.setListHiddenFiles(true);
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
     * Wrapper for upload and download methods
     * Takes a string array userInput that contains the paramemters that will be interpreted and
     * passed to the upload or download method.
     * Syntax: upload | download <local|remote filename to upload|download with path>
     *     <new local|remote filename to associated with filename with path to be uploaded|downloaded>
     * @param userInput
     * @return boolean
     */
    public boolean fileTransfer(String[] userInput) {

        String[] transferArray = null;

        if(!ftp.isConnected()){
            System.out.println("Not connected.");
            return false;
        }

        if(userInput == null) {
            System.out.println("Input was null!");
            return false;
        }

        if(userInput.length == 1) {
            System.out.println("No file specified for file transfer.  Type 'help' for command syntax.");
            return false;
        }

        // User has not completed a valid upload string.
        if((userInput.length % 2) != 1) {
            System.out.println("Incorrect number of parameters for file transfer.  Type 'help' for command syntax.");
            return false;
        }

        for(int i = 1; i < userInput.length; i += 2) {

            transferArray = new String[3];
            transferArray[0] = "";
            transferArray[1] = userInput[i];
            transferArray[2] = userInput[i + 1];

            if(userInput[0].toLowerCase().equals("upload")) {

                if(!upload(transferArray)) {
                    // There was a problem, abort any future transfers.
                    return false;
                }

            }

            else if(userInput[0].toLowerCase().equals("download")) {

                if(!download(transferArray)) {
                    // There was a problem. abort any future transfers.
                    return false;
                }
            }

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

            File uploaded = new File (input[2]);  //create remote file

            if (!uploaded.exists()) {
                System.out.println("File does not exist");
                return false;
            }

            inputStream = new FileInputStream(uploaded);
            boolean success = ftp.storeFile(input[1], inputStream);
            try{
                inputStream.close();
            } catch(IOException e){
                logger.log(Level.SEVERE, "Input stream failed to close.", e);
            }

            if (success) {
                System.out.println("The file uploaded successfully.");

                // Need this here for correct implementation of a boolean function and for testing.
                return true;
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

                // Need this here for correct implementation of a boolean function and for testing.
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

    /**
     * Change directory on remote server (cd command)
     * Takes a string 'input' from the command line and changes the current working directory
     * to the specified directory on the connected remote ftp server using the syntax
     * 'cd <directory>'
     * Example usage (Mac): 'cd upload'
     * @param input The name of the directory to cd to.  Either full path or relative to current
     * @return true if successful
     */
    public boolean cd(String [] input) {
        boolean result = false;
        try {

            if (!ftp.isConnected()) {
                System.out.println("Not connected.");
                return false;
            }
            if (input.length != 2) {
                System.out.println("Incorrect number of parameters for cd. Type 'help' for command syntax.");
            }

            if (input[1].contentEquals("..")) {
                result = ftp.changeToParentDirectory();
            }
            else {
                result = ftp.changeWorkingDirectory(input[1]);
            }
            if (result == false) {
                System.out.println("Unable to change to specified directory on remote server.");
            }
            return result;
        }

        catch (IOException e) {
            System.out.println("Unable to change to specified directory on remote server.  " +
                    "Error communicating with remote server.");
            return false;
        }
    }

    /**
     * Print working directory on remote server
     * Displays the current working directory on the connected remote ftp server using the syntax
     * 'pwd'.  Directory string is printed to System.out, as well as returned.
     * Example usage (Mac): 'pwd'
     * @return String of the current working directory
     *         or null if unsuccessful
     */

    public String pwd() {
        String directoryName = null;
        boolean result = false;
        try {
            if (!ftp.isConnected()) {
                System.out.println("Not connected.");
                return null;
            }

            directoryName = ftp.printWorkingDirectory();
            if (directoryName != null) {
                System.out.println(directoryName);
                return directoryName;
            }
            System.out.println("Unable to determine working directory on remote server.");
            return null;
        }

        catch (IOException e) {
            System.out.println("Unable to determine working directory on remote server.)" +
                    "  Error communicating with remote server.");
            return null;
        }
    }

    /**
     * Make a directory on remote server
     * Takes a string 'input' from the command line and makes the
     * specified directory on the connected remote ftp server using the syntax
     * 'mkdir <directory>'
     * Example usage (Mac): 'mkdir kragle_top_secret'
     * @param input The name of the directory to make
     * @return true if successful
     */
    public boolean mkdir(String [] input) {
        boolean result = false;
        try {

            if (!ftp.isConnected()) {
                System.out.println("Not connected.");
                return false;
            }
            if (input.length != 2) {
                System.out.println("Incorrect number of parameters for mkdir. Type 'help' for command syntax.");
            }
            result = ftp.makeDirectory(input[1]);
            if (result == false) {
                System.out.println("Unable to make specified directory on remote server.");
            }
            return result;
        }

        catch (IOException e) {
            System.out.println("Unable to make specified directory on remote server.  " +
                    "Error communicating with remote server.");
            return false;
        }
    }

    /**
     * Delete a directory on remote server
     * Takes a string 'input' from the command line and deletes the specified directory on
     * the connected remote ftp server using the syntax
     * 'rmdir <directory>'
     * Example usage (Mac): 'rmdir everything_is_awesome_lyrics'
     * @param input The name of the directory to remove.
     * @return true if successful
     */
    public boolean rmdir(String [] input) {
        boolean result = false;
        try {

            if (!ftp.isConnected()) {
                System.out.println("Not connected.");
                return false;
            }
            if (input.length != 2) {
                System.out.println("Incorrect number of parameters for rmdir. Type 'help' for command syntax.");
            }

            result = ftp.removeDirectory(input[1]);
            if (result == false) {
                System.out.println("Unable to remove specified directory on remote server.  " +
                        "Verify that the directory is empty.");
            }
            return result;
        }

        catch (IOException e) {
            System.out.println("Unable to remove specified directory on remote server.  " +
                    "Error communicating with remote server.");
            return false;
        }
    }


    /**
     * Wrapper for the renameFile method.
     * Takes a string array userInput that contains the paramemters that will be interpreted and
     * passed to removeFile method.
     * Syntax: rm <remote filename with path to remove>
     * @param userInput
     * @return boolean
     */
    public boolean rm (String []userInput) {

        if (userInput == null) {
            System.out.println("Input was null!");
            return false;
        }

        if (userInput.length != 2) {
            System.out.println("Incorrect number of parameters for rm. Type 'help' for command syntax.");
            return false;
        }

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        if(!removeFile(userInput[1])) {
            return false;
        }

        return true;
    }


    /**
     * Removes a file om the remote server.
     * Takes a string remoteFileWithPath from the wrapper, this contains the absolute path of
     * the remote file to be removed.
     * Syntax: No direct syntax, called through wrapper only.
     * @param remoteFileWithPath
     * @return boolean
     */
    public boolean removeFile (String remoteFileWithPath) {

        if(remoteFileWithPath == null) {
            System.out.println("Remote file name cannot be null.");
            return false;
        }

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {
            if(!ftp.deleteFile(remoteFileWithPath)){
                System.out.println("A problem occurred removing the remote file " + remoteFileWithPath);
                System.out.println("Please ensure that the path and filename are correct.");
                return false;
            }
        }

        catch (IOException e) {
            System.out.println("A problem occurred removing the remote file " + remoteFileWithPath);
            System.out.println("Please ensure that the path and filename are correct.");
            return false;
        }

        System.out.println(remoteFileWithPath + " was successfully removed.");
        return true;

    }


    /**
     * Wrapper for the renameFile method.
     * Takes a string array userInput that contains the paramemters that will be interpreted and
     * passed to renameFile method.
     * Syntax: mv <remote filename with path> <new remote filename with path>
     * @param userInput
     * @return boolean
     */
    public boolean mv (String [] userInput) {

        if (userInput == null) {
            System.out.println("Input was null!");
            return false;
        }

        if (userInput.length != 3) {
            System.out.println("Incorrect number of parameters for mv. Type 'help' for command syntax.");
            return false;
        }

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        if(!renameFile(userInput[1], userInput[2])) {
            return false;
        }

        return true;

    }


    /**
     * Renames a file om the remote server.
     * Takes a string remoteFileWithPath from the wrapper, this contains the absolute path of
     * the remote file. Also takes a string newRemoteFileWithPath, this contains the new absolute path
     * and name of the file.
     * Syntax: No direct syntax, called through wrapper only.
     * @param remoteFileWithPath, newRemoteFileWithPath
     * @return boolean
     */
    public boolean renameFile (String remoteFileWithPath, String newRemoteFileWithPath) {


        if(remoteFileWithPath == null) {
            System.out.println("Remote file name cannot be null.");
            return false;
        }

        if(newRemoteFileWithPath == null) {
            System.out.println("New remote file name cannot be null.");
            return false;
        }

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {
            if(!ftp.rename(remoteFileWithPath, newRemoteFileWithPath)){
                System.out.println("A problem occurred renaming the remote file " + remoteFileWithPath);
                System.out.println("Please ensure that the path and filename are correct.");
                return false;
            }
        }

        catch (IOException e) {
            System.out.println("A problem occurred remaming the remote file " + remoteFileWithPath);
            System.out.println("Please ensure that the path and filename are correct.");
            return false;
        }

        System.out.println(remoteFileWithPath + " was successfully renamed to " + newRemoteFileWithPath);
        return true;

    }


    /**
     * Wrapper for the runCommand method.
     * Takes a string array userInput that contains the paramemters that will be interpreted and
     * passed to runCommand.
     * Syntax: run "remote command to execute"
     * @param userInput
     * @return boolean
     */
    public boolean run (String[] userInput) {

        String userCommand = "";
        String userCommandParams = "";

        if (userInput == null) {
            System.out.println("Input was null!");
            return false;
        }

        if (userInput.length < 2) {
            System.out.println("Incorrect number of parameters for run. Type 'help' for command syntax.");
            return false;
        }

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        userCommand = userInput[1];

        for(int i = 2; i < userInput.length; ++i) {
            userCommandParams = userCommandParams + " " + userInput[i];
        }

        if(!runCommand(userCommand, userCommandParams)) {
            return false;
        }

        return true;
    }


    /**
     * Runs a command on the remote server.
     * Takes a string remoteCommand from the wrapper, which is the remote command that a user wants to
     * run on the remote server. Also takes a string remoteCommandParams from the wrapper, which are the
     * parameters for the command the user wants to run on the remote server.
     * Syntax: No direct syntax, called through wrapper only.
     * @param remoteCommand, remoteCommandParams
     * @return boolean
     */
    public boolean runCommand (String remoteCommand, String remoteCommandParams) {

        if(remoteCommand == null) {
            System.out.println("Remote command cannot be null.");
            return false;
        }

        if(remoteCommandParams == null) {
            System.out.println("Remote command parameters cannot be null.");
            return false;
        }

        if (!ftp.isConnected()) {
            System.out.println("Not connected.");
            return false;
        }

        try {
            if(!ftp.doCommand(remoteCommand, remoteCommandParams)){
                System.out.println("A problem occurred executing remote command.");
                System.out.println("The remote server replied with: " + ftp.getReplyString());
                return false;
            }
        }

        catch (IOException e) {
            System.out.println("A problem occurred executing remote command.");
            return false;
        }

        System.out.println("Remote command successfully executed.");
        System.out.println("The remote server replied with: " + ftp.getReplyString());
        return true;
    }
}
