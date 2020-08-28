/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NetherBiomesTest.MODID)
public class NetherBiomesTest
{
    public static final String MODID = "nether_biomes_test";

    private static final boolean ENABLED = false;
    private static Logger logger;

    public NetherBiomesTest()
    {
        logger = LogManager.getLogger();

        if (ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(NetherBiomesTest::onSetup);
        }
    }

    public static void onSetup(FMLCommonSetupEvent event)
    {
        logger.info("Adding Nether Biome");
        BiomeManager.addNetherBiome(Biomes.PLAINS, new Biome.Attributes(0.0f, 0.2f, 0.2f, 0.2f, 0.0f));
    }
}
