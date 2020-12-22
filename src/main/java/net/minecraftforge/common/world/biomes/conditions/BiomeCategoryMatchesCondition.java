package net.minecraftforge.common.world.biomes.conditions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.BiomeExposer;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

import java.util.List;

public class BiomeCategoryMatchesCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeCategoryMatchesCondition> CODEC =
            Codec.mapEither(
                    Biome.Category.CODEC.listOf().fieldOf("categories"),
                    Biome.Category.CODEC.fieldOf("category")
            ).xmap(e ->
            {
                if(e.left().isPresent())
                    return new BiomeCategoryMatchesCondition(e.left().get());
                else
                    return new BiomeCategoryMatchesCondition(e.right().get());
            }, cond -> cond.categories.size() == 1 ? Either.right(cond.categories.get(0)) : Either.left(cond.categories));

    private final List<Biome.Category> categories;

    public BiomeCategoryMatchesCondition(Biome.Category category)
    {
        this(ImmutableList.of(category));
    }

    public BiomeCategoryMatchesCondition(List<Biome.Category> categories)
    {
        this.categories = categories;
    }

    @Override
    public BiomeConditionType<BiomeCategoryMatchesCondition> getType()
    {
        return ForgeBiomeModifiers.MATCHES_CATEGORY.get();
    }

    @Override
    public boolean test(BiomeExposer biome)
    {
        return categories.contains(biome.getCategory());
    }
}
