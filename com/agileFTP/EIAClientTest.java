package com.agileFTP;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.Assert.*;

public class EIAClientTest {

    private EIAClient ftp = new EIAClient();
    private String []userInput = new String[100];
    private boolean result;
    private String testHost = null;
    private String testDecorator = null;
    private HashMap<String, Runnable> testCommands = new HashMap<String, Runnable>();

    @Test
    public void testExecuteGood() throws Exception {
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        result = ftp.execute(userInput);
        assertTrue(result);

        userInput = "disconnect".split(" ");
        result = ftp.execute(userInput);
    }

    @Test
    public void testExecuteBad() throws Exception {
        ftp.init(testCommands);
        userInput = "notconnect speedtest.tele2.net 21 Anonymous".split(" ");
        result = ftp.execute(userInput);
        assertFalse(result);
    }

    @Test
    public void testGetHost() throws Exception {
        testHost = "speedtest.tele2.net";
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        result = ftp.execute(userInput);
        assertEquals(testHost, ftp.getHost());

        userInput = "disconnect".split(" ");
        result = ftp.execute(userInput);

    }

    @Test
    public void testGetDecorator() throws Exception {
        testDecorator = "speedtest.tele2.net";
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        result = ftp.execute(userInput);
        assertEquals(testDecorator, ftp.getDecorator());

        userInput = "disconnect".split(" ");
        result = ftp.execute(userInput);
    }

    @Test
    public void testInitGood() throws Exception {
        assertEquals(true, ftp.init(testCommands));
    }


    @Test
    public void testInitBad() throws Exception {
        assertEquals(false, ftp.init(null));
    }

    @Test
    public void testConnectWithNoPassword() throws Exception {
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        assertEquals(true, ftp.connect(userInput));

        userInput = "disconnect".split(" ");
        result = ftp.execute(userInput);

    }

    @Test
    public void testConnectWithPassword() throws Exception {
        userInput = "connect speedtest.tele2.net 21 Anonymous test".split(" ");
        assertEquals(true, ftp.connect(userInput));

        userInput = "disconnect".split(" ");
        result = ftp.execute(userInput);

    }

    @Test
    public void testConnectBadSyntax() throws Exception {
        userInput = "connect speedtest.tele2.net".split(" ");
        assertEquals(false, ftp.connect(userInput));

    }

    @Test
    public void testConnectToHostBadParams() throws Exception {

        userInput[0] = null;
        userInput[1] = "";
        userInput[2] = "";
        userInput[3] = "";
        result = ftp.connectToHost(userInput[0], userInput[1], userInput[2], userInput[3]);
        assertEquals(false, result);

        userInput[0] = "";
        userInput[1] = null;
        userInput[2] = "";
        userInput[3] = "";
        result = ftp.connectToHost(userInput[0], userInput[1], userInput[2], userInput[3]);
        assertEquals(false, result);

        userInput[0] = "";
        userInput[1] = "";
        userInput[2] = null;
        userInput[3] = "";
        result = ftp.connectToHost(userInput[0], userInput[1], userInput[2], userInput[3]);
        assertEquals(false, result);

        userInput[0] = "";
        userInput[1] = "";
        userInput[2] = "";
        userInput[3] = null;
        result = ftp.connectToHost(userInput[0], userInput[1], userInput[2], userInput[3]);
        assertEquals(false, result);

    }

    @Test
    public void testConnectToHostAlreadyConnectedBad() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(false, result);

        userInput = "disconnect".split(" ");
        result = ftp.execute(userInput);

    }

    @Test
    public void testConnectToHostBadHostname() throws Exception {

        userInput = "connect speedtest.tele2.ne 21 Anonymous".split(" ");
        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(false, result);

    }

    @Test
    public void testConnectToHostBadUsername() throws Exception {

        userInput = "connect speedtest.tele2.net 21 badtest".split(" ");
        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(false, result);

    }

    @Test
    public void testConnectToHostGood() throws Exception {

        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(true, result);

    }

    @Test
    public void testDisconnectNotConnectedBad() throws Exception {

        assertFalse(ftp.disconnect());
    }

    @Test
    public void testDisconnectGood() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        assertTrue(ftp.disconnect());
    }

    @Test
    public void testLsNotConnectedBad() throws Exception {

        assertFalse(ftp.ls());
    }

    @Test
    public void testLsGood() throws Exception {

        PrintStream stdout = System.out;
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        System.setOut(new PrintStream(outContent));

        assertTrue(ftp.ls());
        result = outContent.toString().contains("Remote listing:");

        assertTrue(result);

        System.setOut(stdout);

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }

    /*
    * speedtest.tele2.net immediately removes any created/deleted files.
    * Unable to test rmdir until we have a fully operational FTP server
    * (hopefully provided by professor?).  Meanwhile, I've tested it on a local ftp server
    * running on my macbook.
    */
    /*
    @Test
    public void testMkDirGood() throws Exception {
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "mkdir testDirectory".split(" ");
        assertTrue(ftp.mkdir(userInput));
    }

    @Test
    public void testMkDirBad() throws Exception {
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "mkdir upload".split(" ");
        assertFalse(ftp.mkdir(userInput));
    }

    @Test
    public void testRmDirGood() throws Exception {
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "rmdir testDirectory".split(" ");
        assertTrue(ftp.rmdir(userInput));
    }

    @Test
    public void testRmDirBad() throws Exception {
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "rmdir upload".split(" ");
        assertFalse(ftp.rmdir(userInput));
    }
    */

    @Test
    public void testCdGood() throws Exception {
        String currentDir;
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        currentDir = ftp.pwd();
        userInput = "cd upload".split(" ");
        assertTrue(ftp.cd(userInput));

        if (currentDir.contentEquals("/")) {
            assertEquals(ftp.pwd(), currentDir + "upload");
        }
        else {
            assertEquals(ftp.pwd(), currentDir + "/upload");
        }
    }


    @Test
    public void testCdDotDotGood() throws Exception {
        String currentDir;
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        // Record current working directory
        currentDir = ftp.pwd();

        // cd to upload directory
        userInput = "cd upload".split(" ");
        ftp.cd(userInput);

        // cd back to base directory
        ftp.cdParent();

        // confirm we're back to the original working directory
        assertEquals(currentDir, ftp.pwd());

    }

    @Test
    public void testCdDotDotBad() throws Exception {
        String currentDir;
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        currentDir = ftp.pwd();
        // cd back to base directory's parent
        // In this case, this server keeps us in the current working directory
        // because starting directory is / (root)
        assertTrue(ftp.cdParent());
        assertEquals(ftp.pwd(), currentDir);
    }

    /**
     * speedtest.tele2.net immediately removes any created/deleted files.
     * Unable to test rmdir until we have a fully operational FTP server
     * (hopefully provided by professor?).  Meanwhile, I've tested it on a local ftp server
     * running on my macbook.
     */
    /*
    @Test
    public void testRmDirGood() throws Exception {
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        assertTrue(ftp.mkdir("rmdir testDirectory".split(" ")));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }
    */
}