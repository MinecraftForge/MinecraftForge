/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class RegistryObject<T> implements Supplier<T>
{
    @Nullable
    private final ResourceLocation name;
    @Nullable
    private ResourceKey<T> key;
    private final boolean optionalRegistry;
    @Nullable
    private T value;
    @Nullable
    private Holder<T> holder;

    /**
     * Factory for a {@link RegistryObject} that stores the value of an object from the provided forge registry once it is ready.
     *
     * @param name the name of the object to look up in the forge registry
     * @param registry the forge registry
     * @return a {@link RegistryObject} that stores the value of an object from the provided forge registry once it is ready
     */
    public static <T, U extends T> RegistryObject<U> create(final ResourceLocation name, IForgeRegistry<T> registry)
    {
        return new RegistryObject<U>(name, registry);
    }

    /**
     * Factory for a {@link RegistryObject} that stores the value of an object from a registry once it is ready based on a lookup of the provided registry key.
     * <p>
     * If a registry with the given key cannot be found, an exception will be thrown when trying to fill this RegistryObject.
     * Use {@link #createOptional(ResourceLocation, ResourceKey, String)} for RegistryObjects of optional registries.
     *
     * @param name the name of the object to look up in a registry
     * @param registryKey the key of the registry. Supports lookups on {@link BuiltInRegistries} and {@link RegistryManager#ACTIVE}.
     * @param modid the mod id calling context
     * @return a {@link RegistryObject} that stores the value of an object from a registry once it is ready
     * @see #createOptional(ResourceLocation, ResourceKey, String)
     * @see #create(ResourceLocation, IForgeRegistry)
     * @see #create(ResourceLocation, ResourceLocation, String)
     */
    public static <T, U extends T> RegistryObject<U> create(final ResourceLocation name, final ResourceKey<? extends Registry<T>> registryKey, String modid)
    {
        return new RegistryObject<>(name, registryKey.location(), modid, false);
    }

    /**
     * Factory for a {@link RegistryObject} that optionally stores the value of an object from a registry once it is ready if the registry exists
     * based on a lookup of the provided registry key.
     * <p>
     * If a registry with the given key cannot be found, it will be silently ignored and this RegistryObject will not be filled.
     * Use {@link #create(ResourceLocation, ResourceKey, String)} for RegistryObjects that should throw exceptions on missing registry.
     *
     * @param name the name of the object to look up in a registry
     * @param registryKey the key of the registry. Supports lookups on {@link BuiltInRegistries} and {@link RegistryManager#ACTIVE}.
     * @param modid the mod id calling context
     * @return a {@link RegistryObject} that stores the value of an object from a registry once it is ready
     * @see #create(ResourceLocation, ResourceKey, String)
     * @see #create(ResourceLocation, IForgeRegistry)
     * @see #create(ResourceLocation, ResourceLocation, String)
     */
    public static <T, U extends T> RegistryObject<U> createOptional(final ResourceLocation name, final ResourceKey<? extends Registry<T>> registryKey,
            String modid)
    {
        return new RegistryObject<>(name, registryKey.location(), modid, true);
    }

    /**
     * Factory for a {@link RegistryObject} that stores the value of an object from a registry once it is ready based on a lookup of the provided registry name.
     * <p>
     * If a registry with the given name cannot be found, an exception will be thrown when trying to fill this RegistryObject.
     * Use {@link #createOptional(ResourceLocation, ResourceLocation, String)} for RegistryObjects of optional registries.
     *
     * @param name the name of the object to look up in a registry
     * @param registryName the name of the registry. Supports lookups on {@link BuiltInRegistries} and {@link RegistryManager#ACTIVE}.
     * @param modid the mod id calling context
     * @return a {@link RegistryObject} that stores the value of an object from a registry once it is ready
     * @see #createOptional(ResourceLocation, ResourceLocation, String)
     * @see #create(ResourceLocation, IForgeRegistry)
     * @see #create(ResourceLocation, ResourceKey, String)
     */
    public static <T, U extends T> RegistryObject<U> create(final ResourceLocation name, final ResourceLocation registryName, String modid)
    {
        return new RegistryObject<>(name, registryName, modid, false);
    }

    /**
     * Factory for a {@link RegistryObject} that optionally stores the value of an object from a registry once it is ready if the registry exists
     * based on a lookup of the provided registry name.
     * <p>
     * If a registry with the given name cannot be found, it will be silently ignored and this RegistryObject will not be filled.
     * Use {@link #create(ResourceLocation, ResourceLocation, String)} for RegistryObjects that should throw exceptions on missing registry.
     *
     * @param name the name of the object to look up in a registry
     * @param registryName the name of the registry. Supports lookups on {@link BuiltInRegistries} and {@link RegistryManager#ACTIVE}.
     * @param modid the mod id calling context
     * @return a {@link RegistryObject} that stores the value of an object from a registry once it is ready
     * @see #create(ResourceLocation, ResourceLocation, String)
     * @see #create(ResourceLocation, IForgeRegistry)
     * @see #create(ResourceLocation, ResourceKey, String)
     */
    public static <T, U extends T> RegistryObject<U> createOptional(final ResourceLocation name, final ResourceLocation registryName, String modid)
    {
        return new RegistryObject<>(name, registryName, modid, true);
    }

    private static final RegistryObject<?> EMPTY = new RegistryObject<>();

    private static <T> RegistryObject<T> empty() {
        @SuppressWarnings("unchecked")
        RegistryObject<T> t = (RegistryObject<T>) EMPTY;
        return t;
    }

    private RegistryObject() {
        this.name = null;
        this.key = null;
        this.optionalRegistry = false;
    }

    @SuppressWarnings("unchecked")
    private RegistryObject(ResourceLocation name, IForgeRegistry<?> registry)
    {
        if (registry == null)
            throw new IllegalArgumentException("Invalid registry argument, must not be null");
        this.name = name;
        this.key = (ResourceKey<T>) ResourceKey.create(registry.getRegistryKey(), name);
        this.optionalRegistry = false;
        ObjectHolderRegistry.addHandler(pred ->
        {
            if (pred.test(registry.getRegistryName()))
                this.updateReference((IForgeRegistry<? extends T>) registry);
        });
        this.updateReference((IForgeRegistry<? extends T>) registry);
    }

    private RegistryObject(final ResourceLocation name, final ResourceLocation registryName, final String modid, boolean optionalRegistry)
    {
        this.name = name;
        this.key = ResourceKey.create(ResourceKey.createRegistryKey(registryName), name);
        this.optionalRegistry = optionalRegistry;
        final Throwable callerStack = new Throwable("Calling Site from mod: " + modid);
        ObjectHolderRegistry.addHandler(new Consumer<>()
        {
            private boolean registryExists = false;
            private boolean invalidRegistry = false;

            @Override
            public void accept(Predicate<ResourceLocation> pred)
            {
                if (invalidRegistry)
                    return;
                if (!RegistryObject.this.optionalRegistry && !registryExists)
                {
                    if (!registryExists(registryName))
                    {
                        invalidRegistry = true;
                        throw new IllegalStateException("Unable to find registry with key " + registryName + " for mod \"" + modid + "\". Check the 'caused by' to see further stack.", callerStack);
                    }
                    registryExists = true;
                }
                if (pred.test(registryName))
                    RegistryObject.this.updateReference(registryName);
            }
        });
        this.updateReference(registryName);
    }

    /**
     * Retrieves the wrapped object in the registry.
     * This value will automatically be updated when the backing registry is updated.
     *
     * @throws NullPointerException If the value is null. Use {@link #isPresent()} to check if the value exists first.
     * @see #isPresent()
     * @see #orElse(Object)
     * @see #orElseGet(Supplier)
     * @see #orElseThrow(Supplier)
     */
    @NotNull
    @Override
    public T get()
    {
        T ret = this.value;
        Objects.requireNonNull(ret, () -> "Registry Object not present: " + this.name);
        return ret;
    }

    @SuppressWarnings("unchecked")
    void updateReference(IForgeRegistry<? extends T> registry)
    {
        if (this.name == null || this.key == null)
            return;
        if (registry.containsKey(this.name))
        {
            this.value = registry.getValue(this.name);
            this.holder = (Holder<T>) registry.getHolder(this.name).orElse(null);
        }
        else
        {
            this.value = null;
            this.holder = null;
        }
    }

    @SuppressWarnings("unchecked")
    void updateReference(Registry<? extends T> registry)
    {
        if (this.name == null || this.key == null)
            return;
        if (registry.containsKey(this.name))
        {
            this.value = registry.get(this.name);
            this.holder = ((Registry<T>) registry).getHolder(this.key).orElse(null);
        }
        else
        {
            this.value = null;
            this.holder = null;
        }
    }

    @SuppressWarnings("unchecked")
    void updateReference(ResourceLocation registryName)
    {
        if (this.name == null)
            return;
        IForgeRegistry<? extends T> forgeRegistry = RegistryManager.ACTIVE.getRegistry(registryName);
        if (forgeRegistry != null)
        {
            updateReference(forgeRegistry);
            return;
        }

        Registry<? extends T> vanillaRegistry = (Registry<? extends T>) BuiltInRegistries.REGISTRY.get(registryName);
        if (vanillaRegistry != null)
        {
            updateReference(vanillaRegistry);
            return;
        }

        this.value = null;
        this.holder = null;
    }

    void updateReference(RegisterEvent event)
    {
        IForgeRegistry<? extends T> forgeRegistry = event.getForgeRegistry();
        if (forgeRegistry != null)
        {
            updateReference(forgeRegistry);
            return;
        }

        Registry<? extends T> vanillaRegistry = event.getVanillaRegistry();
        if (vanillaRegistry != null)
            updateReference(vanillaRegistry);
        else
            this.value = null;
    }

    private static boolean registryExists(ResourceLocation registryName)
    {
        return RegistryManager.ACTIVE.getRegistry(registryName) != null
                || BuiltInRegistries.REGISTRY.containsKey(registryName);
    }

    public ResourceLocation getId()
    {
        return this.name;
    }

    /**
     * Returns the resource key that points to the registry and name of this registry object.
     * Nullable only if this RegistryObject is empty and has no name.
     *
     * @return the resource key that points to the registry and name of this registry object
     */
    @Nullable
    public ResourceKey<T> getKey()
    {
        return this.key;
    }

    public Stream<T> stream() {
        return isPresent() ? Stream.of(get()) : Stream.of();
    }

    /**
     * Return {@code true} if there is a mod object present, otherwise {@code false}.
     *
     * @return {@code true} if there is a mod object present, otherwise {@code false}
     */
    public boolean isPresent() {
        return this.value != null;
    }

    /**
     * If a mod object is present, invoke the specified consumer with the object,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a mod object is present
     * @throws NullPointerException if mod object is present and {@code consumer} is
     * null
     */
    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent())
            consumer.accept(get());
    }

    /**
     * If a mod object is present, and the mod object matches the given predicate,
     * return an {@code RegistryObject} describing the value, otherwise return an
     * empty {@code RegistryObject}.
     *
     * @param predicate a predicate to apply to the mod object, if present
     * @return an {@code RegistryObject} describing the value of this {@code RegistryObject}
     * if a mod object is present and the mod object matches the given predicate,
     * otherwise an empty {@code RegistryObject}
     * @throws NullPointerException if the predicate is null
     */
    public RegistryObject<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(get()) ? this : empty();
    }

    /**
     * If a mod object is present, apply the provided mapping function to it,
     * and if the result is non-null, return an {@code Optional} describing the
     * result.  Otherwise return an empty {@code Optional}.
     *
     * @apiNote This method supports post-processing on optional values, without
     * the need to explicitly check for a return status.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the mod object, if present
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the mod object of this {@code RegistryObject}, if a mod object is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null
     */
    public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return Optional.empty();
        else {
            return Optional.ofNullable(mapper.apply(get()));
        }
    }

    /**
     * If a value is present, apply the provided {@code Optional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code Optional}.  This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already an {@code Optional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code Optional}.
     *
     * @param <U> The type parameter to the {@code Optional} returned by
     * @param mapper a mapping function to apply to the mod object, if present
     *           the mapping function
     * @return the result of applying an {@code Optional}-bearing mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     */
    public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return Optional.empty();
        else {
            return Objects.requireNonNull(mapper.apply(get()));
        }
    }

    /**
     * If a mod object is present, lazily apply the provided mapping function to it,
     * returning a supplier for the transformed result. If this object is empty, or the
     * mapping function returns {@code null}, the supplier will return {@code null}.
     *
     * @apiNote This method supports post-processing on optional values, without
     * the need to explicitly check for a return status.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper A mapping function to apply to the mod object, if present
     * @return A {@code Supplier} lazily providing the result of applying a mapping
     * function to the mod object of this {@code RegistryObject}, if a mod object is present,
     * otherwise a supplier returning {@code null}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public<U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return () -> isPresent() ? mapper.apply(get()) : null;
    }

    /**
     * Return the mod object if present, otherwise return {@code other}.
     *
     * @param other the mod object to be returned if there is no mod object present, may
     * be null
     * @return the mod object, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return isPresent() ? get() : other;
    }

    /**
     * Return the mod object if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no mod object
     * is present
     * @return the mod object if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if mod object is not present and {@code other} is
     * null
     */
    public T orElseGet(Supplier<? extends T> other) {
        return isPresent() ? get() : other.get();
    }

    /**
     * Return the contained mod object, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown
     * @return the present mod object
     * @throws X if there is no mod object present
     * @throws NullPointerException if no mod object is present and
     * {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return get();
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Returns an optional {@link Holder} instance pointing to this RegistryObject's name and value.
     * <p>
     * This should <b>only</b> be used in cases where vanilla code requires passing in a Holder.
     * Mod-written code should rely on RegistryObjects or Suppliers instead.
     * <p>
     * The returned optional will be empty if the registry does not exist or if {@link #isPresent() returns false}.
     *
     * @return an optional {@link Holder} instance pointing to this RegistryObject's name and value
     */
    @NotNull
    public Optional<Holder<T>> getHolder()
    {
        return Optional.ofNullable(this.holder);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof RegistryObject) {
            return Objects.equals(((RegistryObject<?>)obj).name, name);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name);
    }
}
