package banking;

import java.sql.*;

public class DbConnection {
    private static Connection conn = null;

    public static Connection connectToDb() {
        if (conn != null) {
            return conn;
        }

        String url = "jdbc:sqlite:" + GlobalData.fileName;

        try {
            conn = DriverManager.getConnection(url);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot connection to url \"" + url + "\"");
        }
    }
}
