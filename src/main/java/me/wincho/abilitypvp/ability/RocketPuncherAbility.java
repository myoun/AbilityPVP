package me.wincho.abilitypvp.ability;

import me.wincho.abilitypvp.event.PlayerAttackPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RocketPuncherAbility extends Ability {
    public RocketPuncherAbility() {
    }

    public RocketPuncherAbility(Player player) {
        super(player);
    }

    @Override
    public String name() {
        return "RocketPuncher";
    }

    @Override
    public void init() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false, false));
    }

    @Override
    public void reset() {

    }

    @EventHandler
    public void playerAttackPlayerEvent(PlayerAttackPlayerEvent event) {
        if (Ability.abilityMap.get(event.getPlayer().getUniqueId()) instanceof RocketPuncherAbility) {
            Player player = event.getPlayer();
            Player damaged = event.getDamaged();
            damaged.setVelocity(player.getLocation().getDirection().multiply(4));
        }
    }
}
