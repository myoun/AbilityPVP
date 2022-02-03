package me.wincho.abilitypvp.listener;

import me.wincho.abilitypvp.AbilityPVP;
import me.wincho.abilitypvp.ability.Ability;
import me.wincho.abilitypvp.game.Game;
import me.wincho.abilitypvp.utils.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener implements Listener {
    public static final UUID ARCHER = UUID.fromString("949d3edd-7148-2ee2-b267-0d68f88fd2ba");
    public static final UUID BOOMER = UUID.fromString("99c6c0f1-d003-273a-819d-02df3377731f");
    public static final UUID SOLDIER = UUID.fromString("e0f511fb-ca90-2b5e-9bc0-e69ccbc7adaa");
    public static final UUID THIEF = UUID.fromString("6195a7d7-163f-287d-a9a2-ef4d6cdbdef8");
    public static final UUID BEAST = UUID.fromString("a1ca24a9-af4c-2e2b-8dcb-a382c719304a");
    public static final UUID TANKER = UUID.fromString("d7ea89bb-11a3-2ef5-8ac1-599accefdb0f");

    @EventHandler
    public void playerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clicked = event.getRightClicked();
        String name = null;
        if (clicked.getUniqueId().equals(ARCHER)) name = "Archer";
        else if (clicked.getUniqueId().equals(BOOMER)) name = "Boomer";
        else if (clicked.getUniqueId().equals(SOLDIER)) name = "Soldier";
        else if (clicked.getUniqueId().equals(THIEF)) name = "Thief";
        else if (clicked.getUniqueId().equals(BEAST)) name = "Beast";
        else if (clicked.getUniqueId().equals(TANKER)) name = "Tanker";

        if (name != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ability set " + name + " " + player.getName() + " true");
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType().equals(Material.ENDER_PEARL)) {
                if (GameMap.waitingPlayers.contains(event.getPlayer().getUniqueId())) {
                    if (Ability.abilityMap.containsKey(event.getPlayer().getUniqueId())) {
                        GameMap.waitingPlayers.remove(event.getPlayer().getUniqueId());
                        Ability.abilityMap.remove(event.getPlayer().getUniqueId());
                        AbilityPVP.returnToLobby(event.getPlayer());
                        event.setCancelled(true);
                        event.getPlayer().getInventory().clear();
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        AbilityPVP.returnToLobby(event.getPlayer());
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        for (Game game : Game.games) {
            if (game.players.contains(event.getPlayer().getUniqueId())) return;
        }
        event.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
            AbilityPVP.returnToLobby(event.getPlayer());
        }, 1);
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
