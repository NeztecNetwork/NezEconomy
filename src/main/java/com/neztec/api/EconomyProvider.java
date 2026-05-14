package com.neztec.api;

import cn.nukkit.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EconomyProvider {

    // --- Balance queries ---
    double getMoney(Player player);

    double getMoney(UUID uuid);

    CompletableFuture<Double> getMoneyAsync(UUID uuid);

    // --- Account checks ---
    boolean hasAccount(Player player);

    boolean hasAccount(UUID uuid);

    // --- Balance mutations ---
    boolean setMoney(Player player, double amount);

    boolean setMoney(UUID uuid, double amount);

    boolean addMoney(Player player, double amount);

    boolean addMoney(UUID uuid, double amount);

    boolean subtractMoney(Player player, double amount);

    boolean subtractMoney(UUID uuid, double amount);

    // --- Transfers ---
    boolean transferMoney(Player from, Player to, double amount);

    boolean transferMoney(UUID from, UUID to, double amount);

    // --- Formatting ---
    String getCurrencyName();

    String format(double amount);

    String formatShort(double amount);
}