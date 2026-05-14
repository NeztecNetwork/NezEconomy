package com.neztec.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.neztec.NezEconomyPlugin;
import com.neztec.api.EconomyAPI;
import com.neztec.api.EconomyProvider;
import com.neztec.storage.StorageProvider;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaltopCommand extends Command {
    public BaltopCommand() {
        super("baltop", "Show the richest players", "/baltop [page]");
        setAliases(new String[]{"topmoney", "balancetop"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        int page = 1;
        int perPage = 10;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid page number.");
                return true;
            }
        }

        EconomyProvider eco = EconomyAPI.getInstance();
        StorageProvider storage = NezEconomyPlugin.getInstance().getStorage();
        if (storage == null) {
            sender.sendMessage("§cEconomy storage not available.");
            return true;
        }

        List<Map.Entry<UUID, Double>> top = storage.getTopBalances(page * perPage);
        if (top.isEmpty()) {
            sender.sendMessage("§7No balances found.");
            return true;
        }

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, top.size());
        if (start >= top.size()) {
            sender.sendMessage("§cPage " + page + " does not exist.");
            return true;
        }

        sender.sendMessage("§6--- Top Balances (Page " + page + ") ---");
        for (int i = start; i < end; i++) {
            Map.Entry<UUID, Double> entry = top.get(i);
            String name = sender.getServer().getOfflinePlayer(entry.getKey()).getName();
            sender.sendMessage("§e" + (i + 1) + ". §f" + name + " §7- " + eco.format(entry.getValue()));
        }
        return true;
    }
}