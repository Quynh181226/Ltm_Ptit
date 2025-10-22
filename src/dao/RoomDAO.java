package dao;

import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean updateRoom(Room room) {
        String sql = "UPDATE room SET name = ?, description = ? WHERE room_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, room.getName());
            ps.setString(2, room.getDescription());
            ps.setString(3, room.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in updateRoom(): " + e.getMessage());
            return false;
        }
    }


    public boolean addRoom(Room room) {
        String sql = "INSERT INTO room (room_id, name, description) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, room.getId());
            ps.setString(2, room.getName());
            ps.setString(3, room.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in addRoom(): " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT room_id, name, description FROM room";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Room(
                        rs.getString("room_id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error in getAllRooms(): " + e.getMessage());
        }
        return list;
    }

    public boolean canDeleteRoom(String roomId) {
        String sql = "SELECT COUNT(*) FROM asset WHERE room_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // true nếu không có tài sản
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in canDeleteRoom(): " + e.getMessage());
        }
        return false; // Mặc định là không được xóa
    }


    public boolean deleteRoom(String roomId) {
        if (!canDeleteRoom(roomId)) {
            System.out.println("Cannot delete room: assets exist.");
            return false;
        }
        String sql = "DELETE FROM room WHERE room_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error in deleteRoom(): " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getRoomAssetCount() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT r.room_id, r.name, COUNT(a.asset_id) AS asset_count " +
                "FROM room r LEFT JOIN asset a ON r.room_id = a.room_id " +
                "GROUP BY r.room_id, r.name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("room_id"),
                        rs.getString("name"),
                        String.valueOf(rs.getInt("asset_count"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Error in getRoomAssetCount(): " + e.getMessage());
        }
        return result;
    }
}
