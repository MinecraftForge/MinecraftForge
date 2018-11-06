/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.test;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod.EventBusSubscriber
@Mod(modid = SliderConfigElementTest.ID,name = "Slider Config Element Test",version = "0.0")
public class SliderConfigElementTest {

    static final String ID="slider_config_element";

    @Config(modid = ID)
    public static class Configuration
    {
        @Config.SlidingOption
        @Config.RangeDouble(min = 0,max = 1)
        public static double dble = 0.5;

        @Config.SlidingOption
        @Config.RangeInt(min = -100,max = 50)
        public static int integer = 0;

        @Config.SlidingOption
        @Config.RangeDouble(min = 15.6f,max = 344.78f)
        public static float f = 60.7f;

        //this won't do anything
        @Config.SlidingOption
        public static boolean bool = false;

        //this also won't do anything
        @Config.SlidingOption
        public static String string = "I don't slide";


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
