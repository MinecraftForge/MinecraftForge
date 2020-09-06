package net.minecraftforge.common.world.biomes.modifiers.base;

import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.BiomeModifications;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public abstract class BiomeModifier
{
    //wants it separate bcs of generics.
    private static final Codec<BiomeModifierType<?>> FROM_REGISTRY = ResourceLocation.field_240908_a_.xmap(ForgeRegistries.BIOME_MODIFIER_TYPES::getValue, ForgeRegistries.BIOME_MODIFIER_TYPES::getKey);
    public static final Codec<BiomeModifier> GENERAL_CODEC = FROM_REGISTRY.dispatch(BiomeModifier::getType, BiomeModifierType::getCodec);

    private final IBiomeCondition condition;

    public BiomeModifier(IBiomeCondition condition)
    {
        this.condition = condition;
    }

    public abstract BiomeModifierType<?> getType();

    public abstract BiomeModifications modifyBiome(final Biome biome);

    @Nullable
    public BiomeModifications getModifications(final Biome biome)
    {
        return condition.test(biome) ? modifyBiome(biome) : null;
    }

    public IBiomeCondition getCondition()
    {
        return condition;
    }
}
