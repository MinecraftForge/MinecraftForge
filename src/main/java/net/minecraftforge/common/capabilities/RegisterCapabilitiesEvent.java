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

package net.minecraftforge.common.capabilities;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.*;

import static net.minecraftforge.fml.Logging.CAPABILITIES;

/**
 * This event fires when it is time to register your capabilities.
 * @see Capability
 */
public final class RegisterCapabilitiesEvent extends Event implements IModBusEvent
{

    private final IdentityHashMap<String, Capability<?>> capabilities = new IdentityHashMap<>();

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The type to be registered
     */
    public <T> void register(Class<T> type)
    {
        Objects.requireNonNull(type,"Attempted to register a capability with invalid type");
        String realName = type.getName().intern();
        if (capabilities.putIfAbsent(realName, new Capability<>(realName)) != null) {
            CapabilityManager.LOGGER.error(CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
            throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ realName);
        }
    }

    IdentityHashMap<String, Capability<?>> getCapabilities()
    {
        return this.capabilities;
    }

}