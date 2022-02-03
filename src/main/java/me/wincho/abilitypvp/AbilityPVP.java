package me.wincho.abilitypvp;

import me.wincho.abilitypvp.ability.Ability;
import me.wincho.abilitypvp.ability.ArcherAbility;
import me.wincho.abilitypvp.ability.BeastAbility;
import me.wincho.abilitypvp.ability.BoomerAbility;
import me.wincho.abilitypvp.ability.SoldierAbility;
import me.wincho.abilitypvp.ability.TankerAbility;
import me.wincho.abilitypvp.ability.ThiefAbility;
import me.wincho.abilitypvp.ability.WinChoAbility;
import me.wincho.abilitypvp.ability.RocketPuncherAbility;
import me.wincho.abilitypvp.commands.AbilityCommand;
import me.wincho.abilitypvp.commands.SpectateCommand;
import me.wincho.abilitypvp.event.EventCaller;
import me.wincho.abilitypvp.game.Game;
import me.wincho.abilitypvp.listener.PlayerListener;
import me.wincho.abilitypvp.utils.GameMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AbilityPVP extends JavaPlugin {
    public static JavaPlugin plugin;
    public static Scoreboard scoreboard;

    public static void returnToLobby(Player player) {
        player.sendMessage("Move to server 'lobby'.");

        player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 0.5, 0.5));
        player.getInventory().setHeldItemSlot(0);
    }

    public static int getVictory(UUID uniqueId) {
        if (plugin.getConfig().contains("players." + uniqueId + ".victory")) {
            return plugin.getConfig().getInt("players." + uniqueId + ".victory");
        } else {
            plugin.getConfig().set("players." + uniqueId + ".victory", 0);
            return getVictory(uniqueId);
        }
    }

    public static int getDefeat(UUID uniqueId) {
        if (plugin.getConfig().contains("players." + uniqueId + ".defeat")) {
            return plugin.getConfig().getInt("players." + uniqueId + ".defeat");
        } else {
            plugin.getConfig().set("players." + uniqueId + ".defeat", 0);
            return getVictory(uniqueId);
        }
    }

    private Team getTeam(String name) {
        return AbilityPVP.scoreboard.getTeam(name);
    }

    @Override
    public void onEnable() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        plugin = this;
        try {
            getConfig().load(new File(getDataFolder(), "data.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (getTeam("RED") == null)
            AbilityPVP.scoreboard.registerNewTeam("RED").color(NamedTextColor.RED);
        if (getTeam("BLUE") == null)
            AbilityPVP.scoreboard.registerNewTeam("BLUE").color(NamedTextColor.BLUE);
        if (getTeam("SPECTATOR") == null)
            AbilityPVP.scoreboard.registerNewTeam("SPECTATOR").color(NamedTextColor.GRAY);

        Ability.registerAbility(ArcherAbility.class, this);
        Ability.registerAbility(BeastAbility.class, this);
        Ability.registerAbility(BoomerAbility.class, this);
        Ability.registerAbility(SoldierAbility.class, this);
        Ability.registerAbility(TankerAbility.class, this);
        Ability.registerAbility(ThiefAbility.class, this);
        Ability.registerAbility(WinChoAbility.class, this);

        Ability.registerAbility(RocketPuncherAbility.class, this);

        Bukkit.getPluginManager().registerEvents(new GameMap(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getPluginManager().registerEvents(new EventCaller(), this);

        getCommand("ability").setExecutor(new AbilityCommand());
        getCommand("ability").setTabCompleter(new AbilityCommand());

        getCommand("spectate").setExecutor(new SpectateCommand());
        getCommand("spectate").setTabCompleter(new SpectateCommand());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = manager.getNewScoreboard();
                if (scoreboard.getObjective("scoreboard") == null) scoreboard.registerNewObjective("scoreboard", "dummy", Component.text("[ Ability PVP ]"));

                Objective objective = scoreboard.getObjective("scoreboard");
                objective.getScore("Name: " + player.getName()).setScore(4);
                objective.getScore("").setScore(3);
                objective.getScore("Victory: " + getVictory(player.getUniqueId())).setScore(2);
                objective.getScore("Defeat: " + getDefeat(player.getUniqueId())).setScore(1);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                player.setScoreboard(scoreboard);

                player.setFoodLevel(18);
            }
        }, 0, 0);
    }

    @Override
    public void onDisable() {
        for (Game game : Game.games) {
            for (UUID player : game.getPlayers()) {
                Player player1 = Bukkit.getPlayer(player);
                returnToLobby(player1);
                player1.getInventory().clear();
                player1.setGameMode(GameMode.ADVENTURE);
            }
        }
        for (UUID waitingPlayer : GameMap.waitingPlayers) {
            Player player1 = Bukkit.getPlayer(waitingPlayer);
            returnToLobby(player1);
            player1.getInventory().clear();
            player1.setGameMode(GameMode.ADVENTURE);
        }
        Bukkit.getServer().broadcast(Component.text("Reloading the server."));
        try {
            getConfig().save(new File(getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
