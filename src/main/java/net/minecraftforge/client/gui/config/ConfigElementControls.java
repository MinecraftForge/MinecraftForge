package net.minecraftforge.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.config.CategoryConfigScreen.CategoryConfigElementList;
import net.minecraftforge.client.gui.config.CategoryConfigScreen.CategoryConfigElementList.ValueConfigElementData;
import net.minecraftforge.client.gui.config.ConfigElementControls.ConfigElementWidgetData.ValueSetter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

public class ConfigElementControls {
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.RED.getColor() could be null"
    public static final int RED = TextFormatting.RED.getColor();
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.GREEN.getColor() could be null"
    public static final int GREEN = TextFormatting.GREEN.getColor();
    // Copied from TextFieldWidget#enabledColor because it's private
    public static final int TEXT_FIELD_ACTIVE_COLOR = 0xe0e0e0;

    public static final StringTextComponent EMPTY_STRING = new StringTextComponent("");
    public static final int WIDGET_WIDTH = 50;
    public static final int WIDGET_HEIGHT = 20;

    public static ConfigElementControls getDefaultCreator() {
        return DefaultHolder.INSTANCE;
    }

    public ValueConfigElementData<?> makeElementData(ConfigElementList configElementList, ITextComponent title, ConfigValue<?> configValue, ValueSpec valueSpec, Object value) {
        if (value instanceof Boolean)
            return createBooleanElement(configElementList, (ForgeConfigSpec.BooleanValue) configValue, title, (Boolean) value);
        else if (value instanceof Integer)
            return createNumericRangedElement(configElementList, (ForgeConfigSpec.IntValue) configValue, valueSpec, title, (Integer) value, Integer::parseInt, Integer.MIN_VALUE);
        else if (value instanceof Long)
            return createNumericRangedElement(configElementList, (ForgeConfigSpec.LongValue) configValue, valueSpec, title, (Long) value, Long::parseLong, Long.MIN_VALUE);
        else if (value instanceof Double)
            return createNumericRangedElement(configElementList, (ForgeConfigSpec.DoubleValue) configValue, valueSpec, title, (Double) value, Double::parseDouble, Double.NEGATIVE_INFINITY);
        else if (value instanceof Enum<?>)
            return createEnumElement(configElementList, (ForgeConfigSpec.EnumValue) configValue, valueSpec, title, (Enum) value);
        else if (value instanceof String)
            return createStringElement(configElementList, (ConfigValue<String>) configValue, valueSpec, title, (String) value);
        else if (value instanceof List<?>)
            return createListElement(configElementList, (ConfigValue<List>) configValue, valueSpec, title, (List) value);
        else
            return null;
    }

    static class DefaultHolder {
        private static final ConfigElementControls INSTANCE = new ConfigElementControls();
    }

    public static class ConfigElementButton extends ExtendedButton {
        public ConfigElementButton(ITextComponent title, IPressable handler) {
            super(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, title, handler);
        }
    }

    /**
     * Has so many constructors so that it is easily extensible.
     */
    public static class ConfigElementTextField extends TextFieldWidget {

        // TextFields have a 2px border on each side that is not included in their width/height
        public static final int BORDER = 2;

        public ConfigElementTextField(ITextComponent title) {
            this(title, Minecraft.getInstance().fontRenderer);
        }

        public ConfigElementTextField(ITextComponent title, FontRenderer fontRenderer) {
            super(fontRenderer, 0, 0, WIDGET_WIDTH - BORDER * 2, WIDGET_HEIGHT - BORDER * 2, title);
        }

        protected String lastText = null;

        @Override
        public void setResponder(@Nullable Consumer<String> responderIn) {
            super.setResponder(newText -> {
                // By default this gets called whenever a user clicks somewhere in the text box
                // Make it so we only run the listener when the text actually changes
                if (responderIn != null && !Objects.equals(newText, lastText))
                    responderIn.accept(newText);
            });
        }
    }

    /**
     * Just testing to check
     */
    public static class TestColorTextField extends ConfigElementTextField {
        public TestColorTextField(ITextComponent title) {
            super(title);
        }

        @Override
        public void setResponder(@Nullable Consumer<String> responderIn) {
            super.setResponder(newText -> {
                if (responderIn != null)
                    responderIn.accept(newText);
                setColor(newText);
            });
        }

