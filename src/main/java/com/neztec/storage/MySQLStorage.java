package com.neztec.storage;

import com.neztec.NezEconomyPlugin;
import com.neztec.config.Settings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

public class MySQLStorage implements StorageProvider {

    private final HikariDataSource dataSource;

    public MySQLStorage(NezEconomyPlugin plugin) {
        Settings settings = new Settings(plugin);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + settings.getMySQLHost() + ":" + settings.getMySQLPort() +
                "/" + settings.getMySQLDatabase() + "?useSSL=false&allowPublicKeyRetrieval=true");
        config.setUsername(settings.getMySQLUsername());
        config.setPassword(settings.getMySQLPassword());
        config.setMaximumPoolSize(settings.getMySQLPoolSize());
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS nezeconomy_accounts (" +
                    "uuid VARCHAR(36) PRIMARY KEY, balance DOUBLE NOT NULL)");
        } catch (SQLException e) {
            plugin.getLogger().error("Failed to initialize MySQL database", e);
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
                     "INSERT INTO nezeconomy_accounts (uuid, balance) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE balance = VALUES(balance)")) {
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT uuid, balance FROM nezeconomy_accounts ORDER BY balance DESC LIMIT ?")) {
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