package com.neztec.config;

import cn.nukkit.utils.Config;
import com.neztec.NezEconomyPlugin;

public class Settings {
    private final Config config;

    public Settings(NezEconomyPlugin plugin) {
        this.config = plugin.getConfig();
    }

    public void reload() {
        config.reload();
    }

    public String getCurrencySymbol() {
        return config.getString("currency.symbol", "$");
    }

    public String getCurrencyFormat() {
        return config.getString("currency.format", "#,###.##");
    }

    public String getCurrencySuffix() {
        return config.getString("currency.suffix", "");
    }

    public char getThousandSeparator() {
        String s = config.getString("currency.thousand-separator", ",");
        return s.isEmpty() ? ',' : s.charAt(0);
    }

    public char getDecimalSeparator() {
        String s = config.getString("currency.decimal-separator", ".");
        return s.isEmpty() ? '.' : s.charAt(0);
    }

    public double getStartingBalance() {
        return config.getDouble("currency.starting-balance", 0.0);
    }

    public String getStorageType() {
        return config.getString("storage.type", "yaml");
    }

    public String getMySQLHost() {
        return config.getString("mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("mysql.database", "nezeconomy");
    }

    public String getMySQLUsername() {
        return config.getString("mysql.username", "root");
    }

    public String getMySQLPassword() {
        return config.getString("mysql.password", "");
    }

    public int getMySQLPoolSize() {
        return config.getInt("mysql.pool-size", 10);
    }
}