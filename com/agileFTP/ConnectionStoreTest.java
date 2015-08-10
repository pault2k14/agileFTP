package com.agileFTP;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
/**
 * Created by Thomas Nelson on 7/23/2015.
 */
public class ConnectionStoreTest {
    private ConnectionStore store = new ConnectionStore();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final String connectionName = "test";
    private final String connectionNamePass = "testPass";
    private final String[] connection = {"127.0.0.1", "21", "testUser"};
    private final String[] connectionPass = { "127.0.0.1", "21", "testUser", "password"};

    @Before
    public void before() {
        addStream();
        addEntries();
    }
    @After
    public void after() {
        deleteEntries();
        deleteStream();
    }
    public void addStream() {
        System.setOut(new PrintStream(outContent));
    }

    public void addEntries() {
        outContent.reset();

        String fullSave[] = new String[5];
        String fullSavePass[] = new String[6];
        fullSavePass[0] = fullSave[0] = "save";
        fullSave[1] = connectionName;
        fullSavePass[1] = connectionNamePass;
        for(int i = 2; i < 5; ++i) {
            fullSave[i] = connection[i - 2];
            fullSavePass[i] = connectionPass[i - 2];
        }
        fullSavePass[5] = connectionPass[3];
        store.saveConnection(fullSave);
        store.saveConnection(fullSavePass);

        assertEquals("Saved connection \'" + connectionName    + "\'.\r\n" +
                     "Saved connection \'" + connectionNamePass + "\'.\r\n", outContent.toString());
        outContent.reset();
    }

    public void deleteEntries() {
        outContent.reset();
        String fullDelete[] = {"delete", connectionName};
        String fullDeletePass[] = {"delete", connectionNamePass};
        store.deleteConnection(fullDelete);
        store.deleteConnection(fullDeletePass);

        assertEquals("Deleted connection \'" + connectionName + "\'.\r\n" +
                     "Deleted connection \'" + connectionNamePass + "\'.\r\n", outContent.toString());
        outContent.reset();
    }

    public void deleteStream() {
        System.setOut(null);
    }

    @Test
    public void testSaveConnectionBad()  throws Exception {
        String[] save = {"Bad Number"};
        store.saveConnection(save);
        assertEquals("Incorrect number of parameters for save. Type 'help' for command syntax.\r\n", outContent.toString());
    }
    @Test
    public void testRetrieveConnectionGood()  throws Exception {
        String[] retrieve = store.retrieveConnection(connectionName);
        String[] retrievePass = store.retrieveConnection(connectionNamePass);

        String fullConnect[] = new String[4];
        String fullConnectPass[] = new String[5];
        fullConnect[0] = fullConnectPass[0] = "connect";
        for(int i = 1; i < 4; ++i) {
            fullConnect[i] = connection[i - 1];
            fullConnectPass[i] = connectionPass[i - 1];
        }
        fullConnectPass[4] = connectionPass[3];

        assertArrayEquals(fullConnect, retrieve);
        assertArrayEquals(fullConnectPass, retrievePass);
    }
    @Test
    public void testListConnections() throws Exception {
        store.listConnections();
        assertTrue(outContent.toString().contains("Available saved connections:"));
        assertTrue(outContent.toString().contains(connectionName));
        assertTrue(outContent.toString().contains(connectionNamePass));
    }
    @Test
    public void testListWithoutConnections() throws Exception {
        deleteEntries();

        store.listConnections();
        assertEquals("There are no saved connections.\r\n", outContent.toString());

        addEntries();
    }
    @Test
    public void testDeleteConnectionBad()  throws Exception {
        String[] save = {"Bad Number"};
        store.deleteConnection(save);
        assertEquals("Incorrect number of parameters for delete. Type 'help' for command syntax.\r\n", outContent.toString());
    }
}
