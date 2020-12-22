package net.minecraftforge.common.world.biomes.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.world.biomes.BiomeExposer;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

public class NonVoidBiomeCondition implements IBiomeCondition
{
    public static final NonVoidBiomeCondition INSTANCE = new NonVoidBiomeCondition();

    public static final MapCodec<NonVoidBiomeCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public BiomeConditionType<?> getType()
    {
        return ForgeBiomeModifiers.NON_VOID.get();
    }

    @Override
    public boolean test(BiomeExposer biome)
    {
        return !biome.getName().equals(Biomes.THE_VOID.getLocation());
    }
}
