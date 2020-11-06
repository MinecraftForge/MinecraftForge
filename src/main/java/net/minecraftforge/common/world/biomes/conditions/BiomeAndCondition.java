package net.minecraftforge.common.world.biomes.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

import java.util.List;

public class BiomeAndCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeAndCondition> CODEC = IBiomeCondition.INNER_CODEC.listOf().xmap(BiomeAndCondition::new, and -> and.conditions).fieldOf("conditions");

    private final List<IBiomeCondition> conditions;

    public BiomeAndCondition(List<IBiomeCondition> conditions)
    {
        this.conditions = conditions;
    }

    @Override
    public BiomeConditionType<BiomeAndCondition> getType()
    {
        return ForgeBiomeModifiers.AND_COMBINED.get();
    }

    @Override
    public boolean test(Biome biome)
    {
        for(IBiomeCondition cond : conditions)
        {
            if(!cond.test(biome))
                return false;
        }
        return true;
    }
}
