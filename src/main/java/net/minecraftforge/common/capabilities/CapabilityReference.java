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

package net.minecraftforge.common.capabilities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A reference to a {@link Capability} object, whose value is injected at the appropriate time.
 *
 * <p>An instance of this class should be created and stored in a {@code static final} field, and referenced whenever
 * the {@code Capability} object within is needed. </p>
 *
 * <p>A capability reference does not contain the Capability object for the specified type when constructed. The
 * Capability value is injected at a later time by the {@link CapabilityManager}. To head off possible issues with
 * creating capability references too late, a warning is logged for each time an instance is created after capability
 * values have been injected.</p>
 *
 * <p>This serves as an alternative to annotating fields or methods with {@link CapabilityInject}. The most notable
 * use is to store this reference in a field of a capability <em>interface</em>, which is impossible due to the
 * de-finalizing effects of {@code CapabilityInject} conflicting with the JVM stipulation that all fields within an
 * interface must be {@code public static final}.</p>
 *
 * @param <T> the type of the referenced capability
 * @see CapabilityInject
 * @see net.minecraftforge.common.asm.CapabilityInjectDefinalize
 */
public class CapabilityReference<T> implements Supplier<Capability<T>>
{
    /**
     * Constructs a reference to the given capability specified by its {@link Class}.
     *
     * @param capabilityType class of the capability to be referenced
     * @param <T>            the referenced capability type
     * @return a reference to the given capability
     * @throws NullPointerException     if the given {@code Class} is {@code null}
     * @throws IllegalArgumentException if the given {@code Class} represents a {@linkplain Class#isPrimitive()
     *                                  primitive type} or an {@linkplain Class#isArray() array class}.
     */
    public static <T> CapabilityReference<T> create(Class<T> capabilityType)
    {
        if (hasPreviouslyInjected)
        {
            CapabilityManager.LOGGER.warn("""
                    A CapabilityReference was created after capabilities have been injected.
                    This may not work properly -- please report this to the mod as indicated in the stacktrace.
                    """, new RuntimeException("Mod created a CapabilityReference after injection -- this is only a stacktrace."));
        }

        final CapabilityReference<T> object = new CapabilityReference<>(capabilityType);

        synchronized (ALL_REFERENCES)
        {
            ALL_REFERENCES.add(object);
        }

        return object;
    }

    private final Class<T> capabilityType;
    @Nullable
    private Capability<T> capability = null;

    private CapabilityReference(Class<T> capabilityType)
    {
        checkNotNull(capabilityType, "Capability type must not be null");
        checkArgument(!capabilityType.isPrimitive(), "Primitives are not valid capability types");
        checkArgument(!capabilityType.isArray(), "Arrays are not valid capability types");

        this.capabilityType = capabilityType;
    }

    /**
     * {@return the {@code Class} for the capability referenced by this object}
     */
    public Class<T> getCapabilityType()
    {
        return capabilityType;
    }

    /**
     * If the {@code Capability} object is present in this reference, returns the object, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the {@code Capability} object for the capability referenced by this object
     * @throws NoSuchElementException if no {@code Capability} object is present
     */
    @Override
    public Capability<T> get()
    {
        if (capability == null)
        {
            throw new NoSuchElementException("No such capability object for " + capabilityType.getName());
        }
        return capability;
    }

    /**
     * If the {@code Capability} object is present in this reference, returns the object, otherwise returns {@code null}.
     *
     * @return the {@code Capability} object for the capability referenced by this object, or {@code null} if no
     * object is present yet
     */
    @Nullable
    public Capability<T> getOrNull()
    {
        return capability;
    }

    /**
     * {@return an optional which may contain the {@code Capability} object for the referenced capability}
     */
    public Optional<Capability<T>> getOptional()
    {
        return Optional.ofNullable(capability);
    }

    /**
     * {@return if this reference contains the {@link Capability} for its type}
     */
    public boolean isPresent()
    {
        return capability != null;
    }

    @Override
    public String toString()
    {
        return "CapabilityReference[" + capabilityType.getName() + "]." + (capability == null ? "empty" : "present");
    }

    /* ***** Beyond here lies dragons and Forge API implementation details! ***** */

    // Accesses to this set should be synchronized, because parallel mod construction which is when accesses to this
    // map are most likely
    // Uses a weak hash map wrapped as a set, to prevent us from accidentally preventing garbage collection
    private static final Set<CapabilityReference<?>> ALL_REFERENCES = Collections.newSetFromMap(new WeakHashMap<>());

    // Used to log a warning whenever a CapabilityReference is created after capabilities have been injected
    static boolean hasPreviouslyInjected = false;

    static void addCallbacks(Map<String, List<Consumer<Capability<?>>>> callbacks)
    {
        for (CapabilityReference<?> capObject : ALL_REFERENCES)
        {
            final Class<?> capType = capObject.getCapabilityType();
            final String capName = capType.getTypeName().intern(); // Since the map passed in is IdentityHashMap
            callbacks.computeIfAbsent(capName, k -> new ArrayList<>()).add(capObject::setCapability);
        }
    }

    // This exists because the compiler won't let us cast different Capability<?> instances
    @SuppressWarnings("unchecked")
    private void setCapability(@Nullable Capability<?> capability)
    {
        this.capability = (Capability<T>) capability;
    }
}
