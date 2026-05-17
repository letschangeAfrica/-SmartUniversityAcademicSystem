package smartuniversityacademicsystem.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import smartuniversityacademicsystem.model.Role;
import smartuniversityacademicsystem.model.User;

public class UserDAO {

    // Uses SHA2 password hashing stored in MySQL.
    // For plain-text passwords during development, replace the WHERE clause with:
    //   AND password = ?
    private static final String LOGIN_SQL =
        "SELECT id, username, full_name, role " +
        "FROM users " +
        "WHERE username = ? AND password = SHA2(?, 256) AND active = TRUE";

    public User authenticate(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(LOGIN_SQL)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        Role.valueOf(rs.getString("role").toUpperCase())
                    );
                }
            }
        }
        return null; // credentials invalid
    }
}
