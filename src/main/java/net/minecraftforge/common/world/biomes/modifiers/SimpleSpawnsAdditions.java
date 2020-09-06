package net.minecraftforge.common.world.biomes.modifiers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.common.world.biomes.BiomeModifications;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifier;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifierType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSpawnsAdditions extends BiomeModifier
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Codec<Map<EntityClassification, List<MobSpawnInfo.Spawners>>> SPAWNERS_CODEC =
            Codec.simpleMap(
                    EntityClassification.field_233667_g_,
                    MobSpawnInfo.Spawners.field_242587_b.listOf().promotePartial(Util.func_240982_a_("Spawn data: ", LOGGER::error)),
                    IStringSerializable.func_233025_a_(EntityClassification.values())
            ).codec();

    public static final Codec<Map<EntityType<?>, MobSpawnInfo.SpawnCosts>> SPAWN_COSTS_CODEC =
            Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnInfo.SpawnCosts.field_242579_a, Registry.ENTITY_TYPE).codec();

    public static final MapCodec<SimpleSpawnsAdditions> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    IBiomeCondition.FIELD_CODEC.forGetter(BiomeModifier::getCondition),
                    SPAWNERS_CODEC.optionalFieldOf("spawners", ImmutableMap.of()).forGetter(m -> m.spawners),
                    SPAWN_COSTS_CODEC.optionalFieldOf("spawns_costs", ImmutableMap.of()).forGetter(m -> m.spawnCosts)
            ).apply(inst, SimpleSpawnsAdditions::new)
    );

    private final Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners;
    private final Map<EntityType<?>, MobSpawnInfo.SpawnCosts> spawnCosts;

    public SimpleSpawnsAdditions(IBiomeCondition condition, Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners, Map<EntityType<?>, MobSpawnInfo.SpawnCosts> spawnCosts)
    {
        super(condition);
        this.spawners = spawners;
        this.spawnCosts = spawnCosts;
    }

    @Override
    public BiomeModifications modifyBiome(Biome biome)
    {
        return new BiomeModifications()
                .modifySpawners(sp ->
                {
                    Map<EntityClassification, List<MobSpawnInfo.Spawners>> mutable = new HashMap<>(sp.entrySet().stream().map(e -> Pair.of(e.getKey(), new ArrayList<>(e.getValue()))).collect(Pair.toMap()));
                    for(EntityClassification cls : this.spawners.keySet())
                        mutable.computeIfAbsent(cls, k -> new ArrayList<>()).addAll(this.spawners.get(cls));
                    return mutable;
                })
                .modifySpawnCosts(sc ->
                {
                    Map<EntityType<?>, MobSpawnInfo.SpawnCosts> mutable = new HashMap<>(sc);
                    mutable.putAll(this.spawnCosts);
                    return mutable;
                });
    }

    @Override
    public BiomeModifierType<?> getType()
    {
        return ForgeBiomeModifiers.SIMPLE_SPAWNS.get();
    }
}
