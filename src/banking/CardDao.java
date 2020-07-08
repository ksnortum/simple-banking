package banking;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CardDao {
    private static final String CREATE_CARD_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS card (" +
            "   id INTEGER PRIMARY KEY," +
            "   number TEXT NOT NULL," +
            "   pin TEXT NOT NULL," +
            "   balance INTEGER DEFAULT 0);";

    public static void createCardTable() {
        Connection conn = DbConnection.connectToDb();

        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_CARD_TABLE_SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
