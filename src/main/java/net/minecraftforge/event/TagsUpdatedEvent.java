/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import net.minecraft.tags.TagContainer;
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
    private final TagContainer manager;

    public TagsUpdatedEvent(TagContainer manager)
    {
        this.manager = manager;
    }

    /**
     * @return The network tag manager that has been updated with newly received tags.
     */
    public TagContainer getTagManager()
    {
        return manager;
    }

    /**
     * Fired after the Vanilla Tag types have been processed
     */
    public static class VanillaTagTypes extends TagsUpdatedEvent
    {
        public VanillaTagTypes(TagContainer manager)
        {
            super(manager);
        }
    }

    /**
     * Fired after any custom tag types have been processed
     */
    public static class CustomTagTypes extends TagsUpdatedEvent
    {
        public CustomTagTypes(TagContainer manager)
        {
            super(manager);
        }
    }
}
