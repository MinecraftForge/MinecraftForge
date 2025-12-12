/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionBrewing.Builder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingDrownEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent.AllowDespawn;
import net.minecraftforge.event.entity.living.MobSpawnEvent.PositionCheck;
import net.minecraftforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent.ProgressType;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PermissionsChangedEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.AlterGroundEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.BlockEvent.BlockToolModificationEvent;
import net.minecraftforge.event.level.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.level.BlockEvent.EntityMultiPlaceEvent;
import net.minecraftforge.event.level.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.ChunkTicketLevelUpdatedEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.network.ChannelRegistrationChangeEvent;
import net.minecraftforge.event.network.ConnectionStartEvent;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
import net.minecraftforge.fml.LogicalSide;

@ApiStatus.Internal
public final class ForgeEventFactory {
    private ForgeEventFactory() {}

    public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
        var snap = blockSnapshots.getFirst();
        var placedAgainst = snap.getLevel().getBlockState(snap.getPos().relative(direction.getOpposite()));
        return EntityMultiPlaceEvent.BUS.post(new EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity));
    }

    public static boolean onBlockPlace(@Nullable Entity entity, @NonNull BlockSnapshot blockSnapshot, @NonNull Direction direction) {
        var placedAgainst = blockSnapshot.getLevel().getBlockState(blockSnapshot.getPos().relative(direction.getOpposite()));
        return BlockEvent.EntityPlaceEvent.BUS.post(new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity));
    }

    public static boolean onNeighborNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
        return NeighborNotifyEvent.BUS.post(new NeighborNotifyEvent(level, pos, state, notifiedSides, forceRedstoneUpdate));
    }

    public static boolean doPlayerHarvestCheck(Player player, BlockState state, boolean success) {
        return PlayerEvent.HarvestCheck.BUS.fire(new PlayerEvent.HarvestCheck(player, state, success)).canHarvest();
    }

    public static float getBreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
        var event = new PlayerEvent.BreakSpeed(player, state, original, pos);
        return PlayerEvent.BreakSpeed.BUS.post(event) ? -1 : event.getNewSpeed();
    }

    public static void onPlayerDestroyItem(Player player, @NonNull ItemStack stack, @Nullable InteractionHand hand) {
        onPlayerDestroyItem(player, stack, hand.asEquipmentSlot());
    }

    public static void onPlayerDestroyItem(Player player, @NonNull ItemStack stack, @Nullable EquipmentSlot slot) {
        PlayerDestroyItemEvent.BUS.post(new PlayerDestroyItemEvent(player, stack, slot));
    }

    public static boolean checkSpawnPlacements(EntityType<?> entityType, ServerLevelAccessor level, EntitySpawnReason spawnType, BlockPos pos, RandomSource random, boolean defaultResult) {
        var result = SpawnPlacementCheck.BUS.fire(new SpawnPlacementCheck(entityType, level, spawnType, pos, random, defaultResult)).getResult();
        return result == Result.DEFAULT ? defaultResult : result == Result.ALLOW;
    }

    /**
     * Checks if the current position of the passed mob is valid for spawning, by firing {@link PositionCheck}.<br>
     * The default check is to perform the logical and of {@link Mob#checkSpawnRules} and {@link Mob#checkSpawnObstruction}.<br>
     * @param mob The mob being spawned.
     * @param level The level the mob will be added to, if successful.
     * @param spawnType The spawn type of the spawn.
     * @return True, if the position is valid, as determined by the contract of {@link PositionCheck}.
     * @see PositionCheck
     */
    public static boolean checkSpawnPosition(Mob mob, ServerLevelAccessor level, EntitySpawnReason spawnType) {
        var result = PositionCheck.BUS.fire(new PositionCheck(mob, level, spawnType, null)).getResult();
        if (result == Result.DEFAULT)
            return mob.checkSpawnRules(level, spawnType) && mob.checkSpawnObstruction(level);
        return result == Result.ALLOW;
    }

    /**
     * Specialized variant of {@link #checkSpawnPosition} for spawners, as they have slightly different checks.
     * @see #checkSpawnPosition(Mob, ServerLevelAccessor, EntitySpawnReason)
     * @implNote See in-line comments about custom spawn rules.
     */
    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, EntitySpawnReason spawnType, SpawnData spawnData, BaseSpawner spawner) {
        var result = PositionCheck.BUS.fire(new PositionCheck(mob, level, spawnType, null)).getResult();
        if (result == Result.DEFAULT) {
            // Spawners do not evaluate Mob#checkSpawnRules if any custom rules are present. This is despite the fact that these two methods do not check the same things.
            return (spawnData.getCustomSpawnRules().isPresent() || mob.checkSpawnRules(level, spawnType)) && mob.checkSpawnObstruction(level);
        }
        return result == Result.ALLOW;
    }

    /**
     * Vanilla calls to {@link Mob#finalizeSpawn} are replaced with calls to this method via coremod.<br>
     * Mods should call this method in place of calling {@link Mob#finalizeSpawn}. Super calls (from within overrides) should not be wrapped.
     * <p>
     * When interfacing with this event, write all code as normal, and replace the call to {@link Mob#finalizeSpawn} with a call to this method.<p>
     * As an example, the following code block:
     * <code><pre>
     * var zombie = new Zombie(level);
     * zombie.finalizeSpawn(level, difficulty, spawnType, spawnData, spawnTag);
     * level.tryAddFreshEntityWithPassengers(zombie);
     * if (zombie.isAddedToWorld()) {
     *     // Do stuff with your new zombie
     * }
     * </pre></code>
     * Would become:
     * <code><pre>
     * var zombie = new Zombie(level);
     * ForgeEventFactory.onFinalizeSpawn(zombie, level, difficulty, spawnType, spawnData, spawnTag);
     * level.tryAddFreshEntityWithPassengers(zombie);
     * if (zombie.isAddedToWorld()) {
     *     // Do stuff with your new zombie
     * }
     * </pre></code>
     * The only code that changes is the {@link Mob#finalizeSpawn} call.
     * @return The SpawnGroupData from this event, or null if it was canceled. The return value of this method has no bearing on if the entity will be spawned.
     * @see MobSpawnEvent.FinalizeSpawn
     * @see Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)
     * @apiNote Callers do not need to check if the entity's spawn was cancelled, as the spawn will be blocked by Forge.
     * @implNote Changes to the signature of this method must be reflected in the method redirector coremod.
     */
    @Nullable
    @SuppressWarnings("deprecation") // Call to deprecated Mob#finalizeSpawn is expected.
    public static SpawnGroupData onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnData) {
        var event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, spawnType, spawnData, null, null);
        boolean cancel = MobSpawnEvent.FinalizeSpawn.BUS.post(event);

        if (!cancel)
            return mob.finalizeSpawn(level, event.getDifficulty(), event.getSpawnReason(), event.getSpawnData());

        return null;
    }

    /**
     * Returns the FinalizeSpawn event instance, or null if it was canceled.<br>
     * This is separate since mob spawners perform special finalizeSpawn handling when NBT data is present, but we still want to fire the event.<br>
     * This overload is also the only way to pass through a {@link BaseSpawner} instance.
     * @see #onFinalizeSpawn
     */
    public static MobSpawnEvent.@Nullable FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable ValueInput spawnTag, BaseSpawner spawner) {
        var event = new MobSpawnEvent.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, EntitySpawnReason.SPAWNER, spawnData, spawnTag, spawner);
        return MobSpawnEvent.FinalizeSpawn.BUS.post(event) ? null : event;
    }

    public static Result canEntityDespawn(Mob entity, ServerLevelAccessor level) {
        return AllowDespawn.BUS.fire(new AllowDespawn(entity, level)).getResult();
    }

    public static int getItemBurnTime(@NonNull ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType) {
        return FurnaceFuelBurnTimeEvent.BUS.fire(new FurnaceFuelBurnTimeEvent(itemStack, burnTime, recipeType)).getBurnTime();
    }

    public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
       var event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
       if (LivingExperienceDropEvent.BUS.post(event))
           return 0;
       return event.getDroppedExperience();
    }

    public static int getMaxSpawnPackSize(Mob entity) {
        var maxCanSpawnEvent = LivingPackSizeEvent.BUS.fire(new LivingPackSizeEvent(entity));
        return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnClusterSize();
    }

    public static Component getPlayerDisplayName(Player player, Component username) {
        return PlayerEvent.NameFormat.BUS.fire(new PlayerEvent.NameFormat(player, username)).getDisplayname();
    }

    public static Component getPlayerTabListDisplayName(Player player) {
        return PlayerEvent.TabListNameFormat.BUS.fire(new PlayerEvent.TabListNameFormat(player)).getDisplayName();
    }

    public static BlockState fireFluidPlaceBlockEvent(LevelAccessor level, BlockPos pos, BlockPos liquidPos, BlockState state) {
        return BlockEvent.FluidPlaceBlockEvent.BUS.fire(new BlockEvent.FluidPlaceBlockEvent(level, pos, liquidPos, state)).getNewState();
    }

    public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags) {
        return ItemTooltipEvent.BUS.fire(new ItemTooltipEvent(itemStack, entityPlayer, list, flags));
    }

    public static SummonAidEvent fireZombieSummonAid(Zombie zombie, Level level, int x, int y, int z, LivingEntity attacker, double summonChance) {
        return SummonAidEvent.BUS.fire(new SummonAidEvent(zombie, level, x, y, z, attacker, summonChance));
    }

    public static boolean onEntityStruckByLightning(Entity entity, LightningBolt bolt) {
        return EntityStruckByLightningEvent.BUS.post(new EntityStruckByLightningEvent(entity, bolt));
    }

    public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration) {
        var event = new LivingEntityUseItemEvent.Start(entity, item, duration);
        return LivingEntityUseItemEvent.Start.BUS.post(event) ? -1 : event.getDuration();
    }

    public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
        var event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
        return LivingEntityUseItemEvent.Tick.BUS.post(event) ? -1 : event.getDuration();
    }

    public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration) {
        return LivingEntityUseItemEvent.Stop.BUS.post(new LivingEntityUseItemEvent.Stop(entity, item, duration));
    }

    public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
        return LivingEntityUseItemEvent.Finish.BUS.fire(new LivingEntityUseItemEvent.Finish(entity, item, duration, result)).getResultStack();
    }

    public static void onStartEntityTracking(Entity entity, Player player) {
        PlayerEvent.StartTracking.BUS.post(new PlayerEvent.StartTracking(player, entity));
    }

    public static void onStopEntityTracking(Entity entity, Player player) {
        PlayerEvent.StopTracking.BUS.post(new PlayerEvent.StopTracking(player, entity));
    }

    public static void firePlayerLoadingEvent(Player player, File playerDirectory, String uuidString) {
        PlayerEvent.LoadFromFile.BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
    }

    public static void firePlayerSavingEvent(Player player, File playerDirectory, String uuidString) {
        PlayerEvent.SaveToFile.BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
    }

    @Nullable
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, ToolAction toolAction, boolean simulate) {
        var event = new BlockToolModificationEvent(originalState, context, toolAction, simulate);
        return BlockToolModificationEvent.BUS.post(event) ? null : event.getFinalState();
    }

    public static int onApplyBonemeal(@Nullable Player player, Level level, BlockPos pos, BlockState state, ItemStack stack) {
        if (player == null)
            return 0;

        var event = new BonemealEvent(player, level, pos, state, stack);
        if (BonemealEvent.BUS.post(event)) return -1;
        if (event.getResult() == Result.ALLOW) {
            if (!level.isClientSide())
                stack.shrink(1);
            return 1;
        }
        return 0;
    }

    @Nullable
    public static InteractionResult onBucketUse(@NonNull Player player, @NonNull Level level, @NonNull ItemStack stack, @Nullable HitResult target) {
        var event = new FillBucketEvent(player, stack, level, target);
        if (FillBucketEvent.BUS.post(event))
            return InteractionResult.FAIL;

        if (event.getResult() == Result.ALLOW) {
            if (player.getAbilities().instabuild)
                return InteractionResult.SUCCESS.heldItemTransformedTo(stack);

            stack.shrink(1);
            if (stack.isEmpty())
                return InteractionResult.SUCCESS.heldItemTransformedTo(event.getFilledBucket());

            if (!player.getInventory().add(event.getFilledBucket()))
                player.drop(event.getFilledBucket(), false);

            return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
        }
        return null;
    }

    public static PlayLevelSoundEvent.@Nullable AtEntity onPlaySoundAtEntity(Level level, Entity entity, Holder<SoundEvent> name, SoundSource category, float volume, float pitch) {
        var event = new PlayLevelSoundEvent.AtEntity(level, entity, name, category, volume, pitch);
        return PlayLevelSoundEvent.AtEntity.BUS.post(event) ? null : event;
    }

    public static PlayLevelSoundEvent.AtPosition onPlaySoundAtPosition(Level level, double x, double y, double z, Holder<SoundEvent> name, SoundSource category, float volume, float pitch) {
        var event = new PlayLevelSoundEvent.AtPosition(level, new Vec3(x, y, z), name, category, volume, pitch);
        return PlayLevelSoundEvent.AtPosition.BUS.post(event) ? null : event;
    }

    public static int onItemExpire(ItemEntity entity, @NonNull ItemStack item) {
        if (item.isEmpty()) return -1;
        var event = new ItemExpireEvent(entity, (item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.level())));
        if (!ItemExpireEvent.BUS.post(event)) return -1;
        return event.getExtraLife();
    }

    public static int onItemPickup(ItemEntity entityItem, Player player) {
        var event = new EntityItemPickupEvent(player, entityItem);
        if (EntityItemPickupEvent.BUS.post(event)) return -1;
        return event.getResult() == Result.ALLOW ? 1 : 0;
    }

    public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
        boolean isCanceled = EntityMountEvent.BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.level(), isMounting));

        if (isCanceled) {
            entityMounting.absSnapTo(entityMounting.getX(), entityMounting.getY(), entityMounting.getZ(), entityMounting.yRotO, entityMounting.xRotO);
            return false;
        } else {
            return true;
        }
    }

    public static boolean onAnimalTame(Animal animal, Player tamer) {
        return AnimalTameEvent.BUS.post(new AnimalTameEvent(animal, tamer));
    }

    public static boolean onPlayerPickupXp(Player player, ExperienceOrb orb) {
        return PlayerXpEvent.PickupXp.BUS.post(new PlayerXpEvent.PickupXp(player, orb));
    }

    public static Player.BedSleepingProblem onPlayerSleepInBed(Player player, Optional<BlockPos> pos) {
        return PlayerSleepInBedEvent.BUS.fire(new PlayerSleepInBedEvent(player, pos)).getResultStatus();
    }

    public static void onPlayerWakeup(Player player, boolean wakeImmediately, boolean updateLevel) {
        PlayerWakeUpEvent.BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateLevel));
    }

    public static void onPlayerFall(Player player, float distance, float multiplier) {
        PlayerFlyableFallEvent.BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
    }

    public static boolean onPlayerSpawnSet(ServerPlayer player, ServerPlayer.RespawnConfig config) {
        return PlayerSetSpawnEvent.BUS.post(new PlayerSetSpawnEvent(player, config));
    }

    public static PlayerSpawnPhantomsEvent onPlayerSpawnPhantom(Player player, int phantomsToSpawn) {
        return PlayerSpawnPhantomsEvent.BUS.fire(new PlayerSpawnPhantomsEvent(player, phantomsToSpawn));
    }

    public static void onPlayerClone(Player player, Player oldPlayer, boolean wasDeath) {
        PlayerEvent.Clone.BUS.post(new PlayerEvent.Clone(player, oldPlayer, wasDeath));
    }

    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return ExplosionEvent.Start.BUS.post(new ExplosionEvent.Start(level, explosion));
    }

    public static void onExplosionDetonate(Level level, Explosion explosion, List<BlockPos> blocks, List<Entity> entities, double diameter) {
        ExplosionEvent.Detonate.BUS.post(new ExplosionEvent.Detonate(level, explosion, blocks, entities));
    }

    public static boolean onCreateWorldSpawn(Level level, ServerLevelData settings) {
        return LevelEvent.CreateSpawnPosition.BUS.post(new LevelEvent.CreateSpawnPosition(level, settings));
    }

    public static float onLivingHeal(LivingEntity entity, float amount) {
        var event = new LivingHealEvent(entity, amount);
        return LivingHealEvent.BUS.post(event) ? 0 : event.getAmount();
    }

    public static boolean onPotionAttemptBrew(NonNullList<ItemStack> stacks) {
        var tmp = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int x = 0; x < tmp.size(); x++)
            tmp.set(x, stacks.get(x).copy());

        var event = new PotionBrewEvent.Pre(tmp);
        if (PotionBrewEvent.Pre.BUS.post(event)) {
            boolean changed = false;
            for (int x = 0; x < stacks.size(); x++) {
                changed |= ItemStack.matches(tmp.get(x), stacks.get(x));
                stacks.set(x, event.getItem(x));
            }
            if (changed)
                onPotionBrewed(stacks);
            return true;
        }
        return false;
    }

    public static void onPotionBrewed(NonNullList<ItemStack> brewingItemStacks) {
        PotionBrewEvent.Post.BUS.post(new PotionBrewEvent.Post(brewingItemStacks));
    }

    public static void onPlayerBrewedPotion(Player player, ItemStack stack) {
        PlayerBrewedPotionEvent.BUS.post(new PlayerBrewedPotionEvent(player, stack));
    }

