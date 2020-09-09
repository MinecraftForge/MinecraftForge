package net.minecraftforge.common.world.biomes;

import net.minecraftforge.common.world.biomes.conditions.*;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.NonVoidBiomeCondition;
import net.minecraftforge.common.world.biomes.modifiers.SimpleFeaturesAdditions;
import net.minecraftforge.common.world.biomes.modifiers.SimpleSpawnsAdditions;
import net.minecraftforge.common.world.biomes.modifiers.base.BiomeModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeBiomeModifiers
{
    public static final DeferredRegister<BiomeConditionType<?>> BIOME_CONDITIONS = DeferredRegister.create(ForgeRegistries.BIOME_CONDITION_TYPES, "forge");
    public static final DeferredRegister<BiomeModifierType<?>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.BIOME_MODIFIER_TYPES, "forge");

    public static final RegistryObject<BiomeConditionType<BiomeCategoryMatchesCondition>> MATCHES_CATEGORY = BIOME_CONDITIONS.register("matches_category", () -> new BiomeConditionType<>(BiomeCategoryMatchesCondition.CODEC));
    public static final RegistryObject<BiomeConditionType<BiomeMatchesCondition>> MATCHES_BIOME = BIOME_CONDITIONS.register("matches_biome", () -> new BiomeConditionType<>(BiomeMatchesCondition.CODEC));
    public static final RegistryObject<BiomeConditionType<BiomeTagMatchesCondition>> MATCHES_TAG = BIOME_CONDITIONS.register("matches_tag", () -> new BiomeConditionType<>(BiomeTagMatchesCondition.CODEC));
    public static final RegistryObject<BiomeConditionType<BiomeInvertedCondition>> INVERTED = BIOME_CONDITIONS.register("inverted", () -> new BiomeConditionType<>(BiomeInvertedCondition.CODEC));
    public static final RegistryObject<BiomeConditionType<BiomeAndCondition>> AND_COMBINED = BIOME_CONDITIONS.register("and_combined", () -> new BiomeConditionType<>(BiomeAndCondition.CODEC));
    public static final RegistryObject<BiomeConditionType<BiomeOrCondition>> OR_COMBINED = BIOME_CONDITIONS.register("or_combined", () -> new BiomeConditionType<>(BiomeOrCondition.CODEC));
    public static final RegistryObject<BiomeConditionType<NonVoidBiomeCondition>> NON_VOID = BIOME_CONDITIONS.register("non_void", () -> new BiomeConditionType<>(NonVoidBiomeCondition.CODEC));

    public static final RegistryObject<BiomeModifierType<SimpleFeaturesAdditions>> SIMPLE_FEATURES = BIOME_MODIFIERS.register("features_additions", () -> new BiomeModifierType<>(SimpleFeaturesAdditions.CODEC));
    public static final RegistryObject<BiomeModifierType<SimpleSpawnsAdditions>> SIMPLE_SPAWNS = BIOME_MODIFIERS.register("spawns_additions", () -> new BiomeModifierType<>(SimpleSpawnsAdditions.CODEC));

    public static void registerAll(IEventBus modBus)
    {
        BIOME_CONDITIONS.register(modBus);
        BIOME_MODIFIERS.register(modBus);
    }
}