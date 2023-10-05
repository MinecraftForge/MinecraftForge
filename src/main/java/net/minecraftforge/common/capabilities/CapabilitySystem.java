/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CapabilitySystem
{

    private static final HashMap<Class<?>, ListenerList> LISTENERS = new HashMap<>();

    private static final List<Class<?>> IGNORE = List.of(
            Object.class,
            ICapabilityProvider.class,
            CapabilityProvider.class
    );


    // Do not touch this class...
    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    private final static class ListenerList {
        Set<ListenerList> supers = new HashSet<>();
        Set<ListenerList> subs = new HashSet<>();
        NonNullList<Consumer<AttachCapabilitiesEvent<?>>> listeners = NonNullList.create();
        boolean dirty = true;
        Consumer<AttachCapabilitiesEvent<?>>[] all = null;

        Class<?> type;
        private ListenerList(Class<?> type) {
            this.type = type; // To check for what this ListenerList is for?
        }

        public void addListener(Consumer<AttachCapabilitiesEvent<?>> c) {
            listeners.add(c);
            this.dirty = true;
            this.subs.forEach(s -> s.dirty = true);
        }

        @SuppressWarnings({"unchecked", "ConstantConditions"}) // IDE will not shut up about this, but it is safe.
        public void post(AttachCapabilitiesEvent<?> event) {
            synchronized (this) {
                if (this.dirty) {
                    ArrayList<Consumer<AttachCapabilitiesEvent<?>>> all = new ArrayList<>();

                    this.supers.forEach(list -> {
                        list.listeners.forEach(e -> {
                            if (!all.contains(e)) all.add(e);
                        });
                    });

                    synchronized (this) { /// double lock fun!
                        if (this.dirty) {
                            this.all = all.toArray(new Consumer[all.size()]);
                            this.dirty = false;
                        }
                    }
                }

                if (this.all == null) return;
                for (Consumer<AttachCapabilitiesEvent<?>> consumer : this.all)
                    consumer.accept(event);
            }
        }
    }

    private static ListenerList getListeners(Class<?> cls) {
        var ret = LISTENERS.get(cls);
        if (ret == null) {

            synchronized(LISTENERS) {
                ret = LISTENERS.computeIfAbsent(cls, ListenerList::new);
            }

            // this is the expensive bit, but doesnt matter if we do it multiple times as we're just adding to a set
            Set<Class<?>> inh = new HashSet<>();
            visit(inh, cls.getSuperclass());
            for (Class<?> i : inh) {
                var lst = getListeners(i);
                ret.supers.add(lst);
                lst.subs.add(ret);
            }

        }
        return ret;
    }

    private static void visit(Set<Class<?>> set, Class<?> cls) {
        if (cls == null || IGNORE.contains(cls)) return;

        set.add(cls);
        visit(set, cls.getSuperclass());

        for (Class<?> inf : cls.getInterfaces()) {
            set.add(inf);
            visit(set, inf.getSuperclass());
        }
    }


    public static void post(AttachCapabilitiesEvent<?> event)
    {
        var listenrs = getListeners(event.getType());
        listenrs.post(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> void addListener(Class<T> type, Consumer<AttachCapabilitiesEvent<T>> eventConsumer)
    {
        if (Item.class.isAssignableFrom(type))
            throw new IllegalStateException("Unable to add Listener for Items. Use CapabilitySystem.addItemListener(ItemClass, Consumer)");

        var listeners = getListeners(type);
        listeners.addListener((Consumer) eventConsumer);
    }


    public static <W extends Item> void addWrappedItemListener(Class<W> type, BiConsumer<AttachCapabilitiesEvent<ItemStack>, W> eventConsumer)
    {
        addWrappedListener(ItemStack.class, type, ItemStack::getItem, eventConsumer);
    }

    @SuppressWarnings("unchecked")
    public static <T, W, X> void addWrappedListener(Class<? extends T> type, Class<W> wrappedClass, Function<T, X> conversion, BiConsumer<AttachCapabilitiesEvent<T>, W> eventConsumer)
    {
        var listeners = getListeners(wrappedClass);
        listeners.addListener((event) -> {eventConsumer.accept((AttachCapabilitiesEvent<T>) event, (W) conversion.apply((T) event.getObject()));});
    }
}
