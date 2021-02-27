package com.javapro;

import com.javapro.entity.Accounts;
import com.javapro.entity.Clients;
import com.javapro.workspace.Actions;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main( String[] args ) {
        Scanner sc = new Scanner(System.in);
        try {
            EntityManagerFactory emf = Actions.initDB("Bank1", sc);
            try {
                while (true) {
                    System.out.println("\nSelect your action:");
                    System.out.println("\t1. add new client");
                    System.out.println("\t2. add new client account");
                    System.out.println("\t3. put deposit / get withdrawal for client account");
                    System.out.println("\t4. transfer money between accounts");
                    System.out.println("\t5. transfer money between client accounts (currency exchange)");
                    System.out.println("\t6. show summary balance of all client account (in UAH)");
                    System.out.println("\t0. exit");
                    System.out.print(" -> ");
                    String input = sc.nextLine();

                    switch (input) {
                        case "1" -> {
                            Clients client = Actions.addClient();
                            if (client == null) System.out.println("Transaction is not complete...");
                            else System.out.println("Client " + client.getLastName() + " " + client.getName() + " added");
                        }
                        case "2" -> {
                            Accounts account = Actions.addAccount();
                            if (account == null) System.out.println("Transaction is not complete...");
                            else System.out.println("Account " + account.getAccount() + " (" + account.getCurrency() + ") added");
                        }
                        case "3" -> {
                            Accounts account = Actions.putMoney();
                            if (account == null) System.out.println("Transaction is not complete...");
                            else System.out.println("Operation for account "
                                    + account.getAccount() + " (" + account.getCurrency() + ") complete");
                        }
                        case "4" -> {
                            List<Accounts> account = Actions.transferMoney();
                            if (account == null) System.out.println("Transaction is not complete...");
                            else System.out.println("Operation between accounts "
                                    + account.get(0).getAccount() + " and " + account.get(1).getAccount() + " complete");
                        }
                        case "5" -> {
                            List<Accounts> account = Actions.exchangeAccount();
                            if (account == null) System.out.println("Transaction is not complete...");
                            else System.out.println("Currency exchange for account " + account.get(0).getAccount()
                                    + " from " + account.get(0).getCurrency() + " to " + account.get(1).getCurrency() + " complete");
                        }
                        case "6" -> {
                            Double sum = Actions.sumBalance();
                            if (sum == null) System.out.println("Transaction is not complete...");
                            else System.out.println("Summary balance of all accounts is " + sum + " UAH");
                        }
                        case "0" -> {
                            return;
                        }
                        default -> System.out.println("Entered value is invalid. Try again...");
                    }
                }
            } finally {
                if (emf != null) emf.close();
            }
        } catch (PersistenceException pex) {
            pex.printStackTrace();
        }
    }
}
