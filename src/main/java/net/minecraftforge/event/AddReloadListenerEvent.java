/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.ApiStatus;

/**
 * The main ResourceManager is recreated on each reload, just after {@link ReloadableServerResources}'s creation.
 * A copy of the listeners attached from vanilla can be obtained from {@link ReloadableServerResources#listeners()}
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class AddReloadListenerEvent extends Event
{
    @Deprecated(forRemoval = true, since = "1.19.2")
    private final List<PreparableReloadListener> listeners = new ArrayList<>();
    private final List<PreparableReloadListener> returnList;
    private final ReloadableServerResources serverResources;

    @ApiStatus.Internal
    public AddReloadListenerEvent(ReloadableServerResources serverResources, List<PreparableReloadListener> returnList)
    {
        this.serverResources = serverResources;
        this.returnList = returnList;
    }

    /**
     * @deprecated this constructor will be removed as it lacks the proper context
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    @ApiStatus.Internal
    public AddReloadListenerEvent(ReloadableServerResources serverResources)
    {
        this.serverResources = serverResources;
        this.returnList = new ArrayList<>();
    }

   /**
    * @param listener the listener to add to the ResourceManager on reload
    */
    public void addListener(PreparableReloadListener listener)
    {
        var wrapped = new WrappedStateAwareListener(listener);
        listeners.add(wrapped);
        returnList.add(wrapped);
    }


    /**
     * This method inserts a {@link PreparableReloadListener} before a specified vanilla reload listener. It does not work for inserting listeners before modded reload listeners. Use event priorities for that functionality.
     *
     * Only use this method if you do something in a vanilla reload listener that depends on your own reloadable data.
     * For example, if you require some reloadable data to be present in a recipe.
     *
     * @param listener The listener to add to the ResourceManager.
     * @param current The listener which this listener should be loaded before. These can be obtained from various methods in {@link ReloadableServerResources}
     */
    public void addListenerBefore(PreparableReloadListener listener, PreparableReloadListener current)
    {
        var wrapped = new WrappedStateAwareListener(listener);
        int index = returnList.indexOf(current);
        if (index != -1)
        {
            returnList.add(index, wrapped); // success, we can add it to the return list
        }
        else
        {
            returnList.add(wrapped); // we did not find it, just append it to the end
        }
    }

    /**
     * @deprecated use getAllListeners as that contains the list of all listeners rather than mod-added ones
     * {@return a copy of the list of vanilla resource listeners}
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    public List<PreparableReloadListener> getListeners()
    {
       return List.copyOf(listeners);
    }

    /**
     * {@return a copy of the current list of reload listeners} This includes reload listeners from both vanilla and this event.
     */
    public List<PreparableReloadListener> getAllListeners()
    {
        return List.copyOf(returnList);
    }

    /**
     * @return The ReloableServerResources being reloaded.
     */
    public ReloadableServerResources getServerResources()
    {
        return serverResources;
    }

    /**
     * This context object holds data relevant to the current reload, such as staged tags.
     * @return The condition context for the currently active reload.
     */
    public ICondition.IContext getConditionContext()
    {
        return serverResources.getConditionContext();
    }

    private static class WrappedStateAwareListener implements PreparableReloadListener
    {
        private final PreparableReloadListener wrapped;

        private WrappedStateAwareListener(final PreparableReloadListener wrapped)
        {
            this.wrapped = wrapped;
        }

        @Override
        public CompletableFuture<Void> reload(final PreparationBarrier stage, final ResourceManager resourceManager, final ProfilerFiller preparationsProfiler, final ProfilerFiller reloadProfiler, final Executor backgroundExecutor, final Executor gameExecutor)
        {
            if (ModLoader.isLoadingStateValid())
                return wrapped.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            else
                return CompletableFuture.completedFuture(null);
        }
    }
}
