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

package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;

/**
 * The main ResourceManager is recreated on each reload, through {@link DataPackRegistries}'s creation.
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class AddReloadListenerEvent extends Event
{
    private final List<PreparableReloadListener> listeners = new ArrayList<>();
    private final ServerResources dataPackRegistries;
    
    public AddReloadListenerEvent(ServerResources dataPackRegistries)
    {
        this.dataPackRegistries = dataPackRegistries;
    }
    
   /**
    * @param listener the listener to add to the ResourceManager on reload
    */
   public void addListener(PreparableReloadListener listener)
   {
      listeners.add(new WrappedStateAwareListener(listener));
   }

   public List<PreparableReloadListener> getListeners()
   {
      return ImmutableList.copyOf(listeners);
   }
    
    public ServerResources getDataPackRegistries()
    {
        return dataPackRegistries;
    }
    private static class WrappedStateAwareListener implements PreparableReloadListener {
        private final PreparableReloadListener wrapped;

        private WrappedStateAwareListener(final PreparableReloadListener wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public CompletableFuture<Void> reload(final PreparationBarrier stage, final ResourceManager resourceManager, final ProfilerFiller preparationsProfiler, final ProfilerFiller reloadProfiler, final Executor backgroundExecutor, final Executor gameExecutor) {
            if (ModLoader.isLoadingStateValid())
                return wrapped.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            else
                return CompletableFuture.completedFuture(null);
        }
    }
}
