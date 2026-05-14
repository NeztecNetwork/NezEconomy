package com.neztec.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.neztec.NezEconomyPlugin;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("nezeco", "NezEconomy admin commands", "/nezeco reload");
        setPermission("nezeconomy.admin");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("§cUsage: /nezeco reload");
            return false;
        }
        NezEconomyPlugin.getInstance().reloadConfigAndFormatter();
        sender.sendMessage("§aNezEconomy configuration reloaded.");
        return true;
    }
}