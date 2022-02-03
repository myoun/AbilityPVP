package me.wincho.abilitypvp.ability;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class WinChoAbility extends Ability {
    public WinChoAbility() {
    }

    public WinChoAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "WinCho";
    }

    @Override
    public void init() {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        stack.editMeta(itemMeta -> {
            SkullMeta meta = (SkullMeta) itemMeta;
            meta.setPlayerProfile(Bukkit.getOfflinePlayer(UUID.fromString("8ff851e0-59fb-4dd5-9be0-b55d44f8149f")).getPlayer().getPlayerProfile());
        });
        player.getInventory().setHelmet(stack);
    }

    @Override
    public void reset() {

    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (Ability.abilityMap.get(event.getEntity().getUniqueId()) instanceof WinChoAbility) {
            if (event.getDamager() instanceof Player player) {
                player.damage(event.getDamage() * 2, event.getEntity());
            }
        }
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() != null) {
            if (Ability.abilityMap.get(event.getHitEntity().getUniqueId()) instanceof WinChoAbility) {
                if (event.getEntity().getShooter() instanceof Player player) {
                    player.damage(event.getHitEntity().getLastDamageCause().getDamage() * 2, event.getEntity());
                }
            }
        }
    }
}
