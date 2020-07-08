package banking;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardDao {
    private static final String CREATE_CARD_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS card (" +
            "   id INTEGER PRIMARY KEY," +
            "   number TEXT NOT NULL," +
            "   pin TEXT NOT NULL," +
            "   balance INTEGER DEFAULT 0);";

    private static final String INSERT_CARD_SQL =
            "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?);";

    private static final String READ_ALL_SQL =
            "SELECT id, number, pin, balance FROM card;";

    public void createCardTable() {
        Connection conn = DbConnection.connectToDb();

        try (Statement statement = conn.createStatement()) {
            statement.execute(CREATE_CARD_TABLE_SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int create(Account account) {
        Connection conn = DbConnection.connectToDb();

        try (PreparedStatement statement = conn.prepareStatement(INSERT_CARD_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, account.getCreditCardNumber());
            statement.setString(2, account.getPin());
            statement.setInt(3, account.getBalance());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet rs = statement.getGeneratedKeys();

                if (rs.next()) {
                    return rs.getInt(1);
                }
            } else {
                System.err.println("0 rows affected in card Insert");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Account> loadAll() {
        List<Account> accountList = new ArrayList<>();
        Connection conn = DbConnection.connectToDb();

        try (Statement statement = conn.createStatement()) {
            ResultSet rs = statement.executeQuery(READ_ALL_SQL);

            while (rs.next()) {
                int id = rs.getInt(1);
                String creditCardNumber = rs.getString(2);
                String pin = rs.getString(3);
                int balance = rs.getInt(4);
                Account account = new Account(creditCardNumber, pin);
                account.setId(id);
                account.setBalance(balance);
                accountList.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return accountList;
    }
}
