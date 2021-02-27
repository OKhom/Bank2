package com.javapro.workspace;

import com.javapro.entity.Accounts;
import com.javapro.entity.Clients;
import com.javapro.entity.Transactions;
import com.javapro.entity.Currency;
import com.javapro.workspace.enums.CurrencyLit;

import javax.persistence.*;
import java.util.*;

public class Actions {
    static EntityManagerFactory emf;
    static EntityManager em;
    static Scanner sc;

    public static EntityManagerFactory initDB(String persistenceUnitName, Scanner scan) {
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        em = emf.createEntityManager();
        sc = scan;
        CurrencyLit[] currencyName = CurrencyLit.values();
        Double[] currencyValue = {1d, 27.9492, 33.9289};
        em.getTransaction().begin();
        try {
            TypedQuery<Currency> query = em.createNamedQuery("verifyCurrency", Currency.class);
            query.setParameter("currency", CurrencyLit.valueOf("UAH"));
            try {
                query.getSingleResult();
                em.getTransaction().rollback();
                System.out.println("No need currency update");
            } catch (NoResultException nre) {
                for (int i = 0; i < currencyName.length; i++) {
                    Currency currency = new Currency(currencyName[i], currencyValue[i]);
                    em.persist(currency);
                }
                em.getTransaction().commit();
                System.out.println("Currency updated");
            }
        } catch (Exception ex) {
            em.getTransaction().rollback();
            System.out.println("Transaction not complete...");
        }
        return emf;
    }

