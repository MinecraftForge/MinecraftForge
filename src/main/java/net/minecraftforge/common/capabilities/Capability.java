package net.minecraftforge.common.capabilities;

import java.util.concurrent.Callable;

import com.google.common.base.Throwables;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;

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
        NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side);

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
         * @param A NBT holding the data. Must not be null, as doesn't make sense to call this function with nothing to read...
         */
        void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt);
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
     * A NEW instance of the default implementation.
     *
     * If it important to note that if you want to use the default storage
     * you may be required to use this exact implementation.
     * Refer to the owning API of the Capability in question.
     *
     * @return A NEW instance of the default implementation.
     */
    public T getDefaultInstance()
    {
        try
        {
            return this.factory.call();
        }
        catch (Exception e)
        {
            Throwables.propagate(e);
        }
        return null;
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
