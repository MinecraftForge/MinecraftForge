package net.minecraftforge.debug.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.client.GeneratorTypeManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Supplier;

@Mod(GeneratorTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GeneratorTypeTest {

    public static final String MODID = "generator_type_test";

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        // make amplified the default
        GeneratorTypeManager.get().setDefaultGeneratorType(BiomeGeneratorTypeScreens.field_239067_b_);
        // add generator type without an edit screen
        GeneratorTypeManager.get().register(new TestGenerator("no_screen"));
        // add generator type with a dummy edit screen
        GeneratorTypeManager.get().register(new TestGenerator("with_screen"), TestEditScreen::new);
    }

    private static class TestGenerator extends BiomeGeneratorTypeScreens {

        protected TestGenerator(String name) {
            super(name);
        }

        @Override
        protected ChunkGenerator func_241869_a(Registry<Biome> biomes, Registry<DimensionSettings> settings, long seed) {
            BiomeProvider provider = new OverworldBiomeProvider(seed, false, false, biomes);
            Supplier<DimensionSettings> supplier = () -> settings.func_243576_d(DimensionSettings.field_242734_c);
            return new NoiseChunkGenerator(provider, seed, supplier);
        }
    }

    private static class TestEditScreen extends Screen {

        private final CreateWorldScreen parent;

        protected TestEditScreen(CreateWorldScreen parent, DimensionGeneratorSettings settings) {
            super(new StringTextComponent("Demo Edit Screen"));
            this.parent = parent;
        }

        @Override // init gui
        protected void func_231160_c_() {
            super.func_231160_c_();
            func_230480_a_(new Button(this.field_230708_k_ / 2 - 100, 140, 200, 20, DialogTexts.field_240632_c_, button -> this.func_231175_as__()));
        }

        @Override // render
        public void func_230430_a_(MatrixStack matrix, int mx, int my, float partial) {
            // background
            func_238468_a_(matrix, 0, 0, this.field_230708_k_, this.field_230709_l_, -12574688, -11530224);
            // title
            func_238472_a_(matrix, this.field_230712_o_, this.field_230704_d_, this.field_230708_k_ / 2, 90, 16777215);
            super.func_230430_a_(matrix, mx, my, partial);
        }

        @Override // close
        public void func_231175_as__() {
            if (field_230706_i_ != null) field_230706_i_.displayGuiScreen(parent);
        }
    }
}
