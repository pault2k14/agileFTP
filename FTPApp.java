package com.agileFTP;

import java.util.*;


// FTP for testing connect, login, ls, get (Not much else can be done on this server.
//                                          Setup a local ftp for more functionality)
// host speedtest.tele2.net
// port 21
// username Anonymous
// password <none>


// Class for the user interface.
public class FTPApp {

    private EIAClient ftp = new EIAClient();
    private EIALocal local = new EIALocal();
    private EIA inUseMode = null;
    private Scanner scan = new Scanner(System.in);
    private String []input = new String[100];
    private boolean guard = true;
    private String currentMode = null;
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();


    // Setup and initialize the user interface.
    public static void main(String[] args) {

        FTPApp App = new FTPApp();
        App.init();
        App.start();

    }

    // Holds the user input loop.
    public boolean start() {

        while(guard) {
            System.out.print(currentMode + ": " + inUseMode.getDecorator() + " > ");
            input = scan.nextLine().split(" ");
            execute(input);
        }

        return true;

    }

    // Setup hashmap, initialize ftp and local objects.
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

        return true;
    }

    // Displays command syntax help.
    public boolean help() {

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
        System.out.println("connect - Connect to a remote FTP host.");
        System.out.println("          Usage: connect <hostname> <port> <username> [password]");
        System.out.println("          Note: The password field is optional.");
        System.out.println("disconnect - Disconnect from the remote FTP host.");
        System.out.println("");
        System.out.println("Local");
        System.out.println("ls - Displays the contents of the current local directory.");
        System.out.println("");

        return true;
    }

    // Passes user commands from the FTPApp to the class currently in use.
    public boolean execute(String []input) {

        try {
            if(inUseMode.execute(input)) {
                return true;
            }

            else {
                return false;
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Command not found, type 'help' for command syntax.");
            return false;
        }

    }

    // Exit Awesome FTP.
    public void exit() {

        guard = false;
        mode("remote");
        input[0] = "disconnect";
        inUseMode.execute(input);
        System.exit(0);
    }


    // Allows the user to switch the mode they are operating in, local or remote.
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

