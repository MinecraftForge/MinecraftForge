package net.minecraftforge.debug.client.gui.config;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ColorHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.config.ControlCreator;
import net.minecraftforge.client.gui.config.ModConfigScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Just for testing the extensibility of the system.
 *
 * @author Cadiboo
 */
@Mod(ConfigGuiTest.MOD_ID)
public class ConfigGuiTest {
    public static final String MOD_ID = "config_gui_test";

    public ConfigGuiTest() {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, commonSpec);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            IModInfo modInfo = ctx.getActiveContainer().getModInfo();
            ctx.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new CustomConfigScreen(screen, modInfo));
        });
    }

    public static class CustomConfigScreen extends ModConfigScreen {

        private final ControlCreator controlCreator;

        public CustomConfigScreen(Screen screen, IModInfo mod) {
            super(screen, mod);
            controlCreator = new ControlCreator() {

                @Override
                public void createStringInteractionWidget(Interactor<String> interactor) {
                    super.createStringInteractionWidget(interactor);
                    if (interactor.getData(CONFIG_VALUE_KEY) == COMMON.hexColorString)
                        addColorHandlerToStringInteractor(interactor);
                }

                @Override
                public void createBooleanInteractionWidget(Interactor<Boolean> interactor) {
                    super.createBooleanInteractionWidget(interactor);
                    if (interactor.getData(CONFIG_VALUE_KEY) == COMMON.colouredBoolean)
                        addColorHandlerToBooleanInteractor(interactor);
                }
            };
        }

        private void addColorHandlerToStringInteractor(ControlCreator.Interactor<String> interactor) {
            interactor.addUpdateResponder((isValid, newValue) -> {
                if (isValid)
                    parseAndSetHexColor((TextFieldWidget) interactor.control, newValue);
            });
        }

        private void parseAndSetHexColor(TextFieldWidget widget, String newValue) {
            if (newValue.matches("#[0-9A-f][0-9A-f][0-9A-f]")) {
                String parsedR = newValue.substring(1, 2);
                String parsedG = newValue.substring(2, 3);
                String parsedB = newValue.substring(3, 4);
                newValue = "#" + parsedR + parsedR + parsedG + parsedG + parsedB + parsedB;
            }
            if (newValue.matches("#[0-9A-f][0-9A-f][0-9A-f][0-9A-f][0-9A-f][0-9A-f]")) {
                String parsedR = newValue.substring(1, 3);
                String parsedG = newValue.substring(3, 5);
                String parsedB = newValue.substring(5, 7);
                int r;
                int g;
                int b;
                try {
                    r = Integer.parseInt(parsedR, 16);
                    g = Integer.parseInt(parsedG, 16);
                    b = Integer.parseInt(parsedB, 16);
                } catch (NumberFormatException e) {
                    widget.setTextColor(ControlCreator.RED);
                    return;
                }
                int color = ColorHelper.PackedColor.func_233006_a_(0xFF, r, g, b);
                widget.setTextColor(color);
            }
        }

        private void addColorHandlerToBooleanInteractor(ControlCreator.Interactor<Boolean> interactor) {
            interactor.addUpdateResponder((isValid, newValue) -> {
                if (isValid)
                    setInvertedColor((Button) interactor.control, newValue);
            });
        }

        private void setInvertedColor(Button widget, Boolean newValue) {
            int color = newValue ? ControlCreator.GREEN : ControlCreator.RED;
            int inverted = color ^ 0x00FFFFFF;
            widget.setFGColor(inverted);
        }

        @Override
        public ControlCreator getControlCreator() {
            return controlCreator;
        }
    }

    public static final Common COMMON;
    static final ForgeConfigSpec commonSpec;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {

        private final ForgeConfigSpec.ConfigValue<String> hexColorString;
        private final ForgeConfigSpec.BooleanValue colouredBoolean;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("a Boolean comment")
                .translation("forge.configgui.a.boolean")
                .worldRestart()
                .define("aBoolean", false);

            builder.comment("Category comment")
                .push("numbers");
            {
                builder.comment("an Int comment")
                    .translation("forge.configgui.an.int")
                    .defineInRange("anInt", 5, -10, 1000);
                builder.comment("a small Int comment")
                    .translation("forge.configgui.a.small.int")
                    .defineInRange("aSmallInt", 5, -5, 5);
                builder.comment("a Long comment")
                    .translation("forge.configgui.a.long")
                    .defineInRange("aLong", Long.MIN_VALUE, Long.MIN_VALUE, 5);
                builder.comment("a Double comment")
                    .translation("forge.configgui.a.double")
                    .defineInRange("aDouble", 5d, 4d, 6d);

                builder.comment("Sub-category comment")
                    .push("empty");
                builder.pop();

                builder.comment("Sub-category comment")
                    .push("not-empty");
                {
                    builder.comment("sub - an Int comment")
                        .translation("forge.configgui.sub.an.int")
                        .defineInRange("subAnInt", 5, -10, 1000);
                }
                builder.pop();
            }
            builder.pop();

            builder.comment("an Enum comment")
                .translation("forge.configgui.an.enum")
                .defineEnum("anEnum", DyeColor.GREEN, Arrays.stream(DyeColor.values()).filter(dc -> dc.getId() >= 10).collect(Collectors.toList()));
            builder.comment("a String comment")
                .translation("forge.configgui.a.string")
                .define("aString", "foo");
            hexColorString = builder.comment("a Hex Color String comment")
                .translation("forge.configgui.a.hexColorString")
                .define("aHexColorString", "#0FF");
            colouredBoolean = builder.comment("a Coloured Boolean comment")
                .translation("forge.configgui.a.colouredBoolean")
                .define("aColouredBoolean", false);
            builder.comment("an Enum List comment")
                .translation("forge.configgui.an.enum.list")
                .defineList("anEnumList", Lists.newArrayList(), o -> o instanceof String);
            builder.comment("a String In List comment")
                .translation("forge.configgui.a.string.in.list")
                .defineInList("aStringInList", "bar", Lists.newArrayList("foo", "bar", "baz"));

            builder.comment("I'm in Spain but the S is silent")
                .push("Lists!");
            {
                builder.comment("a List<Boolean> comment")
                    .translation("forge.configgui.a.list.boolean")
                    .defineList("aListBoolean", Lists.newArrayList(true, false, true, false, true, true, false, false), o -> o instanceof Boolean);
                builder.comment("a List<Int> comment")
                    .translation("forge.configgui.a.list.int")
                    .defineList("aListInt", Lists.newArrayList(1, 2, 5, 66, 3, 7), o -> o instanceof Integer);
                builder.comment("a List<Long> comment")
                    .translation("forge.configgui.a.list.long")
                    .defineList("aListLong", Lists.newArrayList(5L, 12L, 623L, Long.MIN_VALUE), o -> o instanceof Long);
                builder.comment("a List<Double> comment")
                    .translation("forge.configgui.a.list.double")
                    .defineList("aListDouble", Lists.newArrayList(123.213, 21343.21, 456.32, 9765.2), o -> o instanceof Double);
                builder.comment("a List<Enum> comment")
                    .translation("forge.configgui.a.list.enum")
                    .defineList("aListEnum", Lists.newArrayList(DyeColor.BLACK, DyeColor.GREEN, DyeColor.RED, DyeColor.BLACK), o -> o instanceof DyeColor && ((DyeColor) o).getId() >= 10);
                builder.comment("a List<String> comment")
                    .translation("forge.configgui.a.list.string")
                    .defineList("aListString", Lists.newArrayList("foo", "bar", "baz", "qez"), o -> o instanceof String);
                builder.comment("a List<List<String>> comment")
                    .translation("forge.configgui.a.list.list.string")
                    .defineList("aListListString", Lists.newArrayList(Lists.newArrayList("foo", "bar", "baz", "qez"), Lists.newArrayList("foobar")), o -> o instanceof List);
            }
            builder.pop();
        }
    }
}
