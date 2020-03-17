package net.minecraftforge.debug;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
                .define("list", configList, Object::toString);

        builder.pop();

        ForgeConfigSpec spec = builder.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, spec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, spec);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInit);
    }

    public void preInit(FMLCommonSetupEvent event)
    {
        LOGGER.info("STRING VALUE: " + testStringValue.get());
        LOGGER.info("LIST VALUE: " + testListValue.get().toString());
    }
}
