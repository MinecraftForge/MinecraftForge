/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.objectweb.asm.Type;

import java.util.function.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

public enum CapabilityManager
{
    INSTANCE;

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The Interface to be registered
     * @param storage A default implementation of the storage handler.
     * @param implementation A default implementation of the interface.
     */
    public <T> void register(Class<T> type, Capability.IStorage<T> storage, final Class<? extends T> implementation)
    {
        Preconditions.checkArgument(implementation != null, "Attempted to register a capability with no default implementation");
        register(type, storage, () ->
        {
            try {
                return implementation.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

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
        Preconditions.checkArgument(type    != null, "Attempted to register a capability with invalid type");
        Preconditions.checkArgument(storage != null, "Attempted to register a capability with no storage implementation");
        Preconditions.checkArgument(factory != null, "Attempted to register a capability with no default implementation factory");
        String realName = type.getName().intern();
        Preconditions.checkState(!providers.containsKey(realName), "Can not register a capability implementation multiple times: %s", realName);

        Capability<T> cap = new Capability<T>(realName, storage, factory);
        providers.put(realName, cap);

        List<Function<Capability<?>, Object>> list = callbacks.get(realName);
        if (list != null)
        {
            for (Function<Capability<?>, Object> func : list)
            {
                func.apply(cap);
            }
        }
    }

    // INTERNAL
    private IdentityHashMap<String, Capability<?>> providers = Maps.newIdentityHashMap();
    private IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks = Maps.newIdentityHashMap();
    public void injectCapabilities(ASMDataTable data)
    {
        for (ASMDataTable.ASMData entry : data.getAll(CapabilityInject.class.getName()))
        {
            final String targetClass = entry.getClassName();
            final String targetName = entry.getObjectName();
            Type type = (Type)entry.getAnnotationInfo().get("value");
            if (type == null)
            {
                FMLLog.log.warn("Unable to inject capability at {}.{} (Invalid Annotation)", targetClass, targetName);
                continue;
            }
            final String capabilityName = type.getInternalName().replace('/', '.').intern();

            List<Function<Capability<?>, Object>> list = callbacks.computeIfAbsent(capabilityName, k -> Lists.newArrayList());

            if (entry.getObjectName().indexOf('(') > 0)
            {
                list.add(new Function<Capability<?>, Object>()
                {
                    @Override
                    public Object apply(Capability<?> input)
                    {
                        try
                        {
                            for (Method mtd : Class.forName(targetClass).getDeclaredMethods())
                            {
                                if (targetName.equals(mtd.getName() + Type.getMethodDescriptor(mtd)))
                                {
                                    if ((mtd.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                                    {
                                        FMLLog.log.warn("Unable to inject capability {} at {}.{} (Non-Static)", capabilityName, targetClass, targetName);
                                        return null;
                                    }

                                    mtd.setAccessible(true);
                                    mtd.invoke(null, input);
                                    return null;
                                }
                            }
                            FMLLog.log.warn("Unable to inject capability {} at {}.{} (Method Not Found)", capabilityName, targetClass, targetName);
                        }
                        catch (Exception e)
                        {
                            FMLLog.log.warn("Unable to inject capability {} at {}.{}", capabilityName, targetClass, targetName, e);
                        }
                        return null;
                    }
                });
            }
            else
            {
                list.add(new Function<Capability<?>, Object>()
                {
                    @Override
                    public Object apply(Capability<?> input)
                    {
                        try
                        {
                            Field field = Class.forName(targetClass).getDeclaredField(targetName);
                            if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                            {
                                FMLLog.log.warn("Unable to inject capability {} at {}.{} (Non-Static)", capabilityName, targetClass, targetName);
                                return null;
                            }
                            EnumHelper.setFailsafeFieldValue(field, null, input);
                        }
                        catch (Exception e)
                        {
                            FMLLog.log.warn("Unable to inject capability {} at {}.{}", capabilityName, targetClass, targetName, e);
                        }
                        return null;
                    }
                });
            }
        }
    }
}