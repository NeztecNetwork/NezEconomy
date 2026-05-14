package com.neztec.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import com.neztec.NezEconomyPlugin;
import com.neztec.api.events.MoneyChangeEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger implements Listener {

    private final PrintWriter writer;

    public TransactionLogger(NezEconomyPlugin plugin) {
        try {
            writer = new PrintWriter(new FileWriter(
                    plugin.getDataFolder() + "/transactions.log", true), true);
        } catch (IOException e) {
            throw new RuntimeException("Could not open transaction log", e);
        }
    }

    @EventHandler
    public void onMoneyChange(MoneyChangeEvent event) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String playerName = Server.getInstance().getOfflinePlayer(event.getPlayer()).getName();
        double diff = event.getNewBalance() - event.getOldBalance();
        String logLine = String.format("[%s] %s: %.2f -> %.2f (%+.2f)%n",
                time, playerName, event.getOldBalance(), event.getNewBalance(), diff);
        writer.print(logLine);
        writer.flush();
    }

    public void close() {
        if (writer != null) writer.close();
    }
}