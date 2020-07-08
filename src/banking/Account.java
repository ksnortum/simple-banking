package banking;

public class Account {
    private int id = -1;
    private final String creditCardNumber;
    private final String pin;
    private int balance = 0;

    public Account(String creditCardNumber, String pin) {
        this.creditCardNumber = creditCardNumber;
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Account { id = %d, cc# = %s, pin = %s, balance = %d }",
                id, creditCardNumber, pin, balance);
    }
}