//    @Nullable
//    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
//        return gatherCapabilities(type, provider, null);
//    }

//    @SuppressWarnings("unchecked")
//    @Nullable
//    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
//        return gatherCapabilities(new AttachCapabilitiesEvent<T>((Class<T>) type, provider), parent);
//    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent event, @Nullable ICapabilityProvider parent) {
        return !event.getCapabilities().isEmpty() || parent != null ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent) : null;
    }

    public static boolean fireSleepingLocationCheck(LivingEntity player, BlockPos sleepingLocation) {
        var evt = new SleepingLocationCheckEvent(player, sleepingLocation);
        SleepingLocationCheckEvent.BUS.post(evt);

        Result canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return player.getSleepingPos().map(pos -> player.level().getBlockState(pos).isBed(player.level(), pos, player)).orElse(false);
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static boolean onSleepingTimeCheck(Player player, Optional<BlockPos> sleepingLocation, BedRule rule) {
        var evt = new SleepingTimeCheckEvent(player, sleepingLocation);
        SleepingTimeCheckEvent.BUS.post(evt);

        var canContinueSleep = evt.getResult();
        if (canContinueSleep == Result.DEFAULT)
            return rule.canSleep(player.level());
        else
            return canContinueSleep == Result.ALLOW;
    }

    public static InteractionResult onArrowNock(ItemStack item, Level level, Player player, InteractionHand hand, boolean hasAmmo) {
        var event = new ArrowNockEvent(player, item, hand, level, hasAmmo);
        if (ArrowNockEvent.BUS.post(event))
            return InteractionResult.FAIL;
        return event.getAction();
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        var event = new ArrowLooseEvent(player, stack, level, charge, hasAmmo);
        if (ArrowLooseEvent.BUS.post(event))
            return -1;
        return event.getCharge();
    }

    public static ProjectileImpactEvent.ImpactResult onProjectileImpactResult(Projectile projectile, HitResult ray) {
        return ProjectileImpactEvent.BUS.fire(new ProjectileImpactEvent(projectile, ray)).getImpactResult();
    }

    public static boolean onProjectileImpact(Projectile projectile, HitResult ray) {
        return onProjectileImpactResult(projectile, ray) != ProjectileImpactEvent.ImpactResult.DEFAULT;
    }

    public static @Nullable LootTable onLoadLootTable(Identifier name, LootTable table) {
        var event = new LootTableLoadEvent(name, table);
        return LootTableLoadEvent.BUS.post(event) ? null : event.getTable();
    }

    public static boolean canCreateFluidSource(Level level, BlockPos pos, BlockState state, boolean def) {
        var result = CreateFluidSourceEvent.BUS.fire(new CreateFluidSourceEvent(level, pos, state)).getResult();
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    public static Optional<PortalShape> onTrySpawnPortal(LevelAccessor level, BlockPos pos, Optional<PortalShape> size) {
        if (size.isEmpty()) return size;
        return BlockEvent.PortalSpawnEvent.BUS.post(new BlockEvent.PortalSpawnEvent(level, pos, level.getBlockState(pos), size.get())) ? Optional.empty() : size;
    }

    public static int onEnchantmentLevelSet(Level level, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int enchantmentLevel) {
        return EnchantmentLevelSetEvent.BUS.fire(new EnchantmentLevelSetEvent(level, pos, enchantRow, power, itemStack, enchantmentLevel)).getEnchantLevel();
    }

    public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state) {
        return !LivingDestroyBlockEvent.BUS.post(new LivingDestroyBlockEvent(entity, pos, state));
    }

    public static boolean getMobGriefingEvent(ServerLevel level, @Nullable Entity entity) {
        if (entity == null)
            return level.getGameRules().get(GameRules.MOB_GRIEFING);

        var result = EntityMobGriefingEvent.BUS.fire(new EntityMobGriefingEvent(entity)).getResult();
        return result == Result.DEFAULT ? level.getGameRules().get(GameRules.MOB_GRIEFING) : result == Result.ALLOW;
    }

    @SuppressWarnings("removal")
    public static SaplingGrowTreeEvent blockGrowFeature(LevelAccessor level, RandomSource randomSource, BlockPos pos, @Nullable Holder<ConfiguredFeature<?, ?>> holder) {
        return SaplingGrowTreeEvent.BUS.fire(new SaplingGrowTreeEvent(level, randomSource, pos, holder));
    }

    public static BlockState alterGround(LevelSimulatedReader level, RandomSource random, BlockPos pos, BlockState altered) {
        return AlterGroundEvent.BUS.fire(new AlterGroundEvent(level, random, pos, altered)).getNewAlteredState();
    }

    public static void fireChunkTicketLevelUpdated(ServerLevel level, long chunkPos, int oldTicketLevel, int newTicketLevel, @Nullable ChunkHolder chunkHolder) {
        if (oldTicketLevel != newTicketLevel)
            ChunkTicketLevelUpdatedEvent.BUS.post(new ChunkTicketLevelUpdatedEvent(level, chunkPos, oldTicketLevel, newTicketLevel, chunkHolder));
    }

    public static void fireChunkWatch(ServerPlayer entity, LevelChunk chunk, ServerLevel level) {
        ChunkWatchEvent.Watch.BUS.post(new ChunkWatchEvent.Watch(entity, chunk, level));
    }

    public static void fireChunkUnWatch(ServerPlayer entity, ChunkPos chunkpos, ServerLevel level) {
        ChunkWatchEvent.UnWatch.BUS.post(new ChunkWatchEvent.UnWatch(entity, chunkpos, level));
    }

    public static boolean onPistonMovePre(Level level, BlockPos pos, Direction direction, boolean extending) {
        return PistonEvent.Pre.BUS.post(new PistonEvent.Pre(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static boolean onPistonMovePost(Level level, BlockPos pos, Direction direction, boolean extending) {
        return PistonEvent.Post.BUS.post(new PistonEvent.Post(level, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
    }

    public static long onSleepFinished(ServerLevel level, long newTime, long minTime) {
        return SleepFinishedTimeEvent.BUS.fire(new SleepFinishedTimeEvent(level, newTime, minTime)).getNewTime();
    }

    public static List<PreparableReloadListener> onResourceReload(ReloadableServerResources serverResources, HolderLookup.Provider lookupProvider) {
        return AddReloadListenerEvent.BUS.fire(new AddReloadListenerEvent(serverResources, lookupProvider)).getListeners();
    }

    public static void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment, CommandBuildContext context) {
        RegisterCommandsEvent.BUS.post(new RegisterCommandsEvent(dispatcher, environment, context));
    }

    public static boolean canLivingConvert(LivingEntity entity, EntityType<? extends LivingEntity> outcome, IntConsumer timer) {
        return !LivingConversionEvent.Pre.BUS.post(new LivingConversionEvent.Pre(entity, outcome, timer));
    }

    public static void onLivingConvert(LivingEntity entity, LivingEntity outcome) {
        LivingConversionEvent.Post.BUS.post(new LivingConversionEvent.Post(entity, outcome));
    }

    public static EntityTeleportEvent.@Nullable TeleportCommand onEntityTeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
        var event = new EntityTeleportEvent.TeleportCommand(entity, targetX, targetY, targetZ);
        return EntityTeleportEvent.TeleportCommand.BUS.post(event) ? null : event;
    }

    public static EntityTeleportEvent.@Nullable SpreadPlayersCommand onEntityTeleportSpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ) {
        var event = new EntityTeleportEvent.SpreadPlayersCommand(entity, targetX, targetY, targetZ);
        return EntityTeleportEvent.SpreadPlayersCommand.BUS.post(event) ? null : event;
    }

    public static EntityTeleportEvent.@Nullable EnderEntity onEnderManTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        var event = new EntityTeleportEvent.EnderEntity(entity, targetX, targetY, targetZ);
        return EntityTeleportEvent.EnderEntity.BUS.post(event) ? null : event;
    }

    public static EntityTeleportEvent.@Nullable EnderPearl onEnderPearlLand(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage, HitResult hitResult) {
        var event = new EntityTeleportEvent.EnderPearl(entity, targetX, targetY, targetZ, pearlEntity, attackDamage, hitResult);
        return EntityTeleportEvent.EnderPearl.BUS.post(event) ? null : event;
    }

    public static EntityTeleportEvent.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        return EntityTeleportEvent.ChorusFruit.BUS.fire(new EntityTeleportEvent.ChorusFruit(entity, targetX, targetY, targetZ));
    }

    public static boolean onPermissionChanged(NameAndId gameProfile, @Nullable LevelBasedPermissionSet newLevel, PlayerList playerList) {
        var oldLevel = playerList.getServer().getProfilePermissions(gameProfile);
        var player = playerList.getPlayer(gameProfile.id());
        if (newLevel != oldLevel && player != null)
            return PermissionsChangedEvent.BUS.post(new PermissionsChangedEvent(player, newLevel, oldLevel));
        return false;
    }

    public static void onPlayerChangedDimension(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim) {
        PlayerEvent.PlayerChangedDimensionEvent.BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player, fromDim, toDim));
    }

    public static void firePlayerLoggedIn(Player player) {
        PlayerEvent.PlayerLoggedInEvent.BUS.post(new PlayerEvent.PlayerLoggedInEvent(player));
    }

    public static void firePlayerLoggedOut(Player player) {
        PlayerEvent.PlayerLoggedOutEvent.BUS.post(new PlayerEvent.PlayerLoggedOutEvent(player));
    }

    public static void firePlayerRespawnEvent(Player player, boolean endConquered) {
        PlayerEvent.PlayerRespawnEvent.BUS.post(new PlayerEvent.PlayerRespawnEvent(player, endConquered));
    }

    public static void firePlayerItemPickupEvent(Player player, ItemEntity item, ItemStack clone) {
        PlayerEvent.ItemPickupEvent.BUS.post(new PlayerEvent.ItemPickupEvent(player, item, clone));
    }

    public static void firePlayerCraftingEvent(Player player, ItemStack crafted, Container craftMatrix) {
        PlayerEvent.ItemCraftedEvent.BUS.post(new PlayerEvent.ItemCraftedEvent(player, crafted, craftMatrix));
    }

    public static void firePlayerSmeltedEvent(Player player, ItemStack smelted) {
        PlayerEvent.ItemSmeltedEvent.BUS.post(new PlayerEvent.ItemSmeltedEvent(player, smelted));
    }

    public static void onPlayerPreTick(Player player) {
        TickEvent.PlayerTickEvent.Pre.BUS.post(new TickEvent.PlayerTickEvent.Pre(player));
    }

    public static void onPlayerPostTick(Player player) {
        TickEvent.PlayerTickEvent.Post.BUS.post(new TickEvent.PlayerTickEvent.Post(player));
    }

    public static void onPreLevelTick(Level level, BooleanSupplier haveTime) {
        TickEvent.LevelTickEvent.Pre.BUS.post(new TickEvent.LevelTickEvent.Pre(level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER, level, haveTime));
    }

    public static void onPostLevelTick(Level level, BooleanSupplier haveTime) {
        TickEvent.LevelTickEvent.Post.BUS.post(new TickEvent.LevelTickEvent.Post(level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER, level, haveTime));
    }

    public static void onPreClientTick() {
        TickEvent.ClientTickEvent.Pre.BUS.post(TickEvent.ClientTickEvent.Pre.INSTANCE);
    }

    public static void onPostClientTick() {
        TickEvent.ClientTickEvent.Post.BUS.post(TickEvent.ClientTickEvent.Post.INSTANCE);
    }

    public static void onPreServerTick(BooleanSupplier haveTime, MinecraftServer server) {
        TickEvent.ServerTickEvent.Pre.BUS.post(new TickEvent.ServerTickEvent.Pre(haveTime, server));
    }

    public static void onPostServerTick(BooleanSupplier haveTime, MinecraftServer server) {
        TickEvent.ServerTickEvent.Post.BUS.post(new TickEvent.ServerTickEvent.Post(haveTime, server));
    }

    public static WeightedList<MobSpawnSettings.SpawnerData> getPotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedList<MobSpawnSettings.SpawnerData> oldList) {
        var event = new LevelEvent.PotentialSpawns(level, category, pos, oldList);
        if (LevelEvent.PotentialSpawns.BUS.post(event))
            return WeightedList.of();
        //System.out.println("List: " + oldList.unwrap() + " " + event.getSpawnerDataList());
        return WeightedList.of(event.getSpawnerDataList());
    }

    public static void onAdvancementEarned(Player player, AdvancementHolder holder) {
        AdvancementEarnEvent.BUS.post(new AdvancementEarnEvent(player, holder));
    }

    public static void onAdvancementGrant(Player player, AdvancementHolder holder, AdvancementProgress advancementProgress, String criterion) {
        AdvancementProgressEvent.BUS.post(new AdvancementProgressEvent(player, holder, advancementProgress, criterion, ProgressType.GRANT));
    }

    public static void onAdvancementRevoke(Player player, AdvancementHolder holder, AdvancementProgress advancementProgress, String criterion) {
        AdvancementProgressEvent.BUS.post(new AdvancementProgressEvent(player, holder, advancementProgress, criterion, ProgressType.REVOKE));
    }

    public static void onEntityConstructing(Entity entity) {
        EntityEvent.EntityConstructing.BUS.post(new EntityEvent.EntityConstructing(entity));
    }

    public static void onPlayerOpenContainer(ServerPlayer player, AbstractContainerMenu menu) {
        PlayerContainerEvent.Open.BUS.post(new PlayerContainerEvent.Open(player, menu));
    }

    public static void onPlayerCloseContainer(ServerPlayer player, AbstractContainerMenu menu) {
        PlayerContainerEvent.Close.BUS.post(new PlayerContainerEvent.Close(player, menu));
    }

    public static boolean onTravelToDimension(Entity entity, ResourceKey<Level> dimension) {
        return EntityTravelToDimensionEvent.BUS.post(new EntityTravelToDimensionEvent(entity, dimension));
    }

    public static void onChunkUnload(ChunkAccess chunk) {
        ChunkEvent.Unload.BUS.post(new ChunkEvent.Unload(chunk));
    }

    public static void onChunkLoad(ChunkAccess chunk, boolean newChunk) {
        ChunkEvent.Load.BUS.post(new ChunkEvent.Load(chunk, newChunk));
    }

    public static void onLevelUnload(Level level) {
        LevelEvent.Unload.BUS.post(new LevelEvent.Unload(level));
    }

    public static void onLevelLoad(Level level) {
        LevelEvent.Load.BUS.post(new LevelEvent.Load(level));
    }

    public static void onLevelSave(Level level) {
        LevelEvent.Save.BUS.post(new LevelEvent.Save(level));
    }

    public static void onChunkDataSave(ChunkAccess chunk, LevelAccessor world, SerializableChunkData data) {
        ChunkDataEvent.Save.BUS.post(new ChunkDataEvent.Save(chunk, world, data));
    }

    public static void onChunkDataLoad(ChunkAccess chunk, SerializableChunkData data, ChunkType status) {
        ChunkDataEvent.Load.BUS.post(new ChunkDataEvent.Load(chunk, data, status));
    }

    public static void onGameShuttingDown() {
        GameShuttingDownEvent.BUS.post(new GameShuttingDownEvent());
    }

    public static void gatherLoginConfigTasks(Connection connection, Consumer<ConfigurationTask> add) {
        GatherLoginConfigurationTasksEvent.BUS.post(new GatherLoginConfigurationTasksEvent(connection, add));
    }

    public static void onConnectionStart(Connection connection) {
        ConnectionStartEvent.BUS.post(new ConnectionStartEvent(connection));
    }

    public static void onChannelRegistrationChange(Connection connection, ChannelRegistrationChangeEvent.Type changeType, HashSet<Identifier> changed) {
        ChannelRegistrationChangeEvent.BUS.post(new ChannelRegistrationChangeEvent(connection, changeType, changed));
    }

    public static LivingSwapItemsEvent.@Nullable Hands onLivingSwapHandItems(LivingEntity entity) {
        var event = new LivingSwapItemsEvent.Hands(entity);
        return LivingSwapItemsEvent.Hands.BUS.post(event) ? null : event;
    }

    public static @Nullable ShieldBlockEvent onShieldBlock(LivingEntity blocker, DamageSource source, float blocked, ItemStack blockedWith) {
        var event = new ShieldBlockEvent(blocker, source, blocked, blockedWith);
        return ShieldBlockEvent.BUS.post(event) ? null : event;
    }

    public static void onEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos) {
        EntityEvent.EnteringSection.BUS.post(new EntityEvent.EnteringSection(entity, packedOldPos, packedNewPos));
    }

    public static boolean onLivingTick(LivingEntity entity) {
        return LivingEvent.LivingTickEvent.BUS.post(new LivingEvent.LivingTickEvent(entity));
    }

    public static @Nullable LivingFallEvent onLivingFall(LivingEntity entity, double distance, float damageMultiplier) {
        var event = new LivingFallEvent(entity, distance, damageMultiplier);
        return LivingFallEvent.BUS.post(event) ? null : event;
    }

    public static LivingBreatheEvent onLivingBreathe(LivingEntity entity, boolean canBreathe, int consumeAirAmount, int refillAirAmount, boolean canRefillAir) {
        return LivingBreatheEvent.BUS.fire(new LivingBreatheEvent(entity, canBreathe, consumeAirAmount, refillAirAmount, canRefillAir));
    }

    public static LivingDrownEvent onLivingDrown(LivingEntity entity, boolean isDrowning, float damageAmount, int bubbleCount) {
        return LivingDrownEvent.BUS.fire(new LivingDrownEvent(entity, isDrowning, damageAmount, bubbleCount));
    }

    public static @Nullable LivingKnockBackEvent onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ) {
        var event = new LivingKnockBackEvent(target, strength, ratioX, ratioZ);
        return LivingKnockBackEvent.BUS.post(event) ? null : event;
    }

    public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
        return LivingDeathEvent.BUS.post(new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit) {
        return LivingDropsEvent.BUS.post(new LivingDropsEvent(entity, source, drops, recentlyHit));
    }

    public static void onLeftClickEmpty(Player player) {
        PlayerInteractEvent.LeftClickEmpty.BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
    }

    public static PlayerInteractEvent.@Nullable LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face, ServerboundPlayerActionPacket.Action action) {
        var event = new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.convert(action));
        return PlayerInteractEvent.LeftClickBlock.BUS.post(event) ? null : event;
    }

    public static boolean isLeftClickBlockCancelled(Player player, BlockPos pos, Direction face, ServerboundPlayerActionPacket.Action action) {
        return PlayerInteractEvent.LeftClickBlock.BUS.post(new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.convert(action)));
    }

    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlockHold(Player player, BlockPos pos, Direction face) {
        var event = new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.CLIENT_HOLD);
        if (PlayerInteractEvent.LeftClickBlock.BUS.post(event)) {
            event.setUseBlock(Result.DENY);
            event.setUseItem(Result.DENY);
        }
        return event;
    }

    public static void onRightClickEmpty(Player player, InteractionHand hand) {
        PlayerInteractEvent.RightClickEmpty.BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    public static void addPackFindersServer(Consumer<RepositorySource> consumer) {
        net.minecraftforge.event.AddPackFindersEvent.BUS.post(new AddPackFindersEvent(PackType.SERVER_DATA, consumer));
    }

    public static boolean onEntityJoinLevel(Entity entity, Level level) {
        return EntityJoinLevelEvent.BUS.post(new EntityJoinLevelEvent(entity, level));
    }
    public static boolean onEntityJoinLevel(Entity entity, Level level, boolean loadedFromDisk) {
        return EntityJoinLevelEvent.BUS.post(new EntityJoinLevelEvent(entity, level, loadedFromDisk));
    }

    public static boolean onEntityLeaveLevel(Entity entity, Level level) {
        return EntityLeaveLevelEvent.BUS.post(new EntityLeaveLevelEvent(entity, level));
    }

    public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty) {
        DifficultyChangeEvent.BUS.post(new DifficultyChangeEvent(difficulty, oldDifficulty));
    }

    public static void onTagsUpdated(RegistryAccess registryAccess, boolean fromClientPacket, boolean isIntegratedServerConnection) {
        TagsUpdatedEvent.BUS.post(new TagsUpdatedEvent(registryAccess, fromClientPacket, isIntegratedServerConnection));
    }

    public static boolean onLivingAttackEntity(LivingEntity entity, DamageSource src, float amount) {
        return LivingAttackEvent.BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static boolean onVanillaGameEvent(Level level, Holder<GameEvent> vanillaEvent, Vec3 pos, GameEvent.Context context) {
        return VanillaGameEvent.BUS.post(new VanillaGameEvent(level, vanillaEvent.get(), pos, context));
    }

    public static boolean onLivingEffectExpire(LivingEntity entity, MobEffectInstance effect) {
        return MobEffectEvent.Expired.BUS.post(new MobEffectEvent.Expired(entity, effect));
    }

    public static boolean onLivingEffectAdd(LivingEntity entity, MobEffectInstance oldEffect, MobEffectInstance newEffect, Entity source) {
        return MobEffectEvent.Added.BUS.post(new MobEffectEvent.Added(entity, oldEffect, newEffect, source));
    }

    public static boolean onLivingEffectRemove(LivingEntity entity, MobEffect effect) {
        return MobEffectEvent.Remove.BUS.post(new MobEffectEvent.Remove(entity, effect));
    }

    public static boolean onLivingEffectRemove(LivingEntity entity, MobEffectInstance effect) {
        return MobEffectEvent.Remove.BUS.post(new MobEffectEvent.Remove(entity, effect));
    }

    public static MobEffectEvent.Applicable onLivingEffectCanApply(LivingEntity entity, MobEffectInstance effect) {
        return MobEffectEvent.Applicable.BUS.fire(new MobEffectEvent.Applicable(entity, effect));
    }

    public static void onLivingEquipmentChange(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to) {
        LivingEquipmentChangeEvent.BUS.post(new LivingEquipmentChangeEvent(entity, slot, from, to));
    }

    public static @Nullable LivingChangeTargetEvent onLivingChangeTargetMob(LivingEntity entity, LivingEntity originalTarget) {
        var event = new LivingChangeTargetEvent(entity, originalTarget, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
        return LivingChangeTargetEvent.BUS.post(event) ? null : event;
    }

    public static @Nullable LivingChangeTargetEvent onLivingChangeTargetBehavior(LivingEntity entity, LivingEntity originalTarget) {
        var event = new LivingChangeTargetEvent(entity, originalTarget, LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET);
        return LivingChangeTargetEvent.BUS.post(event) ? null : event;
    }

    public static PlayerXpEvent.@Nullable XpChange onPlayerXpChange(Player player, int xp) {
        var event = new PlayerXpEvent.XpChange(player, xp);
        return PlayerXpEvent.XpChange.BUS.post(event) ? null : event;
    }

    public static PlayerXpEvent.@Nullable LevelChange onPlayerLevelChange(Player player, int levels) {
        var event = new PlayerXpEvent.LevelChange(player, levels);
        return PlayerXpEvent.LevelChange.BUS.post(event) ? null : event;
    }


    public static GrindstoneEvent.@Nullable OnPlaceItem onGrindstoneChange(@NonNull ItemStack top, @NonNull ItemStack bottom, Container outputSlot, int xp) {
        var event = new GrindstoneEvent.OnPlaceItem(top, bottom, xp);
        return GrindstoneEvent.OnPlaceItem.BUS.post(event) ? null : event;
    }

    public static void onBrewingRecipeRegister(Builder builder, FeatureFlagSet features) {
        BrewingRecipeRegisterEvent.BUS.post(new BrewingRecipeRegisterEvent(builder, features));
    }

    public static boolean onItemStackedOn(ItemStack carriedItem, ItemStack stackedOnItem, Slot slot, ClickAction action, Player player, SlotAccess carriedSlotAccess) {
        return ItemStackedOnOtherEvent.BUS.post(new ItemStackedOnOtherEvent(carriedItem, stackedOnItem, slot, action, player, carriedSlotAccess));
    }

    public static NoteBlockEvent.@Nullable Play onNotePlay(Level world, BlockPos pos, BlockState state, int note, NoteBlockInstrument instrument) {
        var event = new NoteBlockEvent.Play(world, pos, state, note, instrument);
        return NoteBlockEvent.Play.BUS.post(event) ? null : event;
    }

    public static AnvilRepairEvent onAnvilRepair(Player player, @NonNull ItemStack output, @NonNull ItemStack left, @NonNull ItemStack right) {
        return AnvilRepairEvent.BUS.fire(new AnvilRepairEvent(player, left, right, output));
    }

    public static void onPlayerTradeWithVillager(Player player, MerchantOffer offer, AbstractVillager villager) {
        TradeWithVillagerEvent.BUS.post(new TradeWithVillagerEvent(player, offer, villager));
    }

    public static GatherComponentsEvent.Item gatherItemComponentsEvent(Item item, DataComponentMap dataComponents) {
        return GatherComponentsEvent.Item.BUS.fire(new GatherComponentsEvent.Item(item, dataComponents));
    }

    public static LootingLevelEvent fireLootingLevel(LivingEntity target, @Nullable DamageSource cause, int level) {
        return LootingLevelEvent.BUS.fire(new LootingLevelEvent(target, cause, level));
    }

    public static boolean fireFarmlandTrampleEvent(ServerLevel level, BlockPos pos, BlockState state, double fallDistance, Entity entity) {
        return BlockEvent.FarmlandTrampleEvent.BUS.post(new BlockEvent.FarmlandTrampleEvent(level, pos, state, fallDistance, entity));
    }
}
