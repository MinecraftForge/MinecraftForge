/*
 * Minecraft Forge
 * Copyright (c) 2020.
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

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BiomeIdOrderTest.MOD_ID)
public class BiomeIdOrderTest {
    public static final boolean ENABLED = true;
    public static final boolean REVERSED = true;
    static final String MOD_ID = "biome_id_order_test";
    private static final Logger LOGGER = LogManager.getLogger();
    public BiomeIdOrderTest() {
        if (ENABLED) FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        LOGGER.info("Registering Biomes!");
        if (REVERSED) {
            event.getRegistry().registerAll(BiomeMaker.func_244252_r().setRegistryName(MOD_ID, "first_biome"), BiomeMaker.func_244252_r().setRegistryName(MOD_ID, "second_biome"));
        } else {
            event.getRegistry().registerAll(BiomeMaker.func_244252_r().setRegistryName(MOD_ID, "second_biome"), BiomeMaker.func_244252_r().setRegistryName(MOD_ID, "first_biome"));
        }
    }
}
