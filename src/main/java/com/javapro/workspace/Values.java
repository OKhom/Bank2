package com.javapro.workspace;

public class Values {
    String name;
    String lastName;
    String account;
    String currencyType;

    Values(String name, String lastName, String currencyType) {
        this.name = name;
        this.lastName = lastName;
        this.currencyType = currencyType;
    }

    Values(String account, String currencyType) {
        this.account = account;
        this.currencyType = currencyType;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }
}
