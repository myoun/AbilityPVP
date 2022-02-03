package me.wincho.abilitypvp.ability;

import me.wincho.abilitypvp.AbilityPVP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NinjaAbility extends Ability {

    public NinjaAbility() {
    }

    private int taskId;

    public NinjaAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "Ninja";
    }

    @Override
    public void init() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(AbilityPVP.plugin, () -> {
            if (player.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3, 1, true, true, true));
            } else {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }, 0, 5);
    }

    @Override
    public void reset() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
