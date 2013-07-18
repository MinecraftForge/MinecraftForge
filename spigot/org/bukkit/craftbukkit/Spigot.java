package org.bukkit.craftbukkit;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.spigotmc.Metrics;
import org.spigotmc.RestartCommand;
import org.spigotmc.WatchdogThread;
// MCPC+ start
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
// MCPC+ end

public class Spigot {

    static net.minecraft.util.AxisAlignedBB maxBB = net.minecraft.util.AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    static net.minecraft.util.AxisAlignedBB miscBB = net.minecraft.util.AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    static net.minecraft.util.AxisAlignedBB animalBB = net.minecraft.util.AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    static net.minecraft.util.AxisAlignedBB monsterBB = net.minecraft.util.AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    private static boolean filterIps;
    public static boolean tabPing = false;
    private static Metrics metrics;
    public static List<String> bungeeIPs;
    public static int textureResolution = 16;
    public static final Pattern validName = Pattern.compile("^[a-zA-Z0-9_-]{2,16}$");

    public static void initialize(CraftServer server, SimpleCommandMap commandMap, YamlConfiguration configuration) {
        if (configuration.getBoolean("settings.tps-command", true)) { // MCPC+ - config option to allow mods to replace command
            commandMap.register("bukkit", new org.bukkit.craftbukkit.command.TicksPerSecondCommand("tps"));
        }

        if (configuration.getBoolean("settings.restart-command", true)) { // MCPC+ - config option to allow mods to replace command
            commandMap.register("restart", new RestartCommand("restart"));
        }

        server.whitelistMessage = configuration.getString("settings.whitelist-message", server.whitelistMessage);
        server.stopMessage = configuration.getString("settings.stop-message", server.stopMessage);
        server.logCommands = configuration.getBoolean("settings.log-commands", true);
        server.commandComplete = configuration.getBoolean("settings.command-complete", true);
        server.spamGuardExclusions = configuration.getStringList("settings.spam-exclusions");
        filterIps = configuration.getBoolean("settings.filter-unsafe-ips", false);

        int configVersion = configuration.getInt("config-version");
        switch (configVersion) {
            case 0:
                configuration.set("settings.timeout-time", 30);
            case 1:
                configuration.set("settings.timeout-time", 60);
        }
        configuration.set("config-version", 2);

        WatchdogThread.doStart(configuration.getInt("settings.timeout-time", 60), configuration.getBoolean("settings.restart-on-crash", false));

        server.orebfuscatorEnabled = configuration.getBoolean("orebfuscator.enable", false);
        server.orebfuscatorEngineMode = configuration.getInt("orebfuscator.engine-mode", 1);
        server.orebfuscatorDisabledWorlds = configuration.getStringList("orebfuscator.disabled-worlds");
        server.orebfuscatorBlocks = configuration.getShortList("orebfuscator.blocks");
        if (server.orebfuscatorEngineMode != 1 && server.orebfuscatorEngineMode != 2) {
            server.orebfuscatorEngineMode = 1;
        }

        if (server.chunkGCPeriod == 0) {
            server.getLogger().severe("[Spigot] You should not disable chunk-gc, unexpected behaviour may occur!");
        }

        tabPing = configuration.getBoolean("settings.tab-ping", tabPing);
        bungeeIPs = configuration.getStringList("settings.bungee-proxies");
        textureResolution = configuration.getInt("settings.texture-resolution", textureResolution);

        if (metrics == null) {
            try {
                metrics = new Metrics();
                metrics.start();
            } catch (IOException ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Could not start metrics service", ex);
            }
        }
    }

    /**
     * Initializes an entities type on construction to specify what group this
     * entity is in for activation ranges.
     *
     * @param entity
     * @return group id
     */
    public static byte initializeEntityActivationType(net.minecraft.entity.Entity entity) {
        if (entity instanceof net.minecraft.entity.monster.EntityMob || entity instanceof net.minecraft.entity.monster.EntitySlime) {
            return 1; // Monster
        } else if (entity instanceof net.minecraft.entity.EntityCreature || entity instanceof net.minecraft.entity.passive.EntityAmbientCreature) {
            return 2; // Animal
        } else {
            return 3; // Misc
        }
    }

