package banking;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Scanner STDIN = new Scanner(System.in);
    private static final Random RANDOM = new Random();

    private final Map<String, Account> accounts = new HashMap<>();
    private boolean loggedIn = false;
    private CardDao cardDao = new CardDao();

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        commandLineArguments(args);
        createDbFile();
        cardDao.createCardTable();
        loadCards();
        boolean appShouldContinue;

        do {
            appShouldContinue = doCreateLogin();

            if (appShouldContinue && loggedIn) {
                appShouldContinue = doBalanceLogout();
            }
        } while (appShouldContinue);

        System.out.printf("%nBye!%n");
    }

    private void commandLineArguments(String[] args) {
        if (args.length > 1 && "-fileName".equals(args[0])) {
            GlobalData.fileName = args[1];
        }
    }

    private void createDbFile() {
        File file = new File(GlobalData.fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create \"" + GlobalData.fileName + "\"");
            }
        }
    }

    private void loadCards() {
        List<Account> accountList = cardDao.loadAll();

        for (Account account : accountList) {
            accounts.put(account.getCreditCardNumber(), account);
        }
    }

    private boolean doCreateLogin() {
        boolean appShouldContinue = true;
        boolean moreToDo = true;

        do {
            printLoginMenu();
            int selected = STDIN.nextInt();
            STDIN.nextLine();

            switch (selected) {
                case 1:
                    createAnAccount();
                    appShouldContinue = true;
                    break;
                case 2:
                    loginToAccount();
                    moreToDo = false;
                    appShouldContinue = true;
                    break;
                case 0:
                    moreToDo = false;
                    appShouldContinue = false;
                    break;
                default:
                    System.out.printf("%nInvalid selection%n%n");
            }
        } while (moreToDo);

        return appShouldContinue;
    }

    private boolean doBalanceLogout() {
        boolean appShouldContinue = true;
        boolean moreToDo = true;

        do {
            printBalanceMenu();
            int selected = STDIN.nextInt();
            STDIN.nextLine();

            switch (selected) {
                case 1:
                    doBalance();
                    appShouldContinue = true;
                    break;
                case 2:
                    doLogout();
                    moreToDo = false;
                    appShouldContinue = true;
                    break;
                case 0:
                    moreToDo = false;
                    appShouldContinue = false;
                    break;
                default:
                    System.out.printf("%nInvalid selection%n%n");
            }
        } while (moreToDo);

        return appShouldContinue;
    }

    private void printLoginMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    private void printBalanceMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Log out");
        System.out.println("0. Exit");
    }

    private void createAnAccount() {
        String creditCardNumber = createCreditCardNumber();
        String pin = String.format("%04d", RANDOM.nextInt(10000));
        Account account = new Account(creditCardNumber, pin);
        CardDao cardDao = new CardDao();
        int id = cardDao.create(account);

        if (id != -1) {
            account.setId(id);
            accounts.put(creditCardNumber, account);
            displayAccount(account);
        }
    }

    private String createCreditCardNumber() {
        String iin = "400000";
        String account = String.format("%09d", RANDOM.nextInt(1000000000));
        String checkBit = createCheckBit(iin + account);
        return iin + account + checkBit;
    }

    private String createCheckBit(String iinAndAccount) {
        String[] chars = iinAndAccount.split("");
        int[] digits = Arrays.stream(chars)
                .mapToInt(Integer::valueOf)
                .toArray();

        for (int i = 0; i < digits.length; i += 2) {
            digits[i] *= 2;
        }

        digits = Arrays.stream(digits)
                .map(i -> i >= 10 ? i - 9 : i)
                .toArray();
        int sum = Arrays.stream(digits).sum();
        int nearestMultipleOfTen = (int) Math.ceil(sum / 10.0) * 10;

        return String.valueOf(nearestMultipleOfTen - sum);
    }

    private void displayAccount(Account account) {
        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getCreditCardNumber());
        System.out.println("Your card PIN:");
        System.out.println(account.getPin());
        System.out.println();
    }

    private void loginToAccount() {
        if (accounts.isEmpty()) {
            System.out.printf("%nThere are no accounts yet!%n%n");
            return;
        }

        System.out.println("Enter your card number:");
        String creditCardEntered = STDIN.nextLine();
        System.out.println("Enter your PIN:");
        String pinEntered = STDIN.nextLine();
        System.out.println();

        if (accounts.containsKey(creditCardEntered)
                && pinEntered.equals(accounts.get(creditCardEntered).getPin())) {
            System.out.println("You have successfully logged in!");
            loggedIn = true;
        } else {
            System.out.println("Wrong card number or PIN!");
        }

        System.out.println();
    }

    private void doBalance() {
        System.out.printf("%nBalance: 0%n%n");
    }

    private void doLogout() {
        System.out.printf("%nYou have successfully logged out!%n%n");
        loggedIn = false;
    }
}
