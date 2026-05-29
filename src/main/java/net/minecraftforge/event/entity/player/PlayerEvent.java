/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event.entity.player;

import java.io.File;
import java.util.Optional;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftsingularity.event.singularityEventFactory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftsingularity.event.entity.living.LivingEvent;
import net.minecraftsingularity.eventbus.api.bus.CancellableEventBus;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.RecordEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * PlayerEvent is a marker interface for whenever an event involving a {@link Player} occurs.
 */
public interface PlayerEvent extends LivingEvent {
    @Override
    Player getEntity();

    /**
     * HarvestCheck is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link Player#hasCorrectToolForDrops(BlockState)}.<br>
     * <br>
     * This event is fired via the {@link singularityEventFactory#doPlayerHarvestCheck(Player, BlockState, boolean)}.<br>
     * <br>
     * {@link #state} contains the {@link BlockState} that is being checked for harvesting. <br>
     * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
     **/
    final class HarvestCheck extends MutableEvent implements PlayerEvent {
        public static final EventBus<HarvestCheck> BUS = EventBus.create(HarvestCheck.class);

        private final Player player;
        private final BlockState state;
        private boolean success;

        public HarvestCheck(Player player, BlockState state, boolean success) {
            this.player = player;
            this.state = state;
            this.success = success;
        }

        @Override
        public Player getEntity() {
            return this.player;
        }

        public BlockState getTargetBlock() { return this.state; }
        public boolean canHarvest() { return this.success; }
        public void setCanHarvest(boolean success) { this.success = success; }
    }

    /**
     * BreakSpeed is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link Player#getDigSpeed(BlockState, BlockPos)}.<br>
     * <br>
     * This event is fired via the {@link singularityEventFactory#getBreakSpeed(Player, BlockState, float, BlockPos)}.<br>
     * <br>
     * {@link #state} contains the block being broken. <br>
     * {@link #originalSpeed} contains the original speed at which the player broke the block. <br>
     * {@link #newSpeed} contains the newSpeed at which the player will break the block. <br>
     * {@link #pos} contains the coordinates at which this event is occurring. Optional value.<br>
     * <br>
     * If it is cancelled, the player is unable to break the block.<br>
     **/
    final class BreakSpeed extends MutableEvent implements Cancellable, PlayerEvent {
        public static final CancellableEventBus<BreakSpeed> BUS = CancellableEventBus.create(BreakSpeed.class);

        private final Player player;
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

        @Override
        public Player getEntity() {
            return this.player;
        }

        public BlockState getState() { return state; }
        public float getOriginalSpeed() { return originalSpeed; }
        public float getNewSpeed() { return newSpeed; }
        public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
        public Optional<BlockPos> getPosition() { return this.pos; }
    }

    /**
     * NameFormat is fired when a player's display name is retrieved.<br>
     * This event is fired whenever a player's name is retrieved in
     * {@link Player#getDisplayName()} or {@link Player#refreshDisplayName()}.<br>
     * <br>
     * This event is fired via the {@link singularityEventFactory#getPlayerDisplayName(Player, Component)}.<br>
     * <br>
     * {@link #username} contains the username of the player.
     * {@link #displayname} contains the display name of the player.
     **/
    final class NameFormat extends MutableEvent implements PlayerEvent {
        public static final EventBus<NameFormat> BUS = EventBus.create(NameFormat.class);

        private final Player player;
        private final Component username;
        private Component displayname;

        public NameFormat(Player player, Component username) {
            this.player = player;
            this.username = username;
            this.setDisplayname(username);
        }

        @Override
        public Player getEntity() {
            return this.player;
        }

        public Component getUsername() {
            return username;
        }

        public Component getDisplayname() {
            return displayname;
        }

        public void setDisplayname(Component displayname) {
            this.displayname = displayname;
        }
    }

    /**
     * TabListNameFormat is fired when a player's display name for the tablist is retrieved.<br>
     * This event is fired whenever a player's display name for the tablist is retrieved in
     * {@link ServerPlayer#getTabListDisplayName()} or {@link ServerPlayer#refreshTabListName()}.<br>
     * <br>
     * This event is fired via the {@link singularityEventFactory#getPlayerTabListDisplayName(Player)}.<br>
     * <br>
     * {@link #getDisplayName()} contains the display name of the player or null if the client should determine the display name itself.
     **/
    final class TabListNameFormat extends MutableEvent implements PlayerEvent {
        public static final EventBus<TabListNameFormat> BUS = EventBus.create(TabListNameFormat.class);

        private final Player player;

        @Nullable
        private Component displayName;

        public TabListNameFormat(Player player) {
            this.player = player;
        }

        @Override
        public Player getEntity() {
            return player;
        }

        @Nullable
        public Component getDisplayName() {
            return displayName;
        }

