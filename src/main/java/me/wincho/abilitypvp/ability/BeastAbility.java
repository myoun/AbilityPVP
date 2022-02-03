package me.wincho.abilitypvp.ability;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeastAbility extends Ability {
    public BeastAbility() {
    }

    public BeastAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Beast";
    }

    @Override
    public void init() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false, false, false));

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);

        ItemStack stack = new ItemStack(Material.RABBIT_FOOT);
        stack.editMeta(itemMeta -> {
            itemMeta.displayName(Component.text("DASH!"));
        });
        player.getInventory().addItem(stack);
    }

    @Override
    public void reset() {
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);

        player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
    }

    @EventHandler
    public void playerJump(PlayerJumpEvent event) {
        if (Ability.abilityMap.get(event.getPlayer().getUniqueId()) instanceof BeastAbility) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (Ability.abilityMap.get(event.getPlayer().getUniqueId()) instanceof BeastAbility) {
                Player player = event.getPlayer();
                if (player.getInventory().getItemInMainHand().getType().equals(Material.RABBIT_FOOT)) {
                    if (player.getCooldown(Material.RABBIT_FOOT) == 0) {
                        player.setVelocity(player.getLocation().getDirection().multiply(5));
                        player.setCooldown(Material.RABBIT_FOOT, 240);
                    }
                }
            }
        }
    }
}
