/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
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

    @Deprecated(forRemoval = true, since = "1.21.9")
    public static EventBus<RegisterClientReloadListenersEvent> getBus(BusGroup modBusGroup) {
        return BUS;
    }

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
