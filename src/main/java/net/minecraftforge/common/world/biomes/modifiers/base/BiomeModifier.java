package net.minecraftforge.common.world.biomes.modifiers.base;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.world.biomes.BiomeExposer;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class BiomeModifier
{
    private static final Codec<BiomeModifierType<?>> FROM_REGISTRY = ResourceLocation.CODEC.xmap(ForgeRegistries.BIOME_MODIFIER_TYPES::getValue, ForgeRegistries.BIOME_MODIFIER_TYPES::getKey);
    public static final Codec<BiomeModifier> GENERAL_CODEC = FROM_REGISTRY.dispatch(BiomeModifier::getType, BiomeModifierType::getCodec);
    public static final Codec<Pair<IBiomeCondition, BiomeModifier>> CONDITION_MODIFIER_PAIR_CODEC =
            Codec.mapPair(IBiomeCondition.GENERAL_CODEC.fieldOf("condition"), GENERAL_CODEC.fieldOf("modifier")).codec();

    public abstract BiomeModifierType<?> getType();

    public abstract void modifyBiome(final BiomeExposer biome);
}
