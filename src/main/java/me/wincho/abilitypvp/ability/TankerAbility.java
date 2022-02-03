package me.wincho.abilitypvp.ability;

import me.wincho.abilitypvp.event.PlayerAttackPlayerEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class TankerAbility extends Ability {
    public TankerAbility() {
    }

    public TankerAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Tanker";
    }

    @Override
    public void init() {
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        helmet.addEnchantment(Enchantment.THORNS, 1);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.THORNS, 1);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        leggings.addEnchantment(Enchantment.THORNS, 1);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        boots.addEnchantment(Enchantment.THORNS, 1);
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
    }

    @Override
    public void reset() {
        player.getInventory().clear();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
    }
}
