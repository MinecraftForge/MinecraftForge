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

import java.util.concurrent.Callable;

import com.google.common.base.Throwables;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the core holder object Capabilities.
 * Each capability will have ONE instance of this class,
 * and it will the the one passed into the ICapabilityProvider functions.
 *
 * The CapabilityManager is in charge of creating this class.
 */
public class Capability<T>
{
    public static interface IStorage<T>
    {
        /**
         * Serialize the capability instance to a NBTTag.
         * This allows for a central implementation of saving the data.
         *
         * It is important to note that it is up to the API defining
         * the capability what requirements the 'instance' value must have.
         *
         * Due to the possibility of manipulating internal data, some
         * implementations MAY require that the 'instance' be an instance
         * of the 'default' implementation.
         *
         * Review the API docs for more info.
         *
         * @param capability The Capability being stored.
         * @param instance An instance of that capabilities interface.
         * @param side The side of the object the instance is associated with.
         * @return a NBT holding the data. Null if no data needs to be stored.
         */
        @Nullable
        INBT writeNBT(Capability<T> capability, T instance, Direction side);

        /**
         * Read the capability instance from a NBT tag.
         *
         * This allows for a central implementation of saving the data.
         *
         * It is important to note that it is up to the API defining
         * the capability what requirements the 'instance' value must have.
         *
         * Due to the possibility of manipulating internal data, some
         * implementations MAY require that the 'instance' be an instance
         * of the 'default' implementation.
         *
         * Review the API docs for more info.         *
         *
         * @param capability The Capability being stored.
         * @param instance An instance of that capabilities interface.
         * @param side The side of the object the instance is associated with.
         * @param nbt A NBT holding the data. Must not be null, as doesn't make sense to call this function with nothing to read...
         */
        void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt);
    }

    /**
     * @return The unique name of this capability, typically this is
     * the fully qualified class name for the target interface.
     */
    public String getName() { return name; }

    /**
     * @return An instance of the default storage handler. You can safely use this store your default implementation in NBT.
     */
    public IStorage<T> getStorage() { return storage; }

    /**
     * Quick access to the IStorage's readNBT.
     * See {@link IStorage#readNBT(Capability, Object, EnumFacing, NBTBase)}  for documentation.
     */
    public void readNBT(T instance, Direction side, INBT nbt)
    {
        storage.readNBT(this, instance, side, nbt);
    }

    /**
     * Quick access to the IStorage's writeNBT.
     * See {@link IStorage#writeNBT(Capability, Object, EnumFacing)} for documentation.
     */
    @Nullable
    public INBT writeNBT(T instance, Direction side)
    {
        return storage.writeNBT(this, instance, side);
    }

    /**
     * A NEW instance of the default implementation.
     *
     * If it important to note that if you want to use the default storage
     * you may be required to use this exact implementation.
     * Refer to the owning API of the Capability in question.
     *
     * @return A NEW instance of the default implementation.
     */
    @Nullable
    public T getDefaultInstance()
    {
        try
        {
            return this.factory.call();
        }
        catch (Exception e)
        {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    public @Nonnull <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst)
    {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    // INTERNAL
    private final String name;
    private final IStorage<T> storage;
    private final Callable<? extends T> factory;

    Capability(String name, IStorage<T> storage, Callable<? extends T> factory)
    {
        this.name = name;
        this.storage = storage;
        this.factory = factory;
    }
}
