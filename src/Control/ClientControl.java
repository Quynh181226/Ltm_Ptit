package control;

import java.io.*;
import java.net.Socket;

public class ClientControl {
    private Socket socket;
    private String host = "localhost";
    private int port = 8080;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public boolean openConnection() {
        try {
            socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
//            System.out.println("Server connect");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendRequest(String command, Object data) {
        try {
            oos.writeObject(command);
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //    public String receiveResponse() {
//        try {
//            Object resp = ois.readObject();
//            if (resp instanceof String) {
//                return (String) resp;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public Object receiveResponse() {
        try {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}