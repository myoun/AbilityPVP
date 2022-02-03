package me.wincho.abilitypvp.ability;

import me.wincho.abilitypvp.event.PlayerAttackPlayerEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.units.qual.A;

import java.util.UUID;

public class ThiefAbility extends Ability {
    private int attackCount = 0;
    private UUID lastPlayer = null;

    public ThiefAbility() {
    }

    public ThiefAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Thief";
    }

    @Override
    public void init() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, false, false, false));
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(8);
    }

    @EventHandler
    public void playerAttackPlayer(PlayerAttackPlayerEvent event) {
        if (Ability.abilityMap.get(event.getPlayer().getUniqueId()) instanceof ThiefAbility) {
            Player damaged = event.getDamaged();
            damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3, false, false, false));
            damaged.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 2, false, false, false));
            damaged.showTitle(Title.title(Component.text("CAUGHT!"), Component.text("You were caught by a thief!")));

            if (lastPlayer != null && lastPlayer.equals(damaged.getUniqueId())) {
                attackCount++;
                if (attackCount > 5) {
                    attackCount = 0;
                    damaged.getWorld().dropItem(damaged.getLocation(), damaged.getInventory().getItemInMainHand());
                    damaged.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
            } else {
                lastPlayer = damaged.getUniqueId();
                attackCount = 0;
            }
        }
    }

    @Override
    public void reset() {
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
    }
}
