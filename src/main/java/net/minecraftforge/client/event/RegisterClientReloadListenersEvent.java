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

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Fired to allow mods to register their reload listeners on the client-side resource manager.
 * This event is fired once during the construction of the {@link Minecraft} instance.
 *
 * <p>For registering reload listeners on the server-side resource manager, see {@link AddReloadListenerEvent}. </p>
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
 */
public class RegisterClientReloadListenersEvent extends Event implements IModBusEvent
{
    private final ReloadableResourceManager resourceManager;

    public RegisterClientReloadListenersEvent(ReloadableResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    /**
     * Registers the given reload listener to the client-side resource manager.
     *
     * @param reloadListener the reload listener
     */
    public void registerReloadListener(PreparableReloadListener reloadListener)
    {
        resourceManager.registerReloadListener(reloadListener); // TODO: does this need to be wrapped and made state-aware?
    }
}