    /**
     * These entities are excluded from Activation range checks.
     *
     * @param entity
     * @param world
     * @return boolean If it should always tick.
     */
    public static boolean initializeEntityActivationState(net.minecraft.entity.Entity entity, CraftWorld world) {
        if ((entity.activationType == 3 && world.miscEntityActivationRange == 0)
                || (entity.activationType == 2 && world.animalEntityActivationRange == 0)
                || (entity.activationType == 1 && world.monsterEntityActivationRange == 0)
                || entity instanceof net.minecraft.entity.player.EntityPlayer
                || entity instanceof net.minecraft.entity.projectile.EntityThrowable
                || entity instanceof net.minecraft.entity.boss.EntityDragon
                || entity instanceof net.minecraft.entity.boss.EntityDragonPart
                || entity instanceof net.minecraft.entity.boss.EntityWither
                || entity instanceof net.minecraft.entity.projectile.EntityFireball
                || entity instanceof net.minecraft.entity.effect.EntityWeatherEffect
                || entity instanceof net.minecraft.entity.item.EntityTNTPrimed
                || entity instanceof net.minecraft.entity.item.EntityEnderCrystal
                || entity instanceof net.minecraft.entity.item.EntityFireworkRocket) {
            return true;
        }

        return false;
    }

    /**
     * Utility method to grow an AABB without creating a new AABB or touching
     * the pool, so we can re-use ones we have.
     *
     * @param target
     * @param source
     * @param x
     * @param y
     * @param z
     */
    public static void growBB(net.minecraft.util.AxisAlignedBB target, net.minecraft.util.AxisAlignedBB source, int x, int y, int z) {
        target.minX = source.minX - x;
        target.minY = source.minY - y;
        target.minZ = source.minZ - z;
        target.maxX = source.maxX + x;
        target.maxY = source.maxY + y;
        target.maxZ = source.maxZ + z;
    }

    /**
     * Find what entities are in range of the players in the world and set
     * active if in range.
     *
     * @param world
     */
    public static void activateEntities(net.minecraft.world.World world) {
        SpigotTimings.entityActivationCheckTimer.startTiming();
        final int miscActivationRange = world.getWorld().miscEntityActivationRange;
        final int animalActivationRange = world.getWorld().animalEntityActivationRange;
        final int monsterActivationRange = world.getWorld().monsterEntityActivationRange;

        int maxRange = Math.max(monsterActivationRange, animalActivationRange);
        maxRange = Math.max(maxRange, miscActivationRange);
        maxRange = Math.min((world.getWorld().viewDistance << 4) - 8, maxRange);

        for (net.minecraft.entity.Entity player : new ArrayList<net.minecraft.entity.Entity>(world.playerEntities)) {

            player.activatedTick = net.minecraft.server.MinecraftServer.currentTick;
            growBB(maxBB, player.boundingBox, maxRange, 256, maxRange);
            growBB(miscBB, player.boundingBox, miscActivationRange, 256, miscActivationRange);
            growBB(animalBB, player.boundingBox, animalActivationRange, 256, animalActivationRange);
            growBB(monsterBB, player.boundingBox, monsterActivationRange, 256, monsterActivationRange);

            int i = net.minecraft.util.MathHelper.floor_double(maxBB.minX / 16.0D);
            int j = net.minecraft.util.MathHelper.floor_double(maxBB.maxX / 16.0D);
            int k = net.minecraft.util.MathHelper.floor_double(maxBB.minZ / 16.0D);
            int l = net.minecraft.util.MathHelper.floor_double(maxBB.maxZ / 16.0D);

            for (int i1 = i; i1 <= j; ++i1) {
                for (int j1 = k; j1 <= l; ++j1) {
                    if (world.getWorld().isChunkLoaded(i1, j1)) {
                        activateChunkEntities(world.getChunkFromChunkCoords(i1, j1));
                    }
                }
            }
        }
        SpigotTimings.entityActivationCheckTimer.stopTiming();
    }

