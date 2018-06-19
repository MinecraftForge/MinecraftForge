/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.ModLifecycleEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ModLoadingStage
{
    ERROR(null),
    CONSTRUCT(null),
    PREINIT(()->FMLPreInitializationEvent::new),
    SIDEDINIT(SidedProvider.SIDEDINIT::get),
    INIT(()->FMLInitializationEvent::new),
    POSTINIT(()->FMLPostInitializationEvent::new),
    COMPLETE(()->FMLLoadCompleteEvent::new),
    DONE(null);

    private final Supplier<Function<ModContainer, ModLifecycleEvent>> modLifecycleEventFunction;

    ModLoadingStage(Supplier<Function<ModContainer, ModLifecycleEvent>> modLifecycleEventFunction)
    {
        this.modLifecycleEventFunction = modLifecycleEventFunction;
    }

    public ModLifecycleEvent getModEvent(ModContainer fmlModContainer)
    {
        return modLifecycleEventFunction.get().apply(fmlModContainer);
    }
}
