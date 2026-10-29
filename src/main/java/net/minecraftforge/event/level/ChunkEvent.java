/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.eventbus.api.bus.EventBus;
import org.jetbrains.annotations.ApiStatus;

/**
 * ChunkEvent is fired when an event involving a chunk occurs.
 **/
public sealed interface ChunkEvent extends LevelEvent
        permits ChunkEvent.Load, ChunkEvent.Unload, ChunkEvent.LightingCalculated, ChunkDataEvent {
    EventBus<ChunkEvent> BUS = EventBus.create(ChunkEvent.class);

    @Override
    default LevelAccessor getLevel() {
        return getChunk().getWorldForge();
    }

    /**
     * @return the Chunk this event is affecting.
     */
    ChunkAccess getChunk();

    /**
     * ChunkEvent.Load is fired when vanilla Minecraft attempts to load a Chunk into the level.<br>
     * This event is fired during chunk loading in <br>
     *
     * Chunk.onChunkLoad(). <br>
     * <strong>Note:</strong> This event may be called before the underlying {@link LevelChunk} is promoted to {@link ChunkStatus#FULL}. You will cause chunk loading deadlocks if you don't delay your level interactions.
     */
    record Load(ChunkAccess getChunk, boolean isNewChunk) implements ChunkEvent {
        public static final EventBus<ChunkEvent.Load> BUS = EventBus.create(ChunkEvent.Load.class);

        @ApiStatus.Internal
        public Load {}

        /**
         * Check whether the Chunk is newly generated, and being loaded for the first time.
         *
         * <p>Will only ever return {@code true} on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.</p>
         *
         * @return whether the Chunk is newly generated
         */
        public boolean isNewChunk() {
            return isNewChunk;
        }
    }

    /**
     * ChunkEvent.Unload is fired when vanilla Minecraft attempts to unload a Chunk from the level.<br>
     * This event is fired during chunk unloading in <br>
     * Chunk.onChunkUnload().
     */
    record Unload(ChunkAccess getChunk) implements ChunkEvent {
        public static final EventBus<ChunkEvent.Unload> BUS = EventBus.create(ChunkEvent.Unload.class);
    }

    /**
     * ChunkEvent.LightingCalculated is fired when MinecraftForge flags that lighting is correct in a chunk.<br>
     * This event is fired during light propagation in ThreadedLevelLightEngine.CompletableFuture(), specifically upon setting
     * the ChunkAccess isLightCorrect to true.<br>
     * <br>
     * The game test for this event is lighting_event_test in net.minecraftforge.debug.chunk<br>
     *
     * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
     *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
     *             We may keep it or add it back in a future version if there is enough demand.
     */
    @Deprecated(forRemoval = true, since = "26.4")
    record LightingCalculated(ChunkAccess getChunk) implements ChunkEvent {
        public static final EventBus<ChunkEvent.LightingCalculated> BUS = EventBus.create(ChunkEvent.LightingCalculated.class);
    }
}
