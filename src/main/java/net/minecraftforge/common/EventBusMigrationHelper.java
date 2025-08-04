/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.EventListener;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.unsafe.UnsafeHacks;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Collection;

/**
 * A small helper class to aid Forge modders in migrating from EventBus 6 to the new EventBus 7.
 * <br>
 * Refer to the migration guide mentioned in the Forge 1.21.6 announcement or ask in the Forge Discord for help with
 * migrating your mod to the new system.
 */
public final class EventBusMigrationHelper {
    public static final EventBusMigrationHelper INSTANCE = new EventBusMigrationHelper(BusGroup.DEFAULT);

    private final BusGroup group;

    public EventBusMigrationHelper(BusGroup group) {
        this.group = group;
    }

    /**
     * A version of {@link BusGroup#register(Lookup, Class)} that doesn't require a lookup. It is strongly recommended
     * to use the BusGroup method directly with {@code MethodHandles.lookup()} as the first argument, as this migration
     * helper method relies on JDK internals that may break without notice.
     * @param clazz the class containing static event listener methods that you want to register.
     * @return a collection of {@link EventListener} instances that were registered, can be passed to
     *         {@link BusGroup#unregister(Collection)} to unregister them later.
     * @see BusGroup#register(Lookup, Class)
     */
    public Collection<EventListener> register(Class<?> clazz) {
        return registerListeners(group, clazz);
    }

    /**
     * A version of {@link BusGroup#register(Lookup, Object)} that doesn't require a lookup. It is strongly recommended
     * to use the BusGroup method directly with {@code MethodHandles.lookup()} as the first argument, as this migration
     * helper method relies on JDK internals that may break without notice.
     * @param instance the instance of a class containing instance and static event listener methods that you want to register.
     * @return a collection of {@link EventListener} instances that were registered, can be passed to
     *         {@link BusGroup#unregister(Collection)} to unregister them later.
     * @see BusGroup#register(Lookup, Object)
     */
    public Collection<EventListener> register(Object instance) {
        return registerListeners(group, instance);
    }

    private static Collection<EventListener> registerListeners(BusGroup group, Object instance) {
        final class DodgyLookup {
            private DodgyLookup() {}
            private static final Lookup INSTANCE;
            static {
                try {
                    var lookupField = Lookup.class.getDeclaredField("IMPL_LOOKUP");
                    UnsafeHacks.setAccessible(lookupField);
                    INSTANCE = (Lookup) lookupField.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (instance.getClass() == Class.class) {
            Class<?> clazz = (Class<?>) instance;
            return group.register(DodgyLookup.INSTANCE.in(clazz), clazz);
        } else {
            return group.register(DodgyLookup.INSTANCE.in(instance.getClass()), instance);
        }
    }


    /**
     * The unique name of this BusGroup.
     */
    public String name() {
        return group.name();
    }

    /**
     * Registers all static methods annotated with {@link SubscribeEvent} in the given class.
     *
     * @param callerLookup {@code MethodHandles.lookup()} from the class containing listeners
     * @param utilityClassWithStaticListeners the class containing the static listeners
     * @return A collection of the registered listeners, which can be used to optionally unregister them later
     *
     * @apiNote This method only registers static listeners.
     *          <p>If you want to register both instance and static methods, use
     *          {@link BusGroup#register(MethodHandles.Lookup, Object)} instead.</p>
     */
    public Collection<EventListener> register(MethodHandles.Lookup callerLookup, Class<?> utilityClassWithStaticListeners) {
        return group.register(callerLookup, utilityClassWithStaticListeners);
    }

    /**
     * Registers all methods annotated with {@link SubscribeEvent} in the given object.
     *
     * @param callerLookup {@code MethodHandles.lookup()} from the class containing the listeners
     * @param listener the object containing the static and/or instance listeners
     * @return A collection of the registered listeners, which can be used to optionally unregister them later
     *
     * @apiNote If you know all the listeners are static methods, use
     *          {@link BusGroup#register(MethodHandles.Lookup, Class)} instead for better registration performance.
     */
    public Collection<EventListener> register(MethodHandles.Lookup callerLookup, Object listener) {
        return group.register(callerLookup, listener);
    }

    /**
     * Unregisters the given listeners from this BusGroup.
     * @param listeners A collection of listeners to unregister, obtained from
     *                  {@link #register(Class)} or {@link #register(Object)}
     *                  {@link #register(MethodHandles.Lookup, Class)} or {@link #register(MethodHandles.Lookup, Object)}
     */
    public void unregister(Collection<EventListener> listeners) {
        group.unregister(listeners);
    }
}
