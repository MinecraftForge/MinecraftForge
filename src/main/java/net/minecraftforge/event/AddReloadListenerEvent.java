/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resources.IFutureReloadListener.IStage;

/**
 * The main ResourceManager is recreated on each reload, through {@link DataPackRegistries}'s creation.
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class AddReloadListenerEvent extends Event
{
    private final List<IFutureReloadListener> listeners = new ArrayList<>();
    private final DataPackRegistries dataPackRegistries;
    
    public AddReloadListenerEvent(DataPackRegistries dataPackRegistries)
    {
        this.dataPackRegistries = dataPackRegistries;
    }
    
   /**
    * @param listener the listener to add to the ResourceManager on reload
    */
   public void addListener(IFutureReloadListener listener)
   {
      listeners.add(new WrappedStateAwareListener(listener));
   }

   public List<IFutureReloadListener> getListeners()
   {
      return ImmutableList.copyOf(listeners);
   }
    
    public DataPackRegistries getDataPackRegistries()
    {
        return dataPackRegistries;
    }
    private static class WrappedStateAwareListener implements IFutureReloadListener {
        private final IFutureReloadListener wrapped;

        private WrappedStateAwareListener(final IFutureReloadListener wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public CompletableFuture<Void> reload(final IStage stage, final IResourceManager resourceManager, final IProfiler preparationsProfiler, final IProfiler reloadProfiler, final Executor backgroundExecutor, final Executor gameExecutor) {
            if (ModLoader.isLoadingStateValid())
                return wrapped.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            else
                return CompletableFuture.completedFuture(null);
        }
    }
}
