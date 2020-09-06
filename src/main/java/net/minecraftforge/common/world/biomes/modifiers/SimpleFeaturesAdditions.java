package net.minecraftforge.common.world.biomes.modifiers;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.common.world.biomes.BiomeModifications;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifier;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifierType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SimpleFeaturesAdditions extends BiomeModifier
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Codec<List<List<Supplier<ConfiguredFeature<?, ?>>>>> FEATURES_CODEC =
            ConfiguredFeature.field_242764_c.promotePartial(Util.func_240982_a_("Feature: ", LOGGER::error)).listOf();

    private static final Codec<List<Supplier<StructureFeature<?, ?>>>> STRUCTURES_CODEC =
            StructureFeature.field_242770_c.promotePartial(Util.func_240982_a_("Structure start: ", LOGGER::error));

    public static final MapCodec<SimpleFeaturesAdditions> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    IBiomeCondition.FIELD_CODEC.forGetter(BiomeModifier::getCondition),
                    FEATURES_CODEC.optionalFieldOf("features", ImmutableList.of()).forGetter(m -> m.features),
                    STRUCTURES_CODEC.optionalFieldOf("starts", ImmutableList.of()).forGetter(m -> m.structures)
            ).apply(inst, SimpleFeaturesAdditions::new)
    );

    private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
    private final List<Supplier<StructureFeature<?, ?>>> structures;

    public SimpleFeaturesAdditions(IBiomeCondition condition, List<List<Supplier<ConfiguredFeature<?, ?>>>> features, List<Supplier<StructureFeature<?, ?>>> structures)
    {
        super(condition);
        this.features = features;
        this.structures = structures;
    }

    @Override
    public BiomeModifications modifyBiome(Biome biome)
    {
        return new BiomeModifications()
                .modifyFeatures(fts -> {
                    List<List<Supplier<ConfiguredFeature<?, ?>>>> mutable = new ArrayList<>(fts).stream().map(ArrayList::new).collect(Collectors.toList());
                    int size = mutable.size();
                    int sizeNeeded = this.features.size();
                    if(sizeNeeded > size) //Fill any missing lists
                        for(int i = size; i < sizeNeeded; i++)
                            mutable.add(new ArrayList<>());

                    for(int i = 0; i < sizeNeeded; i++)
                        mutable.get(i).addAll(this.features.get(i));
                    return mutable;
                })
                .modifyStructures(strs -> {
                    List<Supplier<StructureFeature<?, ?>>> mutable = new ArrayList<>(strs);
                    mutable.addAll(this.structures);
                    return mutable;
                });
    }

    @Override
    public BiomeModifierType<?> getType()
    {
        return ForgeBiomeModifiers.SIMPLE_FEATURES.get();
    }
}
