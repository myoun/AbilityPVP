package me.wincho.abilitypvp.commands;

import me.wincho.abilitypvp.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectateCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equals("spectate")) {
            if (args.length >= 1) {
                if (sender instanceof Player player) {
                    boolean playing = false;
                    for (Game g : Game.games) {
                        if (g.players.contains(player.getUniqueId())) playing = true;
                    }
                    if (!playing) {
                        boolean playerNameMode = Bukkit.getPlayer(args[0]) != null;
                        String id = args[0];
                        Game game = null;
                        for (Game g : Game.games) {
                            if (playerNameMode) {
                                if (g.players.contains(Bukkit.getPlayer(id).getUniqueId())) game = g;
                            } else {
                                if (g.gameId.toString().equals(id)) game = g;
                            }
                        }
                        player.sendMessage("Move to server '" + game.gameId + "'.");
                        player.teleport(new Location(Bukkit.getWorld(game.gameId.toString()), 0, 5, 0));
                        player.setGameMode(GameMode.SPECTATOR);
//                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
                        game.spectators.add(player.getUniqueId());
                    } else {
                        player.sendMessage("You're playing a game.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (alias.equals("spectate")) {
            for (Game game : Game.games) {
                tabComplete.add(game.gameId.toString());
                for (UUID player : game.players) {
                    tabComplete.add(Bukkit.getPlayer(player).getName());
                }
            }
        }
        return tabComplete;
    }
}
