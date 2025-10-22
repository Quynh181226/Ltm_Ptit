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
                        response = (auth != null) ? auth : "Invalid credentials";
                        break;
                    }

                    case "AddAsset": {
                        Asset a = (Asset) data;
                        boolean ok = new AssetDAO().addAsset(a);
                        response = ok ? "SUCCESS" : "FAIL";
                        break;
                    }

                    case "UPDATE_ASSET": {
                        Asset a = (Asset) data;
                        boolean ok = new AssetDAO().updateAsset(a);
                        response = ok ? "SUCCESS" : "FAIL";
                        break;
                    }

                    case "DEL_ASSET": {
                        String assetId = (String) data;
                        boolean ok = new AssetDAO().deleteAsset(assetId);
                        response = ok ? "SUCCESS" : "FAIL";
                        break;
                    }

                    case "GET_ALL_ASSETS": {
                        List<Asset> list = new AssetDAO().getAllAssets();
                        response = list;
                        break;
                    }

                    case "SEARCH_ASSET": {
                        String keyword = (String) data;
                        List<Asset> list = new AssetDAO().searchAsset(keyword);
                        response = list;
                        break;
                    }

                    case "SEARCH_ASSET_BY_ROOM": {
                        String roomId = (String) data;
                        List<Asset> list = new AssetDAO().searchByRoom(roomId);
                        response = list;
                        break;
                    }

                    case "SEARCH_ASSET_BY_VALUE": {
                        double[] range = (double[]) data;
                        List<Asset> list = new AssetDAO().searchByValueRange(range[0], range[1]);
                        response = list;
                        break;
                    }

                    case "GET_ASSETS_BY_ROOM": {
                        String roomId = (String) data;
                        List<Asset> list = new AssetDAO().getAssetsByRoom(roomId);
                        response = list;
                        break;
                    }

                    case "ADD_ROOM": {
                        Room r = (Room) data;
                        boolean ok = new RoomDAO().addRoom(r);
                        response = ok ? "SUCCESS" : "FAIL";
                        break;
                    }

                    case "GET_ALL_ROOMS": {
                        List<Room> list = new RoomDAO().getAllRooms();
                        response = list;
                        break;
                    }

                    case "DEL_ROOM": {
                        String roomId = (String) data;
                        boolean ok = new RoomDAO().deleteRoom(roomId);
                        response = ok ? "SUCCESS" : "CANNOT_DELETE";
                        break;
                    }

                    case "UPDATE_ROOM": {
                        Room room = (Room) data;
                        RoomDAO roomDAO = new RoomDAO();
                        response = roomDAO.updateRoom(room) ? "SUCCESS" : "Failed to update room";
                        break;
                    }

                    case "ROOM_ASSET_COUNT": {
                        List<String[]> stats = new RoomDAO().getRoomAssetCount();
                        response = stats;
                        break;
                    }

                    default:
                        response = "Unknown command: " + command;
                        break;
                }

            } catch (Exception daoEx) {
                daoEx.printStackTrace();
                response = "Server processing error: " + daoEx.getMessage();
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