    public static Clients addClient() {
        Values input = inputClient(false);
        TypedQuery<Clients> query = em.createNamedQuery("verifyClient", Clients.class);
        query.setParameter("name", input.getName());
        query.setParameter("lastName", input.getLastName());
        try {
            em.getTransaction().begin();
            try {
                query.getSingleResult();
                em.getTransaction().rollback();
                return null;
            } catch (NoResultException ex) {
                Clients client = new Clients(input.getName(), input.getLastName());
                em.persist(client);
                em.getTransaction().commit();
                return client;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
    }

    public static Accounts addAccount() {
        Values input = inputClient(true);
        TypedQuery<Clients> query = em.createNamedQuery("verifyClient", Clients.class);
        query.setParameter("name", input.getName());
        query.setParameter("lastName", input.getLastName());
        TypedQuery<Currency> queryCurrency = em.createNamedQuery("verifyCurrency", Currency.class);
        queryCurrency.setParameter("currency", CurrencyLit.valueOf(input.getCurrencyType()));
        try {
            em.getTransaction().begin();
            try {
                Clients client = query.getSingleResult();
                Currency currency = queryCurrency.getSingleResult();
                List<Accounts> accounts = client.getAccounts();
                if (accounts.size() != 0) {
                    for (Accounts a : accounts) {
                        if (a.getCurrency().equals(CurrencyLit.valueOf(input.getCurrencyType()))) {
                            System.out.println("Client " + client.getLastName() + " " + client.getName()
                                    + " has " + a.getCurrency() + " account already. Account doesn't added.");
                            em.getTransaction().rollback();
                            return null;
                        }
                    }
                }
                Accounts account = new Accounts((input.getLastName() + "_" + input.getName()).toLowerCase(),
                        CurrencyLit.valueOf(input.getCurrencyType()), (double) 0);
                client.addAccount(account);
                currency.addAccount(account);
                em.persist(account);
                em.getTransaction().commit();
                return account;
            } catch (NoResultException ex) {
                System.out.print("Client " + input.getLastName() + " " + input.getName() + " doesn't exist. " +
                        "Do you wish create new client? (Y/N) ");
                String in = sc.nextLine();
                if (in.equals("Y") || in.equals("y")) {
                    Clients client = new Clients(input.getName(), input.getLastName());
                    Currency currency = queryCurrency.getSingleResult();
                    Accounts account = new Accounts((input.getLastName() + "_" + input.getName()).toLowerCase(),
                            currency.getCurrency(), (double) 0);
                    client.addAccount(account);
                    currency.addAccount(account);
                    em.persist(client);
                    em.persist(account);
                    em.getTransaction().commit();
                    System.out.println("Client " + client.getLastName() + " " + client.getName() + " created");
                    return account;
                } else {
                    em.getTransaction().rollback();
                    return null;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
    }

    public static Accounts putMoney() {
        Accounts account = selectAccount();
        if (account == null) return null;
        Double curBalance = account.getBalance();
        Double deposit = inputMoney(account);
        Double newBalance = curBalance + deposit;
        em.getTransaction().begin();
        try {
            if (newBalance < 0) {
                em.getTransaction().rollback();
                System.out.println("There is not enough money in the account");
                return null;
            }
            account.setBalance(newBalance);
            Transactions transaction = createTransaction(deposit, newBalance);
            account.addTransaction(transaction);
            em.persist(transaction);
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
        return account;
    }

    public static List<Accounts> transferMoney() {
        System.out.println("Enter account information the money will be transferred FROM");
        Accounts accountFrom = selectAccount();
        if (accountFrom == null) return null;
        System.out.println("Enter account information the money will be transferred TO");
        Accounts accountTo = selectAccount();
        if (accountTo == null) return null;
        boolean sameCurrencyType = accountFrom.getCurrency().equals(accountTo.getCurrency());
        if (!sameCurrencyType) {
            System.out.println("Accounts FROM and TO must to have same currency. Try again...");
            return null;
        }
        Double curBalanceFrom = accountFrom.getBalance();
        Double curBalanceTo = accountTo.getBalance();
        Double transferMoney = inputMoney(accountFrom);
        return doTransfer(accountFrom, accountTo, curBalanceFrom, curBalanceTo, transferMoney, sameCurrencyType);
    }

    public static List<Accounts> exchangeAccount() {
        System.out.println("Enter account information the money will be changed FROM");
        Accounts accountFrom = selectAccount();
        if (accountFrom == null) return null;
        System.out.println("Enter currency of your another account the money will be changed TO");
        Accounts accountTo = selectCurrencyAccount(accountFrom);
        boolean sameCurrencyType = accountFrom.getCurrency().equals(accountTo.getCurrency());
        if (sameCurrencyType) {
            System.out.println("Client accounts FROM and TO must to have different currency. Try again...");
            return null;
        }
        Double curBalanceFrom = accountFrom.getBalance();
        Double curBalanceTo = accountTo.getBalance();
        Double transferMoney = inputMoney(accountTo);
        return doTransfer(accountFrom, accountTo, curBalanceFrom, curBalanceTo, transferMoney, sameCurrencyType);
    }

    private static List<Accounts> doTransfer(Accounts accountFrom, Accounts accountTo, Double curBalanceFrom,
                                             Double curBalanceTo, Double transferMoney, boolean sameCurrencyType) {
        Double newBalanceTo = curBalanceTo + transferMoney;
        Double transferMoneyFrom;
        if (sameCurrencyType) transferMoneyFrom = transferMoney;
        else transferMoneyFrom = transferMoney * accountTo.getCurrencyId().getNbuRate() / accountFrom.getCurrencyId().getNbuRate();
        Double newBalanceFrom = curBalanceFrom - transferMoneyFrom;
        if (newBalanceFrom < 0) {
            System.out.println("There is not enough money in the account");
            return null;
        }
        em.getTransaction().begin();
        try {
            accountFrom.setBalance(newBalanceFrom);
            accountTo.setBalance(newBalanceTo);
            Transactions transactionFrom = createTransaction(-transferMoneyFrom, newBalanceFrom);
            Transactions transactionTo = createTransaction(transferMoney, newBalanceTo);
            accountFrom.addTransaction(transactionFrom);
            accountFrom.addTransactionRelated(transactionTo);
            accountTo.addTransaction(transactionTo);
            accountTo.addTransactionRelated(transactionFrom);
            em.persist(transactionFrom);
            em.persist(transactionTo);
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
        return Arrays.asList(accountFrom, accountTo);
    }

    public static Double sumBalance() {
        Values input = inputClient(false);
        TypedQuery<Accounts> query = em.createNamedQuery("selectAccountByClient", Accounts.class);
        query.setParameter("name", input.getName());
        query.setParameter("lastName", input.getLastName());
        try {
            List<Accounts> accounts = query.getResultList();
            Double sum = 0d;
            for (Accounts account : accounts) {
                System.out.println(account.getAccount() + " " + account.getCurrency() + " " + account.getBalance());
                sum += account.getBalance() * account.getCurrencyId().getNbuRate();
            }
            return sum;
        } catch (NoResultException ex) {
            System.out.println("Client not found!");
            return null;
        }
    }

    private static Values inputClient(boolean inputCurrency) {
        String currencyType = null;
        System.out.print("Enter first name: ");
        String name = sc.nextLine().strip();
        System.out.print("Enter last name:  ");
        String lastName = sc.nextLine().strip();
        if (inputCurrency) currencyType = inputCurrencyType();
        return new Values(name, lastName, currencyType);
    }

    private static Values inputAccount(boolean inputCurrency) {
        String currencyType = null;
        System.out.print("Enter account name: ");
        String account = sc.nextLine().strip();
        if (inputCurrency) currencyType = inputCurrencyType();
        return new Values(account, currencyType);
    }

    private static String inputCurrencyType() {
        while (true) {
            System.out.print("Enter currency type for your account:\n\t1 - UAH, 2 - USD, 3 - EUR -> ");
            String input = sc.nextLine().strip();
            switch (input) {
                case "1" -> {return "UAH";}
                case "2" -> {return "USD";}
                case "3" -> {return "EUR";}
                default -> System.out.println("\nInput format is incorrect. Try again...");
            }
        }
    }

    private static Accounts selectAccount() {
        TypedQuery<Accounts> query;
        if (selectMethodForOperation()) {
            Values input = inputClient(true);
            query = em.createNamedQuery("verifyAccountByClient", Accounts.class);
            query.setParameter("name", input.getName());
            query.setParameter("lastName", input.getLastName());
            query.setParameter("currency", CurrencyLit.valueOf(input.getCurrencyType()));
        } else {
            Values input = inputAccount(true);
            query = em.createNamedQuery("verifyAccountByAccount", Accounts.class);
            query.setParameter("account", input.getAccount());
            query.setParameter("currency", CurrencyLit.valueOf(input.getCurrencyType()));
        }
        return queryAccount(query);
    }

    private static Accounts selectCurrencyAccount(Accounts account) {
        String currencyTo = inputCurrencyType();
        TypedQuery<Accounts> query = em.createNamedQuery("verifyAccountByAccount", Accounts.class);
        query.setParameter("account", account.getAccount());
        query.setParameter("currency", CurrencyLit.valueOf(currencyTo));
        return queryAccount(query);
    }

    private static Accounts queryAccount(TypedQuery<Accounts> query) {
        try {
            Accounts account = query.getSingleResult();
            return account;
        } catch (NoResultException ex) {
            System.out.println("Account not found!");
            return null;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return null;
        }
    }

    private static Double inputMoney(Accounts account) {
        double deposit;
        while (true) {
            System.out.println("Enter amount of money for you operation (deposit - positive value, withdrawal - negative value) in "
                    + account.getCurrency() + ": ");
            System.out.print(" -> ");
            String sDeposit = sc.nextLine();
            try {
                deposit = Double.parseDouble(sDeposit);
                if (deposit != 0) return deposit;
                else System.out.println("\nYou can not to input 0 value. Try again...");
            } catch (NumberFormatException nfex) {
                System.out.println("\nInput format is incorrect. Try again...");
            }
        }
    }

    private static boolean selectMethodForOperation() {
        while (true) {
            System.out.print("You want to do operation by using:\n\t1 - client name, 2 - account name -> ");
            String input = sc.nextLine().strip();
            switch (input) {
                case "1" -> {return true;}
                case "2" -> {return false;}
                default -> System.out.println("\nInput format is incorrect. Try again...");
            }
        }
    }

    private static Transactions createTransaction(Double deposit, Double newBalance) {
        String description = "Deposit to account";
        double withdrawal = 0d;
        if (deposit < 0) {
            withdrawal = -deposit;
            deposit = 0d;
            description = "Withdrawal from account";
        }
        return new Transactions(new Date(), description, withdrawal, deposit, newBalance);
    }
}
