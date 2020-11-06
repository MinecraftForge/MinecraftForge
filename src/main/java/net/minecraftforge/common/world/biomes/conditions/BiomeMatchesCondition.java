package net.minecraftforge.common.world.biomes.conditions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;

import java.util.List;

public class BiomeMatchesCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeMatchesCondition> CODEC =
            Codec.mapEither(
                    ResourceLocation.CODEC.listOf().fieldOf("biomes"),
                    ResourceLocation.CODEC.fieldOf("biome")
            ).xmap(e ->
            {
                if(e.left().isPresent())
                    return new BiomeMatchesCondition(e.left().get());
                else
                    return new BiomeMatchesCondition(e.right().get());
            }, mb -> mb.locations.size() == 1 ? Either.right(mb.locations.get(0)) : Either.left(mb.locations));

    private final List<ResourceLocation> locations;

    public BiomeMatchesCondition(ResourceLocation location)
    {
        this(ImmutableList.of(location));
    }

    public BiomeMatchesCondition(List<ResourceLocation> locations)
    {
        this.locations = locations;
    }

    @Override
    public BiomeConditionType<BiomeMatchesCondition> getType()
    {
        return ForgeBiomeModifiers.MATCHES_BIOME.get();
    }

    @Override
    public boolean test(Biome biome)
    {
        //TODO test, this might not actually work... -> test ForgeRegistries.BIOMES.getKey(biome)
        return locations.contains(biome.getRegistryName());
    }
}
