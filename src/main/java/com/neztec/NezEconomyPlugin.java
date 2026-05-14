package com.neztec;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.neztec.api.EconomyAPI;
import com.neztec.command.*;
import com.neztec.config.Settings;
import com.neztec.listener.PlayerListener;
import com.neztec.listener.TransactionLogger;
import com.neztec.service.CurrencyFormatter;
import com.neztec.service.EconomyService;
import com.neztec.storage.MySQLStorage;
import com.neztec.storage.SQLiteStorage;
import com.neztec.storage.StorageProvider;
import com.neztec.storage.YAMLStorage;

public class NezEconomyPlugin extends PluginBase {

    private static NezEconomyPlugin instance;
    private EconomyService economyService;
    private Settings settings;
    private StorageProvider storage;

    public static NezEconomyPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig(); // copies config.yml from jar to plugin folder
        getLogger().info(TextFormat.WHITE + "NezEconomy loaded!");
    }

    @Override
    public void onEnable() {
        // 1. Load configuration
        this.settings = new Settings(this);

        // 2. Choose storage backend
        switch (settings.getStorageType().toLowerCase()) {
            case "sqlite" -> storage = new SQLiteStorage(this);
            case "mysql" -> storage = new MySQLStorage(this);
            case "yaml" -> storage = new YAMLStorage(this);
            default -> {
                getLogger().warning("Unknown storage type '" + settings.getStorageType() + "'. Falling back to YAML.");
                storage = new YAMLStorage(this);
            }
        }

        // 3. Currency formatter
        CurrencyFormatter formatter = new CurrencyFormatter(settings);

        // 4. Economy service (core)
        economyService = new EconomyService(storage, formatter, this);

        // 5. Expose the global API
        EconomyAPI.setInstance(economyService);

        // 6. Register commands
        getServer().getCommandMap().register("money", new MoneyCommand());
        getServer().getCommandMap().register("pay", new PayCommand());
        getServer().getCommandMap().register("nezeconomy", new AdminEconomyCommand());
        getServer().getCommandMap().register("baltop", new BaltopCommand());
        getServer().getCommandMap().register("nezeco", new ReloadCommand());

        // 7. Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        TransactionLogger transactionLogger = new TransactionLogger(this);
        getServer().getPluginManager().registerEvents(transactionLogger, this);

        getLogger().info(TextFormat.GREEN + "NezEconomy enabled!");
    }

    @Override
    public void onDisable() {
        if (economyService != null) {
            economyService.saveAll();
        }
        getLogger().info(TextFormat.RED + "NezEconomy disabled.");
    }

    // Used by BaltopCommand and ReloadCommand
    public StorageProvider getStorage() {
        return storage;
    }

    public Settings getSettings() {
        return settings;
    }

    public void reloadConfigAndFormatter() {
        settings.reload();
        economyService.setFormatter(new CurrencyFormatter(settings));
        getLogger().info("Configuration reloaded.");
    }
}