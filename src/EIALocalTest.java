package com.agileFTP;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.Assert.*;

public class EIALocalTest {

    private EIALocal local = new EIALocal();
    private String []userInput = new String[100];
    private boolean result;
    private String testPath = null;
    private String testDecorator = null;
    private HashMap<String, Runnable> testCommands = new HashMap<String, Runnable>();

    @Test
    public void testGetPath() throws Exception {

        String resultPath = null;
        testPath = FileUtils.getUserDirectory().getAbsolutePath();
        local.init(testCommands);
        resultPath = local.getPath();

        assertEquals(resultPath, testPath);
    }

    @Test
    public void testGetDecorator() throws Exception {

        String resultDecorator = null;
        testDecorator = FileUtils.getUserDirectory().getAbsolutePath();
        local.init(testCommands);
        resultDecorator = local.getDecorator();

        assertEquals(resultDecorator, testDecorator);
    }

    @Test
    public void testExecuteGood() throws Exception {
        local.init(testCommands);
        userInput = "ls".split(" ");
        result = local.execute(userInput);
        assertTrue(result);
    }

    @Test
    public void testExecuteBad() throws Exception {
        local.init(testCommands);
        userInput = "notls".split(" ");
        result = local.execute(userInput);
        assertFalse(result);
    }

    @Test
    public void testInitGood() throws Exception {
        assertEquals(true, local.init(testCommands));
    }


    @Test
    public void testInitBad() throws Exception {
        assertEquals(false, local.init(null));
    }

    @Test
    public void testLs() throws Exception {

        PrintStream stdout = System.out;
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        local.init(testCommands);
        userInput = "ls".split(" ");
        local.execute(userInput);

        System.setOut(new PrintStream(outContent));

        assertTrue(local.ls());
        result = outContent.toString().contains("Space available");

        assertTrue(result);

        System.setOut(stdout);

    }
}