/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.world;

import net.minecraft.world.level.biome.Biome;
//import net.minecraft.data.worldgen.biome.VanillaBiomes;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

@Mod("worldgen_registry_desync_test")
@Mod.EventBusSubscriber
public class WorldgenRegistryDesyncTest {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(singularityRegistries.FEATURES, "worldgen_registry_desync_test");
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> dungeon = FEATURES.register("dungeon", () -> new MonsterRoomFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(singularityRegistries.BIOMES, "worldgen_registry_desync_test");
    //TODO: public static final RegistryObject<Biome> biome = BIOMES.register("biome", VanillaBiomes::theVoidBiome);

    public WorldgenRegistryDesyncTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FEATURES.register(modEventBus);
        BIOMES.register(modEventBus);
    }
}
