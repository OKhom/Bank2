package com.javapro.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Transactions")
public class Transactions {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "transaction_date")
    private Date date;

    private String description;
    private Double withdrawal;
    private Double deposit;

    @Column(name = "end_balance")
    private Double endBalance;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Accounts account;

    @ManyToOne
    @JoinColumn(name = "account_rel_id")
    private Accounts accountRelated;

    public Transactions() {}

    public Transactions(Date date, String description, Double withdrawal, Double deposit, Double endBalance) {
        this.date = date;
        this.description = description;
        this.withdrawal = withdrawal;
        this.deposit = deposit;
        this.endBalance = endBalance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(Double withdrawal) {
        this.withdrawal = withdrawal;
    }

    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }

    public Double getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(Double endBalance) {
        this.endBalance = endBalance;
    }

    public Accounts getAccount() {
        return account;
    }

    public void setAccount(Accounts account) {
        this.account = account;
    }

    public Accounts getAccountRelated() {
        return accountRelated;
    }

    public void setAccountRelated(Accounts accountRelated) {
        this.accountRelated = accountRelated;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", withdrawal=" + withdrawal +
                ", deposit=" + deposit +
                ", endBalance=" + endBalance +
                '}';
    }
}
