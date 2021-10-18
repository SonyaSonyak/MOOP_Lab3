package lab;

import java.sql. *;

public class Model {
    private final Connection con; // connection to db
    private final Statement stmt; // operator

    // constructor
    public Model(String DBName, String ip, int port)
            throws Exception {

        String url = "jdbc:mysql://" + ip + ":" + port + "/" +
                DBName + "?serverTimezone=Europe/Kiev&useSSL=FALSE";
        con = DriverManager.getConnection(url, "admin", "Password_1");
        stmt = con.createStatement();
    }

    // models list
    public void showModels() {
        String sql = "SELECT ID, Name, ManufacturerID, ColorID, Year, EngineCapacity, Count FROM Model";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("Cars list:");
            System.out.println("ID - Name - ManufacturerID - ColorID - Year - EngineCapacity - Count");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                int man_id = rs.getInt("ManufacturerID");
                int col_id = rs.getInt("ColorID");
                int year = rs.getInt("Year");
                int eng_cap = rs.getInt("EngineCapacity");
                int count = rs.getInt("Count");
                System.out.println(">>" + id + " - " + name + " - " + man_id + " - " + col_id + " - " + year +
                        " - " + eng_cap + " - " + count);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(
                    "ERROR while getting auto's list");
            System.out.println(" >> " + e.getMessage());
        }
    }

    // count models by manufacturer
    public String countModelsByManufacturer() {
        String res = new String();
        String sql = "SELECT SUM(Count) as count, mn.Name FROM Model AS md INNER JOIN Manufacturer AS mn " +
                "ON md.ManufacturerID = mn.ID GROUP BY mn.ID ORDER BY count asc, mn.Name asc";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            res += "Cars count by manufacturer list:\n" + "Cars count - lab.Manufacturer Name\n";
            while (rs.next()) {
                int count = rs.getInt(1);
                String name = rs.getString(2);
                res += ">>" + count + " - " + name + "\n";
            }
            System.out.println(res);
            rs.close();
        } catch (SQLException e) {
            System.out.println(
                    "ERROR while getting auto's count by manufacturer list");
            System.out.println(" >> " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }

        return res;
    }

    // show cars by manufacturer
    public String showModelsByManufacturer() {
        String res = new String();
        String sql = "SELECT md.Name, mn.Name, md.Count FROM Model AS md INNER JOIN Manufacturer AS mn " +
                "ON md.ManufacturerID = mn.ID ORDER BY mn.Name asc, md.Count asc";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            res += "Cars by manufacturer list:\n" + "Car Name - Manufacturer Name - Count\n";
            while (rs.next()) {
                String car_name = rs.getString(1);
                String man_name = rs.getString(2);
                int count = rs.getInt(3);
                res += ">>" + car_name + " - " + man_name + " - " + count + "\n";
            }
            System.out.println(res);
            rs.close();
        } catch (SQLException e) {
            System.out.println(
                    "ERROR while getting cars by manufacturer list");
            System.out.println(" >> " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }

        return res;
    }

    // models by manufacturer ID
    public String findModelsByManID(int manufacturer_id) {
        String res = new String();
        String sql = "SELECT ID, Name, ManufacturerID, ColorID, Year, EngineCapacity, Count FROM Model " +
                "WHERE ManufacturerID = " + manufacturer_id;
        try {
            ResultSet rs = stmt.executeQuery(sql);
            res += "Cars list:\n" + "ID - Name - ManufacturerID - ColorID - Year - EngineCapacity - Count\n";
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                int man_id = rs.getInt("ManufacturerID");
                int col_id = rs.getInt("ColorID");
                int year = rs.getInt("Year");
                int eng_cap = rs.getInt("EngineCapacity");
                int count = rs.getInt("Count");
                res += ">>" + id + " - " + name + " - " + man_id + " - " + col_id + " - " + year +
                        " - " + eng_cap + " - " + count + "\n";
            }
            System.out.println(res);
            rs.close();
        } catch (SQLException e) {
            System.out.println(
                    "ERROR while getting auto's list");
            System.out.println(" >> " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }

        return res;
    }

    // stop work
    public void stop() throws SQLException {
        con.close();
    }

    // add model
    public boolean addModel(String name, int man_id, int col_id, int year, int eng_cap, int count) {
        String sql = "INSERT INTO Model (Name, ManufacturerID, ColorID, Year, EngineCapacity, Count) " +
                "VALUES ('" + name + "', " + man_id + ", " + col_id + ", "
                + year  + ", " + eng_cap  + ", " + count + ")";
        try {
            stmt.executeUpdate(sql);
            System.out.println("lab.Model " + name + " added successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("ERROR! lab.Model " + name + " not added!");
            System.out.println(" >> " + e.getMessage());
            return false;
        }
    }

    // update model
    public boolean updateModel(int id, String name, int man_id, int col_id, int year, int eng_cap, int count) {
        String sql = "UPDATE Model SET ID = " + id;
        if (!name.equals("")) {
            sql += ", Name = '" + name + "'";
        }

        if (man_id != 0) {
            sql += ", ManufacturerID = " + man_id;
        }

        if (col_id != 0) {
            sql += ", ColorID = " + col_id;
        }

        if (year > 0) {
            sql += ", Year = " + year;
        }

        if (eng_cap > 0) {
            sql += ", EngineCapacity = " + eng_cap;
        }

        if (count >= 0) {
            sql += ", Count = " + count;
        }
        sql += " WHERE ID = " + id;

        try {
            stmt.executeUpdate(sql);
            System.out.println("lab.Model " + name + " added successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("ERROR! lab.Model " + name + " not added!");
            System.out.println(" >> " + e.getMessage());
            return false;
        }
    }

    // delete model
    public boolean deleteModel(int id) {
        String sql = "DELETE FROM Model WHERE ID =" + id;
        try {
            int c = stmt.executeUpdate(sql);
            if (c > 0) {
                System.out.println("lab.Model with id " + id + " deleted successfully!");
                return true;
            } else {
                System.out.println("lab.Model with id " + id + " not found!");

                return false;
            }
        } catch (SQLException e) {
            System.out.println("ERROR while deleting model with id " + id);
                    System.out.println(" >> " + e.getMessage());
            return false;
        }
    }
}