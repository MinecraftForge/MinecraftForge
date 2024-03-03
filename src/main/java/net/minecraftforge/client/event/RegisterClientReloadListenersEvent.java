/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

public class RegisterClientReloadListenersEvent extends Event implements IModBusEvent
{
    private final ReloadableResourceManager resourceManager;

    public RegisterClientReloadListenersEvent(ReloadableResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    public void registerReloadListener(PreparableReloadListener reloadListener)
    {
        resourceManager.registerReloadListener(reloadListener);
    }
}
