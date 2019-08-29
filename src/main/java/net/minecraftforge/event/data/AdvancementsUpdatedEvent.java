/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.event.data;

import net.minecraft.advancements.AdvancementManager;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraftforge.eventbus.api.Event;

/**
 * Data event for advancements.
 *
 */
public class AdvancementsUpdatedEvent extends Event
{
    /**
     * Fired on the logical server when the {@link AdvancementManager} has read all advancements from disk.  At this point all advancements are available on the server.
     */
    public static class Server extends AdvancementsUpdatedEvent {}

    /**
     * Fired on the logical client when the {@link ClientAdvancementManager} receives it's first sync packet.  At this point all advancements are available on the client.
     */
    public static class Client extends AdvancementsUpdatedEvent {}
}
