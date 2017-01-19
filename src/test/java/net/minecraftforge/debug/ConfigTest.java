package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ConfigTest.MODID, name = "ConfigTest", version = "1.0", acceptableRemoteVersions = "*")
public class ConfigTest
{
    public static final String MODID = "config_test";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
    }

    @Config(modid = MODID, type = Type.INSTANCE, name = MODID + "_types", category = "testing")
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

        @Categorized("inside")
        public static class NestedType
        {
            public String HeyLook = "I'm Inside!";
        }
    }
    @Config(modid = MODID)
    public static class CONFIG_ANNOTATIONS
    {
        @RangeDouble(min = -10.5, max = 100.5)
        public static double DoubleRange = 10.0;

        @RangeInt(min = -10, max = 100)
        public static double IntRange = 10;

        @LangKey("this.is.not.a.good.key")
        @Comment({"This is a really long", "Multi-line comment"})
        public static String Comments = "Hi Tv!";
    }
}
