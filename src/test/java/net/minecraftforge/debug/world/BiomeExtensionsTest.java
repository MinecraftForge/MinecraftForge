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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.BiomeExtensionType;
import net.minecraftforge.common.world.IBiomeExtension;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Plains has an extension added via BiomeLoadingEvent, and will rain every 3rd x coordinate
 * Forest has an extension added via data packs, and will rain every 6th x coordinate
 */
@Mod(BiomeExtensionsTest.MODID)
@Mod.EventBusSubscriber
public class BiomeExtensionsTest {
    static final String MODID = "biome_extensions_test";
    private static final boolean ENABLED = true;

    private static final DeferredRegister<BiomeExtensionType> BIOME_EXTENSION_TYPES = DeferredRegister.create(ForgeRegistries.BIOME_EXTENSION_TYPES, MODID);
    private static final RegistryObject<BiomeExtensionType> TEST_EXTENSION = BIOME_EXTENSION_TYPES.register("raining_in_rows", () -> new BiomeExtensionType(TestBiomeExtension.CODEC));

    public BiomeExtensionsTest() {
        if (ENABLED) {
            BIOME_EXTENSION_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    @SubscribeEvent
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if ("minecraft:plains".equals(event.getName().toString())) {
            event.getExtensions().add(new TestBiomeExtension(3));
        }
    }

    static class TestBiomeExtension implements IBiomeExtension {

        static final Codec<TestBiomeExtension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("rows").forGetter(c -> c.rows)
        ).apply(instance, TestBiomeExtension::new));

        private final int rows;

        TestBiomeExtension(int rows) {
            this.rows = rows;
        }

        @Override
        public BiomeExtensionType getType() {
            return TEST_EXTENSION.get();
        }

        @Override
        public boolean modifiesPrecipitation() {
            return true;
        }

        @Override
        public Biome.RainType getPrecipitation(Biome biome, IWorldReader world, BlockPos pos) {
            return pos.getX() % rows == 0 ? Biome.RainType.RAIN : Biome.RainType.NONE;
        }
    }
}
