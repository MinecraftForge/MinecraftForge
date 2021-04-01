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

package net.minecraftforge.registries;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.ModFileScanData;

import com.google.common.collect.Maps;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static net.minecraftforge.registries.ForgeRegistry.REGISTRIES;

/**
 * Internal registry for tracking {@link ObjectHolder} references
 */
public class ObjectHolderRegistry
{
    /**
     * Exposed to allow modders to register their own notification handlers.
     * This runnable will be called after a registry snapshot has been injected and finalized.
     * The internal list is backed by a HashSet so it is HIGHLY recommended you implement a proper equals
     * and hashCode function to de-duplicate callers here.
     * The default @ObjectHolder implementation uses the hashCode/equals for the field the annotation is on.
     */
    public static void addHandler(Consumer<Predicate<ResourceLocation>> ref)
    {
        objectHolders.add(ref);
    }

    /**
     * Removed the specified handler from the notification list.
     *
     * The internal list is backed by a hash set, and so proper hashCode and equals operations are required for success.
     *
     * The default @ObjectHolder implementation uses the hashCode/equals for the field the annotation is on.
     *
     * @return true if handler was matched and removed.
     */
    public static boolean removeHandler(Consumer<Predicate<ResourceLocation>> ref)
    {
        return objectHolders.remove(ref);
    }

    //==============================================================
    // Everything below is internal, do not use.
    //==============================================================

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Consumer<Predicate<ResourceLocation>>> objectHolders = new HashSet<>();
    private static final Type OBJECT_HOLDER = Type.getType(ObjectHolder.class);
    private static final Type MOD = Type.getType(Mod.class);

    public static void findObjectHolders()
    {
        LOGGER.debug(REGISTRIES,"Processing ObjectHolder annotations");
        final List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
            .map(ModFileScanData::getAnnotations)
            .flatMap(Collection::stream)
            .filter(a -> OBJECT_HOLDER.equals(a.getAnnotationType()) || MOD.equals(a.getAnnotationType()))
            .collect(Collectors.toList());

        Map<Type, String> classModIds = Maps.newHashMap();
        Map<Type, Class<?>> classCache = Maps.newHashMap();

        // Gather all @Mod classes, so that @ObjectHolder's in those classes don't need to specify the mod id, Modder convince
        annotations.stream().filter(a -> MOD.equals(a.getAnnotationType())).forEach(data -> classModIds.put(data.getClassType(), (String)data.getAnnotationData().get("value")));

        // double pass - get all the class level annotations first, then the field level annotations
        annotations.stream().filter(a -> OBJECT_HOLDER.equals(a.getAnnotationType())).filter(a -> a.getTargetType() == ElementType.TYPE)
        .forEach(data -> scanTarget(classModIds, classCache, data.getClassType(), null, (String)data.getAnnotationData().get("value"), true, data.getClassType().getClassName().startsWith("net.minecraft.")));

        annotations.stream().filter(a -> OBJECT_HOLDER.equals(a.getAnnotationType())).filter(a -> a.getTargetType() == ElementType.FIELD)
        .forEach(data -> scanTarget(classModIds, classCache, data.getClassType(), data.getMemberName(), (String)data.getAnnotationData().get("value"), false, false));
        LOGGER.debug(REGISTRIES,"Found {} ObjectHolder annotations", objectHolders.size());
    }

    private static void scanTarget(Map<Type, String> classModIds, Map<Type, Class<?>> classCache, Type type, @Nullable String annotationTarget, String value, boolean isClass, boolean extractFromValue)
    {
        Class<?> clazz;
        if (classCache.containsKey(type))
        {
            clazz = classCache.get(type);
        }
        else
        {
            try
            {
                clazz = Class.forName(type.getClassName(), extractFromValue, ObjectHolderRegistry.class.getClassLoader());
                classCache.put(type, clazz);
            }
            catch (ClassNotFoundException ex)
            {
                // unpossible?
                throw new RuntimeException(ex);
            }
        }
        if (isClass)
        {
            scanClassForFields(classModIds, type, value, clazz, extractFromValue);
        }
        else
        {
            if (value.indexOf(':') == -1)
            {
                String prefix = classModIds.get(type);
                if (prefix == null)
                {
                    LOGGER.warn(REGISTRIES,"Found an unqualified ObjectHolder annotation ({}) without a modid context at {}.{}, ignoring", value, type, annotationTarget);
                    throw new IllegalStateException("Unqualified reference to ObjectHolder");
                }
                value = prefix + ':' + value;
            }
            try
            {
                Field f = clazz.getDeclaredField(annotationTarget);
                ObjectHolderRef ref = new ObjectHolderRef(f, value, extractFromValue);
                if (ref.isValid())
                    addHandler(ref);
            }
            catch (NoSuchFieldException ex)
            {
                // unpossible?
                throw new RuntimeException(ex);
            }
        }
    }

    private static void scanClassForFields(Map<Type, String> classModIds, Type targetClass, String value, Class<?> clazz, boolean extractFromExistingValues)
    {
        classModIds.put(targetClass, value);
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
        for (Field f : clazz.getFields())
        {
            if (((f.getModifiers() & flags) != flags) || f.isAnnotationPresent(ObjectHolder.class))
                continue;
            ObjectHolderRef ref = new ObjectHolderRef(f, value + ':' + f.getName().toLowerCase(Locale.ENGLISH), extractFromExistingValues);
            if (ref.isValid())
                addHandler(ref);
        }
    }

    public static void applyObjectHolders()
    {
        LOGGER.debug(REGISTRIES,"Applying holder lookups");
        applyObjectHolders(key -> true);
        LOGGER.debug(REGISTRIES,"Holder lookups applied");
    }

    public static void applyObjectHolders(Predicate<ResourceLocation> filter)
    {
        objectHolders.forEach(e -> e.accept(filter));
    }

}
