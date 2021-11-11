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

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("biome_temperature_modifier_test")
public class BiomeTemperatureModifierTest 
{
    private static final boolean ENABLED = false;
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, "biome_temperature_modifier_test");
    public static final ResourceKey<Biome> TEST_BIOME_KEY = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("biome_temperature_modifier_test", "test_biome"));
    public static final RegistryObject<Biome> TEST_BIOME = BIOMES.register("test_biome", BiomeTemperatureModifierTest::makeTestBiome);

    public BiomeTemperatureModifierTest() 
    {
        BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) 
    {
        if (!ENABLED) return;
        event.enqueueWork(() -> {
            BiomeDictionary.addTypes(TEST_BIOME_KEY, BiomeDictionary.Type.MODIFIED);
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeEntry(TEST_BIOME_KEY, 100));
        });
    }

    private static Biome makeTestBiome() 
    {
        Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.create("TEST_MODIFIER", "test_modifier", (pos, noise) -> 2.0F);
        return new Biome.BiomeBuilder().biomeCategory(BiomeCategory.NONE).depth(0.1F).scale(0.1F).downfall(0.0F).generationSettings(new BiomeGenerationSettings.Builder().surfaceBuilder(SurfaceBuilders.GRASS).build())
        .mobSpawnSettings(MobSpawnSettings.EMPTY).specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(128)
        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build()).precipitation(Precipitation.NONE).temperature(2.0F).temperatureAdjustment(temperatureModifier).build();
    }
}