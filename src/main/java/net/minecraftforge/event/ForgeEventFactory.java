/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.ServerResources;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.render.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.render.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
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
import net.minecraftforge.event.entity.player.PermissionsChangedEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.BlockEvent.BlockToolInteractEvent;
import net.minecraftforge.event.level.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.level.BlockEvent.EntityMultiPlaceEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.level.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;

public class ForgeEventFactory
{
    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction)
    {
        BlockSnapshot snap = blockSnapshots.get(0);
        BlockState placedAgainst = snap.getWorld().getBlockState(snap.getPos().relative(direction.getOpposite()));
        EntityMultiPlaceEvent event = new EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onBlockPlace(@Nullable Entity entity, @Nonnull BlockSnapshot blockSnapshot, @Nonnull Direction direction)
    {
        BlockState placedAgainst = blockSnapshot.getWorld().getBlockState(blockSnapshot.getPos().relative(direction.getOpposite()));
        EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static NeighborNotifyEvent onNeighborNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate)
    {
        NeighborNotifyEvent event = new NeighborNotifyEvent(level, pos, state, notifiedSides, forceRedstoneUpdate);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success)
    {
        PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.canHarvest();
    }

    public static float getBreakSpeed(Player player, BlockState state, float original, BlockPos pos)
    {
        PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return (MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getNewSpeed());
    }

    public static void onPlayerDestroyItem(Player player, @Nonnull ItemStack stack, @Nullable InteractionHand hand)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack, hand));
    }

    public static Result canEntitySpawn(Mob entity, LevelAccessor level, double x, double y, double z, BaseSpawner spawner, MobSpawnType spawnReason)
    {
        if (entity == null)
            return Result.DEFAULT;
        LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, level, x, y, z, spawner, spawnReason);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static boolean canEntitySpawnSpawner(Mob entity, Level level, float x, float y, float z, BaseSpawner spawner)
    {
        Result result = canEntitySpawn(entity, level, x, y, z, spawner, MobSpawnType.SPAWNER);
        if (result == Result.DEFAULT)
            return entity.checkSpawnRules(level, MobSpawnType.SPAWNER) && entity.checkSpawnObstruction(level); // vanilla logic (inverted)
        else
            return result == Result.ALLOW;
    }

    public static boolean doSpecialSpawn(Mob entity, Level level, float x, float y, float z, BaseSpawner spawner, MobSpawnType spawnReason)
    {
        return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, level, x, y, z, spawner, spawnReason));
    }

    public static Result canEntityDespawn(Mob entity)
    {
        AllowDespawn event = new AllowDespawn(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static int getItemBurnTime(@Nonnull ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType)
    {
        FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime, recipeType);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getBurnTime();
    }

    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience)
    {
       LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
       if (MinecraftForge.EVENT_BUS.post(event))
       {
           return 0;
       }
       return event.getDroppedExperience();
    }

    public static int getMaxSpawnPackSize(Mob entity)
    {
        LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
        MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnClusterSize();
    }

    public static Component getPlayerDisplayName(Player player, Component username)
    {
        PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDisplayName();
    }
    
    public static Component getPlayerTabListDisplayName(Player player)
    {
        PlayerEvent.TabListNameFormat event = new PlayerEvent.TabListNameFormat(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getDisplayName();
    }

    public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state)
    {
        BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(level, pos, liquidPos, state);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewState();
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags)
    {
        ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, list, flags);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static SummonAidEvent fireZombieSummonAid(Zombie zombie, Level level, int x, int y, int z, LivingEntity attacker, double summonChance)
    {
        SummonAidEvent summonEvent = new SummonAidEvent(zombie, level, x, y, z, attacker, summonChance);
        MinecraftForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

    public static boolean onEntityStruckByLightning(Entity entity, LightningBolt bolt)
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

    public static void onStartEntityTracking(Entity entity, Player player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, Player player)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(Player player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(Player player, File playerDirectory, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerLoadingEvent(Player player, PlayerDataStorage playerFileData, String uuidString)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerFileData.getPlayerDataFolder(), uuidString));
    }

    @Nullable
    public static Component onClientChat(ChatType type, Component message, @Nullable UUID senderUUID)
    {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(type, message, senderUUID);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    @Nonnull
    public static String onClientSendMessage(String message)
    {
        ClientChatEvent event = new ClientChatEvent(message);
        return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
    }

    @Nullable
    public static BlockState onToolUse(BlockState originalState, Level level, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction)
    {
        BlockToolInteractEvent event = new BlockToolInteractEvent(level, pos, originalState, player, stack, toolAction);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getFinalState();
    }

    public static int onApplyBonemeal(@Nonnull Player player, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ItemStack stack)
    {
        BonemealEvent event = new BonemealEvent(player, level, pos, state, stack);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW)
        {
            if (!level.isClientSide)
                stack.shrink(1);
            return 1;
        }
        return 0;
    }

    @Nullable
    public static InteractionResultHolder<ItemStack> onBucketUse(Player player, Level level, ItemStack stack, @Nullable HitResult target)
    {
        FillBucketEvent event = new FillBucketEvent(player, stack, level, target);
        if (MinecraftForge.EVENT_BUS.post(event)) return new InteractionResultHolder<>(InteractionResult.FAIL, stack);

        if (event.getResult() == Result.ALLOW)
        {
            if (player.getAbilities().instabuild)
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

            stack.shrink(1);
            if (stack.isEmpty())
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, event.getFilledBucket());

            if (!player.getInventory().add(event.getFilledBucket()))
                player.drop(event.getFilledBucket(), false);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return null;
    }

    public static boolean canEntityUpdate(Entity entity)
    {
        EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(entity);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCanUpdate();
    }

    public static PlaySoundAtEntityEvent onPlaySoundAtEntity(Entity entity, SoundEvent name, SoundSource category, float volume, float pitch)
    {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, name, category, volume, pitch);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static int onItemExpire(ItemEntity entity, @Nonnull ItemStack item)
    {
        if (item.isEmpty()) return -1;
        ItemExpireEvent event = new ItemExpireEvent(entity, (item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.level)));
        if (!MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getExtraLife();
    }

    public static int onItemPickup(ItemEntity entityItem, Player player)
    {
        Event event = new EntityItemPickupEvent(player, entityItem);
        if (MinecraftForge.EVENT_BUS.post(event)) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting)
    {
        boolean isCanceled = MinecraftForge.EVENT_BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.level, isMounting));

        if(isCanceled)
        {
            entityMounting.absMoveTo(entityMounting.getX(), entityMounting.getY(), entityMounting.getZ(), entityMounting.yRotO, entityMounting.xRotO);
            return false;
        }
        else
            return true;
    }

    public static boolean onAnimalTame(Animal animal, Player tamer)
    {
        return MinecraftForge.EVENT_BUS.post(new AnimalTameEvent(animal, tamer));
    }

    @Nullable
    public static BedSleepingProblem onPlayerSleepInBed(Player player, Optional<BlockPos> pos)
    {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultStatus();
    }

    public static void onPlayerWakeup(Player player, boolean wakeImmediately, boolean updateWorldFlag)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateWorldFlag));
    }

    public static void onPlayerFall(Player player, float distance, float multiplier)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(Player player, ResourceKey<Level> level, BlockPos pos, boolean forced)
    {
        return MinecraftForge.EVENT_BUS.post(new PlayerSetSpawnEvent(player, level, pos, forced));
    }

    public static void onPlayerClone(Player player, Player oldPlayer, boolean wasDeath)
    {
        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerEvent.Clone(player, oldPlayer, wasDeath));
    }

    public static boolean onExplosionStart(Level world, Explosion explosion)
    {
        return MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion));
    }

    public static void onExplosionDetonate(Level world, Explosion explosion, List<Entity> list, double diameter)
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

    public static boolean onCreateWorldSpawn(Level level, ServerLevelData levelData)
    {
        return MinecraftForge.EVENT_BUS.post(new LevelEvent.CreateSpawnPosition(level, levelData));
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
                changed |= ItemStack.matches(tmp.get(x), stacks.get(x));
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

    public static void onPlayerBrewedPotion(Player player, ItemStack stack)
    {
        MinecraftForge.EVENT_BUS.post(new PlayerBrewedPotionEvent(player, stack));
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderFireOverlay(Player player, PoseStack mat)
    {
        return renderBlockOverlay(player, mat, OverlayType.FIRE, Blocks.FIRE.defaultBlockState(), player.blockPosition());
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderWaterOverlay(Player player, PoseStack mat)
    {
        return renderBlockOverlay(player, mat, OverlayType.WATER, Blocks.WATER.defaultBlockState(), player.blockPosition());
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderBlockOverlay(Player player, PoseStack mat, OverlayType type, BlockState block, BlockPos pos)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderBlockOverlayEvent(player, mat, type, block, pos));
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
            return player.getSleepingPos().map(pos-> {
                BlockState state = player.level.getBlockState(pos);
                return state.getBlock().isBed(state, player.level, pos, player);
            }).orElse(false);
        }
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static boolean fireSleepingTimeCheck(Player player, Optional<BlockPos> sleepingLocation)
    {
        SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        MinecraftForge.EVENT_BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return !player.level.isDay();
        else
            return canContinueSleep == Result.ALLOW;
    }

    @Nullable
    public static InteractionResultHolder<ItemStack> onArrowNock(ItemStack item, Level level, Player player, InteractionHand hand, boolean hasAmmo)
    {
        ArrowNockEvent event = new ArrowNockEvent(player, item, hand, level, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new InteractionResultHolder<>(InteractionResult.FAIL, item);
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo)
    {
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, level, charge, hasAmmo);
        if (MinecraftForge.EVENT_BUS.post(event))
            return -1;
        return event.getCharge();
    }

    public static boolean onProjectileImpact(Projectile projectile, HitResult ray)
    {
        return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(projectile, ray));
    }

    public static LootTable loadLootTable(ResourceLocation name, LootTable table, LootTables lootTableManager)
    {
        LootTableLoadEvent event = new LootTableLoadEvent(name, table, lootTableManager);
        if (MinecraftForge.EVENT_BUS.post(event))
            return LootTable.EMPTY;
        return event.getTable();
    }

    public static boolean canCreateFluidSource(LevelReader level, BlockPos pos, BlockState state, boolean def)
    {
        CreateFluidSourceEvent evt = new CreateFluidSourceEvent(level, pos, state);
        MinecraftForge.EVENT_BUS.post(evt);

        Result result = evt.getResult();
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    public static Optional<PortalShape> onTrySpawnPortal(LevelAccessor level, BlockPos pos, Optional<PortalShape> size)
    {
        if (!size.isPresent()) return size;
        return !MinecraftForge.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(level, pos, level.getBlockState(pos), size.get())) ? size : Optional.empty();
    }

    public static int onEnchantmentLevelSet(Level level, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int enchantLevel)
    {
        EnchantmentLevelSetEvent e = new EnchantmentLevelSetEvent(level, pos, enchantRow, power, itemStack, enchantLevel);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e);
        return e.getEnchantLevel();
    }

    public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingDestroyBlockEvent(entity, pos, state));
    }

    public static boolean getMobGriefingEvent(Level level, Entity entity)
    {
        EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
        MinecraftForge.EVENT_BUS.post(event);

        Result result = event.getResult();
        return result == Result.DEFAULT ? level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) : result == Result.ALLOW;
    }

    public static boolean saplingGrowTree(LevelAccessor level, Random rand, BlockPos pos)
    {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(level, rand, pos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    public static void fireChunkWatch(boolean watch, ServerPlayer entity, ChunkPos pos, ServerLevel level)
    {
        if (watch)
            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(entity, pos, level));
        else
            MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(entity, pos, level));
    }

    public static void fireChunkWatch(boolean wasLoaded, boolean load, ServerPlayer entity, ChunkPos pos, ServerLevel level)
    {
        if (wasLoaded != load)
            fireChunkWatch(load, entity, pos, level);
    }

    public static boolean onPistonMovePre(Level level, BlockPos pos, Direction direction, boolean extending)
    {
        return MinecraftForge.EVENT_BUS.post(new PistonEvent.Pre(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static boolean onPistonMovePost(Level level, BlockPos pos, Direction direction, boolean extending)
    {
        return MinecraftForge.EVENT_BUS.post(new PistonEvent.Post(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static long onSleepFinished(ServerLevel level, long newTime, long minTime)
    {
        SleepFinishedTimeEvent event = new SleepFinishedTimeEvent(level, newTime, minTime);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewTime();
    }

    public static List<PreparableReloadListener> onResourceReload(ServerResources serverResources)
    {
        AddReloadListenerEvent event = new AddReloadListenerEvent(serverResources);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getListeners();
    }

    public static void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection)
    {
        RegisterCommandsEvent event = new RegisterCommandsEvent(dispatcher, selection);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static net.minecraftforge.event.entity.EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions size, float eyeHeight)
    {
        EntityEvent.Size evt = new EntityEvent.Size(entity, pose, size, eyeHeight);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static net.minecraftforge.event.entity.EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float newEyeHeight)
    {
        EntityEvent.Size evt = new EntityEvent.Size(entity, pose, oldSize, newSize, entity.getEyeHeight(), newEyeHeight);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> outcome, Consumer<Integer> timer)
    {
        return !MinecraftForge.EVENT_BUS.post(new LivingConversionEvent.Pre(entity, outcome, timer));
    }

    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome)
    {
        MinecraftForge.EVENT_BUS.post(new LivingConversionEvent.Post(entity, outcome));
    }

    public static EntityTeleportEvent.TeleportCommand onEntityTeleportCommand(Entity entity, double targetX, double targetY, double targetZ)
    {
        EntityTeleportEvent.TeleportCommand event = new EntityTeleportEvent.TeleportCommand(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.SpreadPlayersCommand onEntityTeleportSpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ)
    {
        EntityTeleportEvent.SpreadPlayersCommand event = new EntityTeleportEvent.SpreadPlayersCommand(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.EnderEntity onEnderTeleport(LivingEntity entity, double targetX, double targetY, double targetZ)
    {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.EnderPearl onEnderPearlLand(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage)
    {
        EntityTeleportEvent.EnderPearl event = new EntityTeleportEvent.EnderPearl(entity, targetX, targetY, targetZ, pearlEntity, attackDamage);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static EntityTeleportEvent.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ)
    {
        EntityTeleportEvent.ChorusFruit event = new EntityTeleportEvent.ChorusFruit(entity, targetX, targetY, targetZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onPermissionChanged(GameProfile gameProfile, int newLevel, PlayerList playerList)
    {
        int oldLevel = playerList.getServer().getProfilePermissions(gameProfile);
        ServerPlayer player = playerList.getPlayer(gameProfile.getId());
        if (newLevel != oldLevel && player != null)
        {
            return MinecraftForge.EVENT_BUS.post(new PermissionsChangedEvent(player, newLevel, oldLevel));
        }
        return true;
    }
}
