package com.agileFTP;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import org.jasypt.util.text.BasicTextEncryptor;


/**
 * Created by johnschroeder on 7/8/15.
 */
public class ConnectionStore {
    private ConnectionData connectionData;

    private void start() {
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
    }

    private void end() {
        storeConnections();
        connectionData = null;
    }

    public void saveConnection(String [] input) {
        if(input.length == 5) {
            saveConnection(input[1], input[2], input[3], input[4]);
            return;
        }
        if(input.length == 6) {
            saveConnection(input[1], input[2], input[3], input[4], input[5]);
            return;
        }
        System.out.println("Incorrect number of parameters for save. Type 'help' for command syntax.");
    }

    public void saveConnection(String connection, String host, String port, String name) {
        start();
        connectionData.save(connection, host, port, name);
        end();
        System.out.println("Saved connection \'" + connection + "\'.");
    }

    public void saveConnection(String connection, String host, String port, String name, String password) {
        start();
        connectionData.save(connection, host, port, name, password);
        end();
        System.out.println("Saved connection \'" + connection + "\'.");
    }

    public String[] retrieveConnection(String connection) {
        start();
        String [] array = connectionData.retrieve(connection);
        end();
        return array;
    }

    public void listConnections() {
        start();
        connectionData.listConnections();
        end();
    }

    public void deleteConnection(String [] connection) {
        if(connection.length != 2) {
            System.out.println("Incorrect number of parameters for delete. Type 'help' for command syntax.");
            return;
        }
        start();
        connectionData.delete(connection[1]);
        end();
        System.out.println("Deleted connection \'" + connection[1] + "\'.");
    }

    private ConnectionData loadConnections() {
        try
        {
            FileInputStream fileIn = new FileInputStream(PathHelper.getPathFromUserHome("connections.ser"));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            ConnectionData connectionData = (ConnectionData) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            return connectionData;
        }catch(IOException e)
        {
            return new ConnectionData();
        }catch(ClassNotFoundException e)
        {
            System.out.println("ConnectionData class not found");
            e.printStackTrace();
            return new ConnectionData();
        }
    }

    private void storeConnections() {
        try
        {
            File f = new File(PathHelper.getPathFromUserHome("connections.ser"));
            if(!f.exists())
                f.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(f);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.connectionData);
            objectOut.close();
            fileOut.close();
        }catch(IOException i){}
    }

    private static class ConnectionData implements Serializable {
        private HashMap<String, String[]> connections = new HashMap<>();

        public void save(String connection, String host, String port, String name) {
            String [] array = {host, port, name};
            connections.put(connection, array);
        }

        public void save(String connection, String host, String port, String name, String password) {
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword("12345ARandomStringWithNumbers54321");
            password = textEncryptor.encrypt(password);

            String [] array = {host, port, name, password};
            connections.put(connection, array);
        }

        public String [] retrieve(String connection) {
            String [] temp = connections.get(connection);
            if(temp == null) {
                System.out.println("No connection with the name \'" + connection + "\' exists.");
                return null;
            }
            String [] toReturn = new String[temp.length + 1];
            toReturn[0] = "connect";
            for(int i = 1; i < toReturn.length; ++i) {
                toReturn[i] = temp[i - 1];
            }
            if(toReturn.length == 5) {
                BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword("12345ARandomStringWithNumbers54321");
                toReturn[4] = textEncryptor.decrypt(toReturn[4]);
            }
            return toReturn;
        }

        public void delete(String connection) {
            connections.remove(connection);
        }

        public void listConnections() {
            Object [] temp = connections.keySet().toArray();
            String[] connectionList = Arrays.copyOf(temp, temp.length, String[].class);
            if(connectionList.length == 0) {
                System.out.println("There are no saved connections.");
                return;
            }
            System.out.println("Available saved connections:");
            for(String connection : connectionList) {
                System.out.println("    " + connection);
            }
        }
    }
}