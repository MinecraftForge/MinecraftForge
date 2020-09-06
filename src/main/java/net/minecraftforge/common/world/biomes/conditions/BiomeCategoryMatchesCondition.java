package net.minecraftforge.common.world.biomes.conditions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

import java.util.List;

public class BiomeCategoryMatchesCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeCategoryMatchesCondition> CODEC =
            Codec.mapEither(
                    Codec.STRING.listOf().fieldOf("categories"),
                    Codec.STRING.fieldOf("category")
            ).xmap(e ->
            {
                if(e.left().isPresent())
                    return new BiomeCategoryMatchesCondition(e.left().get());
                else
                    return new BiomeCategoryMatchesCondition(e.right().get());
            }, cond -> cond.categories.size() == 1 ? Either.right(cond.categories.get(0)) : Either.left(cond.categories));

    private final List<String> categories;

    public BiomeCategoryMatchesCondition(String category)
    {
        this(ImmutableList.of(category));
    }

    public BiomeCategoryMatchesCondition(List<String> categories)
    {
        this.categories = categories;
    }

    @Override
    public BiomeConditionType<BiomeCategoryMatchesCondition> getType()
    {
        return ForgeBiomeModifiers.MATCHES_CATEGORY.get();
    }

    @Override
    public boolean test(Biome biome)
    {
        return categories.contains(biome.getCategory().getName());
    }
}
