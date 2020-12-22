package net.minecraftforge.common.world.biomes.conditions.base;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.BiomeExposer;
import net.minecraftforge.common.world.biomes.conditions.BiomeAndCondition;
import net.minecraftforge.common.world.biomes.conditions.BiomeInvertedCondition;
import net.minecraftforge.common.world.biomes.conditions.BiomeOrCondition;
import net.minecraftforge.common.world.biomes.modifiers.SimpleFeaturesAdditions;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

public interface IBiomeCondition extends Predicate<BiomeExposer>
{
    Codec<BiomeConditionType<?>> FROM_REGISTRY = ResourceLocation.CODEC.xmap(ForgeRegistries.BIOME_CONDITION_TYPES::getValue, ForgeRegistries.BIOME_CONDITION_TYPES::getKey);

    Codec<IBiomeCondition> GENERAL_CODEC = FROM_REGISTRY.dispatch(IBiomeCondition::getType, BiomeConditionType::getCodec);

    BiomeConditionType<?> getType();

    default BiomeInvertedCondition inverse()
    {
        return new BiomeInvertedCondition(this);
    }

    default BiomeAndCondition and(IBiomeCondition... conditions)
    {
        return new BiomeAndCondition(Lists.asList(this, conditions));
    }

    default BiomeOrCondition or(IBiomeCondition... conditions)
    {
        return new BiomeOrCondition(Lists.asList(this, conditions));
    }
}
