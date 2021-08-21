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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CAPABILITIES;

public enum CapabilityManager
{
    INSTANCE;
    static final Logger LOGGER = LogManager.getLogger();
    private static final Type CAP_INJECT = Type.getType(CapabilityInject.class);

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     * This method is safe to call during parallel mod loading.
     *
     * @param type The class type to be registered
     * @deprecated use {@link RegisterCapabilitiesEvent}
     */
    @Deprecated(forRemoval = true)
    public <T> void register(Class<T> type)
    {
        Objects.requireNonNull(type,"Attempted to register a capability with invalid type");
        String realName = type.getName().intern();
        Capability<T> cap;

        synchronized (providers)
        {
            if (providers.containsKey(realName)) {
                LOGGER.error(CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
                throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ realName);
            }

            cap = new Capability<>(realName);
            providers.put(realName, cap);
        }

        fireCallbacks(this.callbacks, List.of(cap));
    }

    // INTERNAL
    private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap<>();
    private volatile IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks;
    public void injectCapabilities(List<ModFileScanData> data)
    {
        final List<ModFileScanData.AnnotationData> elementsToInject = data.stream()
            .map(ModFileScanData::getAnnotations)
            .flatMap(Collection::stream)
            .filter(a -> CAP_INJECT.equals(a.annotationType()))
            .collect(Collectors.toList());
        final IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks = new IdentityHashMap<>();
        elementsToInject.forEach(entry -> gatherCallbacks(callbacks, entry));

        var event = new RegisterCapabilitiesEvent();
        ModLoader.get().postEvent(event);
        fireCallbacks(callbacks, event.getCapabilities().values());

        // TODO 1.18: remove these fields
        this.providers.putAll(event.getCapabilities());
        this.callbacks = callbacks;
    }

    private static void fireCallbacks(Map<String, List<Function<Capability<?>, Object>>> callbacks, Iterable<Capability<?>> caps)
    {
        for (var cap : caps)
        {
            callbacks.getOrDefault(cap.getName(), List.of()).forEach(f -> f.apply(cap));
        }
    }

    private static void gatherCallbacks(Map<String, List<Function<Capability<?>, Object>>> callbacks, ModFileScanData.AnnotationData annotationData)
    {
        final String targetClass = annotationData.clazz().getClassName();
        final String targetName = annotationData.memberName();
        Type type = (Type)annotationData.annotationData().get("value");
        if (type == null)
        {
            LOGGER.warn(CAPABILITIES,"Unable to inject capability at {}.{} (Invalid Annotation)", targetClass, targetName);
            return;
        }
        final String capabilityName = type.getInternalName().replace('/', '.').intern();

        List<Function<Capability<?>, Object>> list = callbacks.computeIfAbsent(capabilityName, k -> new ArrayList<>());

        if (annotationData.memberName().indexOf('(') > 0)
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
