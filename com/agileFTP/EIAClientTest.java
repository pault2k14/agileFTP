package com.agileFTP;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        result = ftp.execute(userInput);
        assertTrue(result);

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);
    }

    @Test
    public void testExecuteBad() throws Exception {
        ftp.init(testCommands);
        userInput = FTPApp.split("notconnect speedtest.tele2.net 21 Anonymous");
        result = ftp.execute(userInput);
        assertFalse(result);
    }

    @Test
    public void testGetHost() throws Exception {
        testHost = "speedtest.tele2.net";
        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        result = ftp.execute(userInput);
        assertEquals(testHost, ftp.getHost());

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);

    }

    @Test
    public void testGetDecorator() throws Exception {
        testDecorator = "speedtest.tele2.net";
        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        result = ftp.execute(userInput);
        assertEquals(testDecorator, ftp.getDecorator());

        userInput = FTPApp.split("disconnect");
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
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        assertEquals(true, ftp.connect(userInput));

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);

    }

    @Test
    public void testConnectWithPassword() throws Exception {
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous test");
        assertEquals(true, ftp.connect(userInput));

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);

    }

    @Test
    public void testConnectBadSyntax() throws Exception {
        userInput = FTPApp.split("connect speedtest.tele2.net");
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
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);

        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(false, result);

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);

    }

    @Test
    public void testConnectToHostBadHostname() throws Exception {

        userInput = FTPApp.split("connect speedtest.tele2.ne 21 Anonymous");
        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(false, result);

    }

    @Test
    public void testConnectToHostBadUsername() throws Exception {

        userInput = FTPApp.split("connect speedtest.tele2.net 21 badtest");
        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], "");
        assertEquals(false, result);

    }

    @Test
    public void testConnectToHostGood() throws Exception {

        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
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
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
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
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);

        System.setOut(new PrintStream(outContent));

        assertTrue(ftp.ls());
        result = outContent.toString().contains("Remote listing:");

        assertTrue(result);

        System.setOut(stdout);

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testFileTransferNotConnected() throws Exception {

        userInput = FTPApp.split("download 512KB.zip local512KB.zip");
        assertFalse(ftp.fileTransfer(userInput));

    }

    @Test
    public void testFileTransferUserInputNull() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);
        assertFalse(ftp.fileTransfer(null));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }

    @Test
    public void testFileTransferNoFileSpecified() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);

        userInput = FTPApp.split("download");
        assertFalse(ftp.fileTransfer(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }

    @Test
    public void testFileTransferIncorrectNumberOfParams() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);

        userInput = FTPApp.split("download 512KB.zip");
        assertFalse(ftp.fileTransfer(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }


    @Test
    public void testFileTransferMultiDownloadAndUploadGood() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);

        userInput = FTPApp.split("download 512KB.zip local512KB.zip 1MB.zip local1MB.zip");
        assertTrue(ftp.fileTransfer(userInput));

        File upload1 = FileUtils.getFile(PathHelper.getDownloadsPath() + "local512KB.zip");
        File upload2 = FileUtils.getFile(PathHelper.getDownloadsPath() + "local1MB.zip");

        userInput = new String[5];

        userInput[0] = "upload";
        userInput[1] = "upload/new512KB.zip";
        userInput[2] = upload1.getAbsolutePath();
        userInput[3] = "upload/new1MB.zip";
        userInput[4] = upload2.getAbsolutePath();

        assertTrue(ftp.fileTransfer(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

        if(upload1.exists()) {
            upload1.delete();
        }

        if(upload2.exists()) {
            upload2.delete();
        }

    }


    // This test won't pass until the underlying problems with specifying a non existent filename in
    // the upload and download functions are fixed.
    // Please enable when those are fixed.
    //@Test
    public void testFileTransferMultiDownloadAndUploadBad() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect speedtest.tele2.net 21 Anonymous");
        ftp.execute(userInput);

        userInput = FTPApp.split("download test12345.zip localtest12345.zip testabcde.zip localtestabcde.zip");
        assertFalse(ftp.fileTransfer(userInput));


        userInput = new String[5];

        userInput[0] = "upload";
        userInput[1] = "upload/new512KB.zip";
        userInput[2] = PathHelper.getDownloadsPath() + "file_that_does_not_exist_1.zip";
        userInput[3] = "upload/new1MB.zip";
        userInput[4] = PathHelper.getDownloadsPath() + "file_that_does_not_exist_2.zip";

        assertFalse(ftp.fileTransfer(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }


    @Test
    public void testDownload() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "download 512KB.zip localtest.zip".split(" ");
        assertEquals(true, ftp.download(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }


}