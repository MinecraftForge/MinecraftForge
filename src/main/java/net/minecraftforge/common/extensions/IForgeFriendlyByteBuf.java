/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

/**
 * Extension-Interface providing methods for writing registry-id's instead of their registry-names.
 */
public interface IForgeFriendlyByteBuf
{
    private FriendlyByteBuf self()
    {
        return (FriendlyByteBuf) this;
    }

    /**
     * Writes the given entries integer id to the buffer. Notice however that this will only write the id of the given entry and will not check whether it actually exists
     * in the given registry. Therefore no safety checks can be performed whilst reading it and if the entry is not in the registry a default value will be written.
     * @param registry The registry containing the given entry
     * @param entry The entry who's registryName is to be written
     * @param <T> The type of the entry.
     */
    default <T extends IForgeRegistryEntry<T>> void writeRegistryIdUnsafe(@NotNull IForgeRegistry<T> registry, @NotNull T entry)
    {
        ForgeRegistry<T> forgeRegistry = (ForgeRegistry<T>) registry;
        int id = forgeRegistry.getID(entry);
        self().writeVarInt(id);
    }

    /**
     * Writes the given entries integer id to the buffer. Notice however that this will only write the id of the given entry and will not check whether it actually exists
     * in the given registry. Therefore no safety checks can be performed whilst reading it and if the entry is not in the registry a default value will be written.
     * @param registry The registry containing the entry represented by this key
     * @param entryKey The registry-name of an entry in this {@link IForgeRegistry}
     */
    default void writeRegistryIdUnsafe(@NotNull IForgeRegistry<?> registry, @NotNull ResourceLocation entryKey)
    {
        ForgeRegistry<?> forgeRegistry = (ForgeRegistry<?>) registry;
        int id = forgeRegistry.getID(entryKey);
        self().writeVarInt(id);
    }

    /**
     * Reads an integer value from the buffer, which will be interpreted as an registry-id in the given registry. Notice that if there is no value in the specified registry for the
     * read id, that the registry's default value will be returned.
     * @param registry The registry containing the entry
     */
    default <T extends IForgeRegistryEntry<T>> T readRegistryIdUnsafe(@NotNull IForgeRegistry<T> registry)
    {
        ForgeRegistry<T> forgeRegistry = (ForgeRegistry<T>) registry;
        int id = self().readVarInt();
        return forgeRegistry.getValue(id);
    }

    /**
     * Writes an given registry-entry's integer id to the specified buffer in combination with writing the containing registry's id. In contrast to
     * {@link #writeRegistryIdUnsafe(IForgeRegistry, IForgeRegistryEntry)} this method checks every single step performed as well as
     * writing the registry-id to the buffer, in order to prevent any unexpected behaviour. Therefore this method is to be preferred whenever possible,
     * over using the unsafe methods.
     * @param entry The entry to write
     * @param <T> The type of the registry-entry
     * @throws NullPointerException if the entry was null
     * @throws IllegalArgumentException if the the registry could not be found or the registry does not contain the specified value
     */
    default <T extends IForgeRegistryEntry<T>> void writeRegistryId(@NotNull T entry)
    {
        Class<T> regType = Objects.requireNonNull(entry,"Cannot write a null registry entry!").getRegistryType();
        IForgeRegistry<T> retrievedRegistry = RegistryManager.ACTIVE.getRegistry(regType);
        Preconditions.checkArgument(retrievedRegistry!=null,"Cannot write registry id for an unknown registry type: %s",regType.getName());
        ResourceLocation name = retrievedRegistry.getRegistryName();
        Preconditions.checkArgument(retrievedRegistry.containsValue(entry),"Cannot find %s in %s",entry.getRegistryName()!=null?entry.getRegistryName():entry,name);
        ForgeRegistry<T> reg = (ForgeRegistry<T>)retrievedRegistry;
        self().writeResourceLocation(name); //TODO change to writing a varInt once registries use id's
        self().writeVarInt(reg.getID(entry));
    }

    /**
     * Reads an registry-entry from the specified buffer. Notice however that the type cannot be checked without providing an additional class parameter
     * - see {@link #readRegistryIdSafe(Class)} for an safe version.
     * @param <T> The type of the registry-entry. Notice that this should match the actual type written to the buffer.
     * @throws NullPointerException if the registry could not be found.
     */
    default <T extends IForgeRegistryEntry<T>> T readRegistryId()
    {
        ResourceLocation location = self().readResourceLocation(); //TODO change to reading a varInt once registries use id's
        ForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(location);
        return registry.getValue(self().readVarInt());
    }

    /**
     * Reads an registry-entry from the specified buffer. This method also verifies, that the value read is of the appropriate type.
     * @param <T> The type of the registry-entry.
     * @throws IllegalArgumentException if the retrieved entries registryType doesn't match the one passed in.
     * @throws NullPointerException if the registry could not be found.
     */
    default <T extends IForgeRegistryEntry<T>> T readRegistryIdSafe(Class<? super T> registrySuperType)
    {
        T value = readRegistryId();
        if (!value.getRegistryType().equals(registrySuperType))
            throw new IllegalArgumentException("Attempted to read an registryValue of the wrong type from the Buffer!");
        return value;
    }

    /**
     * Writes a FluidStack to the packet buffer, easy enough. If EMPTY, writes a FALSE.
     * This behavior provides parity with the ItemStack method in PacketBuffer.
     *
     * @param stack FluidStack to be written to the packet buffer.
     */
    default void writeFluidStack(FluidStack stack)
    {
        if (stack.isEmpty()) {
            self().writeBoolean(false);
        } else {
            self().writeBoolean(true);
            stack.writeToPacket(self());
        }
    }

    /**
     * Reads a FluidStack from this buffer.
     */
    default FluidStack readFluidStack()
    {
        return !self().readBoolean() ? FluidStack.EMPTY : FluidStack.readFromPacket(self());
    }
}
