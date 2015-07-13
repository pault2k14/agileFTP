package com.agileFTP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

// Class for local machine based tasks.
public class EIALocal implements EIA {

    private String []input = null;
    private File currentDirectory = FileUtils.getUserDirectory();
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();

    // Return the current local path variable.
    public String getPath() {

        return currentDirectory.getAbsolutePath();
    }

   // Return the current local decorator variable.
    public String getDecorator() {

        return currentDirectory.getAbsolutePath();
    }

    // Lookup the method in the hashmap and then execute the
    // corresponding method.
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

    // Add all of the FTPApp's local haspmap into our hashmap.
    // Then load our local hashmap key's and lambda functions.
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

    // Display the contents of the currentDirectory.
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
