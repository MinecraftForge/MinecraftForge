/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on the client when {@link NetworkTagManager} has all of its tags synced from the server to the client (just after a client has connected).
 * Fired on the server when {@link NetworkTagManager} has read all tags from disk (during a data reload).
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 * On the client, this event fires on the Client Thread.
 * On the server, this event may be fired on the Server Thread, or an async reloader thread.
 */
public class TagsUpdatedEvent extends Event
{
    
    private final NetworkTagManager manager;
    
    public TagsUpdatedEvent(NetworkTagManager manager)
    {
        this.manager = manager;
    }

    /**
     * @return The network tag manager that has been updated with newly received tags.
     */
    public NetworkTagManager getTagManager()
    {
        return manager;
    }
}
