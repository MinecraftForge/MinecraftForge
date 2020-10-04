package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ICodecExtra
{
    Codec<CodecExtraType<?>> FROM_REGISTRY = ResourceLocation.field_240908_a_.xmap(ForgeRegistries.CODEC_EXTRA_TYPES::getValue, ForgeRegistries.CODEC_EXTRA_TYPES::getKey);
    Codec<ICodecExtra> GENERIC_CODEC = FROM_REGISTRY.dispatch(ICodecExtra::getType, CodecExtraType::getCodec);
    Codec<List<ICodecExtra>> LIST_CODEC = GENERIC_CODEC.listOf().fieldOf("forge_extras").codec().flatXmap(ICodecExtra::ensureUnique, ICodecExtra::ensureUnique);

    static DataResult<List<ICodecExtra>> ensureUnique(List<ICodecExtra> extras)
    {
        Set<ResourceLocation> toTest = new HashSet<>();
        for(ICodecExtra extra : extras)
        {
            if(!toTest.add(extra.getType().getRegistryName()))
                return DataResult.error("Duplicate codec extra entries were found: " + extra.getType().getRegistryName(), extras);
        }

        return DataResult.success(extras);
    }

    CodecExtraType<?> getType();
}
