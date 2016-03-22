package net.minecraftforge.fml.common.registry;

import com.google.common.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public interface IForgeRegistryEntry<V>
{
    /**
     * Sets a unique name for this Item. This should be used for uniquely identify the instance of the Item.
     * This is the valid replacement for the atrocious 'getUnlocalizedName().substring(6)' stuff that everyone does.
     * Unlocalized names have NOTHING to do with unique identifiers. As demonstrated by vanilla blocks and items.
     *
     * The supplied name will be prefixed with the currently active mod's modId.
     * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
     *
     * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
     *
     * Returns 'this' to allow for chaining.
     *
     * @param name Unique registry name
     * @return This instance
     */
    V setRegistryName(ResourceLocation name);

    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    ResourceLocation getRegistryName();

    // Default implementation, modders who make extra items SHOULD extend this instead of Object.
    // We have to do this until we get default implementations in Java 8.
    @SuppressWarnings({ "serial", "unchecked" })
    public static class Impl<T  extends IForgeRegistryEntry<T>> implements IForgeRegistryEntry<T>
    {
        private TypeToken<T> token = new TypeToken<T>(getClass()){};
        public final RegistryDelegate<T> delegate = PersistentRegistryManager.makeDelegate((T)this, (Class<T>)token.getRawType());
        private ResourceLocation registryName = null;

        public final T setRegistryName(String name)
        {
            if (getRegistryName() != null)
                throw new IllegalStateException("Attempted to set registry name with exisiting registry name! New: " + name + " Old: " + getRegistryName());

            int index = name.lastIndexOf(':');
            String oldPrefix = index == -1 ? "" : name.substring(0, index);
            name = index == -1 ? name : name.substring(index + 1);
            ModContainer mc = Loader.instance().activeModContainer();
            String prefix = mc == null ? "minecraft" : mc.getModId();
            if (!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
            {
                FMLLog.bigWarning("Dangerous alternative prefix %s for name %s, invalid registry invocation/invalid name?", oldPrefix, name);
                prefix = oldPrefix;
            }
            this.registryName = new ResourceLocation(prefix, name);
            return (T)this;
        }

        //Helper functions
        public final T setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }
        public final T setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

        public final ResourceLocation getRegistryName()
        {
            if (delegate.name() != null) return delegate.name();
            return registryName != null ? registryName : null;
        }
    }
}
