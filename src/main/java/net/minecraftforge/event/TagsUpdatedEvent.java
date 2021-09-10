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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on the client when :
 *      {@link TagContainer} has all of its tags synced from the server to the client (just after a client has connected).
 *      The integrated server is about to be created see {@link net.minecraft.client.Minecraft#doLoadLevel}
 * Fired on the server when {@link TagContainer} has read all tags from disk, during initial load and after the reload command is used
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
}
