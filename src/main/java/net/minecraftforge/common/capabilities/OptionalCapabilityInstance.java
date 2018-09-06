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
package net.minecraftforge.common.capabilities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalCapabilityInstance<T>
{
    private final NonNullSupplier<T> supplier;
    private AtomicReference<T> resolved;
    private Set<Consumer<OptionalCapabilityInstance<T>>> listeners = new HashSet<>();
    private boolean isValid = true;

    private static final OptionalCapabilityInstance<Void> EMPTY = new OptionalCapabilityInstance<>(null);

    @SuppressWarnings("unchecked")
    public static <T> OptionalCapabilityInstance<T> empty()
    {
        return (OptionalCapabilityInstance<T>)EMPTY;
    }

    @SuppressWarnings("unchecked")
    public <X> OptionalCapabilityInstance<X> cast()
    {
        return (OptionalCapabilityInstance<X>)this;
    }

    private OptionalCapabilityInstance(NonNullSupplier<T> instanceSupplier)
    {
        this.supplier = instanceSupplier;
    }

    public static <T> OptionalCapabilityInstance<T> of(final NonNullSupplier<T> instanceSupplier)
    {
        return new OptionalCapabilityInstance<>(instanceSupplier);
    }

    private T getValue()
    {
        if (!isValid)
            return null;
        if (resolved != null)
            return resolved.get();

        if (supplier != null)
        {
            resolved = new AtomicReference<>(null);
            try
            {
                T temp = supplier.get();
                if (temp == null)
                    throw new IllegalStateException("Supplier must not return null value");
                resolved.set(temp);
                return resolved.get();
            }
            catch (Throwable e)
            {
                return null;
            }
        }
        return null;
    }

    /**
     * Return {@code true} if there is a mod object present, otherwise {@code false}.
     *
     * @return {@code true} if there is a mod object present, otherwise {@code false}
     */
    public boolean isPresent()
    {
        return supplier != null && isValid;
    }

    /**
     * If a mod object is present, invoke the specified consumer with the object,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a mod object is present
     * @throws NullPointerException if mod object is present and {@code consumer} is
     * null
     */
    public void ifPresent(Consumer<? super T> consumer)
    {
        if (isValid && getValue() != null)
            consumer.accept(getValue());
    }

    /**
     * If a mod object is present, and the mod object matches the given predicate,
     * return an {@code OptionalMod} describing the value, otherwise return an
     * empty {@code OptionalMod}.
     *
     * @param predicate a predicate to apply to the mod object, if present
     * @return an {@code OptionalMod} describing the value of this {@code OptionalMod}
     * if a mod object is present and the mod object matches the given predicate,
     * otherwise an empty {@code OptionalMod}
     * @throws NullPointerException if the predicate is null
     */
    public OptionalCapabilityInstance<T> filter(Predicate<? super T> predicate)
    {
        Objects.requireNonNull(predicate);
        final T value = getValue(); // To keep the non-null contract we have to evaluate right now. Should we allow this function at all?
        return predicate.test(value) ? OptionalCapabilityInstance.of(()->value) : empty();
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
     * function to the mod object of this {@code OptionalMod}, if a mod object is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function is null
     */
    public<U> OptionalCapabilityInstance<U> map(Function<? super T, ? extends U> mapper)
    {
        Objects.requireNonNull(mapper);
        return isPresent() ? OptionalCapabilityInstance.of(()->mapper.apply(getValue())) : empty();
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
    public<U> OptionalCapabilityInstance<U> flatMap(Function<? super T, Optional<U>> mapper)
    {//I am not sure this is valid, or how to handle this, it's just a copy pasta from Optional. I dont think its needed. Returning a null supplier is bad
        Objects.requireNonNull(mapper);
        final U value = map(mapper).orElse(Optional.empty()).orElse(null); // To keep the non-null contract we have to evaluate right now. Should we allow this function at all?
        return value != null ? OptionalCapabilityInstance.of(() -> value) : OptionalCapabilityInstance.empty();
    }

    /**
     * Return the mod object if present, otherwise return {@code other}.
     *
     * @param other the mod object to be returned if there is no mod object present, may
     * be null
     * @return the mod object, if present, otherwise {@code other}
     */
    public T orElse(T other)
    {
        return getValue() != null ? getValue() : other;
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
    public T orElseGet(Supplier<? extends T> other)
    {
        return getValue() != null ? getValue() : other.get();
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
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X
    {
        if (getValue() != null)
            return getValue();
        throw exceptionSupplier.get();
    }

    /**
     * Registers a listener that will be called when this Optional becomes invalid.
     */
    public void addListener(Consumer<OptionalCapabilityInstance<T>> listener)
    {
        if (!isPresent())
            listener.accept(this); // They are stupid so just directly call them.
        else
            this.listeners.add(listener);
    }

    /*
     * Should only be called by the 'Owner' of this capability, this is to notify any listerners that this has become invalid and they should update.
     * For example, a TE would call this, if they are covered with a microblock panel, thus cutting off pipe connectivity to this side.
     * Also should be called for all caps when a TE is invalidated, or a world/chunk unloads, or a entity dies, etc...
     * This allows modders to keep a cache of Capabilities instead of re-checking them every tick.
     */
    public void invalidate()
    {
        this.isValid = false;
        this.listeners.forEach(e -> e.accept(this));
    }
}
