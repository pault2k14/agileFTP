package com.EIA;

import java.util.*;


// http://www.dreamincode.net/forums/topic/255103-how-to-create-console-application-with-different-commands/
// http://www.journaldev.com/1624/command-design-pattern-in-java-example-tutorial

// FTP for testing connect, login, ls, get (Not much else can be done on this server.
//                                          Setup a local ftp for more functionality)
// host speedtest.tele2.net
// port 21
// username Anonymous
// password <none>, can use anything really, in this basic implementation you must provide a password of some kind.

// Add a command to toggle local and remote mode.
// Like
// mode local
// mode remote
// Will switch between two sets of hashmaps.
//
// Setup the Status display Remote: Not connected>
//                          Remote: Host: 192.168.1.1>
//
//                          Local: C:\Users\Paul>
//

public class FTPApp {

    private EIAClient ftp = new EIAClient();
    private EIALocal local = new EIALocal();
    private Scanner scan = new Scanner(System.in);
    private String []input = null;
    private boolean guard = true;
    private String currentMode = "remote";
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();


    public static void main(String[] args) {

        FTPApp App = new FTPApp();
        App.init();
        App.scanInput();

    }

    public boolean init() {

        commands.put("help", () -> { help();     });
        commands.put("exit", () -> { guard = false;
            System.exit(0);});
        commands.put("mode", () -> { mode(input[1]); });

        ftp.init(commands);
        local.init(commands);

        System.out.println("Team Everything is Awesome presents: Awesome FTP!");
        return true;
    }

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

    public boolean scanInput() {

        while(guard) {

            try {
                if (currentMode.equalsIgnoreCase("local")) {
                    System.out.print("Local: " + local.getPath() + " > ");
                    input = scan.nextLine().split(" ");
                    local.execute(input);
                }

                if (currentMode.equalsIgnoreCase("remote")) {
                    System.out.print("Remote: " + ftp.getHost() + " > ");
                    input = scan.nextLine().split(" ");
                    ftp.execute(input);
                }
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Command not found, type 'help' for command syntax.");
            }


        }

        return true;

    }

    public boolean mode(String choice) {

        if (choice.equalsIgnoreCase("remote")) {
            currentMode = "remote";

        }

        else if (choice.equalsIgnoreCase("local")) {
            currentMode = "local";
        }

        return true;

    }
}

