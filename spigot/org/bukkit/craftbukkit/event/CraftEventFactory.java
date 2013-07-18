package org.bukkit.craftbukkit.event;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftDamageSource;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.BookMeta;
// MCPC+ start
import net.minecraft.entity.item.EntityItem;
import org.bukkit.block.CreatureSpawner;
// MCPC+ end


public class CraftEventFactory {
    public static final net.minecraft.util.DamageSource MELTING = CraftDamageSource.copyOf(net.minecraft.util.DamageSource.onFire);
    public static final net.minecraft.util.DamageSource POISON = CraftDamageSource.copyOf(net.minecraft.util.DamageSource.magic);

    // helper methods
    private static boolean canBuild(CraftWorld world, Player player, int x, int z) {
        net.minecraft.world.WorldServer worldServer = world.getHandle();
        int spawnSize = Bukkit.getServer().getSpawnRadius();

        if (world.getHandle().provider.dimensionId != 0) return true;
        if (spawnSize <= 0) return true;
        if (((CraftServer) Bukkit.getServer()).getHandle().getOps().isEmpty()) return true;
        if (player.isOp()) return true;

        net.minecraft.util.ChunkCoordinates chunkcoordinates = worldServer.getSpawnPoint();

        int distanceFromSpawn = Math.max(Math.abs(x - chunkcoordinates.posX), Math.abs(z - chunkcoordinates.posZ));
        return distanceFromSpawn >= spawnSize;
    }

