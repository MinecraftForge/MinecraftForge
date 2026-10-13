/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
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
     *
     * @deprecated Use {{@link #addHandler(ResourceLocation, Runnable)} if you are only targeting a single registry
     */
    public static synchronized void addHandler(Consumer<Predicate<ResourceLocation>> ref)
    {
        objectHolders.add(ref);
    }


    /**
     * Registers a handler that is run after the named registry has been injected and finalized.
     * Handlers are kept in a HashSet per registry, so the same instance is only ever registered once.
     */
    public static synchronized void addHandler(ResourceLocation registry, Runnable ref)
    {
        namedHandlers.computeIfAbsent(registry, id -> new HashSet<>()).add(ref);
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

    /**
     * Removes a handler registered with {@link #addHandler(ResourceLocation, Runnable)}.
     *
     * @return true if handler was matched and removed.
     */
    public static synchronized boolean removeHandler(ResourceLocation registry, Runnable ref)
    {
        var handlers = namedHandlers.get(registry);
        return handlers != null && handlers.remove(ref);
    }

    /**
     * Registers a check that is run once, before the handlers of the next holder pass.
     * Used to report holders that point at a registry that does not exist.
     */
    public static synchronized void addValidator(Runnable ref)
    {
        validators.add(ref);
    }

    //==============================================================
    // Everything below is internal, do not use.
    //==============================================================

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Consumer<Predicate<ResourceLocation>>> objectHolders = new HashSet<>();
    // Order is not guaranteed, the old implementation kept every handler in one HashSet.
    private static final Map<ResourceLocation, Set<Runnable>> namedHandlers = new HashMap<>();
    private static final List<Runnable> validators = new ArrayList<>();

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
        LOGGER.debug(ForgeRegistry.REGISTRIES,"Processing ObjectHolder annotations");
        final HashMap<Type, String> classModIds = new HashMap<>();
        final List<ModFileScanData.AnnotationData> annotations = new ArrayList<>();

        for (var data : ModList.get().getAllScanData()) {
            for (var annotation : data.getAnnotations()) {
                if (OBJECT_HOLDER.equals(annotation.annotationType())) {
                    annotations.add(annotation);
                }
                // Gather all @Mod classes so that @ObjectHolder's in those classes don't need to specify the mod id; modder convenience
                else if (MOD.equals(annotation.annotationType())) {
                    classModIds.put(annotation.clazz(), (String)annotation.annotationData().get("value"));
                }
            }
        }

        // In theory when a mod is installed, we want to make sure we can update vanilla fields that are overriden by mods.
        // However, due to Mojang registry changes, and this code being old and having an oversight, this short circuit is expected behavior
        // So Lets just ignore the potential pitfalls for if they ever come up.
        if (annotations.isEmpty()) {
             return; // No object holders found, skip the rest of the processing,
        }

        Map<Type, Class<?>> classCache = Maps.newHashMap();

        // Validate all the vanilla class-level object holders then scan those first
        for (var data : VANILLA_OBJECT_HOLDERS) {
            try {
                Class<?> holderClass = Class.forName(data.holderClass(), true, ObjectHolderRegistry.class.getClassLoader());
                Class<?> registryClass = Class.forName(data.registryType(), true, ObjectHolderRegistry.class.getClassLoader());

                Type holderType = Type.getType(holderClass);
                classCache.put(holderType, holderClass);
                scanTarget(classModIds, classCache, holderType, null, registryClass, data.registryName(), "minecraft", true, true);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Vanilla class not found, should not be possible", e);
            }
        }

        // Scan actual fields annotated with @ObjectHolder second
        for (var data : annotations) {
            if (data.targetType() == ElementType.FIELD) {
                var registry = (String)data.annotationData().get("registryName");
                var name = (String)data.annotationData().get("value");
                scanTarget(classModIds, classCache, data.clazz(), data.memberName(), null, registry, name, false, false);
            }
            // This is where we WOULD scan for class targets, but Shrimp broke that in 1.19 and so I don't want to re-add a feature that apparently nobody used.
        }

        int count = objectHolders.size();
        for (var set : namedHandlers.values())
            count += set.size();

        LOGGER.debug(ForgeRegistry.REGISTRIES, "Found {} ObjectHolder annotations", count);
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
        ResourceLocation regName = ResourceLocation.parse(registryName);
        if (isClass)
        {
            scanClassForFields(classModIds, type, regName, registryClass, value, clazz, extractFromValue);
        }
        else
        {
            if (value.indexOf(':') == -1)
            {
                String prefix = classModIds.get(type);
                if (prefix == null)
                {
                    LOGGER.warn(ForgeRegistry.REGISTRIES,"Found an unqualified ObjectHolder annotation ({}) without a modid context at {}.{}, ignoring", value, type, annotationTarget);
                    throw new IllegalStateException("Unqualified reference to ObjectHolder");
                }
                value = prefix + ':' + value;
            }
            try
            {
                Field f = clazz.getDeclaredField(annotationTarget);
                ObjectHolderRef ref = ObjectHolderRef.create(regName, f, value, extractFromValue);
                if (ref != null)
                    addHandler(regName, ref);
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
                addHandler(registryName, ref);
        }
    }

    private static ResourceLocation getRegistryName(Map<Type, ResourceLocation> classRegistryNames, @Nullable String registryName,
            Type targetClass, Object declaration)
    {
        if (registryName != null)
            return ResourceLocation.parse(registryName);

        if (classRegistryNames.containsKey(targetClass))
            return classRegistryNames.get(targetClass);

        throw new IllegalStateException("No registry name was declared for " + declaration);
    }


    private static final Predicate<ResourceLocation> ALWAYS_TRUE = name -> true;
    public static void applyObjectHolders() {
        try {
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups");
            var exceptions = new ArrayList<Exception>();
            validate(exceptions);
            // Call named registries
            for (var handlers : namedHandlers.values())
                apply(handlers, exceptions);

            // Call legacy for backward compatibility
            if (!objectHolders.isEmpty())
                apply(ALWAYS_TRUE, objectHolders, exceptions);

            throwIfNeeded(exceptions);
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
        } catch (RuntimeException e) {
            // It is more important that the calling contexts continue without exception to prevent further cascading errors
            LOGGER.error("", e);
        }
    }

    /**
     * @deprecated use {@link #applyObjectHolders(ResourceLocation)}
     */
    public static void applyObjectHolders(Predicate<ResourceLocation> filter)
    {
        LOGGER.debug(ForgeRegistry.REGISTRIES, "Apply filtered holder lookups");
        var exceptions = new ArrayList<Exception>();
        validate(exceptions);

        // call new registries if needed
        for (var entry : namedHandlers.entrySet()) {
            if (filter.test(entry.getKey())) {
                apply(entry.getValue(), exceptions);
            }
        }

        // Call legacy
        if (!objectHolders.isEmpty())
            apply(filter, objectHolders, exceptions);

        throwIfNeeded(exceptions);
        LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
    }

    /** Runs the handlers of one registry. */
    public static void applyObjectHolders(ResourceLocation registry) {
        var exceptions = new ArrayList<Exception>();
        validate(exceptions);

        var handlers = namedHandlers.get(registry);
        if (handlers != null)
            apply(handlers, exceptions);

        if (!objectHolders.isEmpty())
            apply(registry::equals, objectHolders, exceptions);

        throwIfNeeded(exceptions);
    }

    // Each validator runs once, at the first holder pass after it was registered.
    private static void validate(List<Exception> exceptions) {
        List<Runnable> pending;
        synchronized (ObjectHolderRegistry.class) {
            if (validators.isEmpty())
                return;
            pending = new ArrayList<>(validators);
            validators.clear();
        }

        for (var validator : pending) {
            try {
                validator.run();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    private static void apply(Set<Runnable> handlers, List<Exception> exceptions) {
        for (var handler : handlers) {
            try {
                handler.run();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    private static void apply(Predicate<ResourceLocation> filter, Set<Consumer<Predicate<ResourceLocation>>> handlers, List<Exception> exceptions) {
        for (var handler : handlers) {
            try {
                handler.accept(filter);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    private static void throwIfNeeded(List<Exception> exceptions) {
        if (exceptions.isEmpty())
            return;

        var aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");
        for (var e : exceptions)
            aggregate.addSuppressed(e);
        throw aggregate;
    }

    private record VanillaObjectHolderData(String holderClass, String registryName, String registryType) {}
}
