package banking;

public class Account {
    private final String creditCardNumber;
    private final String pin;

    public Account(String creditCardNumber, String pin) {
        this.creditCardNumber = creditCardNumber;
        this.pin = pin;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getPin() {
        return pin;
    }
}
