package src;

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
        }
        if(input.length == 6) {
            saveConnection(input[1], input[2], input[3], input[4], input[5]);
        }
    }

    public void saveConnection(String connection, String host, String port, String name) {
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
        connectionData.save(connection, host, port, name);
        storeConnections();
        connectionData = null;
    }

    public void saveConnection(String connection, String host, String port, String name, String password) {
        if(connectionData != null) {throw new IllegalStateException("Connection was not deserialized.");}
        connectionData = loadConnections();
        connectionData.save(connection, host, port, name, password);
        storeConnections();
        connectionData = null;
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
            FileInputStream fileIn = new FileInputStream("/tmp/connections.ser");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            ConnectionData connectionData = (ConnectionData) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            return connectionData;
        }catch(IOException e)
        {
            e.printStackTrace();
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
         FileOutputStream fileOut = new FileOutputStream("/tmp/connections.ser", false);
         ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
         objectOut.writeObject(this.connectionData);
         objectOut.close();
         fileOut.close();
      }catch(IOException i)
      {
          i.printStackTrace();
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
                System.out.println("No connection with the name " + connection + " exists.");
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
