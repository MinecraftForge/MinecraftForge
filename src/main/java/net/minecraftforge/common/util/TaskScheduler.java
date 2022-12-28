/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TaskScheduler {
    private static final ArrayList<ForgeTask<?>> tasks = new ArrayList<>();

    private TaskScheduler() {

    }

    public static void requestTask(ForgeTask<?> task) {
        tasks.add(task);
    }

    public static void requestTask(@Nullable ResourceLocation name, Level level, TickEvent.Phase phase, int initialTickDelay, Consumer<ForgeTask<?>> toRun) {
        ForgeTask<?> task = new ForgeTask<>(name, level, phase, initialTickDelay, -1, (ignored1, ignored2) -> null, (provided, ignored) -> toRun.accept(provided), null);
        tasks.add(task);
    }

    public static <T> void requestRepeatingTask(@Nullable ResourceLocation name, Level level, TickEvent.Phase phase, int initialTickDelay, int repeatingTickDelay, BiFunction<ForgeTask<T>, T, T> toRepeat, BiConsumer<ForgeTask<T>, T> whenDone, T initialSharedInfo) {
        ForgeTask<T> task = new ForgeTask<>(name, level, phase, initialTickDelay, repeatingTickDelay, toRepeat, whenDone, initialSharedInfo);
        tasks.add(task);
    }

    @ApiStatus.Internal
    public static void onStart(Level level) {
        ProfilerFiller profiler = level.getProfiler();

        for (ForgeTask<?> task : tasks) {
            if (task.level != level) continue;
            if (task.phase != TickEvent.Phase.START) continue;
            if (task.currentTickDelay != 0) {
                task.currentTickDelay--;
                continue;
            }

            String name;
            if (task.name != null) {
                name = task.name.toString();
            } else {
                name = "null";
            }
            profiler.push(name);
            task.process();
            profiler.pop();
            if (task.hasFailed) continue;
            if (task.repeatTickDelay >= 0) {
                task.currentTickDelay = task.repeatTickDelay;
                continue;
            }
            task.isDone = true;
            profiler.push(name + "-done");
            ((BiConsumer)task.callback).accept(task, task.passed);
            profiler.pop();
        }

        tasks.removeIf(task -> task.hasFailed || task.isDone);
    }

    @ApiStatus.Internal
    public static void onEnd(Level level) {
        ProfilerFiller profiler = level.getProfiler();

        for (ForgeTask<?> task : tasks) {
            if (task.level != level) continue;
            if (!task.isReady) {
                task.isReady = true;
                continue;
            }
            if (task.phase != TickEvent.Phase.END) continue;
            if (task.currentTickDelay != 0) {
                task.currentTickDelay--;
                continue;
            }

            String name;
            if (task.name != null) {
                name = task.name.toString();
            } else {
                name = "null";
            }
            profiler.push(name);
            task.process();
            profiler.pop();
            if (task.hasFailed) continue;
            if (!task.isDone && task.repeatTickDelay >= 0) {
                task.currentTickDelay = task.repeatTickDelay;
                continue;
            }
            task.isDone = true;
            profiler.push(name + "-done");
            ((BiConsumer)task.callback).accept(task, task.passed);
            profiler.pop();
        }

        tasks.removeIf(task -> task.hasFailed || task.isDone);
    }


    public static final class ForgeTask<T>{
        @Nullable
        public final ResourceLocation name;

        public final Level level;

        public final TickEvent.Phase phase;

        private boolean isReady = false;

        private final int initialTickDelay;

        private final int repeatTickDelay;

        private int currentTickDelay;

        private final BiFunction<ForgeTask<T>, T, T> processor;

        private final BiConsumer<ForgeTask<T>, T> callback;

        private T passed;

        public boolean hasFailed = false;

        private boolean isDone = false;

        public ForgeTask(@Nullable ResourceLocation name, Level level, TickEvent.Phase phase, int initialTickDelay, int repeatTickDelay, BiFunction<ForgeTask<T>, T, T> processor, BiConsumer<ForgeTask<T>, T> callback, T passed) {
            this.name = name;
            this.level = level;
            this.phase = phase;
            this.initialTickDelay = initialTickDelay;
            currentTickDelay = initialTickDelay;
            this.repeatTickDelay = repeatTickDelay;
            this.processor = processor;
            this.callback = callback;
            this.passed = passed;
        }

        public void fail() {
            hasFailed = true;
        }

        public void done() {
            isDone = true;
        }

        private void process() {
            try {
                passed = processor.apply(this, passed);
            } catch (Exception e) {
                System.err.println("An error has occurred processing ForgeTask!");
                e.printStackTrace();
                fail();
            }
        }

        @Override
        public String toString() {
            return "ForgeTask{" +
                    "name='" + name + '\'' +
                    ", level=" + level +
                    ", phase=" + phase +
                    ", isReady=" + isReady +
                    ", initialTickDelay=" + initialTickDelay +
                    ", repeatTickDelay=" + repeatTickDelay +
                    ", currentTickDelay=" + currentTickDelay +
                    ", passed=" + passed +
                    ", hasFailed=" + hasFailed +
                    ", isDone=" + isDone +
                    '}';
        }
    }
}
