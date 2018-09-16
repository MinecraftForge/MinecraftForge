/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import net.minecraftforge.fml.ExtensionPoint;

import java.util.function.Supplier;

public class FMLModLoadingContext
{
    private static ThreadLocal<FMLModLoadingContext> context = ThreadLocal.withInitial(FMLModLoadingContext::new);
    private FMLModContainer activeContainer;
    public static FMLModLoadingContext get() {
        return context.get();
    }

    public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension) {
        getActiveContainer().registerExtensionPoint(point, extension);
    }

    public IEventBus getModEventBus()
    {
        return getActiveContainer().getEventBus();
    }

    public FMLModContainer getActiveContainer()
    {
        return activeContainer;
    }

    public void setActiveContainer(FMLModContainer activeContainer)
    {
        this.activeContainer = activeContainer;
    }
}
