/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TaskScheduler {
    private static final ArrayList<ForgeTask<?>> tasks = new ArrayList<>();
    private final Level level;

    @ApiStatus.Internal
    public TaskScheduler(Level level) {
        this.level = level;
    }

    public <T> void requestTask(ResourceLocation name, int initialTickDelay, Consumer<ForgeTask<T>> toRun) {
        ForgeTask<T> task = new ForgeTask<>(name, initialTickDelay, -1, toRun);
        tasks.add(task);
    }

    public <T> void requestRepeatingTask(ResourceLocation name, int initialTickDelay, int repeatingTickDelay, Consumer<ForgeTask<T>> toRun) {
        ForgeTask<T> task = new ForgeTask<>(name, initialTickDelay, repeatingTickDelay, toRun);
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

        private final int initialTickDelay;

        private final int repeatTickDelay;

        private int currentTickDelay;

        private final Consumer<ForgeTask<T>> task;

        public boolean isCanceled = false;

        private ForgeTask(ResourceLocation name, int initialTickDelay, int repeatTickDelay, Consumer<ForgeTask<T>> task) {
            this.name = name;
            this.initialTickDelay = initialTickDelay;
            currentTickDelay = initialTickDelay;
            this.repeatTickDelay = repeatTickDelay;
            this.task = task;
        }

        public void cancel() {
            isCanceled = true;
        }

        private void process() {
            try {
                task.accept(this);
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
                    ", initialTickDelay=" + initialTickDelay +
                    ", repeatTickDelay=" + repeatTickDelay +
                    ", currentTickDelay=" + currentTickDelay +
                    ", isCanceled=" + isCanceled +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TaskScheduler[" + level + "]";
    }
}
