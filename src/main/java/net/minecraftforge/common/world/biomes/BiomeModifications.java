package net.minecraftforge.common.world.biomes;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Collection of operators to perform on a biome's objects.
 * These will chain with other modifiers, so if compatibility is wanted, the operator must be implemented with it in mind.
 *
 * There is no guarantee to the mutability of the objects given (i.e. a List), so it is always necessary to copy the
 * object thoroughly. Nested objects aren't guaranteed mutability as well (i.e. a {@code List<List<>>})
 */
public class BiomeModifications
{
    //Biome.Climate
    private UnaryOperator<Float> temperature = UnaryOperator.identity();
    private UnaryOperator<Float> downfall = UnaryOperator.identity();
    private UnaryOperator<Biome.RainType> precipitation = UnaryOperator.identity();
    private UnaryOperator<Biome.TemperatureModifier> temperatureModifier = UnaryOperator.identity();

    private UnaryOperator<Biome.Category> category = UnaryOperator.identity();
    private UnaryOperator<Float> depth = UnaryOperator.identity();
    private UnaryOperator<Float> scale = UnaryOperator.identity();
    private UnaryOperator<BiomeAmbience> effects = UnaryOperator.identity();

    //BiomeGenerationSettings
    private UnaryOperator<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = UnaryOperator.identity();
    private UnaryOperator<Map<GenerationStage.Carving, List<Supplier<ConfiguredCarver<?>>>>> carvers = UnaryOperator.identity();
    private UnaryOperator<List<List<Supplier<ConfiguredFeature<?, ?>>>>> features = UnaryOperator.identity();
    private UnaryOperator<List<Supplier<StructureFeature<?, ?>>>> structures = UnaryOperator.identity();

    //MobSpawnInfo
    private UnaryOperator<Float> creatureSpawnProbability = UnaryOperator.identity();
    private UnaryOperator<Map<EntityClassification, List<MobSpawnInfo.Spawners>>> spawners = UnaryOperator.identity();
    private UnaryOperator<Map<EntityType<?>, MobSpawnInfo.SpawnCosts>> spawnCosts = UnaryOperator.identity();
    private UnaryOperator<Boolean> playerSpawnFriendly = UnaryOperator.identity();

    public BiomeModifications modifyTemperature(UnaryOperator<Float> temperature)
    {
        this.temperature = temperature;
        return this;
    }

    public BiomeModifications modifyDownfall(UnaryOperator<Float> downfall)
    {
        this.downfall = downfall;
        return this;
    }

    public BiomeModifications modifyRainType(UnaryOperator<Biome.RainType> precipitation)
    {
        this.precipitation = precipitation;
        return this;
    }

    public BiomeModifications modifyTemperatureModifier(UnaryOperator<Biome.TemperatureModifier> temperatureModifier)
    {
        this.temperatureModifier = temperatureModifier;
        return this;
    }

    public BiomeModifications modifyCategory(UnaryOperator<Biome.Category> category)
    {
        this.category = category;
        return this;
    }

    public BiomeModifications modifyDepth(UnaryOperator<Float> depth)
    {
        this.depth = depth;
        return this;
    }

    public BiomeModifications modifyScale(UnaryOperator<Float> scale)
    {
        this.scale = scale;
        return this;
    }

    public BiomeModifications modifyBiomeAmbience(UnaryOperator<BiomeAmbience> effects)
    {
        this.effects = effects;
        return this;
    }

    public BiomeModifications modifySurfaceBuilder(UnaryOperator<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder)
    {
        this.surfaceBuilder = surfaceBuilder;
        return this;
    }

    public BiomeModifications modifyCarvers(UnaryOperator<Map<GenerationStage.Carving, List<Supplier<ConfiguredCarver<?>>>>> carvers)
    {
        this.carvers = carvers;
        return this;
    }

    public BiomeModifications modifyFeatures(UnaryOperator<List<List<Supplier<ConfiguredFeature<?, ?>>>>> features)
    {
        this.features = features;
        return this;
    }

    public BiomeModifications modifyStructures(UnaryOperator<List<Supplier<StructureFeature<?, ?>>>> structures)
    {
        this.structures = structures;
        return this;
    }

    public BiomeModifications modifyCreatureSpawnProbability(UnaryOperator<Float> creatureSpawnProbability)
    {
        this.creatureSpawnProbability = creatureSpawnProbability;
        return this;
    }

    public BiomeModifications modifySpawners(UnaryOperator<Map<EntityClassification, List<MobSpawnInfo.Spawners>>> spawners)
    {
        this.spawners = spawners;
        return this;
    }

    public BiomeModifications modifySpawnCosts(UnaryOperator<Map<EntityType<?>, MobSpawnInfo.SpawnCosts>> spawnCosts)
    {
        this.spawnCosts = spawnCosts;
        return this;
    }

    public BiomeModifications modifyPlayerSpawnFriendly(UnaryOperator<Boolean> playerSpawnFriendly)
    {
        this.playerSpawnFriendly = playerSpawnFriendly;
        return this;
    }

    static Biome performModifications(List<BiomeModifications> modifications, Biome base)
    {
        return modifications.isEmpty() ? base :
                new Biome.Builder()
                        .temperature(fromlist(modifications, mods -> mods.temperature, base.func_242445_k()))
                        .downfall(fromlist(modifications, mods -> mods.downfall, base.getDownfall()))
                        .precipitation(fromlist(modifications, mods -> mods.precipitation, base.getPrecipitation()))
                        .func_242456_a(fromlist(modifications, mods -> mods.temperatureModifier, base.getTemperatureModifier()))
                        .category(fromlist(modifications, mods -> mods.category, base.getCategory()))
                        .depth(fromlist(modifications, mods -> mods.depth, base.getDepth()))
                        .scale(fromlist(modifications, mods -> mods.scale, base.getScale()))
                        .func_235097_a_(fromlist(modifications, mods -> mods.effects, base.func_235089_q_()))
                        .func_242457_a(new BiomeGenerationSettings(
                                fromlist(modifications, mods -> mods.surfaceBuilder, base.func_242440_e().func_242500_d()),
                                fromlist(modifications, mods -> mods.carvers, base.func_242440_e().getAllCarvers()),
                                fromlist(modifications, mods -> mods.features, base.func_242440_e().func_242498_c()),
                                fromlist(modifications, mods -> mods.structures, (List<Supplier<StructureFeature<?,?>>>) base.func_242440_e().func_242487_a())
                        ))
                        .func_242458_a(new MobSpawnInfo(
                                fromlist(modifications, mods -> mods.creatureSpawnProbability, base.func_242433_b().func_242557_a()),
                                fromlist(modifications, mods -> mods.spawners, base.func_242433_b().getAllSpawners()),
                                fromlist(modifications, mods -> mods.spawnCosts, base.func_242433_b().getAllSpawnCosts()),
                                fromlist(modifications, mods -> mods.playerSpawnFriendly, base.func_242433_b().func_242562_b())
                        ))
                        .func_242455_a();
    }

    /**
     * Applies the modifications from first to last.
     * This means that the LAST modification in the list has the MOST priority/power.
     * (a modification that clears a list will be most effective at the end then at the start).
     */
    private static <E> E fromlist(List<BiomeModifications> modifications, Function<BiomeModifications, UnaryOperator<E>> getter, E base)
    {
        E ret = base;
        for(BiomeModifications mod : modifications)
            ret = getter.apply(mod).apply(ret);
        return ret;
    }
}
