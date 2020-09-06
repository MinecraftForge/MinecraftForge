package net.minecraftforge.common.world.biomes.modifiers.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.mojang.serialization.codecs.PairCodec;
import com.mojang.serialization.codecs.PairMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * BiomeModifier Codec holder
 */
public class BiomeModifierType<A extends BiomeModifier> extends ForgeRegistryEntry<BiomeModifierType<?>>
{
    private final Codec<A> codec;

    /**
     * Requires a {@link MapCodec}, so that the {@link Codec#dispatch} can function properly, as the {@link KeyDispatchCodec} does
     * an instance check on {@link MapCodec.MapCodecCodec}.
     *
     * There is always a way to create a MapCodec implementation that is equivalent to the Codec implementation:
     *
     * {@link RecordCodecBuilder#mapCodec} vs {@link RecordCodecBuilder#create}
     * or {@link PairMapCodec} vs {@link PairCodec}
     *
     * The dispatch is done through {@link BiomeModifier#GENERAL_CODEC}
     */
    public BiomeModifierType(MapCodec<A> codec)
    {
        this.codec = codec.codec();
    }

    public Codec<A> getCodec()
    {
        return codec;
    }
}
