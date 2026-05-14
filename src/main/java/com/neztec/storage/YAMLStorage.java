package com.neztec.storage;

import cn.nukkit.utils.Config;
import com.neztec.NezEconomyPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class YAMLStorage implements StorageProvider {

    private final File playersFolder;

    public YAMLStorage(NezEconomyPlugin plugin) {
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        if (!playersFolder.exists()) playersFolder.mkdirs();
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playersFolder, uuid.toString() + ".yml");
    }

    @Override
    public double loadBalance(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (!file.exists()) return 0.0;
        Config config = new Config(file, Config.YAML);
        return config.getDouble("balance", 0.0);
    }

    @Override
    public void saveBalance(UUID uuid, double balance) {
        File file = getPlayerFile(uuid);
        Config config = new Config(file, Config.YAML);
        config.set("balance", balance);
        config.save();
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return getPlayerFile(uuid).exists();
    }

    @Override
    public void createAccount(UUID uuid, double initialBalance) {
        saveBalance(uuid, initialBalance);
    }

    @Override
    public Map<UUID, Double> loadAllBalances() {
        Map<UUID, Double> balances = new HashMap<>();
        File[] files = playersFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return balances;
        for (File file : files) {
            String uuidStr = file.getName().replace(".yml", "");
            try {
                UUID uuid = UUID.fromString(uuidStr);
                Config config = new Config(file, Config.YAML);
                balances.put(uuid, config.getDouble("balance", 0.0));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return balances;
    }

    @Override
    public List<Map.Entry<UUID, Double>> getTopBalances(int limit) {
        return loadAllBalances().entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll() {
    }
}