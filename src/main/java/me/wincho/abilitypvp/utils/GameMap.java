package me.wincho.abilitypvp.utils;

import me.wincho.abilitypvp.AbilityPVP;
import me.wincho.abilitypvp.ability.Ability;
import me.wincho.abilitypvp.game.Game;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameMap implements Listener {
    public static final List<UUID> waitingPlayers = new ArrayList<>();

    private static final World waitingRoom = new WorldCreator("waiting").generator(new ChunkGenerator() {
        @Override
        public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (chunkX == 0 && chunkZ == 0) {
                        chunkData.setBlock(chunkX + x, 64, chunkZ + z, Material.STONE);
                    }
                }
            }
        }
    }).createWorld();

    static {
        waitingRoom.getWorldBorder().setCenter(8, 8);
        waitingRoom.getWorldBorder().setSize(16);
        waitingRoom.getWorldBorder().setWarningDistance(0);
    }

    public static void sendToWaitZone(Player player) {
        player.getInventory().clear();
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setGameMode(GameMode.ADVENTURE);
        waitingPlayers.add(player.getUniqueId());
        player.teleport(new Location(waitingRoom, 8, 64 + 3, 8));
        player.sendActionBar(Component.text("Ability: " + Ability.abilityMap.get(player.getUniqueId()).name()));
        ItemStack leave = new ItemStack(Material.ENDER_PEARL);
        leave.editMeta(itemMeta -> {
            itemMeta.displayName(Component.text("Going back to the lobby."));
        });
        player.getInventory().setItem(8, leave);

        player.getInventory().setHeldItemSlot(0);

        if (waitingPlayers.size() >= 2) {
            for (UUID waitingPlayer : waitingPlayers) {
                Bukkit.getPlayer(waitingPlayer).sendMessage(Component.text("The game starts in 0 seconds."));
            }
            new Game(waitingPlayers);
            waitingPlayers.clear();
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        waitingPlayers.remove(event.getPlayer().getUniqueId());
        for (UUID waitingPlayer : waitingPlayers) {
            Bukkit.getPlayer(waitingPlayer).sendMessage(Component.text(event.getPlayer().getName() + " left the server."));
        }
    }

    public enum MapType {
        SEA(Paths.get("sea-world"), 14.5f, 3.0f, -10.5f, -11.5f, 3.0f, 11.5f),
        PLAINS(Paths.get("plains-world"), 14.5f, 3.0f, -10.5f, -11.5f, 3.0f, 11.5f);

        public final Path worldPath;
        private final float blueX, blueY, blueZ;
        private final float redX, redY, redZ;

        MapType(Path worldPath, float blueX, float blueY, float blueZ, float redX, float redY, float redZ) {
            this.worldPath = worldPath;
            this.blueX = blueX;
            this.blueY = blueY;
            this.blueZ = blueZ;
            this.redX = redX;
            this.redY = redY;
            this.redZ = redZ;
        }

        public Location generateBlueSpawn(World world) {
            return new Location(world, blueX, blueY, blueZ);
        }

        public Location generateRedSpawn(World world) {
            return new Location(world, redX, redY, redZ);
        }
    }
}
