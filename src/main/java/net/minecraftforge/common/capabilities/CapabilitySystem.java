/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CapabilitySystem
{
    private static final HashMap<Class<?>, ArrayList<Consumer<AttachCapabilitiesEvent<?>>>> LISTENER_LIST = new HashMap<>();
    private static final HashMap<Class<?>, HashSet<Consumer<AttachCapabilitiesEvent<?>>>> CACHED_LISTENER_LIST = new HashMap<>();
    private static final HashSet<Class<?>> CLASSES = new HashSet<>();

    private static final ArrayList<Consumer<AttachCapabilitiesEvent<?>>> EMPTY_LIST = new ArrayList<>();
    private static final HashSet<Consumer<AttachCapabilitiesEvent<?>>> EMPTY_HASH_SET = new HashSet<>();


    private static void reconstruct(Class<?> mainClass) {
        AtomicReference<Class<?>> cls = new AtomicReference<>(mainClass);
        while (cls.get() != null) {
            Class<?> main = cls.get();

            for (Class<?> anInterface : main.getInterfaces()) {
                CACHED_LISTENER_LIST.put(anInterface, getListenerList(new HashSet<>(), anInterface));
            }

            CACHED_LISTENER_LIST.put(main, getListenerList(new HashSet<>(), main));
            cls.set(main.getSuperclass());
        }
    }

    private static boolean shouldRebuild(Class<?> cls) {
        return !CACHED_LISTENER_LIST.containsKey(cls);
    }

    private static HashSet<Consumer<AttachCapabilitiesEvent<?>>> getListenerList(Class<?> cls) {
        return CACHED_LISTENER_LIST.getOrDefault(cls, EMPTY_HASH_SET);
    }

    private static HashSet<Consumer<AttachCapabilitiesEvent<?>>> getListenerList(HashSet<Consumer<AttachCapabilitiesEvent<?>>> lists, Class<?> cls)
    {
        lists.addAll(LISTENER_LIST.getOrDefault(cls, EMPTY_LIST));

        for (Class<?> anInterface : cls.getInterfaces())
            getListenerList(lists, anInterface);

        return cls.getSuperclass() != null ? getListenerList(lists, cls.getSuperclass()) : lists;
    }

    public static void post(AttachCapabilitiesEvent<?> event)
    {
        if (shouldRebuild(event.getType()))
            reconstruct(event.getType());
        getListenerList(event.getType()).forEach(e -> e.accept(event));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> void addListener(Class<T> type, Consumer<AttachCapabilitiesEvent<T>> eventConsumer)
    {
        if (Item.class.isAssignableFrom(type))
            throw new IllegalStateException("Unable to add Listener for Items. Use CapabilitySystem.addItemListener(ItemClass, Consumer)");
        LISTENER_LIST.computeIfAbsent(type, (e) -> new ArrayList<>()).add((Consumer) eventConsumer);
    }


    public static <W extends Item> void addWrappedItemListener(Class<W> type, BiConsumer<AttachCapabilitiesEvent<ItemStack>, W> eventConsumer)
    {
        addWrappedListener(ItemStack.class, type, ItemStack::getItem, eventConsumer);
    }

    @SuppressWarnings("unchecked")
    public static <T, W, X> void addWrappedListener(Class<? extends T> type, Class<W> wrappedClass, Function<T, X> conversion, BiConsumer<AttachCapabilitiesEvent<T>, W> eventConsumer)
    {
        LISTENER_LIST.computeIfAbsent(wrappedClass, (e) -> new ArrayList<>()).add((event) -> {eventConsumer.accept((AttachCapabilitiesEvent<T>) event, (W) conversion.apply((T) event.getObject()));});
    }
}
