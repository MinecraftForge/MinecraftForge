package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class DimensionExtraCodec<A extends DimensionExtra> extends ForgeRegistryEntry<DimensionExtraCodec<?>>
{
    /**
     * @return the codec of the DimensionExtra associated with this object.
     */
    public abstract Codec<A> codec();
}
