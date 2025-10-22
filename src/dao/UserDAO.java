package dao;

import model.User;
import java.sql.*;

public class UserDAO {

    public User authenticate(User u) {
        String sql = "SELECT username, role FROM users WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString("username"), null, rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in UserDAO.authenticate(): " + e.getMessage());
        }
        return null; // Không tìm thấy
    }
}
