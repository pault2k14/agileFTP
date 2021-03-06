package com.agileFTP;

import java.util.*;


// Class for the FTPApp user interface, and intergration
// with the EIAClient and EIALocal class objects.
public class FTPApp {

    private EIAClient ftp = new EIAClient();
    private EIALocal local = new EIALocal();
    private com.agileFTP.EIA inUseMode = null;
    private Scanner scan = new Scanner(System.in);
    private String []input = new String[100];
    private boolean guard = true;
    private String currentMode = null;
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();


    /**
     * etup and initialize the user interface.
     * @param
     * @return boolean
     */
    public static void main(String[] args) {

        FTPApp App = new FTPApp();
        App.init();
        App.start();

    }


    /**
     * Holds the user input loop.
     * @param
     * @return boolean
     */
    public boolean start() {

        while(guard) {
            System.out.print(currentMode + ": " + inUseMode.getDecorator() + " > ");
            input = split(scan.nextLine());
            execute(input);
        }

        return true;

    }


    /**
     * Initializes the EIAClient class variables.
     * Sets up the hashmap, initialize ftp and local objects.
     * @param
     * @return boolean
     */
    public boolean init() {

        try {
            commands.put("help", () -> { help();     });
            commands.put("exit", () -> { exit();});
            commands.put("mode", () -> { mode(input[1]); });
        }

        catch (NullPointerException e) {
            System.out.println("Command not found, type 'help' for command syntax.\"");
            return false;
        }

        ftp.init(commands);
        local.init(commands);

        mode("remote");

        System.out.println("Team Everything is Awesome presents: Awesome FTP!");
        System.out.println("Type 'help' for command syntax assistance.");

        return true;
    }


    /**
     * Splits a user's input by spaces, but keeps input surrounded by quotes as one unit.
     * Takes a string input, that contains the user's input to be parsed.
     * @param input
     * @return String
     */
    public static String[] split(String input) {

        char[] userInput = null;
        String[] inputArray = new String[100];
        String[] cleanedArray = null;
        List<String> stockList = new ArrayList<String>();
        boolean firstQuote = false;
        int y = 0;
        int i = 0;

        if(input == null || input == "") {

            cleanedArray = new String[1];
            cleanedArray[0] = "";
            return cleanedArray;
        }

        userInput = input.trim().toCharArray();

        while(i < userInput.length) {

            // If it's the first quote, ignore the
            // character.
            if(userInput[i] == '"' && !firstQuote) {

                firstQuote = true;
                ++i;
            }

            // Also ignore if it is the second quote.
            else if(userInput[i] == '"' && firstQuote) {

                firstQuote = false;
                ++i;
            }

            // If we are inside of a quote then add the space into
            // the current index.
            else if(userInput[i] == ' ' && firstQuote) {

                if(inputArray[y] == null) {
                    inputArray[y] = String.valueOf(userInput[i]);
                }

                else {
                    inputArray[y] = inputArray[y].concat(String.valueOf(userInput[i]));
                }

                ++i;
            }

            // If we are not inside of a quote then skip past this
            // and any following spaces and then increment the inputArray
            // as the current keyword has been completed.
            else if(userInput[i] == ' ' && !firstQuote) {

                // Go past any spaces outside of quotes.
                while(userInput[i] == ' ') {
                    ++i;
                }

                // Increment the currently being constructed inputArray.
                ++y;
            }

            // In this case just add characters to the
            // inputArray and then increment only the userInput array.
            else {

                if(inputArray[y] == null) {
                    inputArray[y] = String.valueOf(userInput[i]);
                }

                else {
                    inputArray[y] = inputArray[y].concat(String.valueOf(userInput[i]));
                }

                ++i;
            }

        }

        // Count the number of elements in our new that are not null.
        for(i = 0, y = 0; i < inputArray.length ; ++i) {

            if(inputArray[i] != null) {
                ++y;
            }
        }

        // Create an array with the size of non-null elements in
        // in the inputArray.
        cleanedArray = new String[y];

        // Copy the non-null elements into our cleaned array.
        for(i = 0; i < y; ++i) {

            cleanedArray[i] = inputArray[i];
        }

        return cleanedArray;
    }


