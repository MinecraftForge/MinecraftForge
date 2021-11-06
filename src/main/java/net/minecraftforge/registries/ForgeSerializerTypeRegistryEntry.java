package net.minecraftforge.registries;

import com.google.common.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

import javax.annotation.Nullable;

public class ForgeSerializerTypeRegistryEntry<V extends IForgeRegistryEntry<V>, T> extends SerializerType<T> implements IForgeRegistryEntry<V>
{

    public ForgeSerializerTypeRegistryEntry(Serializer<? extends T> p_79330_)
    {
        super(p_79330_);
    }

    @SuppressWarnings("serial")
    private final TypeToken<V> token = new TypeToken<V>(getClass()){};
    public final IRegistryDelegate<V> delegate = new RegistryDelegate<>((V)this, (Class<V>)token.getRawType());
    private ResourceLocation registryName = null;

    public final V setRegistryName(String name)
    {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = checkRegistryName(name);
        return (V)this;
    }

    //Helper functions
    public final V setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }

    public final V setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Nullable
    public final ResourceLocation getRegistryName()
    {
        if (delegate.name() != null) return delegate.name();
        return registryName != null ? registryName : null;
    }

    public final Class<V> getRegistryType() { return (Class<V>)token.getRawType(); }

    /**
     * This will assert that the registry name is valid and warn about potential registry overrides
     * It is important as it detects cases where modders unintentionally register objects with the "minecraft" namespace, leading to dangerous errors later.
     * @param name The registry name
     * @return A verified "correct" registry name
     */
    ResourceLocation checkRegistryName(String name)
    {
        return GameData.checkPrefix(name, true);
    }

}
