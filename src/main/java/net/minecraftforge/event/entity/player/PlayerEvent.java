/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import java.io.File;
import java.util.Optional;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import net.minecraftforge.eventbus.api.event.MarkerEvent;
import org.jetbrains.annotations.Nullable;

/**
 * PlayerEvent is fired whenever an event involving a {@link Player} occurs. <br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@MarkerEvent
public sealed interface PlayerEvent extends LivingEvent
        permits AdvancementEvent, AnvilRepairEvent, ArrowLooseEvent, AttackEntityEvent, CriticalHitEvent,
                EntityItemPickupEvent, PermissionsChangedEvent, PlayerContainerEvent, PlayerDestroyItemEvent,
                PlayerEvent.BreakSpeed, PlayerEvent.Clone, PlayerEvent.HarvestCheck, PlayerEvent.ItemCraftedEvent,
                PlayerEvent.ItemPickupEvent, PlayerEvent.ItemSmeltedEvent, PlayerEvent.LoadFromFile,
                PlayerEvent.NameFormat, PlayerEvent.PlayerChangeGameModeEvent, PlayerEvent.PlayerChangedDimensionEvent,
                PlayerEvent.PlayerLoggedInEvent, PlayerEvent.PlayerLoggedOutEvent, PlayerEvent.PlayerRespawnEvent,
                PlayerEvent.SaveToFile, PlayerEvent.StartTracking, PlayerEvent.StopTracking,
                PlayerEvent.TabListNameFormat, PlayerSleepInBedEvent, PlayerWakeUpEvent, PlayerXpEvent,
                TradeWithVillagerEvent {
    @Override
    Player entity();

    /**
     * HarvestCheck is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link Player#hasCorrectToolForDrops(BlockState)}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#doPlayerHarvestCheck(Player, BlockState, boolean)}.<br>
     * <br>
     * {@link #state} contains the {@link BlockState} that is being checked for harvesting. <br>
     * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    final class HarvestCheck implements PlayerEvent {
        public static final EventBus<HarvestCheck> BUS = EventBus.create(HarvestCheck.class);

        private final Player player;
        private final BlockState state;
        private boolean success;

        public HarvestCheck(Player player, BlockState state, boolean success) {
            this.player = player;
            this.state = state;
            this.success = success;
        }

        public BlockState getTargetBlock() { return this.state; }
        public boolean canHarvest() { return this.success; }
        public void setCanHarvest(boolean success){ this.success = success; }

        @Override
        public Player entity() { return player; }
    }

    /**
     * BreakSpeed is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link Player#getDigSpeed(BlockState, BlockPos)}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getBreakSpeed(Player, BlockState, float, BlockPos)}.<br>
     * <br>
     * {@link #state} contains the block being broken. <br>
     * {@link #originalSpeed} contains the original speed at which the player broke the block. <br>
     * {@link #newSpeed} contains the newSpeed at which the player will break the block. <br>
     * {@link #pos} contains the coordinates at which this event is occurring. Optional value.<br>
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If it is canceled, the player is unable to break the block.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    final class BreakSpeed implements Cancellable, PlayerEvent {
        public static final CancellableEventBus<BreakSpeed> BUS = CancellableEventBus.create(BreakSpeed.class);

        private final Player player;
        private static final BlockPos LEGACY_UNKNOWN = new BlockPos(0, -1, 0);
        private final BlockState state;
        private final float originalSpeed;
        private float newSpeed = 0.0f;
        private final Optional<BlockPos> pos; // Y position of -1 notes unknown location

        public BreakSpeed(Player player, BlockState state, float original, @Nullable BlockPos pos) {
            this.player = player;
            this.state = state;
            this.originalSpeed = original;
            this.setNewSpeed(original);
            this.pos = Optional.ofNullable(pos);
        }

        public BlockState getState() { return state; }
        public float getOriginalSpeed() { return originalSpeed; }
        public float getNewSpeed() { return newSpeed; }
        public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
        public Optional<BlockPos> getPosition() { return this.pos; }

        @Override
        public Player entity() { return player; }
    }

    /**
     * NameFormat is fired when a player's display name is retrieved.<br>
     * This event is fired whenever a player's name is retrieved in
     * {@link Player#getDisplayName()} or {@link Player#refreshDisplayName()}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getPlayerDisplayName(Player, Component)}.<br>
     * <br>
     * {@link #username} contains the username of the player.
     * {@link #displayname} contains the display name of the player.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    final class NameFormat implements PlayerEvent {
        public static final EventBus<NameFormat> BUS = EventBus.create(NameFormat.class);

        private final Player player;
        private final Component username;
        private Component displayname;

        public NameFormat(Player player, Component username) {
            this.player = player;
            this.username = username;
            this.setDisplayname(username);
        }

        public Component getUsername()
        {
            return username;
        }

        public Component getDisplayname()
        {
            return displayname;
        }

        public void setDisplayname(Component displayname)
        {
            this.displayname = displayname;
        }

        @Override
        public Player entity() { return player; }
    }

    /**
     * TabListNameFormat is fired when a player's display name for the tablist is retrieved.<br>
     * This event is fired whenever a player's display name for the tablist is retrieved in
     * {@link ServerPlayer#getTabListDisplayName()} or {@link ServerPlayer#refreshTabListName()}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getPlayerTabListDisplayName(Player)}.<br>
     * <br>
     * {@link #getDisplayName()} contains the display name of the player or null if the client should determine the display name itself.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    final class TabListNameFormat implements PlayerEvent {
        public static final EventBus<TabListNameFormat> BUS = EventBus.create(TabListNameFormat.class);

        private final Player player;

        @Nullable
        private Component displayName;

        public TabListNameFormat(Player player) {
            this.player = player;
        }

        @Nullable
        public Component getDisplayName()
        {
            return displayName;
        }

        public void setDisplayName(@Nullable Component displayName)
        {
            this.displayName = displayName;
        }

        @Override
        public Player entity() { return player; }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the impl sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     *
     * @param original The old player that this new entity is a clone of.
     * @param isWasDeath True if this event was fired because the player died. False if it was fired because the entity switched dimensions.
     */
    record Clone(Player entity, Player original, boolean isWasDeath) implements PlayerEvent {
        public static final EventBus<Clone> BUS = EventBus.create(Clone.class);
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     * @param target The Entity now being tracked.
     */
    record StartTracking(Player entity, Entity target) implements PlayerEvent {
        public static final EventBus<StartTracking> BUS = EventBus.create(StartTracking.class);
    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     * @param target The Entity no longer being tracked.
     */
    record StopTracking(Player entity, Entity target) implements PlayerEvent {
        public static final EventBus<StopTracking> BUS = EventBus.create(StopTracking.class);
    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     *
     * @param playerDirectory The directory where player data is being stored. Use this to locate your mod additional file.
     * @param playerUUID The UUID is the standard for player related file storage. It is broken out here for convenience for quick file generation.
     */
    record LoadFromFile(Player entity, File playerDirectory, String playerUUID) implements PlayerEvent {
        public static final EventBus<LoadFromFile> BUS = EventBus.create(LoadFromFile.class);

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         */
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(playerDirectory(), playerUUID() + "." +suffix);
        }
    }
    /**
     * The player is being saved to the world store. Note that the
     * player may be in the process of logging out or otherwise departing
     * from the world. Don't assume it's association with the world.
     * This allows mods to load an additional file from the players directory
     * containing additional mod related player data.
     * <br>
     * Use this event to save the additional mod related player data to the world.
     *
     * <br>
     * <em>WARNING</em>: Do not overwrite the player's .dat file here. You will
     * corrupt the world state.
     *
     * @param playerDirectory The directory where player data is being stored. Use this to locate your mod additional file.
     * @param playerUUID The UUID is the standard for player related file storage. It is broken out here for convenience for quick file generation.
     */
    record SaveToFile(Player entity, File playerDirectory, String playerUUID) implements PlayerEvent {
        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         */
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(playerDirectory(), playerUUID() + "." + suffix);
        }
    }

    /**
     * @param originalEntity The original item entity with the current remaining stack size
     * @param stack The clone item stack, containing the item and amount picked up
     */
    record ItemPickupEvent(Player entity, ItemEntity originalEntity, ItemStack stack) implements PlayerEvent {
        public static final EventBus<ItemPickupEvent> BUS = EventBus.create(ItemPickupEvent.class);
    }

    record ItemCraftedEvent(Player entity, ItemStack crafting, Container inventory) implements PlayerEvent {
        public static final EventBus<ItemCraftedEvent> BUS = EventBus.create(ItemCraftedEvent.class);
    }

    record ItemSmeltedEvent(Player entity, ItemStack smelting) implements PlayerEvent {
        public static final EventBus<ItemSmeltedEvent> BUS = EventBus.create(ItemSmeltedEvent.class);
    }

    record PlayerLoggedInEvent(Player entity) implements PlayerEvent {
        public static final EventBus<PlayerLoggedInEvent> BUS = EventBus.create(PlayerLoggedInEvent.class);
    }

    record PlayerLoggedOutEvent(Player entity) implements PlayerEvent {
        public static final EventBus<PlayerLoggedOutEvent> BUS = EventBus.create(PlayerLoggedOutEvent.class);
    }

    /**
     * @param isEndConquered True if the respawn event came from the player conquering the end
     */
    record PlayerRespawnEvent(Player entity, boolean isEndConquered) implements PlayerEvent {
        public static final EventBus<PlayerRespawnEvent> BUS = EventBus.create(PlayerRespawnEvent.class);
    }

    record PlayerChangedDimensionEvent(Player entity, ResourceKey<Level> from, ResourceKey<Level> to) implements PlayerEvent {
        public static final EventBus<PlayerChangedDimensionEvent> BUS = EventBus.create(PlayerChangedDimensionEvent.class);
    }

    /**
     * Fired when the game type of a server player is changed to a different value than what it was previously. Eg Creative to Survival, not Survival to Survival.
     * If the event is cancelled the game mode of the player is not changed and the value of <code>newGameMode</code> is ignored.
     */
    final class PlayerChangeGameModeEvent implements Cancellable, PlayerEvent {
        public static final CancellableEventBus<PlayerChangeGameModeEvent> BUS = CancellableEventBus.create(PlayerChangeGameModeEvent.class);

        private final Player player;
        private final GameType currentGameMode;
        private GameType newGameMode;

        public PlayerChangeGameModeEvent(Player player, GameType currentGameMode, GameType newGameMode) {
            this.player = player;
            this.currentGameMode = currentGameMode;
            this.newGameMode = newGameMode;
        }

        public GameType getCurrentGameMode()
        {
            return currentGameMode;
        }

        public GameType getNewGameMode()
        {
            return newGameMode;
        }

        /**
         * Sets the game mode the player will be changed to if this event is not cancelled.
         */
        public void setNewGameMode(GameType newGameMode)
        {
            this.newGameMode = newGameMode;
        }

        @Override
        public Player entity() { return player; }
    }
}