    /**
     * Displays command syntax help.
     * Syntax: help
     * @param
     * @return void
     */
    public boolean help() {
        String save = "save - Saves a connection config." +
                "\n          Usage: save <name of connection> <hostname> <port> <username> [password]" +
                "\n          Note: The password field is optional.";
        String delete = "delete - Deletes a saved connection." +
                "\n          Usage: delete <name of connection>";
        String list = "list - Lists available saved connections.";


        System.out.println("");
        System.out.println("Global");
        System.out.println("help - Displays this help text.");
        System.out.println("mode - Switches between local and remote command mode.");
        System.out.println("       Usage: mode <connection>");
        System.out.println("              connection: remote | local");
        System.out.println("exit - Exit the program.");
        System.out.println("");
        System.out.println("Remote");
        System.out.println("ls - Displays the contents of the current remote directory.");
        System.out.println("connect - Connect to a remote FTP host or saved connection.");
        System.out.println("          Usage: connect <hostname> <port> <username> [password]");
        System.out.println("             or: connect <name of saved connection>");
        System.out.println("          Note: The password field is optional.");
        System.out.println("disconnect - Disconnect from the remote FTP host.");
        System.out.println("download - Download a single file from the FTP host.");
        System.out.println("          Note: Use any number of pairs of <remote filename with path> <local filename>");
        System.out.println("          Usage: download /home/remote_file1.zip local_file1.zip /home/remote_file2.zip local_file2.zip");
        System.out.println("upload - Upload any number of files to the FTP host.");
        System.out.println("          Note: Use any number of pairs of <remote filename with path> <local filename with path>");
        System.out.println("          Usage: upload /upload/test1.txt C:\\test\\test1.txt /upload/test2.txt C:\\test\\test2.txt");
        System.out.println("mkdir - Make a directory on the remote FTP host.");
        System.out.println("          Usage: mkdir <directory name>");
        System.out.println("rmdir - Remove a directory on the remote FTP host.");
        System.out.println("          Usage: rmdir <directory name>");
        System.out.println("cd - Change current working directory on the remote FTP host.");
        System.out.println("          Usage: cd <directory name>");
        System.out.println("             or: cd ..");
        System.out.println("pwd - Print working directory of the remote FTP host.");
        System.out.println("          Usage: pwd");
        System.out.println("rm - Remove a file on the remote FTP host.");
        System.out.println("          Usage: rm <remote filename with path>");
        System.out.println("mv - Rename a file on the remote FTP host.");
        System.out.println("          Usage: mv <remote filename with path> <new remote filename with path>");
        System.out.println("run - Execute a arbritary command on the remote FTP server.");
        System.out.println("          Note: This can be used for any command the remote server supports.");
        System.out.println("                Such as to change permissions of the file upload/p.txt - run \"site chmod 664 upload/p.txt\"");
        System.out.println("          Usage: run \"command to run\"");
        System.out.println(save);
        System.out.println(list);
        System.out.println(delete);
        System.out.println("");
        System.out.println("Local");
        System.out.println("ls - Displays the contents of the current local directory.");
        System.out.println("");

        return true;
    }


    /**
     * Looks up the users command in the FTPApp hashmap, then runs the command.
     * Takes a string array userInput that contains the paramemters that will be passed to the method
     * to be executed.
     * @param input
     * @return boolean
     */
    public boolean execute(String []input) {

        try {
            if (inUseMode.execute(input)) {
                return true;
            } else {
                return false;
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Command not found, type 'help' for command syntax.");
            return false;
        }

    }


    /**
     * Exits from the FTP application.
     * Disconnects from the remote server, and terminates the program.
     * Syntax: exit
     * @param
     * @return void
     */
    public void exit() {

        guard = false;
        mode("remote");
        input[0] = "disconnect";
        inUseMode.execute(input);
        System.exit(0);
    }


    /**
     * Allows the user to switch the mode they are operating in, local or remote.
     * Takes a string choice, that contains the mode choice of the user.
     * Syntax: mode <remote|local>
     * @param choice
     * @return boolean
     */
    public boolean mode(String choice) {

        if (choice.equalsIgnoreCase("remote")) {
            currentMode = "Remote";
            inUseMode = ftp;

        }

        else if (choice.equalsIgnoreCase("local")) {
            currentMode = "Local";
            inUseMode = local;
        }

        else {
            System.out.println("Unrecognized mode, Try 'mode local' or 'mode remote'.");
            return false;
        }

        return true;

    }
}

