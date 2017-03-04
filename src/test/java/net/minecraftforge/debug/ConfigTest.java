package net.minecraftforge.debug;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ConfigTest.MODID, name = "ConfigTest", version = "1.0", acceptableRemoteVersions = "*", guiFactory = ConfigTest.GUI_FACTORY)
public class ConfigTest implements IModGuiFactory
{
    public static final String MODID = "config_test";
    public static final String GUI_FACTORY = "net.minecraftforge.debug.ConfigTest";
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
      System.out.println("Old: " + CONFIG_TYPES.bool);
      CONFIG_TYPES.bool = !CONFIG_TYPES.bool;
      System.out.println("New: " + CONFIG_TYPES.bool);
      ConfigManager.sync(MODID, Type.INSTANCE);
      System.out.println("After sync: " + CONFIG_TYPES.bool);
    }
    
    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent event) {
        if(event.getModID().equals(MODID))
        {
            ConfigManager.sync(MODID, Type.INSTANCE);
        }
    }

    @LangKey("config_test.config.types")
    @Config(modid = MODID, type = Type.INSTANCE, name = MODID + "_types")
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

        @Comment("Category Comment Test")
        public static NestedType Inner = new NestedType();

        public static class NestedType
        {
            public String HeyLook = "Go in!";
        }
    }
    @LangKey("config_test.config.subcats")
    @Config(modid = MODID, name = MODID + "_subcats", category = "")
    public static class CONFIG_SUBCATS
    {
        //public static String THIS_WILL_ERROR = "DUH";

        @Name("test_a")
        public static SubCat sub1 = new SubCat("Hello");
        @Name("test_b")
        public static SubCat sub2 = new SubCat("Goodbye");

        public static class SubCat
        {
            @Name("i_say")
            public static String value;
            public SubCat(String value)
            {
                this.value = value;
            }
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance)
    {}

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new GuiConfig(parentScreen, MODID, false, false, "Config test config screen", CONFIG_ANNOTATIONS.class, CONFIG_SUBCATS.class, CONFIG_TYPES.class);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return null;
    }
}
