package com.neztec.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

import com.neztec.api.EconomyAPI;
import com.neztec.api.EconomyProvider;

public class AdminEconomyCommand extends Command {

    public AdminEconomyCommand() {

        super("nezeconomy", "Admin economy management", "/nezeconomy <set|add|remove> <player> <amount>");

        setPermission("nezeconomy.admin");

        addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newEnum("adminAction", new String[]{"set", "add", "remove"}),
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("amount", CommandParamType.FLOAT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(getUsage());
            return false;
        }
        String action = args[0].toLowerCase();
        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cAmount must be a non‑negative number.");
            return true;
        }
        EconomyProvider eco = EconomyAPI.getInstance();
        boolean success = switch (action) {
            case "set" -> eco.setMoney(target, amount);
            case "add" -> eco.addMoney(target, amount);
            case "remove" -> eco.subtractMoney(target, amount);
            default -> {
                sender.sendMessage("§cUnknown action. Use set, add, or remove.");
                yield false;
            }
        };
        if (success) {
            sender.sendMessage("§a" + action.substring(0, 1).toUpperCase() + action.substring(1) +
                    " balance for " + target.getName() + " to " + eco.format(eco.getMoney(target)));
        } else {
            sender.sendMessage("§cFailed to modify balance.");
        }
        return true;
    }
}