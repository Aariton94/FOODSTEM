package foodstem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import foodstem.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InventoryService {



public boolean addItem(String itemName,
                       String category,
                       int quantity,
                       float pricePerUnit,
                       float totalPrice) {

    String sql = "INSERT INTO Inventory (Item_Name, Category, Quantity, Price_Per_Unit, Total_Price) " +
                 "VALUES (?, ?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, itemName);
        pst.setString(2, category);
        pst.setInt(3, quantity);
        pst.setFloat(4, pricePerUnit);
        pst.setFloat(5, totalPrice);

        return pst.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
    

// search
 
public List<Object[]> searchItems(String search) {
    String sql = "SELECT Item_ID, Item_Name, Category, Quantity, Price_Per_Unit, Total_Price " +
                 "FROM Inventory " +
                 "WHERE Item_Name LIKE ? OR Category LIKE ? OR Item_ID = ?";

    List<Object[]> results = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, "%" + search + "%");
        pst.setString(2, "%" + search + "%");

        // dacă search nu e număr, pune 0 ca să nu crape
        int id = 0;
        try { id = Integer.parseInt(search); } catch (NumberFormatException ignored) {}
        pst.setInt(3, id);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[] {
                    rs.getInt("Item_ID"),
                    rs.getString("Item_Name"),
                    rs.getString("Category"),
                    rs.getInt("Quantity"),
                    rs.getFloat("Price_Per_Unit"),
                    rs.getFloat("Total_Price")
                });
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return results;
}
    


// delete
 
public boolean deleteItem(int itemId) {
    String sql = "DELETE FROM Inventory WHERE Item_ID = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, itemId);
        return pst.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


// - update method
 
public List<Object[]> findAllItems() {
    String sql = "SELECT Item_ID, Item_Name, Category, Quantity, Price_Per_Unit, Total_Price FROM Inventory";
    List<Object[]> results = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            results.add(new Object[] {
                rs.getInt("Item_ID"),
                rs.getString("Item_Name"),
                rs.getString("Category"),
                rs.getInt("Quantity"),
                rs.getBigDecimal("Price_Per_Unit"),
                rs.getBigDecimal("Total_Price")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return results;
}
 
 
public boolean updateItem(
        int itemId,
        String itemName,
        String category,
        int quantity,
        float pricePerUnit,
        float totalPrice
) {

    String sql = """
            UPDATE Inventory
            SET Item_Name = ?, Category = ?, Quantity = ?, 
                Price_Per_Unit = ?, Total_Price = ?
            WHERE Item_ID = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, itemName);
            pst.setString(2, category);
            pst.setInt(3, quantity);
            pst.setFloat(4, pricePerUnit);
            pst.setFloat(5, totalPrice);
            pst.setInt(6, itemId);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
}
 


public List<Object[]> getItemsByCategory(String category) {
    // Add Item_ID în SELECT
    String sql = "SELECT Item_ID, Item_Name, Price_Per_Unit, Quantity " +
                 "FROM Inventory WHERE Category = ? ORDER BY Item_Name";

    List<Object[]> results = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, category);

        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[] {
                    rs.getInt("Item_ID"),           // 0: id (int)
                    rs.getString("Item_Name"),      // 1: name (String)
                    rs.getFloat("Price_Per_Unit"),  // 2: price (float)
                    rs.getInt("Quantity")           // 3: stock (int)
                });
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return results;
}



public boolean itemExists(String itemName, String category) {
    String sql = "SELECT COUNT(*) FROM Inventory WHERE Item_Name = ? AND Category = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, itemName);
        pst.setString(2, category);

        try (ResultSet rs = pst.executeQuery()) {
            rs.next();
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


}


 
