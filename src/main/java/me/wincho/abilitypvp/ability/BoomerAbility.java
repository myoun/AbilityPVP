package me.wincho.abilitypvp.ability;

import me.wincho.abilitypvp.AbilityPVP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class BoomerAbility extends Ability {
    public BoomerAbility() {
    }

    public BoomerAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Boomer";
    }

    @Override
    public void init() {
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
        chest.editMeta(itemMeta -> {
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        });

        player.getInventory().setChestplate(chest);
        player.getInventory().setItem(0, new ItemStack(Material.TNT, 1));
    }

    @Override
    public void reset() {
        player.getInventory().clear();
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.TNT)) {
                if (event.getPlayer().getCooldown(Material.TNT) == 0) {
                    Block block = event.getClickedBlock();
                    Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
                        block.getLocation().createExplosion(5);
                    }, 60);
                    event.getPlayer().setCooldown(Material.TNT, 5);
                    event.setCancelled(true);
                }
            }
        }
    }
}
