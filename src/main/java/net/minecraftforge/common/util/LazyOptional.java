/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This object encapsulates a lazy value, with typical transformation operations
 * (map/ifPresent) available, much like {@link Optional}.
 * <p>
 * It also provides the ability to listen for invalidation, via
 * {@link #addListener(NonNullConsumer)}. This method is invoked when the provider of
 * this object calls {@link #invalidate()}.
 * <p>
 * To create an instance of this class, use {@link #of(NonNullSupplier)}. Note
 * that this accepts a {@link NonNullSupplier}, so the result of the supplier
 * must never be null.
 * <p>
 * The empty instance can be retrieved with {@link #empty()}.
 * 
 * @param <T> The type of the optional value.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LazyOptional<T>
{
    private final NonNullSupplier<T> supplier;
    private final Object lock = new Object();
    // null -> not resolved yet
    // non-null and contains non-null value -> resolved
    // non-null and contains null -> resolved, but supplier returned null (contract violation)
    private Mutable<T> resolved;
    private Set<NonNullConsumer<LazyOptional<T>>> listeners = new HashSet<>();
    private boolean isValid = true;

    private static final @Nonnull LazyOptional<Void> EMPTY = new LazyOptional<>(null);
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Construct a new {@link LazyOptional} that wraps the given
     * {@link NonNullSupplier}.
     * 
     * @param instanceSupplier The {@link NonNullSupplier} to wrap. Cannot return
     *                         null, but can be null itself. If null, this method
     *                         returns {@link #empty()}.
     */
    public static <T> LazyOptional<T> of(final @Nullable NonNullSupplier<T> instanceSupplier)
    {
        return instanceSupplier == null ? empty() : new LazyOptional<>(instanceSupplier);
    }

    /**
     * @return The singleton empty instance
     */
    public static <T> LazyOptional<T> empty()
    {
        return EMPTY.cast();
    }

    /**
     * This method hides an unchecked cast to the inferred type. Only use this if
     * you are sure the type should match. For capabilities, generally
     * {@link Capability#orEmpty(Capability, LazyOptional)} should be used.
     * 
     * @return This {@link LazyOptional}, cast to the inferred generic type
     */
    @SuppressWarnings("unchecked")
    public <X> LazyOptional<X> cast()
    {
        return (LazyOptional<X>)this;
    }

    private LazyOptional(@Nullable NonNullSupplier<T> instanceSupplier)
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
     * Check if this {@link LazyOptional} is non-empty.
     * 
     * @return {@code true} if this {@link LazyOptional} is non-empty, i.e. holds a
     *         non-null supplier
     */
    public boolean isPresent()
    {
        return supplier != null && isValid;
    }

    /**
     * If non-empty, invoke the specified {@link NonNullConsumer} with the object,
     * otherwise do nothing.
     *
     * @param consumer The {@link NonNullConsumer} to run if this optional is non-empty.
     * @throws NullPointerException if {@code consumer} is null and this {@link LazyOptional} is non-empty
     */
    public void ifPresent(NonNullConsumer<? super T> consumer)
    {
        Objects.requireNonNull(consumer);
        T val = getValue();
        if (isValid && val != null)
            consumer.accept(val);
    }

    /**
     * If a this {@link LazyOptional} is non-empty, return a new
     * {@link LazyOptional} encapsulating the mapping function. Otherwise, returns
     * {@link #empty()}.
     * <p>
     * The supplier inside this object is <strong>NOT</strong> resolved.
     *
     * @apiNote This method supports post-processing on optional values, without the
     *          need to explicitly check for a return status.
     *
     * @apiNote The returned value does not receive invalidation messages from the original {@link LazyOptional}.
     *          If you need the invalidation, you will need to manage them yourself.
     *
     * @param mapper A mapping function to apply to the mod object, if present
     * @return A {@link LazyOptional} describing the result of applying a mapping
     *         function to the value of this {@link LazyOptional}, if a value is
     *         present, otherwise an empty {@link LazyOptional}
     * @throws NullPointerException if {@code mapper} is null.
     */
    public <U> LazyOptional<U> lazyMap(NonNullFunction<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);
        return isPresent() ? of(() -> mapper.apply(getValueUnsafe())) : empty();
    }

    /**
     * If a this {@link LazyOptional} is non-empty, return a new
     * {@link Optional} encapsulating the mapped value. Otherwise, returns
     * {@link Optional#empty()}.
     *
     * @apiNote This method explicitly resolves the value of the {@link LazyOptional}.
     *          For a non-resolving mapper that will lazily run the mapping, use {@link #lazyMap(NonNullFunction)}.
     *
     * @param mapper A mapping function to apply to the mod object, if present
     * @return An {@link Optional} describing the result of applying a mapping
     *         function to the value of this {@link Optional}, if a value is
     *         present, otherwise an empty {@link Optional}
     * @throws NullPointerException if {@code mapper} is null.
     */
    public <U> Optional<U> map(NonNullFunction<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);
        return isPresent() ? Optional.of(mapper.apply(getValueUnsafe())) : Optional.empty();
    }

    /**
     * Resolve the contained supplier if non-empty, and filter it by the given
     * {@link NonNullPredicate}, returning empty if false.
     * <p>
     * <em>It is important to note that this method is <strong>not</strong> lazy, as
     * it must resolve the value of the supplier to validate it with the
     * predicate.</em>
     * 
     * @param predicate A {@link NonNullPredicate} to apply to the result of the
     *                  contained supplier, if non-empty
     * @return An {@link Optional} containing the result of the contained
     *         supplier, if and only if the passed {@link NonNullPredicate} returns
     *         true, otherwise an empty {@link Optional}
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
     * Resolves the value of this LazyOptional, turning it into a standard non-lazy {@link Optional<T>}
     * @return The resolved optional.
     */
    public Optional<T> resolve()
    {
        return isPresent() ? Optional.of(getValueUnsafe()) : Optional.empty();
    }

    /**
     * Resolve the contained supplier if non-empty and return the result, otherwise return
     * {@code other}.
     *
     * @param other the value to be returned if this {@link LazyOptional} is empty
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
     *              {@link LazyOptional} is empty
     * @return The result of the supplier, if non-empty, otherwise the result of
     *         {@code other.get()}
     * @throws NullPointerException If {@code other} is null and this
     *                              {@link LazyOptional} is non-empty
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
     * @throws X                    If this {@link LazyOptional} is empty
     * @throws NullPointerException If {@code exceptionSupplier} is null and this
     *                              {@link LazyOptional} is empty
     */
    public <X extends Throwable> T orElseThrow(NonNullSupplier<? extends X> exceptionSupplier) throws X
    {
        T val = getValue();
        if (val != null)
            return val;
        throw exceptionSupplier.get();
    }

    /**
     * Register a {@link NonNullConsumer listener} that will be called when this {@link LazyOptional} becomes invalid (via {@link #invalidate()}).
     * <p>
     * If this {@link LazyOptional} is empty, the listener will be called immediately.
     */
    public void addListener(NonNullConsumer<LazyOptional<T>> listener)
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
     * Invalidate this {@link LazyOptional}, making it unavailable for further use,
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
     */
    public void invalidate()
    {
        if (this.isValid)
        {
            this.isValid = false;
            this.listeners.forEach(e -> e.accept(this));
        }
    }
}
