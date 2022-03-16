/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Utility class to help with managing registry entries.
 * Maintains a list of all suppliers for entries and registers them during the proper Register event.
 * Suppliers should return NEW instances every time.
 *
 *Example Usage:
 *<pre>{@code
 *   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
 *   private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
 *
 *   public static final RegistryObject<Block> ROCK_BLOCK = BLOCKS.register("rock", () -> new Block(Block.Properties.create(Material.ROCK)));
 *   public static final RegistryObject<Item> ROCK_ITEM = ITEMS.register("rock", () -> new BlockItem(ROCK_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
 *
 *   public ExampleMod() {
 *       ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *       BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *   }
 *}</pre>
 *
 * @param <T> The base registry type, must be a concrete base class, do not use subclasses or wild cards.
 */
public class DeferredRegister<T extends IForgeRegistryEntry<T>>
{
    /**
     * Use for vanilla/forge registries. See example above.
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(IForgeRegistry<B> reg, String modid)
    {
        return new DeferredRegister<>(reg, modid);
    }

    /**
     * Use for custom registries that are made during the NewRegistry event.
     *
     * @deprecated Use {@link #create(ResourceLocation, String)} and {@link #makeRegistry(Class, Supplier)} instead
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    public static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(Class<B> base, String modid)
    {
        return new DeferredRegister<>(null, base, modid);
    }

    /**
     * DeferredRegister factory for custom registries that are made during the {@link NewRegistryEvent} event
     * or with {@link #makeRegistry(Class, Supplier)}.
     *
     * @param key The key of the registry to reference
     * @param modid The namespace for all objects registered to this DeferredRegister
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(ResourceKey<? extends Registry<B>> key, String modid)
    {
        return new DeferredRegister<>(key.location(), null, modid);
    }

    /**
     * DeferredRegister factory for custom registries that are made during the {@link NewRegistryEvent} event
     * or with {@link #makeRegistry(Class, Supplier)}.
     *
     * @param registryName The name of the registry, should include namespace. May come from another DeferredRegister through {@link #getRegistryName()}.
     * @param modid The namespace for all objects registered to this DeferredRegister
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(ResourceLocation registryName, String modid)
    {
        return new DeferredRegister<>(registryName, null, modid);
    }

    private final ResourceLocation registryName;
    private final Class<T> superType;
    private final String modid;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private IForgeRegistry<T> type;
    private Supplier<IForgeRegistry<T>> typeSupplier;
    private Supplier<RegistryBuilder<T>> registryFactory;
    private SetMultimap<TagKey<T>, Supplier<T>> optionalTags;
    private boolean seenRegisterEvent = false;

    private DeferredRegister(@Nullable ResourceLocation registryName, @Nullable Class<T> base, String modid)
    {
        this.registryName = registryName;
        this.superType = base;
        this.modid = modid;
    }

    private DeferredRegister(IForgeRegistry<T> reg, String modid)
    {
        this(reg.getRegistryName(), reg.getRegistrySuperType(), modid);
        this.type = reg;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup)
    {
        if (seenRegisterEvent)
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after RegistryEvent.Register has been fired.");
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        final ResourceLocation key = new ResourceLocation(modid, name);

        RegistryObject<I> ret;
        IForgeRegistry<T> reg = getForgeRegistry();
        if (reg != null)
            ret = RegistryObject.of(key, reg);
        else if (this.registryName != null)
            ret = RegistryObject.of(key, this.registryName, this.modid);
        else if (this.superType != null)
            ret = RegistryObject.of(key, this.superType, this.modid);
        else
            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");

        if (entries.putIfAbsent((RegistryObject<T>) ret, () -> sup.get().setRegistryName(key)) != null) {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }

        return ret;
    }

    /**
     * For custom registries only, fills the {@link #registryFactory} to be called later see {@link #register(IEventBus)}
     *
     * Calls {@link RegistryBuilder#setName} and {@link RegistryBuilder#setType} automatically.
     *
     * @param name  Path of the registry's {@link ResourceLocation}
     * @param sup   Supplier of the RegistryBuilder that is called to fill {@link #type} during the NewRegistry event
     * @return      A supplier of the {@link IForgeRegistry} created by the builder.
     *
     * @deprecated Use {@link #create(ResourceLocation, String)} and {@link #makeRegistry(Class, Supplier)} instead
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    public Supplier<IForgeRegistry<T>> makeRegistry(final String name, final Supplier<RegistryBuilder<T>> sup)
    {
        return makeRegistry(new ResourceLocation(modid, name), this.superType, sup);
    }

    /**
     * Only used for custom registries to fill the forge registry held in this DeferredRegister.
     *
     * Calls {@link RegistryBuilder#setName} and {@link RegistryBuilder#setType} automatically.
     *
     * @param base The base type to use in the created {@link IForgeRegistry}
     * @param sup Supplier of a RegistryBuilder that initializes a {@link IForgeRegistry} during the {@link NewRegistryEvent} event
     * @return A supplier of the {@link IForgeRegistry} created by the builder.
     * Will always return null until after the {@link NewRegistryEvent} event fires.
     */
    public Supplier<IForgeRegistry<T>> makeRegistry(final Class<T> base, final Supplier<RegistryBuilder<T>> sup)
    {
        return makeRegistry(this.registryName, base, sup);
    }

    /**
     * Creates a tag key based on the current modid and provided path as the location and the registry name linked to this DeferredRegister.
     * To control the namespace, use {@link #createTagKey(ResourceLocation)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     * Use the factories that take {@link #create(ResourceLocation, String) a registry name} or {@link #create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(ResourceLocation)
     * @see #createOptionalTagKey(String, Set)
     */
    @NotNull
    public TagKey<T> createTagKey(@NotNull String path)
    {
        Objects.requireNonNull(path);
        return createTagKey(new ResourceLocation(this.modid, path));
    }

    /**
     * Creates a tag key based on the provided resource location and the registry name linked to this DeferredRegister.
     * To use the current modid as the namespace, use {@link #createTagKey(String)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     * Use the factories that take {@link #create(ResourceLocation, String) a registry name} or {@link #create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(String)
     * @see #createOptionalTagKey(ResourceLocation, Set)
     */
    @NotNull
    public TagKey<T> createTagKey(@NotNull ResourceLocation location)
    {
        if (this.registryName == null)
            throw new IllegalStateException("The registry name was not set, cannot create a tag key");
        Objects.requireNonNull(location);
        return TagKey.create(ResourceKey.createRegistryKey(this.registryName), location);
    }

    /**
     * Creates a tag key with the current modid and provided path that will use the set of defaults if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     * To control the namespace, use {@link #createOptionalTagKey(ResourceLocation, Set)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     * Use the factories that take {@link #create(ResourceLocation, String) a registry name} or {@link #create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(String)
     * @see #createTagKey(ResourceLocation)
     * @see #createOptionalTagKey(ResourceLocation, Set)
     * @see #addOptionalTagDefaults(TagKey, Set)
     */
    @NotNull
    public TagKey<T> createOptionalTagKey(@NotNull String path, @NotNull Set<? extends Supplier<T>> defaults)
    {
        Objects.requireNonNull(path);
        return createOptionalTagKey(new ResourceLocation(this.modid, path), defaults);
    }

    /**
     * Creates a tag key with the provided location that will use the set of defaults if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     * To use the current modid as the namespace, use {@link #createOptionalTagKey(String, Set)}.
     *
     * @throws IllegalStateException If the registry name was not set.
     * Use the factories that take {@link #create(ResourceLocation, String) a registry name} or {@link #create(IForgeRegistry, String) forge registry}.
     * @see #createTagKey(String)
     * @see #createTagKey(ResourceLocation)
     * @see #createOptionalTagKey(String, Set)
     * @see #addOptionalTagDefaults(TagKey, Set)
     */
    @NotNull
    public TagKey<T> createOptionalTagKey(@NotNull ResourceLocation location, @NotNull Set<? extends Supplier<T>> defaults)
    {
        TagKey<T> tagKey = createTagKey(location);

        addOptionalTagDefaults(tagKey, defaults);

        return tagKey;
    }

    /**
     * Adds defaults to an existing tag key.
     * The set of defaults will be bound to the tag if the tag is not loaded from any datapacks.
     * Useful on the client side when a server may not provide a specific tag.
     *
     * @throws IllegalStateException If the registry name was not set.
     * Use the factories that take {@link #create(ResourceLocation, String) a registry name} or {@link #create(IForgeRegistry, String) forge registry}.
     * @see #createOptionalTagKey(String, Set)
     * @see #createOptionalTagKey(ResourceLocation, Set)
     */
    public void addOptionalTagDefaults(@NotNull TagKey<T> name, @NotNull Set<? extends Supplier<T>> defaults)
    {
        Objects.requireNonNull(defaults);
        if (optionalTags == null)
            optionalTags = Multimaps.newSetMultimap(new IdentityHashMap<>(), HashSet::new);

        optionalTags.putAll(name, defaults);
    }

    /**
     * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
     * See the example usage.
     *
     * @param bus The Mod Specific event bus.
     */
    public void register(IEventBus bus)
    {
        bus.register(new EventDispatcher(this));
        if (this.type == null && this.registryFactory != null) {
            bus.addListener(this::createRegistry);
        }
    }
    public static class EventDispatcher {
        private final DeferredRegister<?> register;

        public EventDispatcher(final DeferredRegister<?> register) {
            this.register = register;
        }

        @SubscribeEvent
        public void handleEvent(RegistryEvent.Register<?> event) {
            register.addEntries(event);
        }
    }
    /**
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries()
    {
        return entriesView;
    }

    /**
     * @return The registry name stored in this deferred register. Useful for creating new deferred registers based on an existing one.
     */
    @Nullable
    public ResourceLocation getRegistryName()
    {
        return this.registryName;
    }

    private Supplier<IForgeRegistry<T>> makeRegistry(final ResourceLocation registryName, final Class<T> superType, final Supplier<RegistryBuilder<T>> sup) {
        if (registryName == null)
            throw new IllegalStateException("Cannot create a registry without specifying a registry name");
        if (superType == null)
            throw new IllegalStateException("Cannot create a registry without specifying a base type");
        if (this.type != null || this.registryFactory != null)
            throw new IllegalStateException("Cannot create a registry for a type that already exists");

        this.registryFactory = () -> sup.get().setName(registryName).setType(superType);
        return this::getForgeRegistry;
    }

    private IForgeRegistry<T> getForgeRegistry()
    {
        if (this.type == null && this.typeSupplier != null)
            this.type = this.typeSupplier.get();

        return this.type;
    }

    private void onFill(IForgeRegistry<T> registry)
    {
        if (this.optionalTags == null)
            return;

        ITagManager<T> tagManager = registry.tags();
        if (tagManager == null)
            throw new IllegalStateException("The forge registry " + registry.getRegistryName() + " does not support tags, but optional tags were registered!");

        Multimaps.asMap(this.optionalTags).forEach(tagManager::addOptionalTagDefaults);
    }

    private void addEntries(RegistryEvent.Register<?> event)
    {
        IForgeRegistry<T> storedType = this.getForgeRegistry();
        if (storedType == null && this.registryFactory == null)
        {
            //If there is no type yet and we don't have a registry factory, attempt to capture the registry
            //Note: This will only ever get run on the first registry event, as after that time,
            // the type will no longer be null. This is needed here rather than during the NewRegistry event
            // to ensure that mods can properly use deferred registers for custom registries added by other mods
            captureRegistry();
            storedType = this.type;
        }
        if (storedType != null && event.getGenericType() == storedType.getRegistrySuperType())
        {
            this.seenRegisterEvent = true;
            @SuppressWarnings("unchecked")
            IForgeRegistry<T> reg = (IForgeRegistry<T>)event.getRegistry();
            for (Entry<RegistryObject<T>, Supplier<? extends T>> e : entries.entrySet())
            {
                reg.register(e.getValue().get());
                e.getKey().updateReference(reg);
            }
        }
    }

    private void createRegistry(NewRegistryEvent event)
    {
        this.typeSupplier = event.create(this.registryFactory.get(), this::onFill);
    }

    private void captureRegistry()
    {
        if (this.registryName != null)
        {
            this.type = RegistryManager.ACTIVE.getRegistry(this.registryName);
            if (this.type == null)
                throw new IllegalStateException("Unable to find registry with key " + this.registryName + " for modid \"" + modid + "\" after NewRegistry event");
        }
        else if (this.superType != null)
        {
            this.type = RegistryManager.ACTIVE.getRegistry(this.superType);
            if (this.type == null)
                throw new IllegalStateException("Unable to find registry for type " + this.superType.getName() + " for modid \"" + modid + "\" after NewRegistry event");
        }
        else
            throw new IllegalStateException("Unable to find registry for mod \"" + modid + "\" No lookup criteria specified.");
    }
}
