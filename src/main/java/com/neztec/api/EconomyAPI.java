package com.neztec.api;

public class EconomyAPI {

    private static EconomyProvider instance;

    public static EconomyProvider getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NezEconomy not enabled yet!");
        }
        return instance;
    }

    public static void setInstance(EconomyProvider provider) {
        if (instance != null) {
            throw new IllegalStateException("EconomyAPI already initialized");
        }
        instance = provider;
    }
}