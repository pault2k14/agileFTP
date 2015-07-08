package agileFTP;

import java.io.File;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

// Class for local machine based tasks.
public class EIALocal {

    private String []input = null;
    private File currentDirectory = FileUtils.getUserDirectory();
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();

    public String getPath() {
        return FileUtils.getUserDirectory().getAbsolutePath();
    }


    // Lookup the method in the hashmap and then execute the
    // corresponding method.
    public boolean execute(String []userInput) {
        input = userInput;
        commands.get(input[0].toLowerCase()).run();
        return true;
    }

    // Add all of the FTPApp's local haspmap into our hashmap.
    // Then load our local hashmap key's and lambda functions.
    public boolean init(HashMap main) {
        commands.putAll(main);
        commands.put("ls", () -> { ls(); } );

        return true;
    }

    // Display the contents of the currentDirectory.
    public boolean ls() {

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
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
