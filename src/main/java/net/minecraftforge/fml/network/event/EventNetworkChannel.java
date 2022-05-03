/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.network.event;

import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkInstance;

import java.util.function.Consumer;

public class EventNetworkChannel
{
    private final NetworkInstance instance;

    public EventNetworkChannel(NetworkInstance instance)
    {
        this.instance = instance;
    }

    public <T extends NetworkEvent> void addListener(Consumer<T> eventListener)
    {
        instance.addListener(eventListener);
    }

    public void registerObject(Object object)
    {
        instance.registerObject(object);
    }

    public void unregisterObject(Object object)
    {
        instance.unregisterObject(object);
    }

    public boolean isRemotePresent(NetworkManager manager) {
        return instance.isRemotePresent(manager);
    }
}
