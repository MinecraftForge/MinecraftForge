package net.minecraftforge.common.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * An Attached Capability Provider is a simple capability provider that can be attached
 * to game objects.<br>
 * 
 * 
 * @param <C> The type of the capability.
 * @param <O> The type of the provider being attached to.
 */
public interface IAttachedCapabilityProvider<C, O extends ICapabilityProvider>
{

    /**
     * @return The type of capability this provider can return.
     */
    CapabilityType<C> getType();

    /**
     * @return The ID of this provider.
     */
    ResourceLocation getId();

    /**
     * Retrieves a capability from this provider.
     * @param direction The side being queried. Will always be null if the underlying provider is not {@link ICapabilityProvider#isDirectionSensitive()}.
     * @return The provided {@link Capability}, or {@link Capability#empty()} if not available for the <code>direction</code>
     */
    @NotNull Capability<C> getCapability(@Nullable Direction direction);

    /**
     * @see {@link ICapabilityProvider#invalidateCaps()} for documentation.
     */
    void invalidateCaps();

    /**
     * @see {@link ICapabilityProvider#reviveCaps()} for documentation.
     */
    void reviveCaps();

    /**
     * Serialize this provider to NBT, if applicable.<br>
     * @return A {@link CompoundTag} representing the serialized form of this provider, or null, if not serializable.
     */
    default @Nullable CompoundTag serializeNBT()
    {
        return null;
    }

    /**
     * Reads this provider from NBT.<br>
     * This method will only be called if this provider was serialized via {@link #serializeNBT()}.
     * @param tag The serialized tag from {@link #serializeNBT()}.
     */
    default void deserializeNBT(CompoundTag tag)
    {
    }

    /**
     * For {@link ItemStack}s to stack, it must be validated that they are equivalent.<br>
     * This means validating that all attached capabilities are equivalent as well.<br>
     * This method allows attached capabilities to define if they are equivalent or not.
     * <p>
     * Specifically, this method compares this provider to another instance of this provider
     * on another object, or null (if it is not present on the other object).
     * <p>
     * The default implementation of this method always returns true, meaning this provider
     * will be ignored during comparison.
     * <p>
     * If your capability must be equivalent for itemstacks to merge, you MUST override this method.
     * 
     * @param other Another instance of this provider from another object, or null, if it was not present.
     * @return true if these providers are equivalent, otherwise false.
     */
    default boolean isEquivalentTo(@Nullable IAttachedCapabilityProvider<C, O> other)
    {
        return true;
    }

    /**
     * When {@link ItemStack}s are copied, it is inefficient to have all providers be serialized,
     * re-attached, and deserialized.  Instead, this method allows you to copy your provider to the new stack.
     * <p>
     * Specifically, when a parent object is copied, this method will be called for all attached providers
     * so that they can be copied over to the newly copied object.
     * <p>
     * The default implementation returns null, and returning null means this capability will NOT be attached
     * to the newly copied object.  There will also be no way to attach to the copied object.
     * <p>
     * If you want to attach to copied itemstacks, you MUST override this method.
     * 
     * @param copiedParent The newly copied parent object.
     * @return A deep copy of this {@link IAttachedCapabilityProvider}, or null, if it should not be copied.<br>
     *         Note that copies will not fire {@link AttachCapabilitiesEvent}, so you must use this method.
     */
    default @Nullable IAttachedCapabilityProvider<C, O> copy(O copiedParent)
    {
        return null;
    }
}