    public static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Block place methods
     */
    public static BlockPlaceEvent callBlockPlaceEvent(net.minecraft.world.World world, net.minecraft.entity.player.EntityPlayer who, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ) {
        CraftWorld craftWorld = world.getWorld();
        CraftServer craftServer = world.getServer();

        Player player = (who == null) ? null : (Player) who.getBukkitEntity();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        Block placedBlock = replacedBlockState.getBlock();

        boolean canBuild = canBuild(craftWorld, player, placedBlock.getX(), placedBlock.getZ());

        BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, replacedBlockState, blockClicked, player.getItemInHand(), player, canBuild);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Mob spawner event
     */
    public static SpawnerSpawnEvent callSpawnerSpawnEvent(net.minecraft.entity.Entity spawnee, int spawnerX, int spawnerY, int spawnerZ) {
        org.bukkit.craftbukkit.entity.CraftEntity entity = spawnee.getBukkitEntity();
        BlockState state = entity.getWorld().getBlockAt(spawnerX, spawnerY, spawnerZ).getState();

        if (!(state instanceof CreatureSpawner)) {
            state = null;
        }

        SpawnerSpawnEvent event = new SpawnerSpawnEvent(entity, (CreatureSpawner) state);
        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Bucket methods
     */
    public static PlayerBucketEmptyEvent callPlayerBucketEmptyEvent(net.minecraft.entity.player.EntityPlayer who, int clickedX, int clickedY, int clickedZ, int clickedFace, net.minecraft.item.ItemStack itemInHand) {
        return (PlayerBucketEmptyEvent) getPlayerBucketEvent(false, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, net.minecraft.item.Item.bucketEmpty);
    }

    public static PlayerBucketFillEvent callPlayerBucketFillEvent(net.minecraft.entity.player.EntityPlayer who, int clickedX, int clickedY, int clickedZ, int clickedFace, net.minecraft.item.ItemStack itemInHand, net.minecraft.item.Item bucket) {
        return (PlayerBucketFillEvent) getPlayerBucketEvent(true, who, clickedX, clickedY, clickedZ, clickedFace, itemInHand, bucket);
    }

    private static PlayerEvent getPlayerBucketEvent(boolean isFilling, net.minecraft.entity.player.EntityPlayer who, int clickedX, int clickedY, int clickedZ, int clickedFace, net.minecraft.item.ItemStack itemstack, net.minecraft.item.Item item) {
        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = CraftItemStack.asNewCraftStack(item);
        Material bucket = Material.getMaterial(itemstack.itemID);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        PlayerEvent event = null;
        if (isFilling) {
            event = new PlayerBucketFillEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketFillEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        } else {
            event = new PlayerBucketEmptyEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketEmptyEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        }

        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Player Interact event
     */
    public static PlayerInteractEvent callPlayerInteractEvent(net.minecraft.entity.player.EntityPlayer who, Action action, net.minecraft.item.ItemStack itemstack) {
        if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
            throw new IllegalArgumentException();
        }
        return callPlayerInteractEvent(who, action, 0, 256, 0, 0, itemstack);
    }

    public static PlayerInteractEvent callPlayerInteractEvent(net.minecraft.entity.player.EntityPlayer who, Action action, int clickedX, int clickedY, int clickedZ, int clickedFace, net.minecraft.item.ItemStack itemstack) {
        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = clickedY > 255 ? null : craftWorld.getBlockAt(clickedX, clickedY, clickedZ); // MCPC+ - Don't bother getting the block if it's just going to be set to null later. Avoids pointles chunkloading if ChunkProviderServer.loadChunkOnProvideRequest is enabled
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        if (clickedY > 255) {
            blockClicked = null;
            switch (action) {
            case LEFT_CLICK_BLOCK:
                action = Action.LEFT_CLICK_AIR;
                break;
            case RIGHT_CLICK_BLOCK:
                action = Action.RIGHT_CLICK_AIR;
                break;
            }
        }

        if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
            itemInHand = null;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, action, itemInHand, blockClicked, blockFace);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * EntityShootBowEvent
     */
    public static EntityShootBowEvent callEntityShootBowEvent(net.minecraft.entity.EntityLivingBase who, net.minecraft.item.ItemStack itemstack, net.minecraft.entity.projectile.EntityArrow entityArrow, float force) {
        LivingEntity shooter = (LivingEntity) who.getBukkitEntity();
        CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);
        Arrow arrow = (Arrow) entityArrow.getBukkitEntity();

        if (itemInHand != null && (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0)) {
            itemInHand = null;
        }

        EntityShootBowEvent event = new EntityShootBowEvent(shooter, itemInHand, arrow, force);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * BlockDamageEvent
     */
    public static BlockDamageEvent callBlockDamageEvent(net.minecraft.entity.player.EntityPlayer who, int x, int y, int z, net.minecraft.item.ItemStack itemstack, boolean instaBreak) {
        Player player = (who == null) ? null : (Player) who.getBukkitEntity();
        CraftItemStack itemInHand = CraftItemStack.asCraftMirror(itemstack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(x, y, z);

        BlockDamageEvent event = new BlockDamageEvent(player, blockClicked, itemInHand, instaBreak);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * CreatureSpawnEvent
     */
    public static CreatureSpawnEvent callCreatureSpawnEvent(net.minecraft.entity.EntityLivingBase entityliving, SpawnReason spawnReason) {
        LivingEntity entity = (LivingEntity) entityliving.getBukkitEntity();
        CraftServer craftServer = (CraftServer) entity.getServer();

        CreatureSpawnEvent event = new CreatureSpawnEvent(entity, spawnReason);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityTameEvent
     */
    public static EntityTameEvent callEntityTameEvent(net.minecraft.entity.EntityLiving entity, net.minecraft.entity.player.EntityPlayer tamer) {
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        org.bukkit.entity.AnimalTamer bukkitTamer = (tamer != null ? tamer.getBukkitEntity() : null);
        CraftServer craftServer = (CraftServer) bukkitEntity.getServer();

        entity.persistenceRequired = true;

        EntityTameEvent event = new EntityTameEvent((LivingEntity) bukkitEntity, bukkitTamer);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemSpawnEvent
     */
    public static ItemSpawnEvent callItemSpawnEvent(net.minecraft.entity.item.EntityItem entityitem) {
        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) entityitem.getBukkitEntity();
        CraftServer craftServer = (CraftServer) entity.getServer();

        ItemSpawnEvent event = new ItemSpawnEvent(entity, entity.getLocation());

        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemDespawnEvent
     */
    public static ItemDespawnEvent callItemDespawnEvent(net.minecraft.entity.item.EntityItem entityitem) {
        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) entityitem.getBukkitEntity();

        ItemDespawnEvent event = new ItemDespawnEvent(entity, entity.getLocation());

        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * PotionSplashEvent
     */
    public static PotionSplashEvent callPotionSplashEvent(net.minecraft.entity.projectile.EntityPotion potion, Map<LivingEntity, Double> affectedEntities) {
        ThrownPotion thrownPotion = (ThrownPotion) potion.getBukkitEntity();

        PotionSplashEvent event = new PotionSplashEvent(thrownPotion, affectedEntities);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * BlockFadeEvent
     */
    public static BlockFadeEvent callBlockFadeEvent(Block block, int type) {
        BlockState state = block.getState();
        state.setTypeId(type);

        BlockFadeEvent event = new BlockFadeEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static void handleBlockSpreadEvent(Block block, Block source, int type, int data) {
        BlockState state = block.getState();
        state.setTypeId(type);
        state.setRawData((byte) data);

        BlockSpreadEvent event = new BlockSpreadEvent(block, source, state);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            state.update(true);
        }
    }

    public static EntityDeathEvent callEntityDeathEvent(net.minecraft.entity.EntityLivingBase victim) {
        return callEntityDeathEvent(victim, new ArrayList<org.bukkit.inventory.ItemStack>(0));
    }

    public static EntityDeathEvent callEntityDeathEvent(net.minecraft.entity.EntityLivingBase victim, List<org.bukkit.inventory.ItemStack> drops) {
        CraftLivingEntity entity = (CraftLivingEntity) victim.getBukkitEntity();
        EntityDeathEvent event = new EntityDeathEvent(entity, drops, victim.getExpReward());
        //org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        victim.expToDrop = event.getDroppedExp();
        // MCPC+ start - handle any drop changes from plugins
        victim.calledDeathEvent = true;

        victim.capturedDrops.clear();
        for (org.bukkit.inventory.ItemStack stack : event.getDrops())
        {
            net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(victim.worldObj, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), CraftItemStack.asNMSCopy(stack));
            if (entityitem != null)
            {
                victim.capturedDrops.add((EntityItem)entityitem);
            }
        }
        // MCPC+ end

        return event;
    }

    public static PlayerDeathEvent callPlayerDeathEvent(net.minecraft.entity.player.EntityPlayerMP victim, List<org.bukkit.inventory.ItemStack> drops, String deathMessage) {
        CraftPlayer entity = victim.getBukkitEntity();
        PlayerDeathEvent event = new PlayerDeathEvent(entity, drops, victim.getExpReward(), 0, deathMessage);
        //org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        victim.keepLevel = event.getKeepLevel();
        victim.newLevel = event.getNewLevel();
        victim.newTotalExp = event.getNewTotalExp();
        victim.expToDrop = event.getDroppedExp();
        victim.newExp = event.getNewExp();

        for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            // MCPC+ start - add support for Forge's PlayerDropsEvent
            //world.dropItemNaturally(entity.getLocation(), stack); // handle world drop in EntityPlayerMP
            if (victim.captureDrops)
            {
                net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(victim.worldObj, entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), CraftItemStack.asNMSCopy(stack));
                if (entityitem != null)
                {
                    victim.capturedDrops.add((EntityItem)entityitem);
                }
            }
            // MCPC+ end
        }

        return event;
    }

    /**
     * Server methods
     */
    public static ServerListPingEvent callServerListPingEvent(Server craftServer, InetAddress address, String motd, int numPlayers, int maxPlayers) {
        ServerListPingEvent event = new ServerListPingEvent(address, motd, numPlayers, maxPlayers);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityDamage(ByEntityEvent)
     */
    public static EntityDamageEvent callEntityDamageEvent(net.minecraft.entity.Entity damager, net.minecraft.entity.Entity damagee, DamageCause cause, double damage) {
        EntityDamageEvent event;
        if (damager != null) {
            event = new EntityDamageByEntityEvent(damager.getBukkitEntity(), damagee.getBukkitEntity(), cause, damage);
        } else {
            event = new EntityDamageEvent(damagee.getBukkitEntity(), cause, damage);
        }

        callEvent(event);

        if (!event.isCancelled()) {
            event.getEntity().setLastDamageCause(event);
        }

        return event;
    }

    public static EntityDamageEvent handleEntityDamageEvent(net.minecraft.entity.Entity entity, net.minecraft.util.DamageSource source, float damage) {
        if (source instanceof net.minecraft.util.EntityDamageSource) {
            net.minecraft.entity.Entity damager = source.getEntity();
            DamageCause cause = DamageCause.ENTITY_ATTACK;

            if (source instanceof net.minecraft.util.EntityDamageSourceIndirect) {
                damager = ((net.minecraft.util.EntityDamageSourceIndirect) source).getProximateDamageSource();
                // MCPC+ start - vanilla compatibility
                if (damager != null) {
                    if (damager.getBukkitEntity() instanceof ThrownPotion) {
                        cause = DamageCause.MAGIC;
                    } else if (damager.getBukkitEntity() instanceof Projectile) {
                        cause = DamageCause.PROJECTILE;
                    }
                }
                // MCPC+ end
            } else if ("thorns".equals(source.damageType)) {
                cause = DamageCause.THORNS;
            }

            return callEntityDamageEvent(damager, entity, cause, damage);
        } else if (source == net.minecraft.util.DamageSource.outOfWorld) {
            EntityDamageEvent event = callEvent(new EntityDamageByBlockEvent(null, entity.getBukkitEntity(), DamageCause.VOID, damage));
            if (!event.isCancelled()) {
                event.getEntity().setLastDamageCause(event);
            }
            return event;
        }

        DamageCause cause = null;
        if (source == net.minecraft.util.DamageSource.inFire) {
            cause = DamageCause.FIRE;
        } else if (source == net.minecraft.util.DamageSource.starve) {
            cause = DamageCause.STARVATION;
        } else if (source == net.minecraft.util.DamageSource.wither) {
            cause = DamageCause.WITHER;
        } else if (source == net.minecraft.util.DamageSource.inWall) {
            cause = DamageCause.SUFFOCATION;
        } else if (source == net.minecraft.util.DamageSource.drown) {
            cause = DamageCause.DROWNING;
        } else if (source == net.minecraft.util.DamageSource.onFire) {
            cause = DamageCause.FIRE_TICK;
        } else if (source == MELTING) {
            cause = DamageCause.MELTING;
        } else if (source == POISON) {
            cause = DamageCause.POISON;
        } else if (source == net.minecraft.util.DamageSource.magic) {
            cause = DamageCause.MAGIC;
        }

        if (cause != null) {
            return callEntityDamageEvent(null, entity, cause, damage);
        }

        // If an event was called earlier, we return null.
        // EG: Cactus, Lava, EntityEnderPearl "fall", FallingSand
        return null;
    }

    // Non-Living Entities such as EntityEnderCrystal need to call this
    public static boolean handleNonLivingEntityDamageEvent(net.minecraft.entity.Entity entity, net.minecraft.util.DamageSource source, float damage) {
        if (!(source instanceof net.minecraft.util.EntityDamageSource)) {
            return false;
        }
        // We don't need to check for null, since EntityDamageSource will always return an event
        EntityDamageEvent event = handleEntityDamageEvent(entity, source, damage);
        return event.isCancelled() || event.getDamage() == 0;
    }

    public static PlayerLevelChangeEvent callPlayerLevelChangeEvent(Player player, int oldLevel, int newLevel) {
        PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(player, oldLevel, newLevel);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerExpChangeEvent callPlayerExpChangeEvent(net.minecraft.entity.player.EntityPlayer entity, int expAmount) {
        Player player = (Player) entity.getBukkitEntity();
        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, expAmount);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static void handleBlockGrowEvent(net.minecraft.world.World world, int x, int y, int z, int type, int data) {
        Block block = world.getWorld().getBlockAt(x, y, z);
        CraftBlockState state = (CraftBlockState) block.getState();
        state.setTypeId(type);
        state.setRawData((byte) data);

        BlockGrowEvent event = new BlockGrowEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            state.update(true);
        }
    }

    public static FoodLevelChangeEvent callFoodLevelChangeEvent(net.minecraft.entity.player.EntityPlayer entity, int level) {
        FoodLevelChangeEvent event = new FoodLevelChangeEvent(entity.getBukkitEntity(), level);
        entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static PigZapEvent callPigZapEvent(net.minecraft.entity.Entity pig, net.minecraft.entity.Entity lightning, net.minecraft.entity.Entity pigzombie) {
        PigZapEvent event = new PigZapEvent((Pig) pig.getBukkitEntity(), (LightningStrike) lightning.getBukkitEntity(), (PigZombie) pigzombie.getBukkitEntity());
        pig.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static HorseJumpEvent callHorseJumpEvent(net.minecraft.entity.Entity horse, float power) {
        HorseJumpEvent event = new HorseJumpEvent((Horse) horse.getBukkitEntity(), power);
        horse.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(org.bukkit.entity.Entity entity, Block block, Material material) {
        return callEntityChangeBlockEvent(entity, block, material, 0);
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(net.minecraft.entity.Entity entity, Block block, Material material) {
        return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, 0);
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(net.minecraft.entity.Entity entity, int x, int y, int z, int type, int data) {
        Block block = entity.worldObj.getWorld().getBlockAt(x, y, z);
        Material material = Material.getMaterial(type);

        return callEntityChangeBlockEvent(entity.getBukkitEntity(), block, material, data);
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(org.bukkit.entity.Entity entity, Block block, Material material, int data) {
        EntityChangeBlockEvent event = new EntityChangeBlockEvent(entity, block, material, (byte) data);
        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static CreeperPowerEvent callCreeperPowerEvent(net.minecraft.entity.Entity creeper, net.minecraft.entity.Entity lightning, CreeperPowerEvent.PowerCause cause) {
        CreeperPowerEvent event = new CreeperPowerEvent((Creeper) creeper.getBukkitEntity(), (LightningStrike) lightning.getBukkitEntity(), cause);
        creeper.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityTargetEvent callEntityTargetEvent(net.minecraft.entity.Entity entity, net.minecraft.entity.Entity target, EntityTargetEvent.TargetReason reason) {
        EntityTargetEvent event = new EntityTargetEvent(entity.getBukkitEntity(), target == null ? null : target.getBukkitEntity(), reason);
        entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityTargetLivingEntityEvent callEntityTargetLivingEvent(net.minecraft.entity.Entity entity, net.minecraft.entity.EntityLivingBase target, EntityTargetEvent.TargetReason reason) {
        EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(entity.getBukkitEntity(), (LivingEntity) target.getBukkitEntity(), reason);
        entity.getBukkitEntity().getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityBreakDoorEvent callEntityBreakDoorEvent(net.minecraft.entity.Entity entity, int x, int y, int z) {
        org.bukkit.entity.Entity entity1 = entity.getBukkitEntity();
        Block block = entity1.getWorld().getBlockAt(x, y, z);

        EntityBreakDoorEvent event = new EntityBreakDoorEvent((LivingEntity) entity1, block);
        entity1.getServer().getPluginManager().callEvent(event);

        return event;
    }

    public static net.minecraft.inventory.Container callInventoryOpenEvent(net.minecraft.entity.player.EntityPlayerMP player, net.minecraft.inventory.Container container) {
        if (player.openContainer != player.inventoryContainer) { // fire INVENTORY_CLOSE if one already open
            player.playerNetServerHandler.handleCloseWindow(new net.minecraft.network.packet.Packet101CloseWindow(player.openContainer.windowId));
        }

        CraftServer server = player.worldObj.getServer();
        CraftPlayer craftPlayer = player.getBukkitEntity();
        // MCPC+ start - vanilla compatibility
        try {
            player.openContainer.transferTo(container, craftPlayer);
        }
        catch (AbstractMethodError e) {
            // do nothing
        }
        // MCPC+ end
        InventoryOpenEvent event = new InventoryOpenEvent(container.getBukkitView());
        if (container.getBukkitView() != null) server.getPluginManager().callEvent(event); // MCPC+ - allow vanilla mods to bypass

        if (event.isCancelled()) {
            container.transferTo(player.openContainer, craftPlayer);
            return null;
        }

        return container;
    }

    public static net.minecraft.item.ItemStack callPreCraftEvent(net.minecraft.inventory.InventoryCrafting matrix, net.minecraft.item.ItemStack result, InventoryView lastCraftView, boolean isRepair) {
        CraftInventoryCrafting inventory = new CraftInventoryCrafting(matrix, matrix.resultInventory);
        inventory.setResult(CraftItemStack.asCraftMirror(result));

        PrepareItemCraftEvent event = new PrepareItemCraftEvent(inventory, lastCraftView, isRepair);
        Bukkit.getPluginManager().callEvent(event);

        org.bukkit.inventory.ItemStack bitem = event.getInventory().getResult();

        return CraftItemStack.asNMSCopy(bitem);
    }

    public static ProjectileLaunchEvent callProjectileLaunchEvent(net.minecraft.entity.Entity entity) {
        Projectile bukkitEntity = (Projectile) entity.getBukkitEntity();
        ProjectileLaunchEvent event = new ProjectileLaunchEvent(bukkitEntity);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static ProjectileHitEvent callProjectileHitEvent(net.minecraft.entity.Entity entity) {
        ProjectileHitEvent event = new ProjectileHitEvent((Projectile) entity.getBukkitEntity());
        entity.worldObj.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static ExpBottleEvent callExpBottleEvent(net.minecraft.entity.Entity entity, int exp) {
        ThrownExpBottle bottle = (ThrownExpBottle) entity.getBukkitEntity();
        ExpBottleEvent event = new ExpBottleEvent(bottle, exp);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static BlockRedstoneEvent callRedstoneChange(net.minecraft.world.World world, int x, int y, int z, int oldCurrent, int newCurrent) {
        BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(x, y, z), oldCurrent, newCurrent);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static NotePlayEvent callNotePlayEvent(net.minecraft.world.World world, int x, int y, int z, byte instrument, byte note) {
        NotePlayEvent event = new NotePlayEvent(world.getWorld().getBlockAt(x, y, z), org.bukkit.Instrument.getByType(instrument), new org.bukkit.Note(note));
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static void callPlayerItemBreakEvent(net.minecraft.entity.player.EntityPlayer human, net.minecraft.item.ItemStack brokenItem) {
        CraftItemStack item = CraftItemStack.asCraftMirror(brokenItem);
        PlayerItemBreakEvent event = new PlayerItemBreakEvent((Player) human.getBukkitEntity(), item);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.world.World world, int x, int y, int z, int igniterX, int igniterY, int igniterZ) {
        org.bukkit.World bukkitWorld = world.getWorld();
        Block igniter = bukkitWorld.getBlockAt(igniterX, igniterY, igniterZ);
        IgniteCause cause;
        switch (igniter.getType()) {
            case LAVA:
            case STATIONARY_LAVA:
                cause = IgniteCause.LAVA;
                break;
            case DISPENSER:
                cause = IgniteCause.FLINT_AND_STEEL;
                break;
            case FIRE: // Fire or any other unknown block counts as SPREAD.
            default:
                cause = IgniteCause.SPREAD;
        }

        BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), cause, igniter);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.world.World world, int x, int y, int z, net.minecraft.entity.Entity igniter) {
        org.bukkit.World bukkitWorld = world.getWorld();
        org.bukkit.entity.Entity bukkitIgniter = igniter.getBukkitEntity();
        IgniteCause cause;
        switch (bukkitIgniter.getType()) {
        case ENDER_CRYSTAL:
            cause = IgniteCause.ENDER_CRYSTAL;
            break;
        case LIGHTNING:
            cause = IgniteCause.LIGHTNING;
            break;
        case SMALL_FIREBALL:
        case FIREBALL:
            cause = IgniteCause.FIREBALL;
            break;
        default:
            cause = IgniteCause.FLINT_AND_STEEL;
        }

        BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), cause, bukkitIgniter);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.world.World world, int x, int y, int z, net.minecraft.world.Explosion explosion) {
        org.bukkit.World bukkitWorld = world.getWorld();
        org.bukkit.entity.Entity igniter = explosion.exploder == null ? null : explosion.exploder.getBukkitEntity();

        BlockIgniteEvent event = new BlockIgniteEvent(bukkitWorld.getBlockAt(x, y, z), IgniteCause.EXPLOSION, igniter);
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static BlockIgniteEvent callBlockIgniteEvent(net.minecraft.world.World world, int x, int y, int z, IgniteCause cause, net.minecraft.entity.Entity igniter) {
        BlockIgniteEvent event = new BlockIgniteEvent(world.getWorld().getBlockAt(x, y, z), cause, igniter.getBukkitEntity());
        world.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static void handleInventoryCloseEvent(net.minecraft.entity.player.EntityPlayer human) {
        InventoryCloseEvent event = new InventoryCloseEvent(human.openContainer.getBukkitView());
        if (human.openContainer.getBukkitView() != null) human.worldObj.getServer().getPluginManager().callEvent(event); // MCPC+ - allow vanilla mods to bypass
        human.openContainer.transferTo(human.inventoryContainer, human.getBukkitEntity());
    }

    public static void handleEditBookEvent(net.minecraft.entity.player.EntityPlayerMP player, net.minecraft.item.ItemStack newBookItem) {
        int itemInHandIndex = player.inventory.currentItem;

        PlayerEditBookEvent editBookEvent = new PlayerEditBookEvent(player.getBukkitEntity(), player.inventory.currentItem, (BookMeta) CraftItemStack.getItemMeta(player.inventory.getCurrentItem()), (BookMeta) CraftItemStack.getItemMeta(newBookItem), newBookItem.itemID == net.minecraft.item.Item.writtenBook.itemID);
        player.worldObj.getServer().getPluginManager().callEvent(editBookEvent);
        net.minecraft.item.ItemStack itemInHand = player.inventory.getStackInSlot(itemInHandIndex);

        // If they've got the same item in their hand, it'll need to be updated.
        if (itemInHand.itemID == net.minecraft.item.Item.writableBook.itemID) {
            if (!editBookEvent.isCancelled()) {
                CraftItemStack.setItemMeta(itemInHand, editBookEvent.getNewBookMeta());
                if (editBookEvent.isSigning()) {
                    itemInHand.itemID = net.minecraft.item.Item.writtenBook.itemID;
                }
            }

            // Client will have updated its idea of the book item; we need to overwrite that
            net.minecraft.inventory.Slot slot = player.openContainer.getSlotFromInventory((net.minecraft.inventory.IInventory) player.inventory, itemInHandIndex);
            player.playerNetServerHandler.sendPacketToPlayer(new net.minecraft.network.packet.Packet103SetSlot(player.openContainer.windowId, slot.slotNumber, itemInHand));
        }
    }
}
