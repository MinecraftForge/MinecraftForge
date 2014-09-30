package net.minecraftforge.fml.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyListElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;

public class FMLConfigGuiFactory implements IModGuiFactory 
{
    public static class FMLConfigGuiScreen extends GuiConfig 
    {

        public FMLConfigGuiScreen(GuiScreen parent)
        {
            super(parent, getConfigElements(), "FML", false, false, I18n.format("fml.config.sample.title"));
        }
        
        private static List<IConfigElement> getConfigElements()
        {
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            List<IConfigElement> listsList = new ArrayList<IConfigElement>();
            List<IConfigElement> stringsList = new ArrayList<IConfigElement>();
            List<IConfigElement> numbersList = new ArrayList<IConfigElement>();
            Pattern commaDelimitedPattern = Pattern.compile("([A-Za-z]+((,){1}( )*|$))+?");
            
            // Top Level Settings
            list.add(new DummyConfigElement("imABoolean", true, ConfigGuiType.BOOLEAN, "fml.config.sample.imABoolean").setRequiresMcRestart(true));
            list.add(new DummyConfigElement("imAnInteger", 42, ConfigGuiType.INTEGER, "fml.config.sample.imAnInteger", -1, 256).setRequiresMcRestart(true));
            list.add(new DummyConfigElement("imADouble", 42.4242D, ConfigGuiType.DOUBLE, "fml.config.sample.imADouble", -1.0D, 256.256D).setRequiresMcRestart(true));
            list.add(new DummyConfigElement("imAString", "http://www.montypython.net/scripts/string.php", ConfigGuiType.STRING, "fml.config.sample.imAString").setRequiresMcRestart(true));
            
            // Lists category
            listsList.add(new DummyListElement("booleanList", new Boolean[] {true, false, true, false, true, false, true, false}, ConfigGuiType.BOOLEAN, "fml.config.sample.booleanList"));
            listsList.add(new DummyListElement("booleanListFixed", new Boolean[] {true, false, true, false, true, false, true, false}, ConfigGuiType.BOOLEAN, "fml.config.sample.booleanListFixed", true));
            listsList.add(new DummyListElement("booleanListMax", new Boolean[] {true, false, true, false, true, false, true, false}, ConfigGuiType.BOOLEAN, "fml.config.sample.booleanListMax", 10));
            listsList.add(new DummyListElement("doubleList", new Double[] {0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D}, ConfigGuiType.DOUBLE, "fml.config.sample.doubleList"));
            listsList.add(new DummyListElement("doubleListFixed", new Double[] {0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D}, ConfigGuiType.DOUBLE, "fml.config.sample.doubleListFixed", true));
            listsList.add(new DummyListElement("doubleListMax", new Double[] {0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D}, ConfigGuiType.DOUBLE, "fml.config.sample.doubleListMax", 15));
            listsList.add(new DummyListElement("doubleListBounded", new Double[] {0.0D, 1.1D, 2.2D, 3.3D, 4.4D, 5.5D, 6.6D, 7.7D, 8.8D, 9.9D}, ConfigGuiType.DOUBLE, "fml.config.sample.doubleListBounded", -1.0D, 10.0D));
            listsList.add(new DummyListElement("integerList", new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, ConfigGuiType.INTEGER, "fml.config.sample.integerList"));
            listsList.add(new DummyListElement("integerListFixed", new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, ConfigGuiType.INTEGER, "fml.config.sample.integerListFixed", true));
            listsList.add(new DummyListElement("integerListMax", new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, ConfigGuiType.INTEGER, "fml.config.sample.integerListMax", 15));
            listsList.add(new DummyListElement("integerListBounded", new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, ConfigGuiType.INTEGER, "fml.config.sample.integerListBounded", -1, 10));
            listsList.add(new DummyListElement("stringList", new String[] {"An", "array", "of", "string", "values"}, ConfigGuiType.STRING, "fml.config.sample.stringList"));
            listsList.add(new DummyListElement("stringListFixed", new String[] {"A", "fixed", "length", "array", "of", "string", "values"}, ConfigGuiType.STRING, "fml.config.sample.stringListFixed", true));
            listsList.add(new DummyListElement("stringListMax", new String[] {"An", "array", "of", "string", "values", "with", "a", "max", "length", "of", "15"}, ConfigGuiType.STRING, "fml.config.sample.stringListMax", 15));
            listsList.add(new DummyListElement("stringListPattern", new String[] {"Valid", "Not Valid", "Is, Valid", "Comma, Separated, Value"}, ConfigGuiType.STRING, "fml.config.sample.stringListPattern", commaDelimitedPattern));
            
            list.add(new DummyCategoryElement("lists", "fml.config.sample.ctgy.lists", listsList));
            
            // Strings category
            stringsList.add(new DummyConfigElement("basicString", "Just a regular String value, anything goes.", ConfigGuiType.STRING, "fml.config.sample.basicString"));
            stringsList.add(new DummyConfigElement("cycleString", "this", ConfigGuiType.STRING, "fml.config.sample.cycleString", new String[] {"this", "property", "cycles", "through", "a", "list", "of", "valid", "choices"}));
            stringsList.add(new DummyConfigElement("patternString", "only, comma, separated, words, can, be, entered, in, this, box", ConfigGuiType.STRING, "fml.config.sample.patternString", commaDelimitedPattern));
            stringsList.add(new DummyConfigElement("chatColorPicker", "c", ConfigGuiType.COLOR, "fml.config.sample.chatColorPicker", new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"}));
            stringsList.add(new DummyConfigElement("modIDSelector", "FML", ConfigGuiType.MOD_ID, "fml.config.sample.modIDSelector"));
            
            list.add(new DummyCategoryElement("strings", "fml.config.sample.ctgy.strings", stringsList));
            
            // Numbers category
            numbersList.add((new DummyConfigElement("basicInteger", 42, ConfigGuiType.INTEGER, "fml.config.sample.basicInteger")));
            numbersList.add((new DummyConfigElement("boundedInteger", 42, ConfigGuiType.INTEGER, "fml.config.sample.boundedInteger", -1, 256)));
            numbersList.add((new DummyConfigElement("sliderInteger", 2000, ConfigGuiType.INTEGER, "fml.config.sample.sliderInteger", 100, 10000)).setCustomListEntryClass(NumberSliderEntry.class));
            numbersList.add(new DummyConfigElement("basicDouble", 42.4242D, ConfigGuiType.DOUBLE, "fml.config.sample.basicDouble"));
            numbersList.add(new DummyConfigElement("boundedDouble", 42.4242D, ConfigGuiType.DOUBLE, "fml.config.sample.boundedDouble", -1.0D, 256.256D));
            numbersList.add(new DummyConfigElement("sliderDouble", 42.4242D, ConfigGuiType.DOUBLE, "fml.config.sample.sliderDouble", -1.0D, 256.256D).setCustomListEntryClass(NumberSliderEntry.class));

            list.add(new DummyCategoryElement("numbers", "fml.config.sample.ctgy.numbers", numbersList));
            
            return list;
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance)
    {
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return FMLConfigGuiScreen.class;
    }

    private static final Set<RuntimeOptionCategoryElement> fmlCategories = ImmutableSet.of(new RuntimeOptionCategoryElement("HELP", "FML"));

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return fmlCategories;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return new RuntimeOptionGuiHandler() {
            @Override
            public void paint(int x, int y, int w, int h)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void close()
            {
            }

            @Override
            public void addWidgets(List<Gui> widgets, int x, int y, int w, int h)
            {
                widgets.add(new GuiButton(100, x+10, y+10, "HELLO"));
            }

            @Override
            public void actionCallback(int actionId)
            {
                // TODO Auto-generated method stub

            }
        };
    }

}