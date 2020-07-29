/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * This defines a Mod to FML.
 * Any class found with this annotation applied will be loaded as a Mod. The instance that is loaded will
 * represent the mod to other Mods in the system. It will be sent various subclasses of {@link ModLifecycleEvent}
 * at pre-defined times during the loading of the game.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod
{
    /**
     * The unique mod identifier for this mod.
     * <b>Required to be lowercased in the english locale for compatibility. Will be truncated to 64 characters long.</b>
     *
     * This will be used to identify your mod for third parties (other mods), it will be used to identify your mod for registries such as block and item registries.
     * By default, you will have a resource domain that matches the modid. All these uses require that constraints are imposed on the format of the modid.
     */
    String value();

    /**
     * Annotate a class which will be subscribed to an Event Bus at mod construction time.
     * Defaults to subscribing the current modid to the {@link MinecraftForge#EVENT_BUS}
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
         * Specify an alternative bus to listen to
         *
         * @return the bus you wish to listen to
         */
        Bus bus() default Bus.FORGE;

        enum Bus {
            /**
             * The main Forge Event Bus.
             * @see MinecraftForge#EVENT_BUS
             */
            FORGE(()-> MinecraftForge.EVENT_BUS),
            /**
             * The mod specific Event bus.
             * @see FMLJavaModLoadingContext#getModEventBus()
             */
            MOD(()-> FMLJavaModLoadingContext.get().getModEventBus());

            private final Supplier<IEventBus> busSupplier;

            Bus(final Supplier<IEventBus> eventBusSupplier) {
                this.busSupplier = eventBusSupplier;
            }

            public Supplier<IEventBus> bus() {
                return busSupplier;
            }
        }
    }
}
