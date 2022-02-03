package me.wincho.abilitypvp.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerAttackPlayerEvent extends PlayerEvent {
    private final Player damaged;
    private static final HandlerList handlers = new HandlerList();

    public PlayerAttackPlayerEvent(@NotNull Player who, Player damaged) {
        super(who);
        this.damaged = damaged;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getDamaged() {
        return damaged;
    }
}
