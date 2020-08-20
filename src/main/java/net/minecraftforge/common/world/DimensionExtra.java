package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DimensionExtra
{
    public static final Codec<DimensionExtra> CODEC = ForgeRegistries.DIMENSION_EXTRA_CODEC
            .dispatch(DimensionExtra::getCodec, DimensionExtraCodec::codec);

    /**
     * Return this object's "(de)serializer".
     */
    public abstract DimensionExtraCodec<?> getCodec();

    /**
     * Errors if the list contains 2 objects with the same "type" key.
     */
    public static DataResult<List<DimensionExtra>> ensureUnique(List<DimensionExtra> extras)
    {
        Set<ResourceLocation> test = new HashSet<>();
        for(DimensionExtra extra : extras)
        {
            if(!test.add(extra.getCodec().getRegistryName()))
                return DataResult.error("Duplicate founds in forge extras!", extras);
        }

        return DataResult.success(extras);
    }
}
