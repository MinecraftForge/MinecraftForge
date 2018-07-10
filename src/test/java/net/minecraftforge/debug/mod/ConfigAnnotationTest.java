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

package net.minecraftforge.debug.mod;

import com.google.common.collect.Maps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = ConfigAnnotationTest.MODID, name = "ConfigTest", version = "1.0", acceptableRemoteVersions = "*")
public class ConfigAnnotationTest
{
    public static final String MODID = "config_test";
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("Starting config modification test.");
        logger.debug("Starting simple variable change test.");
        logger.debug("Old: {}", CONFIG_TYPES.bool);
        CONFIG_TYPES.bool = !CONFIG_TYPES.bool;
        logger.debug("New: {}", CONFIG_TYPES.bool);
        ConfigManager.sync(MODID, Type.INSTANCE);
        logger.debug("After sync: {}", CONFIG_TYPES.bool);

        logger.debug("Starting map key addition test.");
        logger.debug("Old map keys: {}", Arrays.deepToString(ROOT_CONFIG_TEST.mapTest.keySet().toArray()));
        String key = "" + System.currentTimeMillis();
        logger.debug("Adding key: {}", key);
        ROOT_CONFIG_TEST.mapTest.put(key, Math.PI);
        ConfigManager.sync(MODID, Type.INSTANCE);
        logger.debug("Keys after sync: {}", Arrays.deepToString(ROOT_CONFIG_TEST.mapTest.keySet().toArray()));
        logger.debug("Synced. Check config gui to verify the key was added properly");
    }

    @SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID))
        {
            ConfigManager.sync(MODID, Type.INSTANCE);
        }
    }

    @Config(modid = MODID, type = Type.INSTANCE)
    public static class ROOT_CONFIG_TEST {
        public static HashMap<String, Double> mapTest = new HashMap<>();

        static {
            mapTest.put("foobar", 2.0);
            mapTest.put("foobaz", 100.5);
            mapTest.put("barbaz", Double.MAX_VALUE);
        }
    }

    @LangKey("config_test.config.types")
    @Config(modid = MODID, type = Type.INSTANCE, name = MODID + "_types", category = "types")
    public static class CONFIG_TYPES
    {
        public static boolean     bool = false;
        public static boolean[]   boolA = {false, true};
        public static Boolean     Bool = false;
        public static Boolean[]   BoolA = {false, true};
        public static float       flt = 1.0f;
        public static float[]     fltA = {1.0f, 2.0f};
        public static Float       Flt = 1.0f;
        public static Float[]     FltA = {1.0f, 2.0f};
        public static double      dbl = 1.0d;
        public static double[]    dblA = {1.0d, 2.0d};
        public static Double      Dbl = 1.0D;
        public static Double[]    DblA = {1.0D, 2.0D};
        public static byte        byt = 1;
        public static byte[]      bytA = {1, 2};
        public static Byte        Byt = 1;
        public static Byte[]      BytA = {1, 2};
        public static char        chr = 'a';
        public static char[]      chrA = {'a', 'b'};
        public static Character   Chr = 'A';
        public static Character[] ChrA = {'A', 'B'};
        public static short       srt = 1;
        public static short[]     srtA = {1, 2};
        public static Short       Srt = 1;
        public static Short[]     SrtA = {1, 2};
        public static int         int_ = 1;
        public static int[]       intA = {1, 2};
        public static Integer     Int = 1;
        public static Integer[]   IntA = {1, 2};
        public static String      Str = "STRING!";
        public static String[]    StrA = {"STR", "ING!"};
        public static TEST        enu = TEST.BIG;
        public static NestedType  Inner = new NestedType();

        public enum TEST { BIG, BAD, WOLF; }
        public static class NestedType
        {
            public String HeyLook = "I'm Inside!";
        }
    }

    @LangKey("config_test.config.annotations")
    @Config(modid = MODID, category = "annotations")
    public static class CONFIG_ANNOTATIONS
    {
        @RangeDouble(min = -10.5, max = 100.5)
        public static double DoubleRange = 10.0;

        @RangeInt(min = -10, max = 100)
        public static double IntRange = 10;

        @LangKey("this.is.not.a.good.key")
        @Comment({"This is a really long", "Multi-line comment"})
        public static String Comments = "Hi Tv!";

        @Comment("Category Comment Test")
        public static NestedType Inner = new NestedType();

        public static class NestedType
        {
            public String HeyLook = "Go in!";
        }
    }

    @LangKey("config_test.config.subcats")
    @Config(modid = MODID, name = MODID + "_subcats", category = "subcats")
    public static class CONFIG_SUBCATS
    {
        //public static String THIS_WILL_ERROR = "DUH";

        @Name("test_a")
        public static SubCat sub1 = new SubCat("Hello");
        @Name("test_b")
        public static SubCat sub2 = new SubCat("Goodbye");
        @Name("test_c")
        public static SubCat2 sub3 = new SubCat2("Hi");

        public static class SubCat
        {
            @Name("i_say")
            public String value;

            public SubCat(String value)
            {
                this.value = value;
            }
        }

        public static class SubCat2
        {
            @Name("child_cat")
            public SubCat child;

            public SubCat2(String value)
            {
                this.child = new SubCat(value);
            }
        }
    }

    @LangKey("config_test.config.maps")
    @Config(modid = MODID, name = MODID + "_map", category = "maps")
    public static class CONFIG_MAP
    {
        @Name("map")
        @Comment("This comment belongs to the \"map\" category, not the \"general\" category")
        @RequiresMcRestart
        public static Map<String, Integer[]> theMap;

        @Name("regex(test]")
        public static Map<String, String> regexText = new HashMap<>();

        static
        {
            theMap = Maps.newHashMap();
            for (int i = 0; i < 7; i++)
            {
                Integer[] array = new Integer[6];
                for (int x = 0; x < array.length; x++)
                {
                    array[x] = i + x;
                }
                theMap.put("" + i, array);
                regexText.put("" + i, "" + i);
            }
        }
    }
}
