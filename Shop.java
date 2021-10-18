package lab;

import java.sql. *;
import java.util.Calendar;

public class Manufacturer {
    private final Connection con; // connection to db
    private final Statement stmt; // operator

    // constructor
    public Manufacturer(String DBName, String ip, int port)
            throws Exception {

        String url = "jdbc:mysql://" + ip + ":" + port + "/" +
                DBName + "?serverTimezone=Europe/Kiev&useSSL=FALSE&allowPublicKeyRetrieval=true";
        con = DriverManager.getConnection(url, "admin", "Password_1");
        stmt = con.createStatement();
    }

    // manufacturers list
    public String showManufacturers() {
        String res = "";
        String sql = "SELECT ID, Name, FoundationDate FROM Manufacturer";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            res += "Manufacturers list:\n";
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                Date foundation = rs.getDate("FoundationDate");
                res += ">>" + id + " - " + name + " - " + foundation.toString() + "\n";
            }
            System.out.println(res);
            rs.close();
        } catch (SQLException e) {
            System.out.println(
                    "ERROR while getting lab.Manufacturer`s list");
            System.out.println(" >> " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }

        return res;
    }

    public String getManufacturerFoundationDate(int id) {
        String sql = "SELECT FoundationDate FROM Manufacturer WHERE ID = " + id;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Date foundation = rs.getDate("FoundationDate");
                return foundation.toString();
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(
                    "ERROR while getting lab.Manufacturer foundation date");
            System.out.println(" >> " + e.getMessage());
        }

        return "1970-01-01";
    }

    // stop work
    public void stop() throws SQLException {
        con.close();
    }

    // add manufacturer
    public boolean addManufacturer(String name, Calendar foundation_date) {
        int month = foundation_date.get(Calendar.MONTH);
        String month_q;
        if (month < 9) {
            month_q = "0" + String.valueOf(month + 1);
        } else {
            month_q = String.valueOf(month);
        }

        int day = foundation_date.get(Calendar.DAY_OF_MONTH);
        String day_q;
        if (day < 10) {
            day_q = "0" + String.valueOf(day);
        } else {
            day_q = String.valueOf(day);
        }

        String sql = "INSERT INTO Manufacturer (Name, FoundationDate) " +
                "VALUES ('" + name + "', '" + foundation_date.get(Calendar.YEAR) + '-' +
                month_q + '-' + day_q + "')";
        try {
            stmt.executeUpdate(sql);
            System.out.println("lab.Manufacturer " + name + " added successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("ERROR! lab.Manufacturer " + name + " not added!");
            System.out.println(" >> " + e.getMessage());
            return false;
        }
    }

    public boolean updateManufacturer(int id, String name, String foundation_date) {
        String sql;
        if (name.equals("")) {
            sql = "UPDATE Manufacturer SET FoundationDate = '" + foundation_date + "' WHERE ID = " + id;
        } else {
            sql = "UPDATE Manufacturer SET Name = '" + name + "', FoundationDate = '" +
                    foundation_date + "' WHERE ID = " + id;
        }
        try {
            stmt.executeUpdate(sql);
            System.out.println("lab.Manufacturer " + name + " updated successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("ERROR! lab.Manufacturer " + name + " not updated!");
            System.out.println(" >> " + e.getMessage());
            return false;
        }
    }

    // delete lab.Manufacturer
    public boolean deleteManufacturer(int id) {
        String sql = "DELETE FROM Manufacturer WHERE ID =" + id;
        try {
            int c = stmt.executeUpdate(sql);
            if (c > 0) {
                System.out.println("lab.Manufacturer with id " + id + " deleted successfully!");
                return true;
            } else {
                System.out.println("lab.Manufacturer with id " + id + " not found!");

                return false;
            }
        } catch (SQLException e) {
            System.out.println("ERROR while deleting lab.Manufacturer with id " + id);
            System.out.println(" >> " + e.getMessage());
            return false;
        }
    }
}