package net.minecraftforge.common.world.biomes.modifiers;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.world.biomes.BiomeExposer;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifier;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifierType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class SimpleSpawnsAdditions extends BiomeModifier
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Codec<Map<EntityClassification, List<MobSpawnInfo.Spawners>>> SPAWNERS_CODEC =
            Codec.simpleMap(
                    EntityClassification.CODEC,
                    MobSpawnInfo.Spawners.CODEC.listOf().promotePartial(Util.func_240982_a_("Spawn data: ", LOGGER::error)),
                    IStringSerializable.createKeyable(EntityClassification.values())
            ).codec();

    public static final Codec<Map<EntityType<?>, MobSpawnInfo.SpawnCosts>> SPAWN_COSTS_CODEC =
            Codec.simpleMap(Registry.ENTITY_TYPE, MobSpawnInfo.SpawnCosts.CODEC, Registry.ENTITY_TYPE).codec();

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
    public void modifyBiome(BiomeExposer exposer)
    {
        for(Map.Entry<EntityClassification, List<MobSpawnInfo.Spawners>> e : spawners.entrySet()) {
            for (MobSpawnInfo.Spawners spawn : e.getValue())
                exposer.getSpawns().withSpawner(e.getKey(), spawn);
        }

        for(Map.Entry<EntityType<?>, MobSpawnInfo.SpawnCosts> e : spawnCosts.entrySet())
            exposer.getSpawns().withSpawnCost(e.getKey(), e.getValue().getEntitySpawnCost(), e.getValue().getMaxSpawnCost());
    }

    @Override
    public BiomeModifierType<?> getType()
    {
        return ForgeBiomeModifiers.SIMPLE_SPAWNS.get();
    }
}
