/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.RegistryAccessExtension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(RegistryAccessExtensionTest.MODID)
public class RegistryAccessExtensionTest {
    public static final String MODID = "registry_access_extension_test";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(MODID, "worldgen/example");
    public static final RegistryAccessExtension<ExampleComponent> BIOME_FEATURE_PAIR = new RegistryAccessExtension<>(REGISTRY_NAME, ExampleComponent.class, ExampleComponent.CODEC);

    public RegistryAccessExtensionTest() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addGenericListener(RegistryAccessExtension.class, this::registerExtension);

        BIOME_FEATURE_PAIR.getRegister().register(modBus);
        BIOME_FEATURE_PAIR.getRegister().register("builtin_example", builtinSupplier());

        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    private void onServerStart(ServerAboutToStartEvent event) {
        var registries = event.getServer().registryAccess();

        var biomes = registries.registryOrThrow(Registry.BIOME_REGISTRY);
        var features = registries.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        var biomeFeatures = registries.registryOrThrow(BIOME_FEATURE_PAIR.getRegistryKey());

        LOGGER.info("RegistryAccessExtensionTest - ExampleComponent registry:");
        for (var entry : biomeFeatures.entrySet()) {
            var name = entry.getKey().location();
            var pair = entry.getValue();

            // Check that pair holds valid Biome and PlacedFeature references by attempting to
            // retrieve their key from the RegistryAccess instance. Will return null if invalid.
            var biomeName = biomes.getKey(pair.getBiome().get());
            var featureName = features.getKey(pair.getFeature().get());

            LOGGER.info(" - Name: {}, Biome: {}, Feature: {}", name, biomeName, featureName);
        }
    }

    private void registerExtension(RegistryEvent.Register<RegistryAccessExtension<?>> event) {
        event.getRegistry().register(BIOME_FEATURE_PAIR);
    }

    private static Supplier<ExampleComponent> builtinSupplier() {
        return () -> new ExampleComponent(
                () -> ForgeRegistries.BIOMES.getValue(Biomes.TAIGA.location()),
                () -> VegetationPlacements.BAMBOO_VEGETATION
        );
    }

    public static class ExampleComponent extends ForgeRegistryEntry<ExampleComponent> {
        public static final Codec<ExampleComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Biome.CODEC.fieldOf("biome").forGetter(p -> p.biome),
                PlacedFeature.CODEC.fieldOf("feature").forGetter(p -> p.feature)
        ).apply(instance, ExampleComponent::new));

        private final Supplier<Biome> biome;
        private final Supplier<PlacedFeature> feature;

        public ExampleComponent(Supplier<Biome> biome, Supplier<PlacedFeature> feature) {
            this.biome = biome;
            this.feature = feature;
        }

        public Supplier<Biome> getBiome() {
            return biome;
        }

        public Supplier<PlacedFeature> getFeature() {
            return feature;
        }
    }
}
