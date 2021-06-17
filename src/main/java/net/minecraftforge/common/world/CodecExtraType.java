package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CodecExtraType<A extends ICodecExtra> extends ForgeRegistryEntry<CodecExtraType<?>>
{
    private final Codec<A> codec;

    public CodecExtraType(MapCodec<A> codec)
    {
        this.codec = codec.codec();
    }

    public Codec<A> getCodec()
    {
        return codec;
    }
}
