package me.wincho.abilitypvp.ability;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoldierAbility extends Ability {
    public SoldierAbility() {
    }

    public SoldierAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Soldier";
    }

    @Override
    public void init() {
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));

        player.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));

//        Horse horse = player.getWorld().spawn(player.getLocation(), Horse.class);
//        horse.setOwner(player);
//        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
//        horse.addPassenger(player);
//        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.15);
    }

    @Override
    public void reset() {
        player.getInventory().clear();
    }
}
