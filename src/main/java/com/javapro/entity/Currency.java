package com.javapro.entity;

import com.javapro.workspace.enums.CurrencyLit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Currency")
@NamedQuery(name = "verifyCurrency", query = "SELECT c FROM com.javapro.entity.Currency c WHERE c.currency = :currency")
public class Currency {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyLit currency;

    @Column(name = "nbu_rate")
    private Double nbuRate;

    @OneToMany(mappedBy = "currencyId", cascade = CascadeType.ALL)
    private List<Accounts> accounts = new ArrayList<>();

    public Currency() {}

    public Currency(CurrencyLit currency, Double nbuRate) {
        this.currency = currency;
        this.nbuRate = nbuRate;
    }

    public void addAccount(Accounts account) {
        if ( ! accounts.contains(account)) {
            accounts.add(account);
            account.setCurrencyId(this);
        }
    }

    public Accounts getAccount(int index) {
        return accounts.get(index);
    }

    public void clearAccounts() {
        accounts.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyLit getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyLit currency) {
        this.currency = currency;
    }

    public Double getNbuRate() {
        return nbuRate;
    }

    public void setNbuRate(Double nbuRate) {
        this.nbuRate = nbuRate;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency=" + currency +
                ", nbuRate=" + nbuRate +
                '}';
    }
}
