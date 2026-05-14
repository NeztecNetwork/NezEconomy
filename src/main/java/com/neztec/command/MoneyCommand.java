package com.neztec.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.neztec.api.EconomyAPI;
import com.neztec.api.EconomyProvider;

public class MoneyCommand extends Command {
    public MoneyCommand() {
        super("money", "Check your balance", "/money");
        setAliases(new String[]{"bal", "balance"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }
        EconomyProvider eco = EconomyAPI.getInstance();
        double balance = eco.getMoney(player);
        player.sendMessage("§aYour balance: §e" + eco.format(balance));
        return true;
    }
}