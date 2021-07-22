/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

public class FMLJavaModLoadingContext
{
    private final FMLModContainer container;

    FMLJavaModLoadingContext(FMLModContainer container) {
        this.container = container;
    }

    /**
     * @return The mod's event bus, to allow subscription to Mod specific events
     */
    public IEventBus getModEventBus()
    {
        return container.getEventBus();
    }


    /**
     * Helper to get the right instance from the {@link ModLoadingContext} correctly.
     * @return The FMLJavaMod language specific extension from the ModLoadingContext
     */
    public static FMLJavaModLoadingContext get() {
        return ModLoadingContext.get().extension();
    }
}
