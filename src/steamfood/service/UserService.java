/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package steamfood.service;

import steamfood.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    // creare cont nou
    public String createAccount(
            
            String name,                               
            String phone,                               
            String username,                               
            String password,                             
            String confirmPassword,
            String role
    ) {

        // 1. Validări simple
        if (
                name == null || name.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()
                || phone == null || phone.isBlank()
                
            ) {
            return "Please fill in all required fields.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        // 2. Verificăm dacă username există deja
        String checkSql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return "Username already exists.";
            }
        } catch (SQLException e) {
            return "Database error (check): " + e.getMessage();
        }

        // 3. Inserăm user nou
        String insertSql = "INSERT INTO users (name, phone, username, password, role) "
                         + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            insertStmt.setString(1, name);
            insertStmt.setString(2, phone);
            insertStmt.setString(3, username);
            insertStmt.setString(4, password);
            insertStmt.setString(5, role);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                return "OK"; // succes
            } else {
                return "Could not create account.";
            }

        } catch (SQLException e) {
            return "Database error (insert): " + e.getMessage();
        }
    }

    // login
    public boolean login(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true dacă există userul

        } catch (SQLException e) {
            System.out.println("Login DB error: " + e.getMessage());
            return false;
        }
    }
}
