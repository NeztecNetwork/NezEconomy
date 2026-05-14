package com.neztec.storage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StorageProvider {
    double loadBalance(UUID uuid);

    void saveBalance(UUID uuid, double balance);

    boolean hasAccount(UUID uuid);

    void createAccount(UUID uuid, double initialBalance);

    Map<UUID, Double> loadAllBalances();

    List<Map.Entry<UUID, Double>> getTopBalances(int limit);

    void saveAll();
}