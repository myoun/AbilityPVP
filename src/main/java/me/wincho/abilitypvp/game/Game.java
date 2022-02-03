package me.wincho.abilitypvp.game;

import me.wincho.abilitypvp.AbilityPVP;
import me.wincho.abilitypvp.ability.Ability;
import me.wincho.abilitypvp.utils.GameMap;
import me.wincho.abilitypvp.utils.IOUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Game implements Listener {
    public static final List<Game> games = new ArrayList<>();

    public final List<UUID> players = new ArrayList<>();
    public final List<UUID> spectators = new ArrayList<>();
    private final List<UUID> deathPlayers = new ArrayList<>();
    private final World world;
    public UUID gameId;
    private boolean gameEnded = false;

    public Game(List<UUID> waitingPlayers) {
        players.addAll(waitingPlayers);

        games.add(this);

        Bukkit.getPluginManager().registerEvents(this, AbilityPVP.plugin);

        GameMap.MapType map = GameMap.MapType.PLAINS;
        Path worldPath = map.worldPath;
        gameId = UUID.randomUUID();
        try {
            IOUtils.copyDirectory(worldPath.toString(), gameId.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        WorldCreator creator = new WorldCreator(gameId.toString());
        world = creator.createWorld();
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        for (int i = 0; i < players.size(); i++) {
            Player player = Bukkit.getPlayer(players.get(i));
            assert player != null;

            TeamColor team = i % 2 == 0 ? TeamColor.RED : TeamColor.BLUE;
            if (team == TeamColor.RED) {
                player.teleport(map.generateRedSpawn(world));
                AbilityPVP.scoreboard.getTeam("RED").addPlayer(player);
            } else {
                player.teleport(map.generateBlueSpawn(world));
                AbilityPVP.scoreboard.getTeam("BLUE").addPlayer(player);
            }
            Ability.initAbility(player);
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if (gameEnded) return;

        Player player = event.getPlayer();
        if (players.contains(player.getUniqueId())) {
            deathPlayers.add(player.getUniqueId());

            event.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR);

            int blueCount = 0, redCount = 0;
            List<UUID> blue = new ArrayList<>(),
                    red = new ArrayList<>();
            for (UUID uuid : players) {
                Player check = Bukkit.getPlayer(uuid);
                check.sendMessage(player.getName() + " has died!");
                Team team = AbilityPVP.scoreboard.getEntityTeam(check);
                if (team.getName().equals("BLUE")) {
                    if (!deathPlayers.contains(uuid)) blueCount++;
                    blue.add(uuid);
                } else if (team.getName().equals("RED")) {
                    if (!deathPlayers.contains(uuid)) redCount++;
                    red.add(uuid);
                }
            }

            for (UUID uuid : getPlayers()) {
                Bukkit.getPlayer(uuid).sendActionBar(Component.text("RED : " + redCount + " / BLUE : " + blueCount));
            }

            if (blueCount == 0) {
                for (UUID uuid : red) {
                    sendVictoryMessage(Objects.requireNonNull(Bukkit.getPlayer(uuid)), TeamColor.RED);
                }
                for (UUID uuid : blue) {
                    sendDefeatMessage(Objects.requireNonNull(Bukkit.getPlayer(uuid)), TeamColor.RED);
                }
                endGame();
            } else if (redCount == 0) {
                for (UUID uuid : blue) {
                    sendVictoryMessage(Objects.requireNonNull(Bukkit.getPlayer(uuid)), TeamColor.BLUE);
                }
                for (UUID uuid : red) {
                    sendDefeatMessage(Objects.requireNonNull(Bukkit.getPlayer(uuid)), TeamColor.BLUE);
                }
                endGame();
            }
            AbilityPVP.scoreboard.getTeam("SPECTATOR").addPlayer(player);
        }
    }

    private void endGame() {
        gameEnded = true;

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.setGameMode(GameMode.SPECTATOR);
            Ability.abilityMap.get(player.getUniqueId()).reset();
        }

        Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
            for (UUID uuid : getPlayers())
                Bukkit.getPlayer(uuid).sendActionBar(Component.text("You will be moved to the lobby in 5 seconds."));
            Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
                for (UUID uuid : getPlayers())
                    Bukkit.getPlayer(uuid).sendActionBar(Component.text("You will be moved to the lobby in 4 seconds."));
                Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
                    for (UUID uuid : getPlayers())
                        Bukkit.getPlayer(uuid).sendActionBar(Component.text("You will be moved to the lobby in 3 seconds."));
                    Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
                        for (UUID uuid : getPlayers())
                            Bukkit.getPlayer(uuid).sendActionBar(Component.text("You will be moved to the lobby in 2 seconds."));
                        Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
                            for (UUID uuid : getPlayers())
                                Bukkit.getPlayer(uuid).sendActionBar(Component.text("You will be moved to the lobby in 1 seconds."));
                            Bukkit.getScheduler().runTaskLater(AbilityPVP.plugin, () -> {
                                for (UUID uuid : getPlayers()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    AbilityPVP.returnToLobby(player);
                                    player.setGameMode(GameMode.ADVENTURE);
                                    AbilityPVP.scoreboard.getEntityTeam(player).removePlayer(player);
                                    player.getInventory().clear();
                                    Ability.abilityMap.remove(player.getUniqueId());
                                }

                                Bukkit.unloadWorld(world, false);
                                IOUtils.deleteFolder(world.getWorldFolder());
                                games.remove(this);
                            }, 20);
                        }, 20);
                    }, 20);
                }, 20);
            }, 20);
        }, 20);
    }

    public List<UUID> getPlayers() {
        List<UUID> res = new ArrayList<>();
        res.addAll(players);
        res.addAll(spectators);
        return res;
    }

    private void sendDefeatMessage(Player player, TeamColor win) {
        player.setGameMode(GameMode.SPECTATOR);
        player.showTitle(Title.title(Component.text(ChatColor.RED + "Defeat.."), Component.text("The " + win + " team won.")));

        if (AbilityPVP.plugin.getConfig().contains("players." + player.getUniqueId())) {
            AbilityPVP.plugin.getConfig().set("players." + player.getUniqueId() + ".defeat", AbilityPVP.plugin.getConfig().getInt("players." + player.getUniqueId() + ".defeat") + 1);
        } else {
            AbilityPVP.plugin.getConfig().set("players." + player.getUniqueId() + ".defeat", 1);
        }
    }

    private void sendVictoryMessage(Player player, TeamColor win) {
        player.setGameMode(GameMode.SPECTATOR);
        player.showTitle(Title.title(Component.text(ChatColor.GOLD + "Victory!"), Component.text("The " + win + " team won.")));

        if (AbilityPVP.plugin.getConfig().contains("players." + player.getUniqueId())) {
            AbilityPVP.plugin.getConfig().set("players." + player.getUniqueId() + ".victory", AbilityPVP.plugin.getConfig().getInt("players." + player.getUniqueId() + ".victory") + 1);
        } else {
            AbilityPVP.plugin.getConfig().set("players." + player.getUniqueId() + ".victory", 1);
        }
    }

    public enum TeamColor {
        RED, BLUE;
    }
}
