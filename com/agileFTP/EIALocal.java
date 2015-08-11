package com.agileFTP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

// Class for local machine based tasks.
// Must be initialized before use.
public class EIALocal implements EIA {

    private String []input = null;
    private File currentDirectory = FileUtils.getUserDirectory();
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();


    /**
     * Return the current local path variable.
     * @param
     * @return String
     */
    public String getPath() {

        return currentDirectory.getAbsolutePath();
    }


    /**
     * Returns the decorator for this class.
     * @param
     * @return String
     */
    public String getDecorator() {

        return currentDirectory.getAbsolutePath();
    }


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
     * Initializes the EIALocal class variables.
     * Creates a hashmap of commands that can be called.
     * Takes a hashmap from the main application, that adds all of the main application commands
     * to this hashmap.
     * @param main
     * @return boolean
     */
    public boolean init(HashMap main) {
        try {
            commands.putAll(main);
            commands.put("ls", () -> { ls(); } );
        }
        catch (NullPointerException e) {
            return false;
        }

        return true;
    }


    /**
     * Display the contents of the currentDirectory.
     * Syntax: ls
     * @param
     * @return boolean
     */
    protected boolean ls() {

        System.out.println("NAME  SIZE  LAST MODIFIED");
        System.out.println("-------------------------");

        try {
            for(File f: FileUtils.getFile(currentDirectory.getAbsolutePath()).listFiles()) {
                System.out.println(f.getName() + "  " + f.length() + " bytes" + "  " + sdf.format(f.lastModified()));

            }
            System.out.println("");
            System.out.println("Space available " + currentDirectory.getFreeSpace() + " bytes.");
            System.out.println("");

        } catch (NullPointerException e) {
            System.out.println("Unable to display current directory.");
            return false;
        }

        return true;
    }

}
