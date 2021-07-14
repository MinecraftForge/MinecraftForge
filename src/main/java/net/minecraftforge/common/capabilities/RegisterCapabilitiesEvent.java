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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.concurrent.Callable;

import net.minecraftforge.eventbus.api.Event;
import static net.minecraftforge.fml.Logging.CAPABILITIES;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This event fires when it is time to register your capabilities.
 * @see CapabilityManager
 * @see Capability
 */
public final class RegisterCapabilitiesEvent extends Event implements IModBusEvent
{

    private static final Logger LOGGER = LogManager.getLogger("CapabilityManager");

    private final IdentityHashMap<String, Capability<?>> capabilities = new IdentityHashMap<>();

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The Interface to be registered
     * @param storage A default implementation of the storage handler.
     * @param factory A Factory that will produce new instances of the default implementation.
     */
    public <T> void register(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory)
    {
        Objects.requireNonNull(type,"Attempted to register a capability with invalid type");
        Objects.requireNonNull(storage,"Attempted to register a capability with no storage implementation");
        Objects.requireNonNull(factory,"Attempted to register a capability with no default implementation factory");
        String realName = type.getName().intern();
        Capability<T> cap;

        if (capabilities.containsKey(realName)) {
            LOGGER.error(CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
            throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ realName);
        }

        cap = new Capability<>(realName, storage, factory);
        capabilities.put(realName, cap);
    }

    IdentityHashMap<String, Capability<?>> getCapabilities()
    {
        return this.capabilities;
    }

}
