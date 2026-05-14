package com.neztec.api.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

import java.util.UUID;

public class MoneyChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final UUID player;
    private final double oldBalance;
    private double newBalance;
    private boolean cancelled = false;

    public MoneyChangeEvent(UUID player, double oldBalance, double newBalance) {
        this.player = player;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UUID getPlayer() {
        return player;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}