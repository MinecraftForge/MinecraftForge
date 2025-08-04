/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.Bindings;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jspecify.annotations.Nullable;

/**
 * This defines a Mod to FML.
 * Any class found with this annotation applied will be loaded as a Mod. The instance that is loaded will
 * represent the mod to other Mods in the system. It will be sent various subclasses of {@code ModLifecycleEvent}
 * at pre-defined times during the loading of the game.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    /**
     * The unique mod identifier for this mod.
     * <b>Required to be lowercased in the English locale for compatibility. Will be truncated to 64 characters long.</b>
     * <br>
     * This will be used to identify your mod for third parties (other mods), it will be used to identify your mod for registries such as block and item registries.
     * By default, you will have a resource domain that matches the modid. All these uses require that constraints are imposed on the format of the modid.
     */
    String value();

    /**
     * Annotate a class which will be subscribed to an Event Bus at mod construction time.
     * Defaults to subscribing the current modid to the {@code MinecraftForge#EVENT_BUS}
     * on both sides.
     *
     * @see Bus
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface EventBusSubscriber {
        /**
         * Specify targets to load this event subscriber on. Can be used to avoid loading Client specific events
         * on a dedicated server, for example.
         *
         * @return an array of Dist to load this event subscriber on
         */
        Dist[] value() default { Dist.CLIENT, Dist.DEDICATED_SERVER };

        /**
         * Optional value, only necessary if this annotation is not on the same class that has a @Mod annotation.
         * Needed to prevent early classloading of classes not owned by your mod.
         * @return a modid
         */
        String modid() default "";

        /**
         * Specify an alternative bus to listen to.
         * <br>
         * If you know all listeners in this class are for a specific bus, you can set it here to speed up registration.
         *
         * @return the bus you wish to listen to
         */
        Bus bus() default Bus.BOTH;

        enum Bus {
            /**
             * The main BusGroup that most game events are fired on.
             */
            FORGE(Bindings.getForgeBus()),

            /**
             * The mod-specific event BusGroup, usually for mod lifecycle events.
             * @see FMLJavaModLoadingContext#getModBusGroup()
             */
            MOD(()-> FMLJavaModLoadingContext.get().getModBusGroup()),

            /**
             * Both the {@link #FORGE} and {@link #MOD} buses. This is slower to register events in your class but
             * allows you to listen to events from different BusGroup types without needing separate classes annotated
             * with {@link EventBusSubscriber}.
             */
            BOTH(() -> null);

            private final Supplier<@Nullable BusGroup> busSupplier;

            Bus(Supplier<@Nullable BusGroup> eventBusSupplier) {
                this.busSupplier = eventBusSupplier;
            }

            public Supplier<@Nullable BusGroup> bus() {
                return busSupplier;
            }
        }
    }
}
