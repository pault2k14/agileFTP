package com.agileFTP;

import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Scanner;

import static org.junit.Assert.*;

public class FTPAppTest {

    private EIAClient ftp = new EIAClient();
    private EIALocal local = new EIALocal();
    private EIA inUseMode = null;
    private Scanner scan = new Scanner(System.in);
    private String []userInput = new String[100];
    private boolean guard = true;
    private boolean result = true;
    private String currentMode = null;
    private HashMap<String, Runnable> commands = new HashMap<String, Runnable>();
    private FTPApp App = new FTPApp();

    @Test
    public void testInit() throws Exception {

        assertTrue(App.init());
    }

    @Test
    public void testHelp() throws Exception {

        App.init();
        assertTrue(App.help());
    }

    @Test
    public void testExecuteGood() throws Exception {

        App.init();
        userInput = FTPApp.split("help");
        assertTrue(App.execute(userInput));
    }

    @Test
    public void testExecuteBad() throws Exception {

        App.init();
        userInput = FTPApp.split("badtest");
        assertFalse(App.execute(userInput));
    }

    @Test
    public void testModeGood() throws Exception {

        App.init();

        userInput = FTPApp.split("mode local");
        assertTrue(App.mode(userInput[1]));

        userInput = FTPApp.split("mode remote");
        assertTrue(App.mode(userInput[1]));

    }

    @Test
    public void testModeBad() throws Exception {

        App.init();

        userInput = FTPApp.split("mode badTest");
        assertFalse(App.mode(userInput[1]));

    }


    @Test
    public void testSplit() throws Exception {

        String input = "  get multiple    \"/opt/nas/black hat/the blackhat\" \"/opt/nas/whitehat\"  ";
        userInput = FTPApp.split(input);
        assertEquals(4, userInput.length);

        for(int i = 0; i < userInput.length; ++i) {
            System.out.println(userInput[i]);
        }

        input = "";
        userInput = FTPApp.split(input);
        assertEquals(1, userInput.length);

        input = null;
        userInput = FTPApp.split(input);
        assertEquals(1, userInput.length);

    }


}