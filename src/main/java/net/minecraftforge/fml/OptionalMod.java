/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class OptionalMod<T>
{
    private final String modId;
    private T value;
    private boolean searched;

    public static <M> OptionalMod<M> of(final String modId) {
        return new OptionalMod<>(modId);
    }

    private static OptionalMod<?> EMPTY = new OptionalMod<>(true);
    private static <T> OptionalMod<T> empty() {
        @SuppressWarnings("unchecked")
        OptionalMod<T> t = (OptionalMod<T>) EMPTY;
        return t;
    }

    private OptionalMod(boolean searched) {
        this.searched = searched;
        this.modId = "";
    }

    private OptionalMod(String modId)
    {
        this.modId = modId;
    }

    private T getValue()
    {
        if (!searched) {
            this.value = ModList.get().<T>getModObjectById(this.modId).orElse(null);
            searched = true;
        }
        return this.value;
    }

    /**
     * If a mod is present in this {@code OptionalMod}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the modobject held by this {@code OptionalMod}
     * @throws NoSuchElementException if there is no modobject present
     *
     * @see Optional#isPresent()
     */
    public T get() {
        if (getValue() == null) {
            throw new NoSuchElementException("No value present");
        }
        return getValue();
    }

    public String getModId() {
        return this.modId;
    }

    /**
     * Return {@code true} if there is a mod object present, otherwise {@code false}.
     *
     * @return {@code true} if there is a mod object present, otherwise {@code false}
     */
    public boolean isPresent() {
        return getValue() != null;
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
        if (getValue() != null)
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
    public OptionalMod<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(getValue()) ? this : empty();
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
    public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return Optional.empty();
        else {
            return Optional.ofNullable(mapper.apply(getValue()));
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
            return Objects.requireNonNull(mapper.apply(getValue()));
        }
    }

    /**
     * Return the mod object if present, otherwise return {@code other}.
     *
     * @param other the mod object to be returned if there is no mod object present, may
     * be null
     * @return the mod object, if present, otherwise {@code other}
     */
    public T orElse(T other) {
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
    public T orElseGet(Supplier<? extends T> other) {
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
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (getValue() != null) {
            return getValue();
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof OptionalMod) {
            return Objects.equals(((OptionalMod)obj).modId, modId);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(modId);
    }
}
