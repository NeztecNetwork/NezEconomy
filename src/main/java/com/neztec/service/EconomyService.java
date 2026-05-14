package com.neztec.service;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.neztec.NezEconomyPlugin;
import com.neztec.api.EconomyProvider;
import com.neztec.api.events.MoneyChangeEvent;
import com.neztec.storage.StorageProvider;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class EconomyService implements EconomyProvider {

    private final StorageProvider storage;
    private final Map<UUID, Double> cache = new ConcurrentHashMap<>();
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
    private CurrencyFormatter formatter;

    public EconomyService(StorageProvider storage, CurrencyFormatter formatter, NezEconomyPlugin plugin) {
        this.storage = storage;
        this.formatter = formatter;
        cache.putAll(storage.loadAllBalances());
    }

    public void setFormatter(CurrencyFormatter formatter) {
        this.formatter = formatter;
    }

    public StorageProvider getStorage() {
        return storage;
    }

    private ReentrantLock getLock(UUID uuid) {
        return locks.computeIfAbsent(uuid, k -> new ReentrantLock());
    }

    @Override
    public double getMoney(UUID uuid) {
        return cache.computeIfAbsent(uuid, storage::loadBalance);
    }

    @Override
    public double getMoney(Player player) {
        return getMoney(player.getUniqueId());
    }

    @Override
    public CompletableFuture<Double> getMoneyAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> getMoney(uuid));
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return cache.containsKey(uuid) || storage.hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(Player player) {
        return hasAccount(player.getUniqueId());
    }

    @Override
    public boolean setMoney(UUID uuid, double amount) {
        if (amount < 0) return false;
        ReentrantLock lock = getLock(uuid);
        lock.lock();
        try {
            double old = cache.getOrDefault(uuid, 0.0);
            if (old == amount) return true;

            MoneyChangeEvent event = new MoneyChangeEvent(uuid, old, amount);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) return false;

            double finalAmount = event.getNewBalance();
            cache.put(uuid, finalAmount);
            storage.saveBalance(uuid, finalAmount);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean setMoney(Player player, double amount) {
        return setMoney(player.getUniqueId(), amount);
    }

    @Override
    public boolean addMoney(UUID uuid, double amount) {
        if (amount <= 0) return false;
        ReentrantLock lock = getLock(uuid);
        lock.lock();
        try {
            double current = getMoney(uuid);
            return setMoney(uuid, current + amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addMoney(Player player, double amount) {
        return addMoney(player.getUniqueId(), amount);
    }

    @Override
    public boolean subtractMoney(UUID uuid, double amount) {
        if (amount <= 0) return false;
        ReentrantLock lock = getLock(uuid);
        lock.lock();
        try {
            double current = getMoney(uuid);
            if (current < amount) return false;
            return setMoney(uuid, current - amount);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean subtractMoney(Player player, double amount) {
        return subtractMoney(player.getUniqueId(), amount);
    }

    @Override
    public boolean transferMoney(UUID from, UUID to, double amount) {
        if (amount <= 0) return false;
        UUID first = from.compareTo(to) < 0 ? from : to;
        UUID second = first.equals(from) ? to : from;

        ReentrantLock lock1 = getLock(first);
        ReentrantLock lock2 = getLock(second);
        lock1.lock();
        lock2.lock();
        try {
            if (!subtractMoney(from, amount)) return false;
            if (!addMoney(to, amount)) {
                addMoney(from, amount); // rollback
                return false;
            }
            return true;
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }

    @Override
    public boolean transferMoney(Player from, Player to, double amount) {
        return transferMoney(from.getUniqueId(), to.getUniqueId(), amount);
    }

    @Override
    public String getCurrencyName() {
        return formatter.format(0).replace("0", "").trim();
    }

    @Override
    public String format(double amount) {
        return formatter.format(amount);
    }

    @Override
    public String formatShort(double amount) {
        return formatter.formatShort(amount);
    }

    public void saveAll() {
        storage.saveAll();
    }
}