        private void setColor(String newText) {
            if (newText == null)
                return;
            if (newText.matches("#[0-9A-f][0-9A-f][0-9A-f]")) {
                String r = newText.substring(1, 2);
                String g = newText.substring(2, 3);
                String b = newText.substring(3, 4);
                newText = "#" + r + r + g + g + b + b;
            }
            if (newText.matches("#[0-9A-f][0-9A-f][0-9A-f][0-9A-f][0-9A-f][0-9A-f]")) {
                String r = newText.substring(1, 3);
                String g = newText.substring(3, 5);
                String b = newText.substring(5, 7);
                int ir;
                int ig;
                int ib;
                try {
                    ir = Integer.parseInt(r, 16);
                    ig = Integer.parseInt(g, 16);
                    ib = Integer.parseInt(b, 16);
                } catch (NumberFormatException e) {
                    setTextColor(RED);
                    return;
                }
                setTextColor(ir << 16 | ig << 8 | ib);
            }
        }
    }

    public static class ConfigElementWidgetData<T> {
        public final Widget widget;
        public final ValueSetter<T> valueSetter;

        ConfigElementWidgetData(Widget widget, ValueSetter<T> valueSetter) {
            this.widget = widget;
            this.valueSetter = valueSetter;
        }

        @FunctionalInterface
        interface ValueSetter<T> {
            default void setup(T value) {
                setValue(true, value);
            }

            default void setTo(T value) {
                setValue(false, value);
            }

            void setValue(boolean isSetup, T newValue);
        }
    }

    public <T> T copyMutable(T o) {
        // Immutable objects can be returned as is
        if (o instanceof Boolean || o instanceof Number || o instanceof String || o instanceof Enum<?>)
            return o;
        if (o instanceof List<?>)
            return (T) new ArrayList<>((List<?>) o);
        throw new IllegalArgumentException("Unknown object " + (o == null ? "null" : o.getClass()));
    }

    public ConfigElementWidgetData<Boolean> createBooleanButton(Predicate<Boolean> setter, ITextComponent title) {
        boolean[] state = new boolean[1];
        TriConsumer<Button, Boolean, Boolean> setValue = (button, isSetup, newValue) -> {
            state[0] = newValue;
            boolean valid = true;
            if (!isSetup)
                valid = setter.test(newValue);
            button.func_238482_a_(new StringTextComponent(Boolean.toString(state[0])));
            button.setFGColor(valid && state[0] ? GREEN : RED);
        };
        Button control = createButton(title, button -> setValue.accept(button, false, !state[0]));
        return new ConfigElementWidgetData<>(control, (isSetup, newValue) -> setValue.accept(control, isSetup, newValue));
    }

