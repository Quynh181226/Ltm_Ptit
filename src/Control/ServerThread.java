package control;

import model.*;
import dao.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread {
    private final Socket client;

    public ServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(client.getInputStream())) {
            oos.flush();
            String command = (String) ois.readObject();
            Object data = ois.readObject();
            Object response;

            try {
                switch (command) {
                    case "Login": {
                        User u = (User) data;
                        UserDAO userDAO = new UserDAO();
                        User auth = userDAO.authenticate(u);
                        response = (auth != null) ? auth : "InvalidCredentials";
                        break;
                    }

                    case "AddAsset": {
                        Asset a = (Asset) data;
                        boolean ok = new AssetDAO().addAsset(a);
                        response = ok ? "Success" : "Fail";
                        break;
                    }

                    case "UpdateAsset": {
                        Asset a = (Asset) data;
                        boolean ok = new AssetDAO().updateAsset(a);
                        response = ok ? "Success" : "Fail";
                        break;
                    }

                    case "DeleteAsset": {
                        String assetId = (String) data;
                        boolean ok = new AssetDAO().deleteAsset(assetId);
                        response = ok ? "Success" : "Fail";
                        break;
                    }

                    case "GetAllAssets": {
                        List<Asset> list = new AssetDAO().getAllAssets();
                        response = list;
                        break;
                    }

                    case "SearchAsset": {
                        String keyword = (String) data;
                        List<Asset> list = new AssetDAO().searchAsset(keyword);
                        response = list;
                        break;
                    }

                    case "SearchAssetByRoom": {
                        String roomId = (String) data;
                        List<Asset> list = new AssetDAO().searchByRoom(roomId);
                        response = list;
                        break;
                    }

                    case "SearchAssetByValue": {
                        double[] range = (double[]) data;
                        List<Asset> list = new AssetDAO().searchByValueRange(range[0], range[1]);
                        response = list;
                        break;
                    }

                    case "GetAssetsByRoom": {
                        String roomId = (String) data;
                        List<Asset> list = new AssetDAO().getAssetsByRoom(roomId);
                        response = list;
                        break;
                    }

                    case "AddRoom": {
                        Room r = (Room) data;
                        boolean ok = new RoomDAO().addRoom(r);
                        response = ok ? "Success" : "Fail";
                        break;
                    }

                    case "GetAllRooms": {
                        List<Room> list = new RoomDAO().getAllRooms();
                        response = list;
                        break;
                    }

                    case "DeleteRoom": {
                        String roomId = (String) data;
                        boolean ok = new RoomDAO().deleteRoom(roomId);
                        response = ok ? "Success" : "CannotDelete";
                        break;
                    }

                    case "UpdateRoom": {
                        Room room = (Room) data;
                        RoomDAO roomDAO = new RoomDAO();
                        response = roomDAO.updateRoom(room) ? "Success" : "FailedToUpdateRoom";
                        break;
                    }

                    case "RoomAssetCount": {
                        List<String[]> stats = new RoomDAO().getRoomAssetCount();
                        response = stats;
                        break;
                    }

                    default:
                        response = "UnknownCommand: " + command;
                        break;
                }

            } catch (Exception daoEx) {
                daoEx.printStackTrace();
                response = "ServerProcessingError: " + daoEx.getMessage();
            }

            oos.writeObject(response);
            oos.flush();

        } catch (Exception e) {
            System.err.println("ServerThread error for client: " + client.getRemoteSocketAddress());
            e.printStackTrace();
        } finally {
            try {
                if (client != null && !client.isClosed()) client.close();
            } catch (IOException ignored) {
            }
        }
    }
}