/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TaskScheduler {
    private static final ArrayList<ForgeTask<?>> tasks = new ArrayList<>();
    private final Level level;

    @ApiStatus.Internal
    public TaskScheduler(Level level) {
        this.level = level;
    }

    public void requestTask(ResourceLocation name, int initialTickDelay, Consumer<ForgeTask<?>> toRun) {
        ForgeTask<?> task = new ForgeTask<>(name, level, initialTickDelay, -1, (provided, ignored) -> toRun.accept(provided), null);
        tasks.add(task);
    }

    public <T> void requestRepeatingTask(ResourceLocation name, int initialTickDelay, int repeatingTickDelay, BiConsumer<ForgeTask<T>, T> toRun, @Nullable T initialSharedInfo) {
        ForgeTask<T> task = new ForgeTask<>(name, level, initialTickDelay, repeatingTickDelay, toRun, initialSharedInfo);
        tasks.add(task);
    }

    @ApiStatus.Internal
    public void onEnd() {
        ProfilerFiller profiler = level.getProfiler();

        tasks.removeIf(task -> {
            if (task.currentTickDelay != 0) {
                task.currentTickDelay--;
                return false;
            }

            profiler.push("forgeTasks");

            String name;
            if (task.name != null) {
                name = task.name.toString();
            } else {
                name = "null";
            }

            profiler.push(name);
            task.process();
            profiler.pop();

            profiler.pop();

            if (!task.isCanceled && task.repeatTickDelay >= 0) {
                task.currentTickDelay = task.repeatTickDelay;
                return false;
            }
            return true;
        });
    }


    public static final class ForgeTask<T>{
        public final ResourceLocation name;

        public final Level level;

        private final int initialTickDelay;

        private final int repeatTickDelay;

        private int currentTickDelay;

        private final BiConsumer<ForgeTask<T>, T> task;

        @Nullable
        public T passed;

        public boolean isCanceled = false;

        private ForgeTask(ResourceLocation name, Level level, int initialTickDelay, int repeatTickDelay, BiConsumer<ForgeTask<T>, T> task, @Nullable T passed) {
            this.name = name;
            this.level = level;
            this.initialTickDelay = initialTickDelay;
            currentTickDelay = initialTickDelay;
            this.repeatTickDelay = repeatTickDelay;
            this.task = task;
            this.passed = passed;
        }

        public void cancel() {
            isCanceled = true;
        }

        private void process() {
            try {
                task.accept(this, passed);
            } catch (Throwable throwable) {
                System.err.println("An error has occurred processing ForgeTask!");
                throwable.printStackTrace();
                cancel();
            }
        }

        @Override
        public String toString() {
            return "ForgeTask{" +
                    "name='" + name + '\'' +
                    ", level=" + level +
                    ", initialTickDelay=" + initialTickDelay +
                    ", repeatTickDelay=" + repeatTickDelay +
                    ", currentTickDelay=" + currentTickDelay +
                    ", passed=" + passed +
                    ", isCanceled=" + isCanceled +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TaskScheduler[" + level + "]";
    }
}
