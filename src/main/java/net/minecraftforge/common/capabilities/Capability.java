/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.common.util.NonNullSupplier;

/**
 * This object holds a lazily-resolved reference to an instance of a capability
 * class.  The passed supplier will be executed once needed, and cached.<br>
 * <br>
 * Typical transformation operations such as {@link #map(NonNullFunction)} and
 * {@link #ifPresent(NonNullConsumer)} are available.<br>
 * <br>
 * This class also the ability to listen for invalidation, via
 * {@link #addListener(NonNullConsumer)}. This method is invoked when this
 * Capability is invalidated via {@link #invalidate()}<br>
 * <br>
 * To create an instance of this class, use {@link #of(NonNullSupplier)}.
 * The result of the supplier must never be null.
 * <br>
 * The empty instance can be retrieved with {@link #empty()}.
 *
 * @param <T> The type of the optional value.
 * 
 * @see {@link ICapabilityProvider#getCapability(CapabilityType, Direction)} for retrieving capabilities.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class Capability<T>
{
    private final NonNullSupplier<T> supplier;
    private final Object lock = new Object();
    /**
     * Some state of this Capability is based on the value of this object.<br>
     * If this field is null, it means the supplier has not been resolved.<br>
     * If this {@link Mutable} does not contain a value, the supplier was invalid.<br>
     * If this {@link Mutable} contains a value, the supplier has been resolved.<br>
     * <p>
     * Volatile modifier is required to ensure double-checked lock in {@link #getValue()} works.<br>
     * See https://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
     */
    private volatile Mutable<T> resolved;
    private Set<NonNullConsumer<Capability<T>>> listeners = new HashSet<>();
    private boolean isValid = true;

    private static final Capability<Void> EMPTY = new Capability<>(null);
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Construct a new {@link Capability} that wraps the given
     * {@link NonNullSupplier}.
     *
     * @param instanceSupplier The {@link NonNullSupplier} to wrap. Cannot return
     *                         null, but can be null itself. If null, this method
     *                         returns {@link #empty()}.
     */
    public static <T> Capability<T> of(final @Nullable NonNullSupplier<T> instanceSupplier)
    {
        return instanceSupplier == null ? empty() : new Capability<>(instanceSupplier);
    }

    /**
     * @return The singleton empty instance
     */
    public static <T> Capability<T> empty()
    {
        return EMPTY.cast();
    }

    /**
     * This method hides an unchecked cast to the inferred type. Only use this if
     * you are sure the type should match. Prefer
     * {@link CapabilityType#orEmpty(CapabilityType, Capability)} when possible.
     *
     * @return This {@link Capability}, cast to the inferred generic type
     */
    @SuppressWarnings("unchecked")
    public <X> Capability<X> cast()
    {
        return (Capability<X>)this;
    }

    private Capability(@Nullable NonNullSupplier<T> instanceSupplier)
    {
        this.supplier = instanceSupplier;
    }

    private @Nullable T getValue()
    {
        if (!isValid || supplier == null)
            return null;
        if (resolved == null)
        {
            synchronized (lock)
            {
                // resolved == null: Double checked locking to prevent two threads from resolving
                if (resolved == null)
                {
                    T temp = supplier.get();
                    if (temp == null)
                        LOGGER.catching(Level.WARN, new NullPointerException("Supplier should not return null value"));
                    resolved = new MutableObject<>(temp);
                }
            }
        }
        return resolved.getValue();
    }

    private T getValueUnsafe()
    {
        T ret = getValue();
        if (ret == null)
        {
            throw new IllegalStateException("LazyOptional is empty or otherwise returned null from getValue() unexpectedly");
        }
        return ret;
    }

    /**
     * Check if this {@link Capability} is non-empty.<br>
     * This method will not resolve the supplier.
     *
     * @return {@code true} if this {@link Capability} is non-empty, i.e. holds a
     *         non-null supplier
     */
    public boolean isPresent()
    {
        return supplier != null && isValid;
    }

    /**
     * If this capability {@link #isPresent()}, invoke the specified {@link NonNullConsumer} with the object,
     * otherwise do nothing.<br>
     * This method will resolve the supplier if it has not yet been resolved.
     *
     * @param consumer The {@link NonNullConsumer} to run if this optional is non-empty.
     * @throws NullPointerException if {@code consumer} is null and this {@link Capability} is non-empty
     */
    public void ifPresent(NonNullConsumer<? super T> consumer)
    {
        Objects.requireNonNull(consumer);
        T val = getValue();
        if (isValid && val != null)
            consumer.accept(val);
    }

    /**
     * If this capability {@link #isPresent()} return a new
     * {@link Capability} encapsulating the mapping function. Otherwise, returns
     * {@link #empty()}.
     * <p>
     * This method will not immediately resolve the supplier, but resolving
     * the returned object will cause the supplier of this object to be resolved.
     *
     * @apiNote This method supports post-processing on optional values, without the
     *          need to explicitly check for a return status.
     *
     * @apiNote The returned value does not receive invalidation messages from the original {@link Capability}.
     *          If you need the invalidation, you will need to manage them yourself.
     *
     * @param mapper A mapping function to apply to the instance, if present
     * @return A {@link Capability} describing the result of applying a mapping
     *         function to the value of this {@link Capability}, if a value is
     *         present, otherwise an empty {@link Capability}
     * @throws NullPointerException if {@code mapper} is null.
     */
    public <U> Capability<U> lazyMap(NonNullFunction<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);
        return isPresent() ? of(() -> mapper.apply(getValueUnsafe())) : empty();
    }

    /**
     * If a this capability {@link #isPresent()}, return a new
     * {@link Optional} encapsulating the mapped value. Otherwise, returns
     * {@link Optional#empty()}.
     * <p> 
     * This method explicitly resolves the value of the {@link Capability}.
     *
     * @param mapper A mapping function to apply to the instance, if present
     * @return An {@link Optional} describing the result of applying a mapping
     *         function to the value of this {@link Capability}, if a value is
     *         present, otherwise an empty {@link Optional}
     * @throws NullPointerException if {@code mapper} is null.
     * 
     * @see {@link #lazyMap(NonNullFunction)} for the lazy variant.
     */
    public <U> Optional<U> map(NonNullFunction<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);
        return isPresent() ? Optional.of(mapper.apply(getValueUnsafe())) : Optional.empty();
    }

    /**
     * If this capability {@link #isPresent()}, resolve it and filter it by the given
     * {@link NonNullPredicate}.
     * <p>
     * This method explicitly resolves the value of the {@link Capability}.
     *
     * @param predicate A {@link NonNullPredicate} to apply to the instance.
     * 
     * @return An {@link Optional} containing the underlying instance, 
     *         if and only if the passed {@link NonNullPredicate} returns
     *         true, otherwise {@link Optional#empty()}
     * @throws NullPointerException If {@code predicate} is null and this
     *                              {@link Optional} is non-empty
     */
    public Optional<T> filter(NonNullPredicate<? super T> predicate)
    {
        Objects.requireNonNull(predicate);
        final T value = getValue(); // To keep the non-null contract we have to evaluate right now. Should we allow this function at all?
        return value != null && predicate.test(value) ? Optional.of(value) : Optional.empty();
    }

    /**
     * Immediately resolves this {@link Capability} into an {@link Optional}
     * @return The resolved optional, or {@link Optional#empty()} if no value exists.
     */
    public Optional<T> resolve()
    {
        return isPresent() ? Optional.of(getValueUnsafe()) : Optional.empty();
    }

    /**
     * Immediately resolves this {@link Capability} into an object or a default value.
     *
     * @param other the value to be returned if this {@link Capability} is empty
     * @return the result of the supplier, if non-empty, otherwise {@code other}
     */
    public T orElse(T other)
    {
        T val = getValue();
        return val != null ? val : other;
    }

    /**
     * Resolve the contained supplier if non-empty and return the result, otherwise return the
     * result of {@code other}.
     *
     * @param other A {@link NonNullSupplier} whose result is returned if this
     *              {@link Capability} is empty
     * @return The result of the supplier, if non-empty, otherwise the result of
     *         {@code other.get()}
     * @throws NullPointerException If {@code other} is null and this
     *                              {@link Capability} is non-empty
     */
    public T orElseGet(NonNullSupplier<? extends T> other)
    {
        T val = getValue();
        return val != null ? val : other.get();
    }

    /**
     * Resolve the contained supplier if non-empty and return the result, otherwise throw the
     * exception created by the provided {@link NonNullSupplier}.
     *
     * @apiNote A method reference to the exception constructor with an empty
     *          argument list can be used as the supplier. For example,
     *          {@code IllegalStateException::new}
     *
     * @param                   <X> Type of the exception to be thrown
     * @param exceptionSupplier The {@link NonNullSupplier} which will return the
     *                          exception to be thrown
     * @return The result of the supplier
     * @throws X                    If this {@link Capability} is empty
     * @throws NullPointerException If {@code exceptionSupplier} is null and this
     *                              {@link Capability} is empty
     */
    public <X extends Throwable> T orElseThrow(NonNullSupplier<? extends X> exceptionSupplier) throws X
    {
        T val = getValue();
        if (val != null)
            return val;
        throw exceptionSupplier.get();
    }

    /**
     * Register a {@link NonNullConsumer listener} that will be called when this {@link Capability} becomes invalid (via {@link #invalidate()}).
     * <p>
     * Invalidation times are dependent on the underlying object - for example, Entities and BlockEntities become invalid when removed from the level.<br>
     * Chunks and Levels become invalidated when they are unloaded.<br>
     * ItemStacks are special, and are <b>never</b> invalidated.  Do not use the addListener pattern for itemstacks.
     * <p>
     * If this {@link Capability} is empty, the listener will be called immediately.
     */
    public void addListener(NonNullConsumer<Capability<T>> listener)
    {
        if (isPresent())
        {
            this.listeners.add(listener);
        }
        else
        {
            listener.accept(this);
        }
    }

    /**
     * Invalidate this {@link Capability}, making it unavailable for further use,
     * and notifying any {@link #addListener(NonNullConsumer) listeners} that this
     * has become invalid and they should update.
     * <p>
     * This would typically be used with capability objects. For example, a TE would
     * call this, if they are covered with a microblock panel, thus cutting off pipe
     * connectivity to this side.
     * <p>
     * Also should be called for all when a TE is invalidated (for example, when
     * the TE is removed or unloaded), or a world/chunk unloads, or a entity dies,
     * etc... This allows modders to keep a cache of capability objects instead of
     * re-checking them every tick.
     * <p>
     * Listeners are discarded once this capability becomes invalid.
     */
    public void invalidate()
    {
        if (this.isValid)
        {
            this.isValid = false;
            this.listeners.forEach(e -> e.accept(this));
            this.listeners.clear();
        }
    }
}
