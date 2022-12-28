/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.TaskScheduler;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.Nullable;

public interface IForgeLevel extends ICapabilityProvider
{
    /**
     * The maximum radius to scan for entities when trying to check bounding boxes. Vanilla's default is
     * 2.0D But mods that add larger entities may increase this.
     */
    public double getMaxEntityRadius();
    /**
     * Increases the max entity radius, this is safe to call with any value.
     * The setter will verify the input value is larger then the current setting.
     *
     * @param value New max radius to set.
     * @return The new max radius
     */
    public double increaseMaxEntityRadius(double value);
    /**
     * All part entities in this world. Used when collecting entities in an AABB to fix parts being
     * ignored whose parent entity is in a chunk that does not intersect with the AABB.
     */
    public default Collection<PartEntity<?>> getPartEntities()
    {
        return Collections.emptyList();
    }

    public default void requestTask(TaskScheduler.ForgeTask<?> task)
    {
        TaskScheduler.requestTask(task);
    }

    public default void requestTask(@Nullable ResourceLocation name, TickEvent.Phase phase, int initialTickDelay, Consumer<TaskScheduler.ForgeTask<?>> toRun)
    {
        TaskScheduler.requestTask(name, (Level) this, phase, initialTickDelay, toRun);
    }

    public default <T> void requestRepeatingTask(@Nullable ResourceLocation name, TickEvent.Phase phase, int initialTickDelay, int repeatingTickDelay, BiFunction<TaskScheduler.ForgeTask<T>, T, T> toRepeat, BiConsumer<TaskScheduler.ForgeTask<T>, T> whenDone, T initialSharedInfo)
    {
        TaskScheduler.requestRepeatingTask(name, (Level) this, phase, initialTickDelay, repeatingTickDelay, toRepeat, whenDone, initialSharedInfo);
    }
}
