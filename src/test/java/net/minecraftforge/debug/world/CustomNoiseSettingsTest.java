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

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.minecraftforge.common.world.CopyNoiseGeneratorSettings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomNoiseSettingsTest.MODID)
public class CustomNoiseSettingsTest
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "custom_noise_settings_test";
    
    public CustomNoiseSettingsTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onGatherData);
    }
    
    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        generator.addProvider(new IDataProvider() {

            @Override
            public void run(DirectoryCache cache) throws IOException
            {
                Path path = generator.getOutputFolder().resolve(String.join("/", ResourcePackType.SERVER_DATA.getDirectory(), MODID, "worldgen/noise_settings", "copy_test" + ".json"));
                CopyNoiseGeneratorSettings.DATAGEN_CODEC.encodeStart(JsonOps.INSTANCE, DimensionSettings.OVERWORLD)
                    .resultOrPartial(parseFailure -> LOGGER.error(parseFailure))
                    .ifPresent(json -> {
                        try
                        {
                            IDataProvider.save(gson, cache, json, path);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    });
            }

            @Override
            public String getName()
            {
                return "custom_noise_settings_test data provider";
            }
            
        });
    }
}
