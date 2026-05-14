package com.neztec.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import com.neztec.NezEconomyPlugin;
import com.neztec.api.EconomyAPI;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var eco = EconomyAPI.getInstance();
        if (!eco.hasAccount(player)) {
            double startingBalance = NezEconomyPlugin.getInstance()
                    .getSettings().getStartingBalance();
            eco.setMoney(player, startingBalance);
        }
    }
}