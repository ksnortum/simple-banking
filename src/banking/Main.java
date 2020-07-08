package banking;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Scanner STDIN = new Scanner(System.in);
    private static final Random RANDOM = new Random();

    private final CardDao cardDao = new CardDao();
    private Account loggedInAccount = null;

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        commandLineArguments(args);
        createDbFile();
        cardDao.createCardTable();
        boolean appShouldContinue;

        do {
            appShouldContinue = doCreateLogin();

            if (appShouldContinue && loggedInAccount != null) {
                appShouldContinue = doLoggedInProcessing();
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

    private boolean doLoggedInProcessing() {
        boolean appShouldContinue = true;
        boolean moreToDo = true;

        do {
            printLoggedInMenu();
            int selected = STDIN.nextInt();
            STDIN.nextLine();

            switch (selected) {
                case 1:
                    doBalance();
                    appShouldContinue = true;
                    break;
                case 2:
                    doAddIncome();
                    appShouldContinue = true;
                    break;
                case 3:
                    doTransfer();
                    appShouldContinue = true;
                    break;
                case 4:
                    doCloseAccount();
                    appShouldContinue = true;
                    break;
                case 5:
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

    private void printLoggedInMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
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
        int sum = getLuhnSum(iinAndAccount);
        int nearestMultipleOfTen = (int) Math.ceil(sum / 10.0) * 10;

        return String.valueOf(nearestMultipleOfTen - sum);
    }

    private int getLuhnSum(String ccNumber) {
        String iinAndAccount = ccNumber.substring(0, 15);
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

        return Arrays.stream(digits).sum();
    }

    private boolean checkCCNumber(String ccNumber) {
        int luhnSum = getLuhnSum(ccNumber);
        int checkBit = Integer.parseInt(ccNumber.substring(15, 16));

        return (luhnSum + checkBit) % 10 == 0;
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
        System.out.println("Enter your card number:");
        String creditCardNumber = STDIN.nextLine();
        System.out.println("Enter your PIN:");
        String pin = STDIN.nextLine();
        System.out.println();
        Account account = cardDao.accountFromCCNumberAndPin(creditCardNumber, pin);

        if (account != null) {
            loggedInAccount = account;
            System.out.println("You have successfully logged in!");
        } else {
            System.out.println("Wrong card number or PIN!");
        }

        System.out.println();
    }

    private void doBalance() {
        System.out.printf("%nBalance: %d%n%n", loggedInAccount.getBalance());
    }

    private void doAddIncome() {
        System.out.printf("%nEnter income:%n");
        int balance = STDIN.nextInt();
        STDIN.nextLine();
        loggedInAccount.setBalance(balance);
        cardDao.updateBalance(loggedInAccount);
        System.out.printf("Income was added!%n%n");
    }

    private void doTransfer() {
        System.out.printf("%nTransfer%n");
        System.out.println("Enter card number:");
        String transferCCNumber = STDIN.nextLine();

        if (!checkCCNumber(transferCCNumber)) {
            System.out.printf("Probably you made mistake in the card number. Please try again!%n%n");
            return;
        }

        Account transferAccount = cardDao.accountFromCCNumber(transferCCNumber);

        if (transferAccount == null) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int transferAmount = STDIN.nextInt();
        STDIN.nextLine();

        if (transferAmount > loggedInAccount.getBalance()) {
            System.out.printf("Not enough money!%n%n");
            return;
        }

        loggedInAccount.setBalance(loggedInAccount.getBalance() - transferAmount);
        cardDao.updateBalance(loggedInAccount);
        transferAccount.setBalance(transferAccount.getBalance() + transferAmount);
        cardDao.updateBalance(transferAccount);
        System.out.printf("Success!%n%n");
    }

    private void doCloseAccount() {
        cardDao.deleteAccount(loggedInAccount);
        System.out.printf("%nThe account has been closed!%n%n");
    }

    private void doLogout() {
        loggedInAccount = null;
        System.out.printf("%nYou have successfully logged out!%n%n");
    }
}
