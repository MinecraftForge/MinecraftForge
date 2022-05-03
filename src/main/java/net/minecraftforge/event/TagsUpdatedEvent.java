/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on the client when {@link ITagCollectionSupplier} has all of its tags synced from the server to the client (just after a client has connected).
 * Fired on the server when {@link ITagCollectionSupplier} has read all tags from disk (during a data reload).
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 * On the client, this event fires on the Client Thread.
 * On the server, this event may be fired on the Server Thread, or an async reloader thread.
 */
public class TagsUpdatedEvent extends Event
{
    private final ITagCollectionSupplier manager;

    public TagsUpdatedEvent(ITagCollectionSupplier manager)
    {
        this.manager = manager;
    }

    /**
     * @return The network tag manager that has been updated with newly received tags.
     */
    public ITagCollectionSupplier getTagManager()
    {
        return manager;
    }

    /**
     * Fired after the Vanilla Tag types have been processed
     */
    public static class VanillaTagTypes extends TagsUpdatedEvent
    {
        public VanillaTagTypes(ITagCollectionSupplier manager)
        {
            super(manager);
        }
    }

    /**
     * Fired after any custom tag types have been processed
     */
    public static class CustomTagTypes extends TagsUpdatedEvent
    {
        public CustomTagTypes(ITagCollectionSupplier manager)
        {
            super(manager);
        }
    }
}
