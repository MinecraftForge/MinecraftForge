/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
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
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.BlockEvent.EntityMultiPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;

public class ForgeEventFactory
{

    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction)
    {
        BlockSnapshot snap = blockSnapshots.get(0);
        BlockState placedAgainst = snap.getWorld().getBlockState(snap.getPos().offset(direction.getOpposite()));
        EntityMultiPlaceEvent event = new EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onBlockPlace(@Nullable Entity entity, @Nonnull BlockSnapshot blockSnapshot, @Nonnull Direction direction)
    {
        BlockState placedAgainst = blockSnapshot.getWorld().getBlockState(blockSnapshot.getPos().offset(direction.getOpposite()));
        EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static NeighborNotifyEvent onNeighborNotify(World world, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate)
    {
        NeighborNotifyEvent event = new NeighborNotifyEvent(world, pos, state, notifiedSides, forceRedstoneUpdate);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean doPlayerHarvestCheck(PlayerEntity player, BlockState state, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canHarvest();
    }

    public static float getBreakSpeed(PlayerEntity player, BlockState state, float original, BlockPos pos)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getNewSpeed());
    }

    public static void onPlayerDestroyItem(PlayerEntity player, @Nonnull ItemStack stack, @Nullable Hand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack, hand));
    }

    public static Result canEntitySpawn(MobEntity entity, IWorld world, double x, double y, double z, AbstractSpawner spawner)
    {
        if (entity == null)
            return Result.DEFAULT;
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z, spawner);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean canEntitySpawnSpawner(MobEntity entity, World world, float x, float y, float z, AbstractSpawner spawner)
    {
        Result result = canEntitySpawn(entity, world, x, y, z, spawner);
        if (result == Result.DEFAULT)
            return entity.canSpawn(world, SpawnReason.SPAWNER) || !entity.isNotColliding(world); // vanilla logic
        else
            return result == Result.ALLOW;
    }

    public static boolean doSpecialSpawn(MobEntity entity, World world, float x, float y, float z, AbstractSpawner spawner)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, x, y, z, spawner));
    }

    public static Result canEntityDespawn(MobEntity entity)
    {
        AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static int getItemBurnTime(@Nonnull ItemStack itemStack, int burnTime)
    {
        FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getBurnTime();
    }

    public static int getExperienceDrop(LivingEntity entity, PlayerEntity attackingPlayer, int originalExperience)
    {
       LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
       if (MinecraftForge.EVENT_BUS.post(event))
       {
           return 0;
       }
       return event.getDroppedExperience();
    }

    @Nullable
    public static List<Biome.SpawnListEntry> getPotentialSpawns(IWorld world, EntityClassification type, BlockPos pos, List<Biome.SpawnListEntry> oldList)
    {
        WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldList);
        if (MinecraftForge.EVENT_BUS.post(event))
            return Collections.emptyList();
        return event.getList();
    }

    public static int getMaxSpawnPackSize(MobEntity entity)
    {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnedInChunk();
    }

    public static String getPlayerDisplayName(PlayerEntity player, String username)
    {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDisplayname();
    }

    public static float fireBlockHarvesting(NonNullList<ItemStack> drops, World world, BlockPos pos, BlockState state, int fortune, float dropChance, boolean silkTouch, PlayerEntity player)
    {
        BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDropChance();
    }

    public static BlockState fireFluidPlaceBlockEvent(World world, BlockPos pos, BlockPos liquidPos, BlockState state)
    {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(world, pos, liquidPos, state);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewState();
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable PlayerEntity entityPlayer, List<ITextComponent> list, ITooltipFlag flags)
    {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, list, flags);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static SummonAidEvent fireZombieSummonAid(ZombieEntity zombie, World world, int x, int y, int z, LivingEntity attacker, double summonChance)
    {
        SummonAidEvent summonEvent = new SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, LightningBoltEntity bolt)
    {
        return MinecraftForge.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, bolt));
    }

    public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration)
    {
        LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Start(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
    }

    public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration)
    {
        LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
    }

    public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Stop(entity, item, duration));
    }

    public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result)
    {
        LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStack();
    }

    public static void onStartEntityTracking(Entity entity, PlayerEntity player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, PlayerEntity player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(PlayerEntity player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(PlayerEntity player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(PlayerEntity player, IPlayerFileData playerFileData, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, ((SaveHandler)playerFileData).getPlayerFolder(), uuidString));
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

    public static int onHoeUse(ItemUseContext context)
    {
        UseHoeEvent event = new UseHoeEvent(context);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            context.getItem().func_222118_a(1, context.getPlayer(), player -> player.func_213334_d(context.func_221531_n()));
            return 1;
        }
        return 0;
    }

    public static int onApplyBonemeal(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ItemStack stack)
    {
        BonemealEvent event = new BonemealEvent(player, world, pos, state, stack);
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
    public static ActionResult<ItemStack> onBucketUse(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull ItemStack stack, @Nullable RayTraceResult target)
    {
        FillBucketEvent event = new FillBucketEvent(player, stack, world, target);
        if (MinecraftForge.EVENT_BUS.post(event)) return new ActionResult<ItemStack>(ActionResultType.FAIL, stack);

        if (event.getResult() == Result.ALLOW)
        {
            if (player.playerAbilities.isCreativeMode)
                return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);

            stack.shrink(1);
            if (stack.isEmpty())
                return new ActionResult<ItemStack>(ActionResultType.SUCCESS, event.getFilledBucket());

            if (!player.inventory.addItemStackToInventory(event.getFilledBucket()))
                player.dropItem(event.getFilledBucket(), false);

            return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
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

    public static int onItemExpire(ItemEntity entity, @Nonnull ItemStack item)
    {
        if (item.isEmpty()) return -1;
        ItemExpireEvent event = new ItemExpireEvent(entity, (item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.world)));
        if (!MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getExtraLife();
    }

    public static int onItemPickup(ItemEntity entityItem, PlayerEntity player)
    {
        Event event = new EntityItemPickupEvent(player, entityItem);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
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

    public static boolean onAnimalTame(AnimalEntity animal, PlayerEntity tamer)
    {
        return MinecraftForge.EVENT_BUS.post(new AnimalTameEvent(animal, tamer));
    }

    public static SleepResult onPlayerSleepInBed(PlayerEntity player, Optional<BlockPos> pos)
    {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStatus();
    }

    public static void onPlayerWakeup(PlayerEntity player, boolean wakeImmediately, boolean updateWorldFlag, boolean setSpawn)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateWorldFlag, setSpawn));
    }

    public static void onPlayerFall(PlayerEntity player, float distance, float multiplier)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(PlayerEntity player, BlockPos pos, boolean forced) {
        return MinecraftForge.EVENT_BUS.post(new PlayerSetSpawnEvent(player, pos, forced));
    }

    public static void onPlayerClone(PlayerEntity player, PlayerEntity oldPlayer, boolean wasDeath)
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
            if (e.isImmuneToExplosions() || dist > 1.0F) itr.remove();
        }
        */
        MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Detonate(world, explosion, list));
    }

    public static boolean onCreateWorldSpawn(World world, WorldSettings settings)
    {
        return MinecraftForge.EVENT_BUS.post(new WorldEvent.CreateSpawnPosition(world, settings));
    }

    public static float onLivingHeal(LivingEntity entity, float amount)
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

    public static void onPlayerBrewedPotion(PlayerEntity player, ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerBrewedPotionEvent(player, stack));
    }

    public static boolean renderFireOverlay(PlayerEntity player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.FIRE, Blocks.FIRE.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderWaterOverlay(PlayerEntity player, float renderPartialTicks)
    {
        return renderBlockOverlay(player, renderPartialTicks, OverlayType.WATER, Blocks.WATER.getDefaultState(), new BlockPos(player));
    }

    public static boolean renderBlockOverlay(PlayerEntity player, float renderPartialTicks, OverlayType type, BlockState block, BlockPos pos)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderBlockOverlayEvent(player, renderPartialTicks, type, block, pos));
    }

    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider)
    {
        return gatherCapabilities(type, provider, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent)
    {
        return gatherCapabilities(new AttachCapabilitiesEvent<T>((Class<T>) type, provider), parent);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent)
    {
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCapabilities().size() > 0 || parent != null ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent) : null;
    }

    public static boolean fireSleepingLocationCheck(LivingEntity player, BlockPos sleepingLocation)
    {
        SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
        {
            return player.func_213374_dv().map(pos-> {
                BlockState state = player.world.getBlockState(pos);
                return state.getBlock().isBed(state, player.world, pos, player);
            }).orElse(false);
        }
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static boolean fireSleepingTimeCheck(PlayerEntity player, Optional<BlockPos> sleepingLocation)
    {
        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return !player.world.isDaytime();
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static ActionResult<ItemStack> onArrowNock(ItemStack item, World world, PlayerEntity player, Hand hand, boolean hasAmmo)
    {
        ArrowNockEvent event = new ArrowNockEvent(player, item, hand, world, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<ItemStack>(ActionResultType.FAIL, item);
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, World world, PlayerEntity player, int charge, boolean hasAmmo)
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

    public static boolean onProjectileImpact(AbstractArrowEntity arrow, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Arrow(arrow, ray));
    }

    public static boolean onProjectileImpact(DamagingProjectileEntity fireball, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Fireball(fireball, ray));
    }

    public static boolean onProjectileImpact(ThrowableEntity throwable, RayTraceResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Throwable(throwable, ray));
    }

    public static LootTable loadLootTable(ResourceLocation name, LootTable table, LootTableManager lootTableManager)
    {
        LootTableLoadEvent event = new LootTableLoadEvent(name, table, lootTableManager);
        if (MinecraftForge.EVENT_BUS.post(event))
            return LootTable.EMPTY_LOOT_TABLE;
        return event.getTable();
    }

    public static boolean canCreateFluidSource(World world, BlockPos pos, BlockState state, boolean def)
    {
        CreateFluidSourceEvent evt = new CreateFluidSourceEvent(world, pos, state);
        MinecraftForge.EVENT_BUS.post(evt);

        Result result = evt.getResult();
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, NetherPortalBlock.Size size)
    {
        return MinecraftForge.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
    }

    public static int onEnchantmentLevelSet(World world, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int level)
    {
        net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent e = new net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent(world, pos, enchantRow, power, itemStack, level);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e);
        return e.getLevel();
    }

    public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state)
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

    public static boolean saplingGrowTree(IWorld world, Random rand, BlockPos pos)
    {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    public static void fireChunkWatch(boolean watch, ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld world)
    {
        if (watch)
            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(entity, chunkpos, world));
        else
            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(entity, chunkpos, world));
    }
}
