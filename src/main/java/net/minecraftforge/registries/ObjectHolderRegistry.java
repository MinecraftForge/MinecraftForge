/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.ModFileScanData;

import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
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
    public static synchronized void addHandler(Consumer<Predicate<ResourceLocation>> ref)
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
    public static synchronized boolean removeHandler(Consumer<Predicate<ResourceLocation>> ref)
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
    // Hardcoded list of vanilla classes that should have object holders for each field of the given registry type.
    // IMPORTANT: Updates to this collection must be reflected in ObjectHolderDefinalize. Duplicated cuz classloaders, yay!
    // Classnames are validated below.
    private static final List<VanillaObjectHolderData> VANILLA_OBJECT_HOLDERS = List.of(
            new VanillaObjectHolderData("net.minecraft.world.level.block.Blocks", "block", "net.minecraft.world.level.block.Block"),
            new VanillaObjectHolderData("net.minecraft.world.item.Items", "item", "net.minecraft.world.item.Item"),
            new VanillaObjectHolderData("net.minecraft.world.item.enchantment.Enchantments", "enchantment", "net.minecraft.world.item.enchantment.Enchantment"),
            new VanillaObjectHolderData("net.minecraft.world.effect.MobEffects", "mob_effect", "net.minecraft.world.effect.MobEffect"),
            new VanillaObjectHolderData("net.minecraft.core.particles.ParticleTypes", "particle_type", "net.minecraft.core.particles.ParticleType"),
            new VanillaObjectHolderData("net.minecraft.sounds.SoundEvents", "sound_event", "net.minecraft.sounds.SoundEvent")
    );

    public static void findObjectHolders()
    {
        LOGGER.debug(REGISTRIES,"Processing ObjectHolder annotations");
        final List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
            .map(ModFileScanData::getAnnotations)
            .flatMap(Collection::stream)
            .filter(a -> OBJECT_HOLDER.equals(a.annotationType()) || MOD.equals(a.annotationType()))
            .toList();

        Map<Type, String> classModIds = Maps.newHashMap();
        Map<Type, Class<?>> classCache = Maps.newHashMap();

        // Gather all @Mod classes so that @ObjectHolder's in those classes don't need to specify the mod id; modder convenience
        annotations.stream()
                .filter(a -> MOD.equals(a.annotationType()))
                .forEach(data -> classModIds.put(data.clazz(), (String)data.annotationData().get("value")));

        // Validate all the vanilla class-level object holders then scan those first
        VANILLA_OBJECT_HOLDERS.forEach(data -> {
            try
            {
                Class<?> holderClass = Class.forName(data.holderClass(), true, ObjectHolderRegistry.class.getClassLoader());
                Class<?> registryClass = Class.forName(data.registryType(), true, ObjectHolderRegistry.class.getClassLoader());

                Type holderType = Type.getType(holderClass);
                classCache.put(holderType, holderClass);
                scanTarget(classModIds, classCache, holderType, null, registryClass, data.registryName(), "minecraft", true, true);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Vanilla class not found, should not be possible", e);
            }
        });

        // Scan actual fields annotated with @ObjectHolder second
        annotations.stream()
                .filter(a -> OBJECT_HOLDER.equals(a.annotationType())).filter(a -> a.targetType() == ElementType.FIELD)
                .forEach(data -> scanTarget(classModIds, classCache, data.clazz(),
                        data.memberName(), null, (String)data.annotationData().get("registryName"),
                        (String)data.annotationData().get("value"), false, false));

        LOGGER.debug(REGISTRIES,"Found {} ObjectHolder annotations", objectHolders.size());
    }

    private static void scanTarget(Map<Type, String> classModIds, Map<Type, Class<?>> classCache, Type type,
            @Nullable String annotationTarget, @Nullable Class<?> registryClass, String registryName,
            String value, boolean isClass, boolean extractFromValue)
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
            scanClassForFields(classModIds, type, new ResourceLocation(registryName), registryClass, value, clazz, extractFromValue);
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
                ObjectHolderRef ref = ObjectHolderRef.create(new ResourceLocation(registryName), f, value, extractFromValue);
                if (ref != null)
                    addHandler(ref);
            }
            catch (NoSuchFieldException ex)
            {
                // unpossible?
                throw new RuntimeException(ex);
            }
        }
    }

    private static void scanClassForFields(Map<Type, String> classModIds, Type targetClass,
            ResourceLocation registryName, Class<?> registryClass, String value, Class<?> clazz, boolean extractFromExistingValues)
    {
        classModIds.put(targetClass, value);
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC;
        for (Field f : clazz.getFields())
        {
            if (((f.getModifiers() & flags) != flags) || f.isAnnotationPresent(ObjectHolder.class) || !registryClass.isAssignableFrom(f.getType()))
                continue;
            ObjectHolderRef ref = ObjectHolderRef.create(registryName, f, value + ':' + f.getName().toLowerCase(Locale.ENGLISH), extractFromExistingValues);
            if (ref != null)
                addHandler(ref);
        }
    }

    private static ResourceLocation getRegistryName(Map<Type, ResourceLocation> classRegistryNames, @Nullable String registryName,
            Type targetClass, Object declaration)
    {
        if (registryName != null)
            return new ResourceLocation(registryName);

        if (classRegistryNames.containsKey(targetClass))
            return classRegistryNames.get(targetClass);

        throw new IllegalStateException("No registry name was declared for " + declaration);
    }

    public static void applyObjectHolders()
    {
        try
        {
            LOGGER.debug(REGISTRIES, "Applying holder lookups");
            applyObjectHolders(key -> true);
            LOGGER.debug(REGISTRIES, "Holder lookups applied");
        } catch (RuntimeException e)
        {
            // It is more important that the calling contexts continue without exception to prevent further cascading errors
            LOGGER.error("", e);
        }
    }

    public static void applyObjectHolders(Predicate<ResourceLocation> filter)
    {
        RuntimeException aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");
        objectHolders.forEach(objectHolder -> {
            try
            {
                objectHolder.accept(filter);
            }
            catch (Exception e)
            {
                aggregate.addSuppressed(e);
            }
        });

        if (aggregate.getSuppressed().length > 0)
        {
            throw aggregate;
        }
    }

    private record VanillaObjectHolderData(String holderClass, String registryName, String registryType) {}
}