    public <T extends Comparable<? super T>> ConfigElementWidgetData<T> createNumericTextField(Predicate<T> setter, ITextComponent title, Function<String, T> parser, T longestValue) {
        TextFieldWidget control = createTextField(title);
        control.setMaxStringLength(longestValue.toString().length());
        BiConsumer<Boolean, String> stringConsumer = (isSetup, newValue) -> {
            T parsed;
            try {
                parsed = parser.apply(newValue);
            } catch (NumberFormatException ignored) {
                control.setTextColor(RED);
                return;
            }
            boolean valid = true;
            if (!isSetup)
                valid = setter.test(parsed);
            control.setTextColor(valid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };
        Consumer<String> responder = newValue -> stringConsumer.accept(false, newValue);
        control.setResponder(responder);
        ValueSetter<T> valueSetter = (isSetup, newValue) -> {
            if (isSetup) {
                // The normal responder assumes it's not setup
                control.setResponder(null);
                stringConsumer.accept(true, newValue.toString());
                control.setText(newValue.toString());
                control.setResponder(responder);
            } else
                control.setText(newValue.toString());
        };
        return new ConfigElementWidgetData<>(control, valueSetter);
    }

    public <T extends Enum<T>> ConfigElementWidgetData<T> createEnumButton(Predicate<T> setter, ITextComponent title, T[] potential) {
        Enum<?>[] state = new Enum<?>[1];
        TriConsumer<Button, Boolean, Enum<?>> setValue = (button, isSetup, newValue) -> {
            state[0] = newValue;
            boolean valid = true;
            if (!isSetup)
                valid = setter.test((T) newValue);
            button.func_238482_a_(new StringTextComponent(state[0].toString()));
            if (!valid)
                button.setFGColor(RED);
            else {
                if (newValue instanceof DyeColor)
                    button.setFGColor(((DyeColor) newValue).getTextColor());
                else if (newValue instanceof TextFormatting && ((TextFormatting) newValue).isColor())
                    button.setFGColor(((TextFormatting) newValue).getColor());
            }
        };
        ConfigElementButton control = new ConfigElementButton(title, button -> {
            Enum<?> next = potential[(ArrayUtils.indexOf(potential, state[0]) + 1) % potential.length];
            setValue.accept(button, false, next);
        });
//        {
//            @Override
//            // Allow mouse click forwards or backwards
//            protected boolean func_230987_a_(int p_230987_1_) {
//                return p_230987_1_ == 0 || p_230987_1_ == 1;
//            }
//        };
        return new ConfigElementWidgetData<>(control, (isSetup, newValue) -> setValue.accept(control, isSetup, newValue));
    }

    public ConfigElementWidgetData<String> createStringTextField(Predicate<String> setter, ITextComponent title) {
        TextFieldWidget control = createTextField(title);
        control.setMaxStringLength(Integer.MAX_VALUE);
        BiConsumer<Boolean, String> stringConsumer = (isSetup, newValue) -> {
            boolean valid = true;
            if (!isSetup)
                valid = setter.test(newValue);
            control.setTextColor(valid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };
        Consumer<String> responder = newValue -> stringConsumer.accept(false, newValue);
        control.setResponder(responder);
        ValueSetter<String> valueSetter = (isSetup, newValue) -> {
            if (isSetup) {
                // The normal responder assumes it's not setup
                control.setResponder(null);
                stringConsumer.accept(true, newValue);
                control.setText(newValue);
                control.setResponder(responder);
            } else
                control.setText(newValue);
        };
        return new ConfigElementWidgetData<>(control, valueSetter);
    }

    public ValueConfigElementData<Boolean> createBooleanElement(ConfigElementList configElementList, ForgeConfigSpec.BooleanValue value, ITextComponent title, Boolean valueValue) {
        ConfigElementWidgetData<Boolean> control = createBooleanButton(newValue -> {
            value.set(newValue);
            configElementList.configScreen.onChange();
            return true; // Can't have an "invalid" boolean
        }, title);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public <T extends Comparable<? super T>> ValueConfigElementData<T> createNumericRangedElement(ConfigElementList configElementList, ConfigValue<T> value, ValueSpec valueInfo, ITextComponent title, T valueValue, Function<String, T> parser, T longestValue) {
        @Nullable
        ForgeConfigSpec.Range<T> range = valueInfo.getRange();
        ConfigElementWidgetData<T> control = createNumericTextField(newValue -> {
            if (range != null && !range.test(newValue))
                return false;
            value.set(newValue);
            configElementList.configScreen.onChange();
            return true;
        }, title, parser, longestValue);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public <T extends Enum<T>> ValueConfigElementData<T> createEnumElement(ConfigElementList configElementList, ForgeConfigSpec.EnumValue<T> value, ValueSpec valueInfo, ITextComponent title, T valueValue) {
        Enum<?>[] potential = Arrays.stream(((Enum<?>) valueValue).getDeclaringClass().getEnumConstants()).filter(valueInfo::test).toArray(Enum<?>[]::new);
        ConfigElementWidgetData<T> control = createEnumButton(newValue -> {
            value.set(newValue);
            configElementList.configScreen.onChange();
            return true; // We already filtered "potential" to have only valid values
        }, title, (T[]) potential);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public ValueConfigElementData<String> createStringElement(ConfigElementList configElementList, ConfigValue<String> value, ValueSpec valueInfo, ITextComponent title, String valueValue) {
        ConfigElementWidgetData<String> control = createStringTextField(newValue -> {
            if (!valueInfo.test(newValue))
                return false;
            value.set(newValue);
            configElementList.configScreen.onChange();
            return true;
        }, title);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public ValueConfigElementData<List> createListElement(ConfigElementList configElementList, ConfigValue<List> value, ValueSpec valueInfo, ITextComponent title, List valueValue) {
        Widget widget = makePopupButton(title, () -> new ListConfigScreen(configElementList.configScreen, title, copyMutable(value.get()), copyMutable((List) valueInfo.getDefault())) {
            @Override
            public void onModified(List newValue) {
                value.set(newValue);
                onChange();
            }
        });
        return new ValueConfigElementData<>(widget, newValue -> {
            value.set(newValue);
            configElementList.configScreen.onChange();
        });
    }

    public ITextComponent makeTranslationComponent(@Nullable String translationKey, String fallback) {
        if (translationKey == null)
            return new StringTextComponent(fallback);
        ITextComponent title = new TranslationTextComponent(translationKey);
        if (translationKey.equals(title.getString()))
            return new StringTextComponent(fallback);
        return title;
    }

    /**
     * Makes a button that opens a new Screen
     */
    public Widget makePopupButton(ITextComponent title, Supplier<? extends Screen> screenFactory) {
        return createButton(title, b -> Minecraft.getInstance().displayGuiScreen(screenFactory.get()));
    }

    public Button createButton(ITextComponent title, Button.IPressable iPressable) {
        return new ConfigElementButton(title, iPressable);
    }

    public ConfigElementTextField createTextField(ITextComponent title) {
        return new ConfigElementTextField(title);
    }

}
