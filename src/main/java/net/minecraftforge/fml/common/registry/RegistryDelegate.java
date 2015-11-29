package net.minecraftforge.fml.common.registry;

import com.google.common.base.Objects;
import net.minecraft.util.ResourceLocation;


/**
 * A registry delegate for holding references to items or blocks
 * These should be safe to use in things like lists though aliased items and blocks will not
 * have object identity with respect to their delegate.
 *
 * @author cpw
 *
 * @param <T> the type of thing we're holding onto
 */
public interface RegistryDelegate<T> {
    /**
     * Get the referent pointed at by this delegate. This will be the currently active item or block, and will change
     * as world saves come and go. Note that item.delegate.get() may NOT be the same object as item, due to item and
     * block substitution.
     *
     * @return The referred object
     */
    T get();

    /**
     * Get the name of this delegate. This is completely static after registration has completed and will never change.
     *
     * Deprecated in favour of the resource location.
     *
     * @see #getResourceName()
     * @return The name
     */
    @Deprecated
    String name();

    /**
     * Get the unique resource location for this delegate. Completely static after registration has completed, and
     * will never change.
     * @return The name
     */
    ResourceLocation getResourceName();

    /**
     * Get the delegate type. It will be dependent on the registry this delegate is sourced from.
     * @return The type of delegate
     */
    Class<T> type();

    /*
     * This is the internal implementation class of the delegate.
     */
    final class Delegate<T> implements RegistryDelegate<T>
    {
        private T referant;
        private ResourceLocation name;
        private final Class<T> type;

        public Delegate(T referant, Class<T> type) {
            this.referant = referant;
            this.type = type;
        }

        @Override
        public T get() {
            return referant;
        }

        @Override
        public String name() {
            return name.toString();
        }

        @Override
        public ResourceLocation getResourceName() { return name; }

        @Override
        public Class<T> type()
        {
            return this.type;
        }

        void changeReference(T newTarget)
        {
            this.referant = newTarget;
        }

        void setResourceName(ResourceLocation name) { this.name = name; }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Delegate)
            {
                Delegate<?> other = (Delegate<?>) obj;
                return Objects.equal(other.name, name);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(name);
        }
    }
}
