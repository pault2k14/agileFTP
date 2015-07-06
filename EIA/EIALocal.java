package com.EIA;

import java.io.File;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

public class EIALocal {

    private String []input = null;
    private File currentDirectory = FileUtils.getUserDirectory();
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();

    public String getPath() {
        return FileUtils.getUserDirectory().getAbsolutePath();
    }


    public boolean execute(String []userInput) {
        input = userInput;
        commands.get(input[0].toLowerCase()).run();
        return true;
    }

    public boolean init(HashMap main) {
        commands.putAll(main);
        commands.put("ls", () -> { ls(); } );

        return true;
    }

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
