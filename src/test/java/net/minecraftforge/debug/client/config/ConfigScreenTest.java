package net.minecraftforge.debug.client.config;

import net.minecraft.util.Direction;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.gui.screen.DefaultConfigScreen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

@Mod("config_screen_test")
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ConfigScreenTest {

    static ForgeConfigSpec configSpec;

    public ConfigScreenTest() {
        Pair<Object, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(builder -> {
            builder.define("Boolean property", true);
            builder.define("Integer property", 4487);
            builder.define("Double property", 15.2132);
            builder.define("Enum property", Direction.SOUTH);
            builder.define("Long property", 123456789012345000L);
            builder.define("String property", "CoffeeScript");
            builder.defineInRange("Ranged integer", 50, 1, 500);
            builder.defineInRange("Ranged double", 6.8, 1.1, 10.2);
            builder.defineInRange("Ranged long", 5555555, 11111, 555555555555L);
            return builder.build();
        });
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, configSpec = configSpecPair.getRight());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void makeScreen(FMLClientSetupEvent clientSetupEvent) {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> new DefaultConfigScreen(new StringTextComponent("Test"), screen, configSpec));
    }
}
