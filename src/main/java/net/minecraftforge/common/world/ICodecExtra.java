package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public interface ICodecExtra
{
    Codec<CodecExtraType<?>> FROM_REGISTRY = ResourceLocation.field_240908_a_.xmap(ForgeRegistries.CODEC_EXTRA_TYPES::getValue, ForgeRegistryEntry::getRegistryName);
    Codec<ICodecExtra> GENERIC_CODEC = FROM_REGISTRY.dispatch(ICodecExtra::getType, CodecExtraType::getCodec);
    Codec<List<ICodecExtra>> LIST_EXTRAS = GENERIC_CODEC.listOf().fieldOf("forge_extras").codec();

    CodecExtraType<?> getType();
}
