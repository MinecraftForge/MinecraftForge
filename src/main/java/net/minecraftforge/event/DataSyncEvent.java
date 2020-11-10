/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * The DataSyncEvent is fired when it is appropriate to sync data loaded from datapacks. <br>
 * <br>
 * This event is fired from {@link PlayerList#initializeConnectionToPlayer} and {@link PlayerList#reloadResources()} <br>
 * This event is fired only on the logical server, but may be fired off the main thread. <br> 
 * <br>
 * There are two subclasses, {@link BeforeRecipes} and {@link AfterRecipes}. <br>
 * These indicate the timing of data sync. <br>
 * {@link BeforeRecipes} fires before recipes and tags are synced to the client. <br>
 * {@link AfterRecipes} fires after recipes and tags are synced. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class DataSyncEvent extends Event
{
    /**
     * @see {@link DataSyncEvent}
     */
    public static class BeforeRecipes extends DataSyncEvent {}

    /**
     * @see {@link DataSyncEvent}
     */
    public static class AfterRecipes extends DataSyncEvent {}
}
