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

package net.minecraftforge.fmllegacy.network.event;

import net.minecraft.network.Connection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkInstance;

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

    public boolean isRemotePresent(Connection manager) {
        return instance.isRemotePresent(manager);
    }
}
