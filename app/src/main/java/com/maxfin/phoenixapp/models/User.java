package com.maxfin.phoenixapp.models;

public class User {

    private String mJId;
    private String mJPassword;
    private String mNumber;
    private String mBalance;
    private boolean mTariff;

    public User(String JId, String JPassword, String number, String balance, boolean tariff) {
        mJId = JId;
        mJPassword = JPassword;
        mNumber = number;
        mBalance = balance;
        mTariff = tariff;
    }


    public String getJId() {
        return mJId;
    }

    public void setJId(String JId) {
        mJId = JId;
    }

    public String getJPassword() {
        return mJPassword;
    }

    public void setJPassword(String JPassword) {
        mJPassword = JPassword;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getBalance() {
        return mBalance;
    }

    public void setBalance(String balance) {
        mBalance = balance;
    }

    public boolean isTariff() {
        return mTariff;
    }

    public void setTariff(boolean tariff) {
        mTariff = tariff;
    }
}
