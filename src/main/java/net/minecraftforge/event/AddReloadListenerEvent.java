/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftsingularity.common.crafting.conditions.ICondition;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.fml.ModLoader;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * The main ResourceManager is recreated on each reload, just after {@link ReloadableServerResources}'s creation.
 * <p>The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.</p>
 */
@NullMarked
public final class AddReloadListenerEvent extends MutableEvent {
    public static final EventBus<AddReloadListenerEvent> BUS = EventBus.create(AddReloadListenerEvent.class);

    private final List<PreparableReloadListener> listeners = new ArrayList<>();
    private final ReloadableServerResources serverResources;

    private final HolderLookup.Provider registries;

    public AddReloadListenerEvent(ReloadableServerResources serverResources, HolderLookup.Provider registries) {
        this.serverResources = serverResources;
        this.registries = registries;
    }

   /**
    * @param listener the listener to add to the ResourceManager on reload
    */
    public void addListener(PreparableReloadListener listener) {
        listeners.add(new WrappedStateAwareListener(listener));
    }

    public List<PreparableReloadListener> getListeners() {
        if (listeners.isEmpty()) return List.of();
        return List.copyOf(listeners);
    }

    /**
     * @return The ReloableServerResources being reloaded.
     */
    public ReloadableServerResources getServerResources() {
        return serverResources;
    }

    /**
     * This context object holds data relevant to the current reload, such as staged tags.
     * @return The condition context for the currently active reload.
     */
    public ICondition.IContext getConditionContext() {
        return serverResources.getConditionContext();
    }

    /**
     * @return A holder lookup provider containing the registries with updated tags.
     *
     * @see net.minecraft.server.ReloadableServerRegistries.LoadResult#lookupWithUpdatedTags()
     */
    public HolderLookup.Provider getRegistries() {
        return registries;
    }

    private record WrappedStateAwareListener(PreparableReloadListener wrapped) implements PreparableReloadListener {

        @Override
        public CompletableFuture<Void> reload(SharedState state, Executor backgroundExecutor, PreparationBarrier stage, Executor gameExecutor) {
            if (ModLoader.isLoadingStateValid())
                return wrapped.reload(state, backgroundExecutor, stage, gameExecutor);
            else
                return CompletableFuture.completedFuture(null);
        }

        @Override
        public String getName() {
            return wrapped.getName();
        }
    }
}
