package banking;

import java.sql.*;

public class CardDao {
    private static final String CREATE_CARD_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS card (" +
            "   id INTEGER PRIMARY KEY," +
            "   number TEXT NOT NULL," +
            "   pin TEXT NOT NULL," +
            "   balance INTEGER DEFAULT 0)";

    private static final String INSERT_CARD_SQL =
            "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?)";

    private static final String UPDATE_BALANCE_SQL =
            "UPDATE card SET balance = ? WHERE id = ?";

    private static final String ACCOUNT_FROM_CC_NUMBER_AND_PIN_SQL =
            "SELECT id, balance FROM card WHERE number = ? AND pin = ?";

    private static final String ACCOUNT_FROM_CC_NUMBER_SQL =
            "SELECT pin, id, balance FROM card WHERE number = ?";
    private static final String DELETE_ACCOUNT_SQL =
            "DELETE FROM card WHERE id = ?";

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
                System.err.printf("0 rows affected in card create%n%s%n", account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void updateBalance(Account account) {
        Connection conn = DbConnection.connectToDb();

        try (PreparedStatement statement = conn.prepareStatement(UPDATE_BALANCE_SQL)) {
            statement.setInt(1, account.getBalance());
            statement.setInt(2, account.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected != 1) {
                System.err.printf("Wrong number of rows affected (%d) in card update%n%s%n",
                        rowsAffected, account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account accountFromCCNumberAndPin(String creditCardNumber, String pin) {
        Connection conn = DbConnection.connectToDb();

        try (PreparedStatement statement = conn.prepareStatement(ACCOUNT_FROM_CC_NUMBER_AND_PIN_SQL)) {
            statement.setString(1, creditCardNumber);
            statement.setString(2, pin);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Account account = new Account(creditCardNumber, pin);
                account.setId(rs.getInt(1));
                account.setBalance(rs.getInt(2));

                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Account accountFromCCNumber(String creditCardNumber) {
        Connection conn = DbConnection.connectToDb();

        try (PreparedStatement statement = conn.prepareStatement(ACCOUNT_FROM_CC_NUMBER_SQL)) {
            statement.setString(1, creditCardNumber);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String pin = rs.getString(1);
                Account account = new Account(creditCardNumber, pin);
                account.setId(rs.getInt(2));
                account.setBalance(rs.getInt(3));

                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deleteAccount(Account account) {
        Connection conn = DbConnection.connectToDb();

        try (PreparedStatement statement = conn.prepareStatement(DELETE_ACCOUNT_SQL)) {
            statement.setInt(1, account.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected != 1) {
                System.err.printf("Wrong number of rows affected (%d) in card deleteAccount%n%s%n",
                        rowsAffected, account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
