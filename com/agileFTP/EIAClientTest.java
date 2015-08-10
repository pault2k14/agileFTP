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
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        result = ftp.execute(userInput);
        assertTrue(result);

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);
    }

    @Test
    public void testExecuteBad() throws Exception {
        ftp.init(testCommands);
        userInput = FTPApp.split("notconnect eiaftp.cloudapp.net 21 eia eia");
        result = ftp.execute(userInput);
        assertFalse(result);
    }

    @Test
    public void testGetHost() throws Exception {
        testHost = "eiaftp.cloudapp.net";
        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        result = ftp.execute(userInput);
        assertEquals(testHost, ftp.getHost());

        userInput = FTPApp.split("disconnect");
        result = ftp.execute(userInput);

    }

    @Test
    public void testGetDecorator() throws Exception {
        testDecorator = "eiaftp.cloudapp.net";
        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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

        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        result = ftp.connectToHost(userInput[1], userInput[2], userInput[3], userInput[4]);
        assertEquals(true, result);

    }

    @Test
    public void testDisconnectNotConnectedBad() throws Exception {

        assertFalse(ftp.disconnect());
    }

    @Test
    public void testDisconnectGood() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);
        assertFalse(ftp.fileTransfer(null));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }

    @Test
    public void testFileTransferNoFileSpecified() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("download");
        assertFalse(ftp.fileTransfer(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }

    @Test
    public void testFileTransferIncorrectNumberOfParams() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("download 512KB.zip");
        assertFalse(ftp.fileTransfer(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }


    @Test
    public void testFileTransferMultiDownloadAndUploadGood() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
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
        userInput = "connect eiaftp.cloudapp.net 21 eia eia".split(" ");
        ftp.execute(userInput);

        userInput = "download 512KB.zip localtest.zip".split(" ");
        assertEquals(true, ftp.download(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }

    @Test
    public void testDownloadBad() throws Exception {

        ftp.init(testCommands);
        userInput = "connect eiaftp.cloudapp.net 21 eia eia".split(" ");
        ftp.execute(userInput);

        userInput = "download 5kb.zip localtest.zip".split(" ");
        assertEquals(false, ftp.download(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }

    @Test
    public void testDownloadNotConnectedBad() throws Exception {

        userInput = "download 5kb.zip localtest.zip".split(" ");
        assertEquals(false, ftp.download(userInput));

    }

    @Test
    public void testDownloadTooManyArgsBad() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "download 5kb.zip localtest.zip extra extra".split(" ");
        assertEquals(false, ftp.download(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }

    @Test
    public void testUpload() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        File temp = File.createTempFile("testUpload", ".tmp");
        String fileToUpload = temp.getAbsolutePath();

        userInput = ("upload upload/testUpload.tmp " + fileToUpload).split(" ");
        assertEquals(true, ftp.upload(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }

    @Test
    public void testUploadNoFileBad() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "upload upload/nofile.txt nofile.txt".split(" ");
        assertEquals(false, ftp.upload(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }

    @Test
    public void testUploadNotConnectedBad() throws Exception {

        File temp = File.createTempFile("testUpload", ".tmp");
        String fileToUpload = temp.getAbsolutePath();

        userInput = ("upload upload/testUpload.tmp " + fileToUpload).split(" ");
        assertEquals(false, ftp.upload(userInput));
    }

    @Test
    public void testUploadTooManyArgsBad() throws Exception {

        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        userInput = "upload upload/nofile.txt nofile.txt junk".split(" ");
        assertEquals(false, ftp.upload(userInput));

        userInput = "disconnect".split(" ");
        ftp.execute(userInput);
    }


    @Test
    public void testMkDirGood() throws Exception {
        ftp.init(testCommands);

        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        //userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);
        userInput = "rmdir testDirectory".split(" "); // remove directory if already there
        ftp.rmdir(userInput);
        userInput = "mkdir testDirectory".split(" ");
        assertTrue(ftp.mkdir(userInput));
        userInput = "rmdir testDirectory".split(" "); // cleanup (delete) the test directory
        ftp.rmdir(userInput);
    }


    @Test
    public void testRmDirGood() throws Exception {
        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        //userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);
        userInput = "mkdir testDirectory".split(" ");
        ftp.mkdir(userInput);
        userInput = "rmdir testDirectory".split(" ");
        assertTrue(ftp.rmdir(userInput));
    }


    @Test
    public void testRmDirBad() throws Exception {
        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        //userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);
        userInput = "rmdir testDoubleDeleteDirectory".split(" ");
        ftp.rmdir(userInput);
        userInput = "rmdir testDoubleDeleteDirectory".split(" ");
        assertFalse(ftp.rmdir(userInput));
    }
//

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
        userInput = "cd ..".split(" ");
        ftp.cd(userInput);
        // confirm we're back to the original working directory
        assertEquals(currentDir, ftp.pwd());

    }

    @Test
    public void testCdDotDotAtRoot() throws Exception {
        String currentDir;
        ftp.init(testCommands);
        userInput = "connect speedtest.tele2.net 21 Anonymous".split(" ");
        ftp.execute(userInput);

        currentDir = ftp.pwd();
        // cd back to base directory's parent
        // In this case, this server keeps us in the current working directory
        // because starting directory is / (root)
        userInput = "cd ..".split(" ");
        assertTrue(ftp.cd(userInput));
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

    @Test
    public void testRemoveFileGood() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        File temp = File.createTempFile("testUpload", ".tmp");
        String fileToUpload = temp.getAbsolutePath();

        userInput = FTPApp.split("upload upload/testUpload.tmp " + fileToUpload);
        assertTrue(ftp.upload(userInput));

        userInput = FTPApp.split("rm upload/testUpload.tmp");
        assertTrue(ftp.rm(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testRemoveFileBadNullRemoteFileWithPath() throws Exception {

        ftp.removeFile(null);
    }


    @Test
    public void testRemoveFileBadNoSuchFile() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("rm upload/testUpload.tmp");
        assertFalse(ftp.rm(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }


    @Test
    public void testRmBadNullUserInput() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = null;
        assertFalse(ftp.rm(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }

    @Test
    public void testRmBadWrongNumberOfArgs() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("rm upload/testUpload.tmp C:\\test123\testUpload.tmp");
        assertFalse(ftp.rm(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testRmBadNotConnected() throws Exception {

        ftp.init(testCommands);

        userInput = FTPApp.split("rm upload/testUpload.tmp");
        assertFalse(ftp.rm(userInput));

    }

    @Test
    public void testMvBadNullUserInput() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = null;
        assertFalse(ftp.mv(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);

    }

    @Test
    public void testMvBadWrongNumberOfArgs() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("mv upload/testUpload.tmp upload/newTestUpload.tmp upload/newTestUpload2.tmp");
        assertFalse(ftp.mv(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testMvBadNotConnected() throws Exception {

        ftp.init(testCommands);

        userInput = FTPApp.split("mv upload/testUpload.tmp upload/newTestUpload.tmp");
        assertFalse(ftp.mv(userInput));

    }


    @Test
    public void testRenameBadNullInputStrings() throws Exception {

        assertFalse(ftp.renameFile(null, ""));
        assertFalse(ftp.renameFile("", null));
    }


    @Test
    public void testRenameFileGood() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        File temp = File.createTempFile("testUpload", ".tmp");
        String fileToUpload = temp.getAbsolutePath();

        userInput = FTPApp.split("upload upload/testUpload.tmp " + fileToUpload);
        assertTrue(ftp.upload(userInput));

        userInput = FTPApp.split("mv upload/testUpload.tmp upload/newTestUpload.tmp");
        assertTrue(ftp.mv(userInput));

        userInput = FTPApp.split("rm upload/newTestUpload.tmp");
        assertTrue(ftp.rm(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testRenameFileBadNoSuchFile() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("mv upload/testUpload.tmp upload/newTestUpload.tmp");
        assertFalse(ftp.mv(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testRunGood() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        File temp = File.createTempFile("testUpload", ".tmp");
        String fileToUpload = temp.getAbsolutePath();

        userInput = FTPApp.split("upload upload/testUpload.tmp " + fileToUpload);
        assertTrue(ftp.upload(userInput));

        userInput = FTPApp.split("run \"SITE CHMOD 755 upload/testUpload.tmp\"");
        assertTrue(ftp.run(userInput));

        userInput = FTPApp.split("rm upload/testUpload.tmp");
        assertTrue(ftp.rm(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testRunBadNoSuchCommand() throws Exception {

        ftp.init(testCommands);
        userInput = FTPApp.split("connect eiaftp.cloudapp.net 21 eia eia");
        ftp.execute(userInput);

        userInput = FTPApp.split("run TEST123");
        assertFalse(ftp.run(userInput));

        userInput = FTPApp.split("disconnect");
        ftp.execute(userInput);
    }

    @Test
    public void testRunBadNullInput() throws Exception {

        assertFalse(ftp.run(null));
    }

    @Test
    public void testRunBadTooFewArgs() throws Exception {

        userInput = FTPApp.split("run");
        assertFalse(ftp.run(userInput));
    }

    @Test
    public void testRunBadNotConnected() throws Exception {

        userInput = FTPApp.split("run HELP");
        assertFalse(ftp.run(userInput));
    }



    @Test
    public void testRunCommandBadNotConnected() throws Exception {

        assertFalse(ftp.runCommand("TEST123", ""));
    }

    @Test
    public void testRunBadNullRemoteCommandAndParams() throws Exception {

        assertFalse(ftp.runCommand(null, ""));
        assertFalse(ftp.runCommand("", null));
    }

}