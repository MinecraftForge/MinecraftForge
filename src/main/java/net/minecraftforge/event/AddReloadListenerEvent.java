/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;

/**
 * The main ResourceManager is recreated on each reload, through {@link MinecraftServer.ReloadableResources}'s creation.
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class AddReloadListenerEvent extends Event
{
    private final List<PreparableReloadListener> listeners = new ArrayList<>();
    /*
    private final MinecraftServer.ReloadableResources dataPackRegistries;

    public AddReloadListenerEvent(MinecraftServer.ReloadableResources dataPackRegistries)
    {
        this.dataPackRegistries = dataPackRegistries;
    }
    */

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

   /*
    public MinecraftServer.ReloadableResources getDataPackRegistries()
    {
        return dataPackRegistries;
    }
    */
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
