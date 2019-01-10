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

package net.minecraftforge.registries;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.language.ModFileScanData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Internal registry for tracking {@link ObjectHolder} references
 */
public enum ObjectHolderRegistry
{
    INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type OBJ_HOLDER = Type.getType(ObjectHolder.class);
    private static final Type MOD = Type.getType(Mod.class);

    private List<ObjectHolderRef> objectHolders = Lists.newArrayList();

    public void findObjectHolders(List<ModFileScanData> scanData)
    {
        LOGGER.info("Processing ObjectHolder annotations");
        final List<ModFileScanData.AnnotationData> holders = getAnnotationData(scanData, OBJ_HOLDER);
        Map<String, String> classModIds = Maps.newHashMap();
        Map<String, Class<?>> classCache = Maps.newHashMap();

        getAnnotationData(scanData, MOD).forEach(data -> classModIds.put(data.getClassType().getClassName(), (String)data.getAnnotationData().get("value")));

        // double pass - get all the class level annotations first, then the field level annotations
        holders.stream().filter(data -> data.getMemberName().equals(data.getClassType().getClassName())).forEach(data ->
        {
            String value = (String)data.getAnnotationData().get("value");
            scanTarget(classModIds, classCache, data.getClassType().getClassName(), data.getMemberName(), value, true, data.getClassType().getClassName().startsWith("net.minecraft.init"));
        });
        holders.stream().filter(data -> !data.getMemberName().equals(data.getClassType().getClassName())).forEach(data ->
        {
            String value = (String)data.getAnnotationData().get("value");
            scanTarget(classModIds, classCache, data.getClassType().getClassName(), data.getMemberName(), value, false, false);
        });
        LOGGER.info("Found {} ObjectHolder annotations", objectHolders.size());
    }

    private List<ModFileScanData.AnnotationData> getAnnotationData(List<ModFileScanData> data, Type type)
    {
        return data.stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> type.equals(a.getAnnotationType()))
                .collect(Collectors.toList());
    }

    private void scanTarget(Map<String, String> classModIds, Map<String, Class<?>> classCache, String className, @Nullable String annotationTarget, String value, boolean isClass, boolean extractFromValue)
    {
        Class<?> clazz;
        if (classCache.containsKey(className))
        {
            clazz = classCache.get(className);
        }
        else
        {
            try
            {
                clazz = Class.forName(className, extractFromValue, getClass().getClassLoader());
                classCache.put(className, clazz);
            }
            catch (ClassNotFoundException ex)
            {
                // unpossible?
                throw new RuntimeException(ex);
            }
        }
        if (isClass)
        {
            scanClassForFields(classModIds, className, value, clazz, extractFromValue);
        }
        else
        {
            if (value.indexOf(':') == -1)
            {
                String prefix = classModIds.get(className);
                if (prefix == null)
                {
                    LOGGER.warn("Found an unqualified ObjectHolder annotation ({}) without a modid context at {}.{}, ignoring", value, className, annotationTarget);
                    throw new IllegalStateException("Unqualified reference to ObjectHolder");
                }
                value = prefix + ":" + value;
            }
            try
            {
                Field f = clazz.getDeclaredField(annotationTarget);
                addHolderReference(new ObjectHolderRef(f, new ResourceLocation(value), extractFromValue));
            }
            catch (NoSuchFieldException ex)
            {
                // unpossible?
                throw new RuntimeException(ex);
            }
        }
    }

    private void scanClassForFields(Map<String, String> classModIds, String className, String value, Class<?> clazz, boolean extractFromExistingValues)
    {
        classModIds.put(className, value);
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
        for (Field f : clazz.getFields())
        {
            if (((f.getModifiers() & flags) != flags) || f.isAnnotationPresent(ObjectHolder.class))
            {
                continue;
            }
            addHolderReference(new ObjectHolderRef(f, new ResourceLocation(value, f.getName().toLowerCase()), extractFromExistingValues));
        }
    }

    private void addHolderReference(ObjectHolderRef ref)
    {
        if (ref.isValid())
        {
            objectHolders.add(ref);
        }
    }

    public void applyObjectHolders()
    {
        LOGGER.info("Applying holder lookups");
        objectHolders.forEach(ObjectHolderRef::apply);
        LOGGER.info("Holder lookups applied");
    }

}
