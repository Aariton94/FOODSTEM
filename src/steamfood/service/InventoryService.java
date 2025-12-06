package steamfood.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import steamfood.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InventoryService {



 public boolean addItem(String itemID,
                           String itemName,
                           int quantity,
                           float pricePerUnit,
                           float totalPrice) {

        String sql = "INSERT INTO Inventory " +
                     "(Item_ID, Item_Name, Quantity, Price_Per_Unit, Total_Price) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, itemID);
            pst.setString(2, itemName);
            pst.setInt(3, quantity);
            pst.setFloat(4, pricePerUnit);
            pst.setFloat(5, totalPrice);

            int rows = pst.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    

// search
 
    public java.util.List<Object[]> searchItems(String search) {
        String sql = "SELECT Item_ID, Item_Name, Quantity, Price_Per_Unit, Total_Price " +
                     "FROM Inventory " +
                     "WHERE Item_ID = ? OR Item_Name LIKE ?";

        List<Object[]> results = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, search);
            pst.setString(2, "%" + search + "%");  // căutare parțială după nume

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    results.add(new Object[] {
                        rs.getString("Item_ID"),
                        rs.getString("Item_Name"),
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
 
 public boolean deleteItem(String itemId) {

    String sql = "DELETE FROM Inventory WHERE Item_ID = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, itemId);

        int rows = pst.executeUpdate();
        return rows > 0;   // true dacă s-a șters cel puțin un rând

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


// - update method
 
 public List<Object[]> findAllItems() {
    String sql = "SELECT Item_ID, Item_Name, Quantity, Price_Per_Unit, Total_Price FROM Inventory";
    List<Object[]> results = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            results.add(new Object[] {
                rs.getString("Item_ID"),
                rs.getString("Item_Name"),
                rs.getInt("Quantity"),
                rs.getFloat("Price_Per_Unit"),
                rs.getFloat("Total_Price")
            });
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return results;
}
 
 
 public boolean updateItem(String itemID,
                          String itemName,
                          int quantity,
                          float pricePerUnit,
                          float totalPrice) {

    String sql = "UPDATE Inventory " +
                 "SET Item_Name = ?, Quantity = ?, Price_Per_Unit = ?, Total_Price = ? " +
                 "WHERE Item_ID = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setString(1, itemName);
        pst.setInt(2, quantity);
        pst.setFloat(3, pricePerUnit);
        pst.setFloat(4, totalPrice);
        pst.setString(5, itemID);

        int rows = pst.executeUpdate();
        return rows > 0;   // true = s-a modificat cel puțin un rând

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
 
 
}