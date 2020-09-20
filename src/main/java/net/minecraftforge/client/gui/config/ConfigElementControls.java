package net.minecraftforge.client.gui.config;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigElementControls {
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.RED.getColor() could be null"
    static final int RED = TextFormatting.RED.getColor();
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.GREEN.getColor() could be null"
    static final int GREEN = TextFormatting.GREEN.getColor();
    // Copied from TextFieldWidget#enabledColor because it's private
    private static final int TEXT_FIELD_ACTIVE_COLOR = 0xe0e0e0;

    private static final StringTextComponent EMPTY_STRING = new StringTextComponent("");
    public static final int WIDGET_WIDTH = 50;
    public static final int WIDGET_HEIGHT = 20;

    public static StringTextComponent component(@Nullable String name) {
        return name == null ? EMPTY_STRING : new StringTextComponent(name);
    }

    public static <T> T copyMutable(T o) {
        // Immutable objects can be returned as is
        if (o instanceof Boolean || o instanceof Number || o instanceof Enum<?>)
            return o;
        if (o instanceof List<?>)
            return (T) new ArrayList<>((List<?>)o);
        throw new IllegalArgumentException("Unknown object " + (o == null ? "null" : o.getClass()));
    }

    /**
     * Has so many constructors so that it is easily extensible.
     */
    public static class ConfigElementButton extends ExtendedButton {
        public ConfigElementButton(@Nullable String name, IPressable handler) {
            this(component(name), handler);
        }

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

        public ConfigElementTextField(@Nullable String name) {
            this(name, Minecraft.getInstance().fontRenderer);
        }

        public ConfigElementTextField(ITextComponent title) {
            this(title, Minecraft.getInstance().fontRenderer);
        }

        public ConfigElementTextField(@Nullable String name, FontRenderer fontRenderer) {
            this(component(name), fontRenderer);
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
        public TestColorTextField(@Nullable String name) {
            super(name);
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

    public static ConfigElementWidgetData<Boolean> createBooleanButton(Predicate<Boolean> setter, @Nullable String name) {
        final boolean[] state = new boolean[1];
        TriConsumer<Button, Boolean, Boolean> setValue = (button, isSetup, newValue) -> {
            state[0] = newValue;
            boolean valid = true;
            if (!isSetup)
                valid = setter.test(newValue);
            button.func_238482_a_(new StringTextComponent(Boolean.toString(state[0])));
            button.setFGColor(valid && state[0] ? GREEN : RED);
        };
        ConfigElementButton control = new ConfigElementButton(name, button -> setValue.accept(button, false, !state[0]));
        return new ConfigElementWidgetData<>(control, (isSetup, newValue) -> setValue.accept(control, isSetup, newValue));
    }

    public static <T extends Comparable<? super T>> ConfigElementWidgetData<T> createNumericTextField(Predicate<T> setter, @Nullable String name, Function<String, T> parser, T longestValue) {
        ConfigElementTextField control = new ConfigElementTextField(name);
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
        final Consumer<String> responder = newValue -> stringConsumer.accept(false, newValue);
        control.setResponder(responder);
        ConfigElementWidgetData.ValueSetter<T> valueSetter = (isSetup, newValue) -> {
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

    public static <T extends Enum<T>> ConfigElementWidgetData<T> createEnumButton(Predicate<T> setter, @Nullable String name, T[] potential) {
        final Enum<?>[] state = new Enum<?>[1];
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
        ConfigElementButton control = new ConfigElementButton(name, button -> {
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

    public static ConfigElementWidgetData<String> createStringTextField(Predicate<String> setter, @Nullable String name) {
        ConfigElementTextField control = new TestColorTextField(name);
        control.setMaxStringLength(Integer.MAX_VALUE);
        final BiConsumer<Boolean, String> stringConsumer = (isSetup, newValue) -> {
            boolean valid = true;
            if (!isSetup)
                valid = setter.test(newValue);
            control.setTextColor(valid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };
        final Consumer<String> responder = newValue -> stringConsumer.accept(false, newValue);
        control.setResponder(responder);
        ConfigElementWidgetData.ValueSetter<String> valueSetter = (isSetup, newValue) -> {
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

}
