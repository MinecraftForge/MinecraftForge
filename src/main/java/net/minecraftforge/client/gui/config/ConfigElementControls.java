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
import net.minecraftforge.client.gui.config.CategoryConfigScreen.CategoryConfigElementList.ValueConfigElementData;
import net.minecraftforge.client.gui.config.ConfigElementControls.ConfigElementWidgetData.ValueSetter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

/**
 * Handles creating the widgets on a {@link ConfigScreen}.
 * Any of these methods can be overriden by mods wanting to add something extra to how their widgets
 * are displayed on the config screen without having to dive deeply into the guts of the GUIs.
 * To do this just create a class that extends {@link ModConfigScreen} and
 */
public class ConfigElementControls {
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.RED.getColor() could be null"
    public static final int RED = TextFormatting.RED.getColor();
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.GREEN.getColor() could be null"
    public static final int GREEN = TextFormatting.GREEN.getColor();
    // Copied from TextFieldWidget#enabledColor because it's private
    public static final int TEXT_FIELD_ACTIVE_COLOR = 0xe0e0e0;

    public static final StringTextComponent EMPTY_STRING = new StringTextComponent("");

    public static ConfigElementControls getDefaultCreator() {
        return DefaultHolder.INSTANCE;
    }

    @Nullable
    public ValueConfigElementData<?> makeElementData(ConfigElementList configElementList, ITextComponent title, ConfigValue<?> configValue, ValueSpec valueSpec, Object value) {
        if (value instanceof Boolean)
            return createBooleanElement(configElementList, (BooleanValue) configValue, valueSpec, title, (Boolean) value);
        else if (value instanceof Integer)
            return createNumericRangedElement(configElementList, (IntValue) configValue, valueSpec, title, (Integer) value, Integer::parseInt, Integer.MIN_VALUE);
        else if (value instanceof Long)
            return createNumericRangedElement(configElementList, (LongValue) configValue, valueSpec, title, (Long) value, Long::parseLong, Long.MIN_VALUE);
        else if (value instanceof Double)
            return createNumericRangedElement(configElementList, (DoubleValue) configValue, valueSpec, title, (Double) value, Double::parseDouble, Double.NEGATIVE_INFINITY);
        else if (value instanceof Enum<?>)
            return createEnumElement(configElementList, (EnumValue) configValue, valueSpec, title, (Enum) value);
        else if (value instanceof String)
            return createStringElement(configElementList, (ConfigValue<String>) configValue, valueSpec, title, (String) value);
        else if (value instanceof List<?>)
            return createListElement(configElementList, (ConfigValue<List>) configValue, valueSpec, title, (List) value);
        else
            return null;
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
        Button control = createButton(title, button -> {
            Enum<?> next = potential[(ArrayUtils.indexOf(potential, state[0]) + 1) % potential.length];
            setValue.accept(button, false, next);
        });
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

    public ValueConfigElementData<Boolean> createBooleanElement(ConfigElementList configElementList, BooleanValue configValue, ValueSpec valueSpec, ITextComponent title, Boolean valueValue) {
        ConfigElementWidgetData<Boolean> control = createBooleanButton(newValue -> {
            configValue.set(newValue);
            configElementList.configScreen.onChange(valueSpec.needsWorldRestart());
            return true; // Can't have an "invalid" boolean
        }, title);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public <T extends Comparable<? super T>> ValueConfigElementData<T> createNumericRangedElement(ConfigElementList configElementList, ConfigValue<T> configValue, ValueSpec valueSpec, ITextComponent title, T valueValue, Function<String, T> parser, T longestValue) {
        @Nullable
        ForgeConfigSpec.Range<T> range = valueSpec.getRange();
        ConfigElementWidgetData<T> control = createNumericTextField(newValue -> {
            if (range != null && !range.test(newValue))
                return false;
            configValue.set(newValue);
            configElementList.configScreen.onChange(valueSpec.needsWorldRestart());
            return true;
        }, title, parser, longestValue);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public <T extends Enum<T>> ValueConfigElementData<T> createEnumElement(ConfigElementList configElementList, ForgeConfigSpec.EnumValue<T> configValue, ValueSpec valueSpec, ITextComponent title, T valueValue) {
        Enum<?>[] potential = Arrays.stream(((Enum<?>) valueValue).getDeclaringClass().getEnumConstants()).filter(valueSpec::test).toArray(Enum<?>[]::new);
        ConfigElementWidgetData<T> control = createEnumButton(newValue -> {
            configValue.set(newValue);
            configElementList.configScreen.onChange(valueSpec.needsWorldRestart());
            return true; // We already filtered "potential" to have only valid values
        }, title, (T[]) potential);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public ValueConfigElementData<String> createStringElement(ConfigElementList configElementList, ConfigValue<String> configValue, ValueSpec valueSpec, ITextComponent title, String valueValue) {
        ConfigElementWidgetData<String> control = createStringTextField(newValue -> {
            if (!valueSpec.test(newValue))
                return false;
            configValue.set(newValue);
            configElementList.configScreen.onChange(valueSpec.needsWorldRestart());
            return true;
        }, title);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public ValueConfigElementData<List> createListElement(ConfigElementList configElementList, ConfigValue<List> configValue, ValueSpec valueSpec, ITextComponent title, List valueValue) {
        Widget widget = makePopupButton(title, () -> new ListConfigScreen(configElementList.configScreen, title, copyMutable(configValue.get()), copyMutable((List) valueSpec.getDefault())) {
            @Override
            public void onModified(List newValue) {
                configValue.set(newValue);
                onChange(valueSpec.needsWorldRestart());
            }
        });
        return new ValueConfigElementData<>(widget, newValue -> {
            configValue.set(newValue);
            configElementList.configScreen.onChange(valueSpec.needsWorldRestart());
        });
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

    static class DefaultHolder {
        static final ConfigElementControls INSTANCE = new ConfigElementControls();
    }

    public static class ConfigElementButton extends ExtendedButton {
        // Integer.MAX_VALUE / 2 chosen to be very large so as to avoid any scrolling/visibility issues but also not cause integer overflow issues
        public ConfigElementButton(ITextComponent title, IPressable handler) {
            super(0, 0, Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2, title, handler);
        }
    }

    /**
     * Has so many constructors so that it is easily extensible.
     */
    public static class ConfigElementTextField extends TextFieldWidget {

        // TextFields have a 2px border on each side that is not included in their width/height
        public static final int BORDER = 2;
        protected String lastText = null;

        public ConfigElementTextField(ITextComponent title) {
            this(title, Minecraft.getInstance().fontRenderer);
        }

        public ConfigElementTextField(ITextComponent title, FontRenderer fontRenderer) {
            // Integer.MAX_VALUE / 2 chosen to be very large so as to avoid any scrolling/visibility issues but also not cause integer overflow issues
            super(fontRenderer, 0, 0, Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2, title);
        }

        @Override
        public void setResponder(@Nullable Consumer<String> responderIn) {
            super.setResponder(newText -> {
                // By default this gets called whenever a user clicks somewhere in the text box
                // Make it so we only run the listener when the text actually changes
                if (responderIn != null && !Objects.equals(newText, lastText))
                    responderIn.accept(newText);
                lastText = newText;
            });
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

}
