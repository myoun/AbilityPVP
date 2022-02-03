package me.wincho.abilitypvp.ability;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Ability implements Listener {
    public static final Map<String, Class<? extends Ability>> registeredAbility = new HashMap<>();
    public static final Map<UUID, Ability> abilityMap = new HashMap<>();

    protected Player player;

    public Ability(Player player) {
        this.player = player;
    }

    public Ability() {
    }

    public static void setAbility(Player player, Class<? extends Ability> ability) {
        try {
            abilityMap.put(player.getUniqueId(), ability.getConstructor(Player.class).newInstance(player));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void registerAbility(Class<? extends Ability> ability, Plugin plugin) {
        try {
            registeredAbility.put(ability.getConstructor().newInstance().name(), ability);
            Bukkit.getPluginManager().registerEvents(ability.getConstructor().newInstance(), plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Class<? extends Ability> fromName(String name) {
        return registeredAbility.get(name);
    }

    public static void initAbility(Player player) {
        player.setFoodLevel(20);
        player.getInventory().clear();

        abilityMap.get(player.getUniqueId()).init();
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }

    public abstract String name();

    public abstract void init();

    public abstract void reset();
}
