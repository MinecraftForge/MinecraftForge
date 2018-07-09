package net.minecraftforge.test;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.SlidingOption;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created on 7/9/18 by alexiy.
 */
@Mod.EventBusSubscriber
@Mod(modid = SliderConfigElementTest.ID,name = "Slider Config Element Test",version = "0.0")
public class SliderConfigElementTest {

    static final String ID="slider_config_element";

    @Config(modid = ID)
    public static class Configuration
    {
        @SlidingOption
        @Config.RangeDouble(min = 0,max = 1)
        public static double dble=0.5;

        @SlidingOption
        @Config.RangeInt(min = -100,max = 50)
        public static int integer=0;

    }

    @SubscribeEvent
    public static void optionsChanged(ConfigChangedEvent.OnConfigChangedEvent configChangedEvent)
    {
        if(configChangedEvent.getModID().equals(ID))
        {
            ConfigManager.sync(ID,Config.Type.INSTANCE);
        }
    }
}
