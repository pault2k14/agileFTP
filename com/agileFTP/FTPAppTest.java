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
        userInput = "help".split(" ");
        assertTrue(App.execute(userInput));
    }

    @Test
    public void testExecuteBad() throws Exception {

        App.init();
        userInput = "badtest".split(" ");
        assertFalse(App.execute(userInput));
    }

    @Test
    public void testModeGood() throws Exception {

        App.init();

        userInput = "mode local".split(" ");
        assertTrue(App.mode(userInput[1]));

        userInput = "mode remote".split(" ");
        assertTrue(App.mode(userInput[1]));

    }

    @Test
    public void testModeBad() throws Exception {

        App.init();

        userInput = "mode badTest".split(" ");
        assertFalse(App.mode(userInput[1]));

    }


}