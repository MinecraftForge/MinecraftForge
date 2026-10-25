/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.common.util.HasResult;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever a sapling, fungus, mushroom or azalea grows into a tree.
 * <p>
 * This event is not {@linkplain Cancellable cancellable} but does {@linkplain HasResult have a result}.
 * {@linkplain Result#ALLOW ALLOW} and {@linkplain Result#DEFAULT DEFAULT} will allow the sapling to grow
 * using the features set on the event.
 * {@linkplain Result#DENY DENY} will prevent the sapling from growing.
 * <p>
 * This event is fired only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}.
 *
 * @deprecated This event is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
 *             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
 *             We may keep it or add it back in a future version if there is enough demand.
 */
@Deprecated(forRemoval = true, since = "26.4")
public final class BlockFeatureGrowEvent implements LevelEvent, HasResult {
    public static final EventBus<BlockFeatureGrowEvent> BUS = EventBus.create(BlockFeatureGrowEvent.class);

    private final LevelAccessor level;
    private final RandomSource randomSource;
    private final BlockPos pos;
    @Nullable
    private Holder<Feature> feature;
    private Result result = Result.DEFAULT;

    public BlockFeatureGrowEvent(LevelAccessor level, RandomSource randomSource, BlockPos pos, @Nullable Holder<Feature> feature) {
        this.level = level;
        this.randomSource = randomSource;
        this.pos = pos;
        this.feature = feature;
    }

    @Override
    public LevelAccessor getLevel() {
        return level;
    }

    /**
     * {@return the random source which initiated the sapling growth}
     */
    public RandomSource getRandomSource() {
        return this.randomSource;
    }

    /**
     * {@return the coordinates of the sapling attempting to grow}
     */
    public BlockPos getPos() {
        return pos;
    }

    /**
     * {@return the holder of the feature which will be placed, possibly null}
     */
    @Nullable
    public Holder<Feature> getFeature() {
        return feature;
    }

    /**
     * @param feature a {@linkplain Holder} referencing a tree feature to be placed instead of the current feature.
     */
    public void setFeature(@Nullable Holder<Feature> feature) {
        this.feature = feature;
    }

    /**
     * @param featureKey a {@linkplain ResourceKey} referencing a tree feature to be placed instead of the current feature.
     */
    public void setFeature(ResourceKey<Feature> featureKey) {
        this.feature = this.getLevel().registryAccess().lookupOrThrow(Registries.FEATURE).get(featureKey).orElse(null);
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void setResult(Result result) {
        this.result = result;
    }
}
