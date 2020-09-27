/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.dimension.BiomeProviders;
import net.minecraftforge.dimension.DynamicDimensionManager;
import net.minecraftforge.event.dimension.DimensionRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@SuppressWarnings("unused")
@Mod(DimensionTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.FORGE)
public class DimensionTest {
    static final String MODID = "dimension_test";
    private static final Logger LOGGER = LogManager.getLogger();
    
    private boolean REGISTER_ON_EVENT = false;
    
    public DimensionTest() {
		MinecraftForge.EVENT_BUS.register(this);
	}
    
    @SubscribeEvent
    public void onDimensionRegister(DimensionRegisterEvent e) {
    	if(REGISTER_ON_EVENT) {
    		System.out.println("Test");
        	
        	long seed = 0;
        	
        	//Registers the dimension if it not exists already in the registry.
        	e.getDimensionManager().registerDimension(new ResourceLocation("dimension_test", "testworld"), 
        			new Dimension(() -> e.getDimensionManager().getDimensionType(new ResourceLocation("minecraft", "overworld")), 
        					new NoiseChunkGenerator(BiomeProviders.createOverworldBiomeProvider(seed, false, false), seed, () -> {
        				         return e.getDimensionManager().getDimensionSettings(new ResourceLocation("dimension_test", "testworld"));
        				      })));
    	}
    }
    
    @SubscribeEvent
    public void onServerStart(FMLServerStartingEvent e) {
    	if(!REGISTER_ON_EVENT) {
    		long seed = 0;
    		
    		DynamicDimensionManager.getDimensionManager().registerDimension(new ResourceLocation("dimension_test", "testworld"), 
        			new Dimension(() -> DynamicDimensionManager.getDimensionManager().getDimensionType(new ResourceLocation("minecraft", "overworld")), 
        					new NoiseChunkGenerator(BiomeProviders.createOverworldBiomeProvider(seed, false, false), seed, () -> {
        				         return DynamicDimensionManager.getDimensionManager().getDimensionSettings(new ResourceLocation("dimension_test", "testworld"));
        				      })));
    		
    		DynamicDimensionManager.getDimensionManager().loadOrCreateDimension(e.getServer(), new ResourceLocation("dimension_test", "testworld"));
    	}
    	
    	LOGGER.info("Dimension: " + DynamicDimensionManager.getDimensionManager().getServerWorld(e.getServer(), new ResourceLocation("dimension_test", "testworld")));
    }
    
}