    /**
     * Checks for the activation state of all entities in this chunk.
     *
     * @param chunk
     */
    private static void activateChunkEntities(net.minecraft.world.chunk.Chunk chunk) {
        for (List<net.minecraft.entity.Entity> slice : chunk.entityLists) {
            for (net.minecraft.entity.Entity entity : slice) {
                if (net.minecraft.server.MinecraftServer.currentTick > entity.activatedTick) {
                    if (entity.defaultActivationState) {
                        entity.activatedTick = net.minecraft.server.MinecraftServer.currentTick;
                        continue;
                    }
                    switch (entity.activationType) {
                        case 1:
                            if (monsterBB.intersectsWith(entity.boundingBox)) {
                                entity.activatedTick = net.minecraft.server.MinecraftServer.currentTick;
                            }
                            break;
                        case 2:
                            if (animalBB.intersectsWith(entity.boundingBox)) {
                                entity.activatedTick = net.minecraft.server.MinecraftServer.currentTick;
                            }
                            break;
                        case 3:
                        default:
                            if (miscBB.intersectsWith(entity.boundingBox)) {
                                entity.activatedTick = net.minecraft.server.MinecraftServer.currentTick;
                            }
                    }
                }
            }
        }
    }

    /**
     * If an entity is not in range, do some more checks to see if we should
     * give it a shot.
     *
     * @param entity
     * @return
     */
    public static boolean checkEntityImmunities(net.minecraft.entity.Entity entity) {
        // quick checks.
        if (entity.inWater /* isInWater */ || entity.fire > 0) {
            return true;
        }
        if (!(entity instanceof net.minecraft.entity.projectile.EntityArrow)) {
            if (!entity.onGround || entity.riddenByEntity != null
                    || entity.ridingEntity != null) {
                return true;
            }
        } else if (!((net.minecraft.entity.projectile.EntityArrow) entity).inGround) {
            return true;
        }
        // special cases.
        if (entity instanceof net.minecraft.entity.EntityLiving) {
            net.minecraft.entity.EntityLiving living = (net.minecraft.entity.EntityLiving) entity;
            if (living.attackTime > 0 || living.hurtTime > 0 || living.activePotionsMap.size() > 0) {
                return true;
            }
            if (entity instanceof net.minecraft.entity.EntityCreature && ((net.minecraft.entity.EntityCreature) entity).entityToAttack != null) {
                return true;
            }
            if (entity instanceof net.minecraft.entity.passive.EntityAnimal) {
                net.minecraft.entity.passive.EntityAnimal animal = (net.minecraft.entity.passive.EntityAnimal) entity;
                if (animal.isChild() || animal.isInLove() /*love*/) {
                    return true;
                }
                if (entity instanceof net.minecraft.entity.passive.EntitySheep && ((net.minecraft.entity.passive.EntitySheep) entity).getSheared()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the entity is active for this tick.
     *
     * @param entity
     * @return
     */
    public static boolean checkIfActive(net.minecraft.entity.Entity entity) {
        SpigotTimings.checkIfActiveTimer.startTiming();
        // MCPC+ start - check if entity is in forced chunk and if so, set to active
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posZ);
        boolean isForced = entity.worldObj.getPersistentChunks().containsKey(new ChunkCoordIntPair(i >> 4, j >> 4));
        boolean isActive = entity.activatedTick >= net.minecraft.server.MinecraftServer.currentTick || entity.defaultActivationState || isForced;
        // MCPC+ end

        // Should this entity tick?
        if (!isActive) {
            if ((net.minecraft.server.MinecraftServer.currentTick - entity.activatedTick - 1) % 20 == 0) {
                // Check immunities every 20 ticks.
                if (checkEntityImmunities(entity)) {
                    // Triggered some sort of immunity, give 20 full ticks before we check again.
                    entity.activatedTick = net.minecraft.server.MinecraftServer.currentTick + 20;
                }
                isActive = true;
            }
            // Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if (!entity.defaultActivationState && entity.ticksExisted % 4 == 0 && !checkEntityImmunities(entity)) {
            isActive = false;
        }
        int x = net.minecraft.util.MathHelper.floor_double(entity.posX);
        int z = net.minecraft.util.MathHelper.floor_double(entity.posZ);
        // MCPC+ start - disabled, this breaks moving chunkloaders such as AnchorCarts when entering new chunks that are not yet loaded
        // Make sure not on edge of unloaded chunk
        /*if (isActive && !entity.worldObj.doChunksNearChunkExist(x, 0, z, 16)) {
            isActive = false;
        }*/
        // MCPC+ end
        SpigotTimings.checkIfActiveTimer.stopTiming();
        return isActive;
    }

    public static void restart() {
        try {
            String startupScript = net.minecraft.server.MinecraftServer.getServer().server.configuration.getString("settings.restart-script-location", "");
            final File file = new File(startupScript);
            if (file.isFile()) {
                System.out.println("Attempting to restart with " + startupScript);

                // Kick all players
                for (net.minecraft.entity.player.EntityPlayerMP p : (List< net.minecraft.entity.player.EntityPlayerMP>) net.minecraft.server.MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                    p.playerNetServerHandler.netManager.addToSendQueue(new net.minecraft.network.packet.Packet255KickDisconnect("Server is restarting"));
                    p.playerNetServerHandler.netManager.serverShutdown();
                }
                // Give the socket a chance to send the packets
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
                // Close the socket so we can rebind with the new process
                net.minecraft.server.MinecraftServer.getServer().getNetworkThread().stopListening();

                // Give time for it to kick in
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }

                // Actually shutdown
                try {
                    net.minecraft.server.MinecraftServer.getServer().stopServer();
                } catch (Throwable t) {
                }

                // This will be done AFTER the server has completely halted
                Thread shutdownHook = new Thread() {
                    @Override
                    public void run() {
                        try {
                            String os = System.getProperty("os.name").toLowerCase();
                            if (os.contains("win")) {
                                Runtime.getRuntime().exec("cmd /c start " + file.getPath());
                            } else {
                                Runtime.getRuntime().exec(new String[]{"sh", file.getPath()});
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                shutdownHook.setDaemon(true);
                Runtime.getRuntime().addShutdownHook(shutdownHook);
            } else {
                System.out.println("Startup script '" + startupScript + "' does not exist! Stopping server.");
            }
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the range an entity should be 'tracked' by players and visible in
     * the client.
     *
     * @param entity
     * @param defaultRange Default range defined by Mojang
     * @return
     */
    public static int getEntityTrackingRange(net.minecraft.entity.Entity entity, int defaultRange) {
        CraftWorld world = entity.worldObj.getWorld();
        int range = defaultRange;
        if (entity instanceof net.minecraft.entity.player.EntityPlayerMP) {
            range = world.playerTrackingRange;
        } else if (entity.defaultActivationState || entity instanceof net.minecraft.entity.monster.EntityGhast) {
            range = defaultRange;
        } else if (entity.activationType == 1) {
            range = world.monsterTrackingRange;
        } else if (entity.activationType == 2) {
            range = world.animalTrackingRange;
        } else if (entity instanceof net.minecraft.entity.item.EntityItemFrame || entity instanceof net.minecraft.entity.item.EntityPainting || entity instanceof net.minecraft.entity.item.EntityItem || entity instanceof net.minecraft.entity.item.EntityXPOrb) {
            range = world.miscTrackingRange;
        }
        if (range == 0) {
            return defaultRange;
        }
        return Math.min(world.maxTrackingRange, range);
    }

    public static boolean filterIp(net.minecraft.network.NetLoginHandler con) {
        if (filterIps) {
            try {
                InetAddress address = con.getSocket().getInetAddress();
                String ip = address.getHostAddress();

                if (!address.isLoopbackAddress()) {
                    String[] split = ip.split("\\.");
                    StringBuilder lookup = new StringBuilder();
                    for (int i = split.length - 1; i >= 0; i--) {
                        lookup.append(split[i]);
                        lookup.append(".");
                    }
                    lookup.append("xbl.spamhaus.org.");
                    if (InetAddress.getByName(lookup.toString()) != null) {
                        con.raiseErrorAndDisconnect("Your IP address (" + ip + ") is flagged as unsafe by spamhaus.org/xbl");
                        return true;
                    }
                }
            } catch (Exception ex) {
            }
        }
        return false;
    }
}
