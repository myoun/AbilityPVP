package me.wincho.abilitypvp.ability;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ArcherAbility extends Ability {
    private static final int ARROW_STACK_MAX = 5;
    private int attacked = 0;

    public ArcherAbility() {
    }

    public ArcherAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Archer";
    }

    @Override
    public void init() {
        ItemStack bow = new ItemStack(Material.BOW);
        bow.editMeta(itemMeta -> {
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        });
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);

        player.getInventory().setItem(0, bow);
        player.getInventory().setItem(1, new ItemStack(Material.ARROW));
    }

    @Override
    public void reset() {
        player.getInventory().clear();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100000);
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType().equals(EntityType.ARROW)) {
            if (event.getEntity().getShooter() instanceof Player player) {
                if (Ability.abilityMap.get(player.getUniqueId()) instanceof ArcherAbility ability) {
                    if (event.getHitEntity() != null) {
                        ability.attacked++;
                    } else {
                        ability.attacked = 0;
                    }
                    player.sendActionBar(Component.text(ability.attacked + " / " + ARROW_STACK_MAX));
                }
            }
        }
    }

    @EventHandler
    public void projectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            if (event.getEntity().getShooter() instanceof Player player) {
                if (Ability.abilityMap.get(player.getUniqueId()) instanceof ArcherAbility ability) {
                    if (ability.attacked >= ARROW_STACK_MAX) {
                        ability.attacked = 0;
                        arrow.setDamage(5);
                    } else {
                        arrow.setDamage(0.5);
                    }
                }
            }
        }
    }
}
