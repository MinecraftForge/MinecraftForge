package net.minecraftforge.common.world.biomes.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

import java.util.List;

public class BiomeOrCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeOrCondition> CODEC = IBiomeCondition.INNER_CODEC.listOf().xmap(BiomeOrCondition::new, or -> or.conditions).fieldOf("conditions");

    private final List<IBiomeCondition> conditions;

    public BiomeOrCondition(List<IBiomeCondition> conditions)
    {
        this.conditions = conditions;
    }

    @Override
    public BiomeConditionType<BiomeOrCondition> getType()
    {
        return ForgeBiomeModifiers.OR_COMBINED.get();
    }

    @Override
    public boolean test(Biome biome)
    {
        for(IBiomeCondition cond : conditions)
        {
            if(cond.test(biome))
                return true;
        }
        return false;
    }
}
