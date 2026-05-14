package com.neztec.storage;

import com.neztec.NezEconomyPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

public class SQLiteStorage implements StorageProvider {

    private final HikariDataSource dataSource;

    public SQLiteStorage(NezEconomyPlugin plugin) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/economy.db");
        config.setMaximumPoolSize(5);
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("busy_timeout", "5000");
        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS nezeconomy_accounts (" +
                    "uuid TEXT PRIMARY KEY, balance REAL NOT NULL)");
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to initialize SQLite database", e);
        }
    }

    @Override
    public double loadBalance(UUID uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT balance FROM nezeconomy_accounts WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException ignored) {
        }
        return 0.0;
    }

    @Override
    public void saveBalance(UUID uuid, double balance) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO nezeconomy_accounts (uuid, balance) VALUES (?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, balance);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM nezeconomy_accounts WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void createAccount(UUID uuid, double initialBalance) {
        saveBalance(uuid, initialBalance);
    }

    @Override
    public Map<UUID, Double> loadAllBalances() {
        Map<UUID, Double> map = new HashMap<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT uuid, balance FROM nezeconomy_accounts")) {
            while (rs.next()) {
                map.put(UUID.fromString(rs.getString("uuid")), rs.getDouble("balance"));
            }
        } catch (SQLException ignored) {
        }
        return map;
    }

    @Override
    public List<Map.Entry<UUID, Double>> getTopBalances(int limit) {
        List<Map.Entry<UUID, Double>> list = new ArrayList<>();
        String sql = "SELECT uuid, balance FROM nezeconomy_accounts ORDER BY balance DESC LIMIT ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new AbstractMap.SimpleEntry<>(
                        UUID.fromString(rs.getString("uuid")), rs.getDouble("balance")));
            }
        } catch (SQLException ignored) {
        }
        return list;
    }

    @Override
    public void saveAll() {
    }

    public void close() {
        if (dataSource != null) dataSource.close();
    }
}