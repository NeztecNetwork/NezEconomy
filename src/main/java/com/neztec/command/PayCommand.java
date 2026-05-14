package com.neztec.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

import com.neztec.api.EconomyAPI;
import com.neztec.api.EconomyProvider;

public class PayCommand extends Command {

    public PayCommand() {
        super("pay", "Pay another player", "/pay <player> <amount>");

        addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newType("amount", false, CommandParamType.FLOAT)
        });
    
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can send money.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /pay <player> <amount>");
            return false;
        }
        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cAmount must be a positive number.");
            return true;
        }
        EconomyProvider eco = EconomyAPI.getInstance();
        if (eco.transferMoney(player, target, amount)) {
            player.sendMessage("§aYou sent " + eco.format(amount) + " to " + target.getName());
            target.sendMessage("§aYou received " + eco.format(amount) + " from " + player.getName());
        } else {
            player.sendMessage("§cTransfer failed. Check your balance.");
        }
        return true;
    }
}