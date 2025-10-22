package dao;

import model.Asset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetDAO {

    public boolean addAsset(Asset a) {
        String sql = "INSERT INTO asset (asset_id, name, type, room_id, value) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
          ps.setString(1, a.getId());
            ps.setString(2, a.getName());
            ps.setString(3, a.getType());
            ps.setString(4, a.getRoomId());
            ps.setDouble(5, a.getValue());
           return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in addAsset(): " + e.getMessage());
            return false;
        }
    }

    public boolean updateAsset(Asset a) {
        String sql = "UPDATE asset SET name=?, type=?, room_id=?, value=? WHERE asset_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setString(2, a.getType());
            ps.setString(3, a.getRoomId());
            ps.setDouble(4, a.getValue());
            ps.setString(5, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in updateAsset(): " + e.getMessage());
            return false;
        }
    }
    public boolean deleteAsset(String assetId) {
        String sql = "DELETE FROM asset WHERE asset_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, assetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in deleteAsset(): " + e.getMessage());
            return false;
        }
    }


    public List<Asset> getAllAssets() {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT asset_id, name, type, room_id, value FROM asset";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                list.add(new Asset(
                        rs.getString("asset_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("room_id"),
                        rs.getDouble("value")));
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllAssets(): " + e.getMessage());
            }
        return list;
    }















































 public List<Asset> searchAsset(String keyword) {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT asset_id, name, type, room_id, value FROM asset WHERE name LIKE ? OR type LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Asset(
                            rs.getString("asset_id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("room_id"),
                            rs.getDouble("value")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in searchAsset(): " + e.getMessage());
        }
        return list;
    }
    public List<Asset> getAssetsByRoom(String roomId) {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT asset_id, name, type, room_id, value FROM asset WHERE room_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Asset(
                            rs.getString("asset_id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("room_id"),
                            rs.getDouble("value")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getAssetsByRoom(): " + e.getMessage());
        }
        return list;
    }
    public List<Asset> searchByRoom(String roomId) {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT asset_id, name, type, room_id, value FROM asset WHERE room_id LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + roomId + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Asset(
                            rs.getString("asset_id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("room_id"),
                            rs.getDouble("value")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in searchByRoom(): " + e.getMessage());
        }
        return list;
    }
 public List<Asset> searchByValueRange(double min, double max) {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT asset_id, name, type, room_id, value FROM asset WHERE value BETWEEN ? AND ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Asset(
                            rs.getString("asset_id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("room_id"),
                            rs.getDouble("value")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in searchByValueRange(): " + e.getMessage());
        }
        return list;
    }
}