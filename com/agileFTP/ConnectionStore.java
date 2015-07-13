package com.agileFTP;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by johnschroeder on 7/8/15.
 */
public class ConnectionStore {
    private ConnectionData connectionData;

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
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
        connectionData.save(connection, host, port, name);
        storeConnections();
        connectionData = null;
        System.out.println("Saved connection \'" + connection + "\'.");
    }

    public void saveConnection(String connection, String host, String port, String name, String password) {
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
        connectionData.save(connection, host, port, name, password);
        storeConnections();
        connectionData = null;
        System.out.println("Saved connection \'" + connection + "\'.");
    }

    public String[] retrieveConnection(String connection) {
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
        String [] array = connectionData.retrieve(connection);
        storeConnections();
        connectionData = null;
        return array;
    }

    private ConnectionData loadConnections() {
        try
        {
            char dirDivision = '/';
            if(System.getProperty("os.name").toLowerCase().contains("windows")){
                dirDivision = '\\';
            }

            FileInputStream fileIn = new FileInputStream(System.getProperty("user.home") + dirDivision + "connections.ser");
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
          char dirDivision = '/';
          if(System.getProperty("os.name").toLowerCase().contains("windows")){
              dirDivision = '\\';
          }
          File f = new File(System.getProperty("user.home") + dirDivision + "connections.ser");
          if(!f.exists())
              f.createNewFile();
          FileOutputStream fileOut = new FileOutputStream(f);
          ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
          objectOut.writeObject(this.connectionData);
          objectOut.close();
          fileOut.close();
      }catch(IOException i)
      {
          //i.printStackTrace();
      }
    }

    public void listConnections() {
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
        connectionData.listConnections();
        storeConnections();
        connectionData = null;
    }

    public void deleteConnection(String [] connection) {

    }

    private static class ConnectionData implements Serializable {
        private HashMap<String, String[]> connections = new HashMap<>();

        public void save(String connection, String host, String port, String name) {
            String [] array = {host, port, name};
            connections.put(connection, array);
        }

        public void save(String connection, String host, String port, String name, String password) {
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
            return toReturn;
        }

        public void listConnections() {
            Object [] temp = connections.keySet().toArray();
            String[] connectionList = Arrays.copyOf(temp, temp.length, String[].class);
            System.out.println("Available saved connections:");
            for(String connection : connectionList) {
                System.out.println("    " + connection);
            }
        }
    }
}