        public void setDisplayName(@Nullable Component displayName) {
            this.displayName = displayName;
        }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the impl sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     *
     * @param getOriginal The old EntityPlayer that this new entity is a clone of.
     * @param isWasDeath True if this event was fired because the player died. False if it was fired because the entity switched dimensions.
     */
    record Clone(Player getEntity, Player getOriginal, boolean isWasDeath) implements RecordEvent, PlayerEvent {
        public static final EventBus<Clone> BUS = EventBus.create(Clone.class);
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     * @param getTarget The entity now being tracked.
     */
    record StartTracking(Player getEntity, Entity getTarget) implements RecordEvent, PlayerEvent {
        public static final EventBus<StartTracking> BUS = EventBus.create(StartTracking.class);
    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     * @param getTarget The entity no longer being tracked.
     */
    record StopTracking(Player getEntity, Entity getTarget) implements RecordEvent, PlayerEvent {
        public static final EventBus<StopTracking> BUS = EventBus.create(StopTracking.class);
    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     */
    record LoadFromFile(Player getEntity, File getPlayerDirectory, String getPlayerUUID) implements RecordEvent, PlayerEvent {
        public static final EventBus<LoadFromFile> BUS = EventBus.create(LoadFromFile.class);

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         */
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory() {
            return getPlayerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID() {
            return getPlayerUUID;
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
     */
    record SaveToFile(Player getEntity, File getPlayerDirectory, String getPlayerUUID) implements RecordEvent, PlayerEvent {
        public static final EventBus<SaveToFile> BUS = EventBus.create(SaveToFile.class);

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         */
        public File getPlayerFile(String suffix) {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory() {
            return getPlayerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID() {
            return getPlayerUUID;
        }
    }

    /**
     * @param getOriginalEntity Original EntityItem with current remaining stack size
     * @param getStack Clone item stack, containing the item and amount picked up
     */
    record ItemPickupEvent(Player getEntity, ItemEntity getOriginalEntity, ItemStack getStack)
            implements RecordEvent, PlayerEvent {
        public static final EventBus<ItemPickupEvent> BUS = EventBus.create(ItemPickupEvent.class);
    }

    record ItemCraftedEvent(Player getEntity, @NotNull ItemStack getCrafting, Container getContainer)
            implements RecordEvent, PlayerEvent {
        public static final EventBus<ItemCraftedEvent> BUS = EventBus.create(ItemCraftedEvent.class);
    }

    @NullMarked
    record ItemSmeltedEvent(Player getEntity, ItemStack getSmelting) implements RecordEvent, PlayerEvent {
        public static final EventBus<ItemSmeltedEvent> BUS = EventBus.create(ItemSmeltedEvent.class);
    }

    @NullMarked
    record PlayerLoggedInEvent(Player getEntity) implements RecordEvent, PlayerEvent {
        public static final EventBus<PlayerLoggedInEvent> BUS = EventBus.create(PlayerLoggedInEvent.class);
    }

    record PlayerLoggedOutEvent(Player getEntity) implements RecordEvent, PlayerEvent {
        public static final EventBus<PlayerLoggedOutEvent> BUS = EventBus.create(PlayerLoggedOutEvent.class);
    }

    @NullMarked
    record PlayerRespawnEvent(Player getEntity, boolean isEndConquered) implements RecordEvent, PlayerEvent {
        public static final EventBus<PlayerRespawnEvent> BUS = EventBus.create(PlayerRespawnEvent.class);

        /**
         * Did this respawn event come from the player conquering the end?
         * @return if this respawn was because the player conquered the end
         */
        public boolean isEndConquered() {
            return isEndConquered;
        }
    }

    record PlayerChangedDimensionEvent(Player getEntity, ResourceKey<Level> getFrom, ResourceKey<Level> getTo)
            implements RecordEvent, PlayerEvent {
        public static final EventBus<PlayerChangedDimensionEvent> BUS = EventBus.create(PlayerChangedDimensionEvent.class);
    }

    /**
     * Fired when the game type of a server player is changed to a different value than what it was previously. Eg Creative to Survival, not Survival to Survival.
     * If the event is cancelled the game mode of the player is not changed and the value of <code>newGameMode</code> is ignored.
     */
    final class PlayerChangeGameModeEvent extends MutableEvent implements Cancellable, PlayerEvent {
        public static final CancellableEventBus<PlayerChangeGameModeEvent> BUS = CancellableEventBus.create(PlayerChangeGameModeEvent.class);

        private final Player player;
        private final GameType currentGameMode;
        private GameType newGameMode;

        public PlayerChangeGameModeEvent(Player player, GameType currentGameMode, GameType newGameMode) {
            this.player = player;
            this.currentGameMode = currentGameMode;
            this.newGameMode = newGameMode;
        }

        @Override
        public Player getEntity() {
            return this.player;
        }

        public GameType getCurrentGameMode() {
            return currentGameMode;
        }

        public GameType getNewGameMode() {
            return newGameMode;
        }

        /**
         * Sets the game mode the player will be changed to if this event is not cancelled.
         */
        public void setNewGameMode(GameType newGameMode) {
            this.newGameMode = newGameMode;
        }
    }
}
