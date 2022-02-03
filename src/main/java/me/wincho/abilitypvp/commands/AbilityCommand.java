package me.wincho.abilitypvp.commands;

import me.wincho.abilitypvp.ability.Ability;
import me.wincho.abilitypvp.ability.BoomerAbility;
import me.wincho.abilitypvp.ability.WinChoAbility;
import me.wincho.abilitypvp.utils.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbilityCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equals("ability")) {
            if (args.length <= 1) return false;
            if (args[0].equals("set")) {
                if (args.length < 2) return false;
                Player player = null;
                if (sender instanceof Player) player = (Player) sender;
                Class<? extends Ability> ability = Ability.fromName(args[1]);
                if (args.length >= 3) {
                    if (sender.isOp()) {
                        player = Bukkit.getPlayer(args[2]);
                    } else {
                        sender.sendMessage("You are not OP");
                    }
                }
                if (player == null) {
                    sender.sendMessage("Player not found.");
                    return false;
                }
                if (ability == null) {
                    sender.sendMessage("Ability not found.");
                    return false;
                }
                if (ability.equals(WinChoAbility.class)) {
                    if (!sender.getName().equals("wincho_")) {
                        player.sendMessage("WinCho is banned.");
                        return true;
                    }
                }

                if (Ability.abilityMap.get(player.getUniqueId()) == null) {
                    Ability.setAbility(player, ability);
                    GameMap.sendToWaitZone(player);
                } else {
                    if (!(args.length >= 4 && args[3].equals("true"))) {
                        sender.sendMessage(ChatColor.RED + player.getName() + " is already set for ability.");
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (alias.equals("ability")) {
            if (args.length == 1) {
                tabComplete.add("set");
            } else if (args.length == 2) {
                Ability.registeredAbility.forEach((s, ability) -> tabComplete.add(s));
            } else if (args.length == 3) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    tabComplete.add(onlinePlayer.getName());
                }
            }
        }
        return tabComplete;
    }
}
