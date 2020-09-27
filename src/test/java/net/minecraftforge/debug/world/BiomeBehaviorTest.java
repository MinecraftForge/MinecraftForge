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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.common.world.IBiomeBehavior;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLanguageProvider;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * To test: create a buffet world with plains as the only biome. Set weather to true via command. Observe the rain falling in rows aligned with the the z axis
 */
@Mod(BiomeBehaviorTest.MODID)
public class BiomeBehaviorTest {
    static final String MODID = "biome_behavior_test";
    private static final boolean ENABLED = true;

    private static final DeferredRegister<IBiomeBehavior> BEHAVIORS = DeferredRegister.create(ForgeRegistries.BIOME_BEHAVIORS, "minecraft");
    private static final RegistryObject<IBiomeBehavior> FIZZ_BUZZ_WEATHER = BEHAVIORS.register("plains", FizzBuzzBehavior::new);

    public BiomeBehaviorTest() {
        if (ENABLED) {
            BEHAVIORS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    static class FizzBuzzBehavior extends ForgeRegistryEntry<IBiomeBehavior> implements IBiomeBehavior {
        @Override
        public Biome.RainType getPrecipitation(Biome biome, IWorldReader world, BlockPos pos) {
            return pos.getX() % 2 == 0 ? Biome.RainType.NONE : Biome.RainType.RAIN;
        }
    }
}
