/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.Village;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.ThrowableImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.BlockEvent.MultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForgeEventFactory
{

    public static MultiPlaceEvent onPlayerMultiBlockPlace(EntityPlayer player, List<BlockSnapshot> blockSnapshots, EnumFacing direction, EnumHand hand)
    {
        BlockSnapshot snap = blockSnapshots.get(0);
        IBlockState placedAgainst = snap.getWorld().getBlockState(snap.getPos().offset(direction.getOpposite()));
        MultiPlaceEvent event = new MultiPlaceEvent(blockSnapshots, placedAgainst, player, hand);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static PlaceEvent onPlayerBlockPlace(@Nonnull EntityPlayer player, @Nonnull BlockSnapshot blockSnapshot, @Nonnull EnumFacing direction, @Nonnull EnumHand hand)
    {
        IBlockState placedAgainst = blockSnapshot.getWorld().getBlockState(blockSnapshot.getPos().offset(direction.getOpposite()));
        PlaceEvent event = new PlaceEvent(blockSnapshot, placedAgainst, player, hand);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static NeighborNotifyEvent onNeighborNotify(World world, BlockPos pos, IBlockState state, EnumSet<EnumFacing> notifiedSides, boolean forceRedstoneUpdate)
    {
        NeighborNotifyEvent event = new NeighborNotifyEvent(world, pos, state, notifiedSides, forceRedstoneUpdate);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean doPlayerHarvestCheck(EntityPlayer player, IBlockState state, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canHarvest();
    }

    public static float getBreakSpeed(EntityPlayer player, IBlockState state, float original, BlockPos pos)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getNewSpeed());
    }

    public static void onPlayerDestroyItem(EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack, hand));
    }

    /**
     * @deprecated use {@link #canEntitySpawn(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)} instead
     */
    @Deprecated // TODO remove in 1.13
    public static Result canEntitySpawn(EntityLiving entity, World world, float x, float y, float z)
    {
        return canEntitySpawn(entity, world, x, y, z, true);
    }
    /**
     * @deprecated use {@link #canEntitySpawn(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)} instead
     */
    @Deprecated // Still used in base game for non-spawner spawns, which is safe
    public static Result canEntitySpawn(EntityLiving entity, World world, float x, float y, float z, boolean isSpawner)
    {
        if (entity == null)
            return Result.DEFAULT;
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z, isSpawner); // TODO: replace isSpawner with null in 1.13
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static Result canEntitySpawn(EntityLiving entity, World world, float x, float y, float z, MobSpawnerBaseLogic spawner)
    {
        if (entity == null)
            return Result.DEFAULT;
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z, spawner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean canEntitySpawnSpawner(EntityLiving entity, World world, float x, float y, float z, MobSpawnerBaseLogic spawner)
    {
        Result result = canEntitySpawn(entity, world, x, y, z, spawner);
        if (result == Result.DEFAULT)
        {
            return entity.getCanSpawnHere() && entity.isNotColliding(); // vanilla logic
        }
        else
        {
            return result == Result.ALLOW;
        }
    }

    /**
     * @deprecated Use {@link #canEntitySpawnSpawner(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)}
     */
    @Deprecated // TODO remove in 1.13
    public static boolean canEntitySpawnSpawner(EntityLiving entity, World world, float x, float y, float z)
    {
        Result result = canEntitySpawn(entity, world, x, y, z, true);
        if (result == Result.DEFAULT)
        {
            return entity.getCanSpawnHere() && entity.isNotColliding(); // vanilla logic
        }
        else
        {
            return result == Result.ALLOW;
        }
    }

    /**
     * @deprecated Use {@link #canEntitySpawnSpawner(EntityLiving, World, float, float, float, MobSpawnerBaseLogic)}
     */
    @Deprecated // Still used in base game for non-spawner spawns, which is safe
    public static boolean doSpecialSpawn(EntityLiving entity, World world, float x, float y, float z)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z, null));
    }

    public static boolean doSpecialSpawn(EntityLiving entity, World world, float x, float y, float z, MobSpawnerBaseLogic spawner)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z, spawner));
    }

    public static Result canEntityDespawn(EntityLiving entity)
    {
        AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static int getItemBurnTime(@Nonnull ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        int burnTime = item.getItemBurnTime(itemStack);
        FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getBurnTime() < 0)
        {
            // legacy handling
            int fuelValue = GameRegistry.getFuelValueLegacy(itemStack);
            if (fuelValue > 0)
            {
                return fuelValue;
            }
        }
        return event.getBurnTime();
    }

    public static int getExperienceDrop(EntityLivingBase entity, EntityPlayer attackingPlayer, int originalExperience)
    {
       LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
       if (MinecraftForge.EVENT_BUS.post(event))
       {
           return 0;
       }
       return event.getDroppedExperience();
    }

    @Nullable
    public static List<Biome.SpawnListEntry> getPotentialSpawns(WorldServer world, EnumCreatureType type, BlockPos pos, List<Biome.SpawnListEntry> oldList)
    {
        WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return null;
        }
        return event.getList();
    }

    public static int getMaxSpawnPackSize(EntityLiving entity)
    {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnedInChunk();
    }

    public static String getPlayerDisplayName(EntityPlayer player, String username)
    {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDisplayname();
    }

    public static float fireBlockHarvesting(List<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, float dropChance, boolean silkTouch, EntityPlayer player)
    {
        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDropChance();
    }

    public static IBlockState fireFluidPlaceBlockEvent(World world, BlockPos pos, BlockPos liquidPos, IBlockState state)
    {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(world, pos, liquidPos, state);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewState();
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable EntityPlayer entityPlayer, List<String> toolTip, ITooltipFlag flags)
    {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, toolTip, flags);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static SummonAidEvent fireZombieSummonAid(EntityZombie zombie, World world, int x, int y, int z, EntityLivingBase attacker, double summonChance)
    {
        SummonAidEvent summonEvent = new SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, EntityLightningBolt bolt)
    {
        return MinecraftForge.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, bolt));
    }

    public static int onItemUseStart(EntityLivingBase entity, ItemStack item, int duration)
    {
        LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Start(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
    }

    public static int onItemUseTick(EntityLivingBase entity, ItemStack item, int duration)
    {
        LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
    }

    public static boolean onUseItemStop(EntityLivingBase entity, ItemStack item, int duration)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Stop(entity, item, duration));
    }

    public static ItemStack onItemUseFinish(EntityLivingBase entity, ItemStack item, int duration, ItemStack result)
    {
        LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStack();
    }

    public static void onStartEntityTracking(Entity entity, EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(EntityPlayer player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(EntityPlayer player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(EntityPlayer player, IPlayerFileData playerFileData, String uuidString)
    {
        SaveHandler sh = (SaveHandler) playerFileData;
        File dir = ObfuscationReflectionHelper.getPrivateValue(SaveHandler.class, sh, "playersDirectory", "field_"+"75771_c");
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, dir, uuidString));
    }

    @Nullable
    public static ITextComponent onClientChat(ChatType type, ITextComponent message)
    {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(type, message);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    @Nonnull
    public static String onClientSendMessage(String message)
    {
        ClientChatEvent event = new ClientChatEvent(message);
        return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
    }

    public static int onHoeUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos)
    {
        UseHoeEvent event = new UseHoeEvent(player, stack, worldIn, pos);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            stack.damageItem(1, player);
            return 1;
        }
        return 0;
    }


    public static int onApplyBonemeal(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull ItemStack stack, @Nullable EnumHand hand)
    {
        BonemealEvent event = new BonemealEvent(player, world, pos, state, hand, stack);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            if (!world.isRemote)
                stack.shrink(1);
            return 1;
        }
        return 0;
    }

    @Nullable
    public static ActionResult<ItemStack> onBucketUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull ItemStack stack, @Nullable RayTraceResult target)
    {
        FillBucketEvent event = new FillBucketEvent(player, stack, world, target);
        if (MinecraftForge.EVENT_BUS.post(event)) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

        if (event.getResult() == Result.ALLOW)
        {
            if (player.capabilities.isCreativeMode)
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

            stack.shrink(1);
            if (stack.isEmpty())
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, event.getFilledBucket());

            if (!player.inventory.addItemStackToInventory(event.getFilledBucket()))
                player.dropItem(event.getFilledBucket(), false);

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return null;
    }

    public static boolean canEntityUpdate(Entity entity)
    {
        EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCanUpdate();
    }

    public static PlaySoundAtEntityEvent onPlaySoundAtEntity(Entity entity, SoundEvent name, SoundCategory category, float volume, float pitch)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, name, category, volume, pitch);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static int onItemExpire(EntityItem entity, @Nonnull ItemStack item)
    {
        if (item.isEmpty()) return -1;
        ItemExpireEvent event = new ItemExpireEvent(entity, (item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.world)));
        if (!MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getExtraLife();
    }

    public static int onItemPickup(EntityItem entityItem, EntityPlayer player)
    {
        Event event = new EntityItemPickupEvent(player, entityItem);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
    }

    public static void onPlayerDrops(EntityPlayer player, DamageSource cause, List<EntityItem> capturedDrops, boolean recentlyHit)
    {
        PlayerDropsEvent event = new PlayerDropsEvent(player, cause, capturedDrops, recentlyHit);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            for (EntityItem item : capturedDrops)
            {
                player.dropItemAndGetStack(item);
            }
        }
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting)
    {
        boolean isCanceled = MinecraftForge.EVENT_BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.world, isMounting));

        if(isCanceled)
        {
            entityMounting.setPositionAndRotation(entityMounting.posX, entityMounting.posY, entityMounting.posZ, entityMounting.prevRotationYaw, entityMounting.prevRotationPitch);
            return false;
        }
        else
            return true;
    }

    public static boolean onAnimalTame(EntityAnimal animal, EntityPlayer tamer)
    {
        return MinecraftForge.EVENT_BUS.post(new AnimalTameEvent(animal, tamer));
    }

    public static SleepResult onPlayerSleepInBed(EntityPlayer player, BlockPos pos)
    {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStatus();
    }

    public static void onPlayerWakeup(EntityPlayer player, boolean wakeImmediately, boolean updateWorldFlag, boolean setSpawn)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateWorldFlag, setSpawn));
    }

    public static void onPlayerFall(EntityPlayer player, float distance, float multiplier)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(EntityPlayer player, BlockPos pos, boolean forced) {
        return MinecraftForge.EVENT_BUS.post(new PlayerSetSpawnEvent(player, pos, forced));
    }

    public static void onPlayerClone(EntityPlayer player, EntityPlayer oldPlayer, boolean wasDeath)
    {
        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(player, oldPlayer, wasDeath));
    }

    public static boolean onExplosionStart(World world, Explosion explosion)
    {
        return MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion));
    }

    public static void onExplosionDetonate(World world, Explosion explosion, List<Entity> list, double diameter)
    {
        //Filter entities to only those who are effected, to prevent modders from seeing more then will be hurt.
        /* Enable this if we get issues with modders looping to much.
        Iterator<Entity> itr = list.iterator();
        Vec3 p = explosion.getPosition();
        while (itr.hasNext())
        {
            Entity e = itr.next();
            double dist = e.getDistance(p.xCoord, p.yCoord, p.zCoord) / diameter;
            if (e.func_180427_aV() || dist > 1.0F) itr.remove();
        }
        */
        MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Detonate(world, explosion, list));
    }

    public static boolean onCreateWorldSpawn(World world, WorldSettings settings)
    {
        return MinecraftForge.EVENT_BUS.post(new WorldEvent.CreateSpawnPosition(world, settings));
    }

    public static float onLivingHeal(EntityLivingBase entity, float amount)
    {
        LivingHealEvent event = new LivingHealEvent(entity, amount);
        return (MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getAmount());
    }

    public static boolean onPotionAttemptBrew(NonNullList<ItemStack> stacks)
    {
        NonNullList<ItemStack> tmp = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int x = 0; x < tmp.size(); x++)
            tmp.set(x, stacks.get(x).copy());

        PotionBrewEvent.Pre event = new PotionBrewEvent.Pre(tmp);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            boolean changed = false;
            for (int x = 0; x < stacks.size(); x++)
            {
                changed |= ItemStack.areItemStacksEqual(tmp.get(x), stacks.get(x));
                stacks.set(x, event.getItem(x));
            }
            if (changed)
                onPotionBrewed(stacks);
            return true;
        }
        return false;
    }

    public static void onPotionBrewed(NonNullList<ItemStack> brewingItemStacks)
    {
        MinecraftForge.EVENT_BUS.post(new PotionBrewEvent.Post(brewingItemStacks));
    }

    public static void onPlayerBrewedPotion(EntityPlayer player, ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerBrewedPotionEvent(player, stack));
    }

    public static boolean renderFireOverlay(EntityPlayer player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.FIRE, Blocks.FIRE.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderWaterOverlay(EntityPlayer player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.WATER, Blocks.WATER.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderBlockOverlay(EntityPlayer player, float renderPartialTicks, OverlayType type, IBlockState block, BlockPos pos)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderBlockOverlayEvent(player, renderPartialTicks, type, block, pos));
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(TileEntity tileEntity)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<TileEntity>(TileEntity.class, tileEntity), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Entity entity)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<Entity>(Entity.class, entity), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Village village)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<Village>(Village.class, village), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(ItemStack stack, ICapabilityProvider parent)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<ItemStack>(ItemStack.class, stack), parent);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(World world, ICapabilityProvider parent)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<World>(World.class, world), parent);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Chunk chunk)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<Chunk>(Chunk.class, chunk), null);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCapabilities().size() > 0 || parent != null ? new CapabilityDispatcher(event.getCapabilities(), parent) : null;
    }

    public static boolean fireSleepingLocationCheck(EntityPlayer player, BlockPos sleepingLocation)
    {
        SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
        {
            IBlockState state = player.world.getBlockState(player.bedLocation);
            return state.getBlock().isBed(state, player.world, player.bedLocation, player);
        }
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static boolean fireSleepingTimeCheck(EntityPlayer player, BlockPos sleepingLocation)
    {
        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return !player.world.isDaytime();
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static ActionResult<ItemStack> onArrowNock(ItemStack item, World world, EntityPlayer player, EnumHand hand, boolean hasAmmo)
    {
        ArrowNockEvent event = new ArrowNockEvent(player, item, hand, world, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, item);
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, World world, EntityPlayer player, int charge, boolean hasAmmo)
    {
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, world, charge, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return -1;
        return event.getCharge();
    }

    public static boolean onProjectileImpact(Entity entity, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(entity, ray));
    }

    public static boolean onProjectileImpact(EntityArrow arrow, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Arrow(arrow, ray));
    }

    public static boolean onProjectileImpact(EntityFireball fireball, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Fireball(fireball, ray));
    }

    public static boolean onProjectileImpact(EntityThrowable throwable, RayTraceResult ray)
    {
        boolean oldEvent = MinecraftForge.EVENT_BUS.post(new ThrowableImpactEvent(throwable, ray));
        boolean newEvent = MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Throwable(throwable, ray));
        return oldEvent || newEvent; // TODO: clean up when old event is removed
    }

    public static boolean onReplaceBiomeBlocks(IChunkGenerator gen, int x, int z, ChunkPrimer primer, World world)
    {
        ChunkGeneratorEvent.ReplaceBiomeBlocks event = new ChunkGeneratorEvent.ReplaceBiomeBlocks(gen, x, z, primer, world);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;
    }

    public static void onChunkPopulate(boolean pre, IChunkGenerator gen, World world, Random rand, int x, int z, boolean hasVillageGenerated)
    {
        MinecraftForge.EVENT_BUS.post(pre ? new PopulateChunkEvent.Pre(gen, world, rand, x, z, hasVillageGenerated) : new PopulateChunkEvent.Post(gen, world, rand, x, z, hasVillageGenerated));
    }

    public static LootTable loadLootTable(ResourceLocation name, LootTable table, LootTableManager lootTableManager)
    {
        LootTableLoadEvent event = new LootTableLoadEvent(name, table, lootTableManager);
        if (MinecraftForge.EVENT_BUS.post(event))
            return LootTable.EMPTY_LOOT_TABLE;
        return event.getTable();
    }

    public static boolean canCreateFluidSource(World world, BlockPos pos, IBlockState state, boolean def)
    {
        CreateFluidSourceEvent evt = new CreateFluidSourceEvent(world, pos, state);
        MinecraftForge.EVENT_BUS.post(evt);

        Result result = evt.getResult();
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    public static boolean onTrySpawnPortal(World world, BlockPos pos, BlockPortal.Size size)
    {
        return MinecraftForge.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }

    public static int onEnchantmentLevelSet(World world, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int level)
    {
        net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent e = new net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent(world, pos, enchantRow, power, itemStack, level);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e);
        return e.getLevel();
    }

    public static boolean onEntityDestroyBlock(EntityLivingBase entity, BlockPos pos, IBlockState state)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingDestroyBlockEvent(entity, pos, state));
    }

    public static boolean gatherCollisionBoxes(World world, Entity entity, AxisAlignedBB aabb, List<AxisAlignedBB> outList)
    {
        MinecraftForge.EVENT_BUS.post(new GetCollisionBoxesEvent(world, entity, aabb, outList));
        return outList.isEmpty();
    }

    public static boolean getMobGriefingEvent(World world, Entity entity)
    {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
        MinecraftForge.EVENT_BUS.post(event);

        Result result = event.getResult();
        return result == Result.DEFAULT ? world.getGameRules().getBoolean("mobGriefing") : result == Result.ALLOW;
    }
}
