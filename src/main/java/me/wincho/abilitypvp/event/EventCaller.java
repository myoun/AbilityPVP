package me.wincho.abilitypvp.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EventCaller implements Listener {
    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player who) {
            if (event.getEntity() instanceof Player damaged) {
                Bukkit.getPluginManager().callEvent(new PlayerAttackPlayerEvent(who, damaged));
            }
        }
    }
}
