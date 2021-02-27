package com.javapro.entity;

import com.javapro.workspace.enums.CurrencyLit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Accounts")
@NamedQueries({
        @NamedQuery(name = "verifyAccountByClient", query = "SELECT c FROM com.javapro.entity.Accounts c " +
                "WHERE c.client.name = :name AND c.client.lastName = :lastName AND c.currency = :currency"),
        @NamedQuery(name = "verifyAccountByAccount", query = "SELECT c FROM com.javapro.entity.Accounts c " +
                "WHERE c.account = :account AND c.currency = :currency"),
        @NamedQuery(name = "selectAccountByClient", query = "SELECT a FROM com.javapro.entity.Accounts a " +
                "WHERE a.client.name = :name AND a.client.lastName = :lastName")})
public class Accounts {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "account", nullable = false)
    private String account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyLit currency;

    private Double balance;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Clients client;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currencyId;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transactions> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "accountRelated", cascade = CascadeType.ALL)
    private List<Transactions> transactionsRelated = new ArrayList<>();

    public Accounts() {}

    public Accounts(String account, CurrencyLit currency, Double balance) {
        this.account = account;
        this.currency = currency;
        this.balance = balance;
    }

    public void addTransaction(Transactions transaction) {
        if ( ! transactions.contains(transaction)) {
            transactions.add(transaction);
            transaction.setAccount(this);
        }
    }

    public Transactions getTransaction(int index) {
        return transactions.get(index);
    }

    public void clearTransactions() {
        transactions.clear();
    }

    public void addTransactionRelated(Transactions transactionRelated) {
        if ( ! transactionsRelated.contains(transactionRelated)) {
            transactionsRelated.add(transactionRelated);
            transactionRelated.setAccountRelated(this);
        }
    }

    public Transactions getTransactionRelated(int index) {
        return transactionsRelated.get(index);
    }

    public void clearTransactionsRelated() {
        transactionsRelated.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public CurrencyLit getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyLit currency) {
        this.currency = currency;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Clients getClient() {
        return client;
    }

    public void setClient(Clients client) {
        this.client = client;
    }

    public Currency getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Currency currencyId) {
        this.currencyId = currencyId;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }

    public List<Transactions> getTransactionsRelated() {
        return transactionsRelated;
    }

    public void setTransactionsRelated(List<Transactions> transactionsRelated) {
        this.transactionsRelated = transactionsRelated;
    }

    @Override
    public String toString() {
        return "Accounts{" +
                "account='" + account + '\'' +
                ", currency=" + currency +
                ", balance=" + balance +
                '}';
    }
}
