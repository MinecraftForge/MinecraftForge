/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftsingularity.event.AddReloadListenerEvent;
import net.minecraftsingularity.eventbus.api.bus.EventBus;
import net.minecraftsingularity.eventbus.api.event.MutableEvent;
import net.minecraftsingularity.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftsingularity.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Fired to allow mods to register their reload listeners on the client-side resource manager.
 * This event is fired once during the construction of the {@link Minecraft} instance.
 *
 * <p>For registering reload listeners on the server-side resource manager, see {@link AddReloadListenerEvent}.</p>
 *
 * <p>This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
@NullMarked
public final class RegisterClientReloadListenersEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterClientReloadListenersEvent> BUS = EventBus.create(RegisterClientReloadListenersEvent.class);

    private final ReloadableResourceManager resourceManager;

    @ApiStatus.Internal
    public RegisterClientReloadListenersEvent(ReloadableResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * Registers the given reload listener to the client-side resource manager.
     *
     * @param reloadListener the reload listener
     */
    public void registerReloadListener(PreparableReloadListener reloadListener) {
        resourceManager.registerReloadListener(reloadListener);
    }
}
