/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CapabilitySystem {
    private static final HashMap<Class<?>, ArrayList<Consumer<AttachCapabilitiesEvent<?>>>> FIND = new HashMap<>();
    private static final ArrayList<Consumer<AttachCapabilitiesEvent<?>>> EMPTY_LIST = new ArrayList<>();


    private static HashSet<Consumer<AttachCapabilitiesEvent<?>>> find(HashSet<Consumer<AttachCapabilitiesEvent<?>>> lists, Class<?> cls) {
        lists.addAll(FIND.getOrDefault(cls, EMPTY_LIST));

        for (Class<?> anInterface : cls.getInterfaces())
            find(lists, anInterface);

        return cls.getSuperclass() != null ? find(lists, cls.getSuperclass()) : lists;
    }

    public static void post(AttachCapabilitiesEvent<?> event) {
        find(new HashSet<>(), event.getType()).forEach(e -> e.accept(event));
    }

    @SuppressWarnings("unchecked")
    public static <T> void addListener(Class<T> type, Consumer<AttachCapabilitiesEvent<T>> eventConsumer) {
        if (Item.class.isAssignableFrom(type)) throw new IllegalStateException("Unable to add Listener for Items. Use CapabilitySystem.addItemListener(ItemClass, Consumer)");
        FIND.computeIfAbsent(type, (e) -> new ArrayList<>()).add((Consumer) eventConsumer);
    }

    @SuppressWarnings("unchecked")
    public static <T, W> void addWrappedListener(Class<T> type, Class<W> wrappedClass, BiConsumer<AttachCapabilitiesEvent<T>, W> eventConsumer) {
        if (!Item.class.isAssignableFrom(type)) throw new IllegalStateException("Unable to add Listener for Items. Use CapabilitySystem.addListener(Class, Consumer) for non Item related stuff");
        FIND.computeIfAbsent(type, (e) -> new ArrayList<>()).add((cls) -> {
            eventConsumer.accept((AttachCapabilitiesEvent<T>) cls, (W) cls.getObject());
        });
    }
}
