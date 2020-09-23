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

package net.minecraftforge.debug.client.gui;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.ErrorScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(GeneratorTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorTypeTest {

    public static final String MODID = "generator_type_test";

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            new TestGeneratorType("test_screen", TestEditScreen::new);
            new TestGeneratorType("test_no_screen", null);
        });
    }

    private static class TestGeneratorType extends BiomeGeneratorTypeScreens {

        private final BiomeGeneratorTypeScreens.IFactory factory;

        private TestGeneratorType(String name, BiomeGeneratorTypeScreens.IFactory factory) {
            super(name);
            this.factory = factory;
            // add to the generator types list so this shows up as an option
            BiomeGeneratorTypeScreens.field_239068_c_.add(this);
        }

        @Override
        protected ChunkGenerator func_241869_a(Registry<Biome> biomes, Registry<DimensionSettings> settings, long seed) {
            return new FlatChunkGenerator(FlatGenerationSettings.func_242869_a(biomes));
        }

        @Override
        public boolean hasEditScreen() {
            return factory != null;
        }

        @Override
        public IFactory getEditScreenFactory() {
            return factory;
        }
    }

    private static class TestEditScreen extends ErrorScreen {

        private final CreateWorldScreen parent;

        private TestEditScreen(CreateWorldScreen parent, DimensionGeneratorSettings settings) {
            super(new StringTextComponent("Test"), new StringTextComponent("Not implemented!"));
            this.parent = parent;
        }

        @Override
        protected void func_231160_c_() {
            this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 100, 140, 200, 20, DialogTexts.field_240633_d_, button -> {
                this.field_230706_i_.displayGuiScreen(parent);
            }));
        }
    }
}
