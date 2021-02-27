package com.javapro.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Clients")
@NamedQuery(name = "verifyClient", query = "SELECT c FROM com.javapro.entity.Clients c WHERE c.name = :name AND c.lastName = :lastName")
public class Clients {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "first_name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Accounts> accounts = new ArrayList<>();

    public Clients() {}

    public Clients(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
    }

    public void addAccount(Accounts account) {
        if ( ! accounts.contains(account)) {
            accounts.add(account);
            account.setClient(this);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Accounts> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Accounts> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "Clients{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
