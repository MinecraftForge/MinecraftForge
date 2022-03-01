/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.tags.TagContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on the client when :
 *      {@link TagContainer} has all of its tags synced from the server to the client (just after a client has connected).
 *      The integrated server is about to be created see {@code net.minecraft.client.Minecraft#doLoadLevel}
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
     * @return The impl tag manager that has been updated with newly received tags.
     */
    public TagContainer getTagManager()
    {
        return manager;
    }
}
