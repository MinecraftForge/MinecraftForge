/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.EventListener;

import java.util.ArrayList;

public class WorldWorkerManager {
    private static final ArrayList<IWorker> workers = new ArrayList<>();
    private static long startTime = -1;
    private static int index = 0;
    private static EventListener tickStartListener;
    private static EventListener tickEndListener;

    private static void startTick() {
        startTime = System.currentTimeMillis();
    }

    private static void endTick() {
        index = 0;
        IWorker task = getNext();
        if (task == null) {
            clear();
            return;
        }

        long time = 50 - (System.currentTimeMillis() - startTime);
        if (time < 10)
            time = 10; //If ticks are lagging, give us at least 10ms to do something.
        time += System.currentTimeMillis();

        while (System.currentTimeMillis() < time && task != null) {
            boolean again = task.doWork();

            if (!task.hasWork()) {
                remove(task);
                task = getNext();
            } else if (!again) {
                task = getNext();
            }
        }
    }

    @Deprecated(forRemoval = true, since = "1.21.8")
    public static void tick(boolean start) {
        if (start) startTick();
        else endTick();
    }

    public static synchronized void addWorker(IWorker worker) {
        workers.add(worker);
        if (tickStartListener == null) {
            tickStartListener = TickEvent.ServerTickEvent.Pre.BUS.addListener(event -> startTick());
            tickEndListener = TickEvent.ServerTickEvent.Post.BUS.addListener(event -> endTick());
        }
    }

    private static synchronized IWorker getNext() {
        return workers.size() > index ? workers.get(index++) : null;
    }

    private static synchronized void remove(IWorker worker) {
        workers.remove(worker);
        index--;
    }

    //Internal only, used to clear everything when the server shuts down.
    public static synchronized void clear() {
        workers.clear();
        if (tickStartListener != null) {
            TickEvent.ServerTickEvent.Pre.BUS.removeListener(tickStartListener);
            TickEvent.ServerTickEvent.Post.BUS.removeListener(tickEndListener);
            tickStartListener = null;
            tickEndListener = null;
        }
    }

    public interface IWorker {
        boolean hasWork();

        /**
         * Perform a task, returning true from this will have the manager call this function again this tick if there is time left.
         * Returning false will skip calling this worker until next tick.
         */
        boolean doWork();
    }
}
