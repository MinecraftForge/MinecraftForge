package net.minecraftforge.debug.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.logging.Logger;

@Mod(ConfigSupportsStringAndListTest.MODID)
public class ConfigSupportsStringAndListTest
{
    private static final Logger LOGGER = Logger.getLogger("ConfigStrList");
    public static final String MODID = "config_supports_other_types_test";

    public static ForgeConfigSpec.StringValue testStringValue;
    public static ForgeConfigSpec.ListValue<String> testListValue;

    private static final List<String> configList = Lists.newArrayList("world", "DIM1", "DIM-1");

    public ConfigSupportsStringAndListTest()
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Config string and list test")
                .push("test");

        testStringValue = builder
                .defineString("string", "test123");
        testListValue = builder
                .defineList("list", configList, Object::toString);

        builder.pop();

        ForgeConfigSpec spec = builder.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, spec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInitClient);
        MinecraftForge.EVENT_BUS.addListener(this::initServer);
    }

    @SubscribeEvent
    public void preInitClient(FMLClientSetupEvent event)
    {
        LOGGER.info("CLIENT STRING VALUE: " + testStringValue.get());
        LOGGER.info("CLIENT LIST VALUE: " + testListValue.get().toString());
    }

    @SubscribeEvent
    public void initServer(FMLServerAboutToStartEvent event)
    {
        LOGGER.info("SERVER STRING VALUE: " + testStringValue.get());
        LOGGER.info("SERVER LIST VALUE: " + testListValue.get().toString());
    }
}
