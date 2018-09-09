/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import net.minecraftforge.fml.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CAPABILITIES;

public enum CapabilityManager
{
    INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();

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
        if (providers.containsKey(realName)) {
            LOGGER.error(CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
            throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ realName);
        }

        Capability<T> cap = new Capability<>(realName, storage, factory);
        providers.put(realName, cap);

        callbacks.getOrDefault(realName, Collections.emptyList()).forEach(func -> func.apply(cap));
    }

    // INTERNAL
    private IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap<>();
    private IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks = new IdentityHashMap<>();
    public void injectCapabilities(List<ModFileScanData> data)
    {
        final List<ModFileScanData.AnnotationData> capabilities = data.stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).
                filter(a -> Objects.equals(a.getClassType(), Type.getType(CapabilityInject.class))).
                collect(Collectors.toList());
        capabilities.forEach(this::attachCapabilityToMethod);
    }

    private void attachCapabilityToMethod(ModFileScanData.AnnotationData entry)
    {
        final String targetClass = entry.getClassType().getClassName();
        final String targetName = entry.getMemberName();
        Type type = (Type)entry.getAnnotationData().get("value");
        if (type == null)
        {
            LOGGER.warn(CAPABILITIES,"Unable to inject capability at {}.{} (Invalid Annotation)", targetClass, targetName);
            return;
        }
        final String capabilityName = type.getInternalName().replace('/', '.').intern();

        List<Function<Capability<?>, Object>> list = callbacks.computeIfAbsent(capabilityName, k -> new ArrayList<>());

        if (entry.getMemberName().indexOf('(') > 0)
        {
            list.add(input -> {
                try
                {
                    for (Method mtd : Class.forName(targetClass).getDeclaredMethods())
                    {
                        if (targetName.equals(mtd.getName() + Type.getMethodDescriptor(mtd)))
                        {
                            if ((mtd.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                            {
                                LOGGER.warn(CAPABILITIES,"Unable to inject capability {} at {}.{} (Non-Static)", capabilityName, targetClass, targetName);
                                return null;
                            }

                            mtd.setAccessible(true);
                            mtd.invoke(null, input);
                            return null;
                        }
                    }
                    LOGGER.warn(CAPABILITIES,"Unable to inject capability {} at {}.{} (Method Not Found)", capabilityName, targetClass, targetName);
                }
                catch (Exception e)
                {
                    LOGGER.warn(CAPABILITIES,"Unable to inject capability {} at {}.{}", capabilityName, targetClass, targetName, e);
                }
                return null;
            });
        }
        else
        {
            list.add(input -> {
                try
                {
                    Field field = Class.forName(targetClass).getDeclaredField(targetName);
                    if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                    {
                        LOGGER.warn(CAPABILITIES,"Unable to inject capability {} at {}.{} (Non-Static)", capabilityName, targetClass, targetName);
                        return null;
                    }
                    field.setAccessible(true);
                    field.set(null, input);
                }
                catch (Exception e)
                {
                    LOGGER.warn(CAPABILITIES,"Unable to inject capability {} at {}.{}", capabilityName, targetClass, targetName, e);
                }
                return null;
            });
        }
    }
}
