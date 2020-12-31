package net.minecraftforge.common.world.biomes.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraftforge.common.world.biomes.BiomeExposer;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

public class BiomeInvertedCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeInvertedCondition> CODEC = IBiomeCondition.GENERAL_CODEC.xmap(BiomeInvertedCondition::new, inv -> inv.condition).fieldOf("inverse");

    final IBiomeCondition condition;

    public BiomeInvertedCondition(IBiomeCondition condition)
    {
        this.condition = condition;
    }

    @Override
    public BiomeConditionType<BiomeInvertedCondition> getType()
    {
        return ForgeBiomeModifiers.INVERTED.get();
    }

    @Override
    public boolean test(BiomeExposer biome)
    {
        return !condition.test(biome);
    }
}
