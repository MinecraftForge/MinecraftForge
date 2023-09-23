/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;


public class CapabilitySystem {
    private static final HashMap<Class<?>, ArrayList<Consumer<AttachCapabilitiesEvent<?>>>> FIND = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();



    private static ArrayList<Consumer<AttachCapabilitiesEvent<?>>> find(ArrayList<Consumer<AttachCapabilitiesEvent<?>>> lists, Class<?> cls) {
        var list = FIND.get(cls);
        if (list != null) {
            lists.addAll(list);
        }
        if (cls.getSuperclass() != Object.class) {
            return find(lists, cls.getSuperclass());
        }
        return lists;
    }


    static AtomicLong nano = new AtomicLong();
    static AtomicInteger integer = new AtomicInteger();


    public static void post(AttachCapabilitiesEvent<?> event) {
        StopWatch watch = new StopWatch();
        watch.start();
        ArrayList<Consumer<AttachCapabilitiesEvent<?>>> LIST = find(new ArrayList<>(), event.getType());
        LIST.forEach(e -> e.accept(event));
        watch.stop();
        nano.getAndAdd(watch.getNanoTime());
        if (integer.addAndGet(1) >= 1000) {
            integer.set(0);
            System.out.println("POST TIME -> %s".formatted(nano.getAndSet(0)));
        }
    }

    @SuppressWarnings("all")
    public static void addListener(Class<?> type, Consumer<AttachCapabilitiesEvent<?>> eventConsumer) {
        FIND.computeIfAbsent(type, (e) -> new ArrayList<>()).add(eventConsumer);
    }

}
