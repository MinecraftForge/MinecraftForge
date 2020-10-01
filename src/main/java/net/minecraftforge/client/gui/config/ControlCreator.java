package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
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
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Handles creating the widgets on a {@link ConfigScreen}.
 * Any of these methods can be overridden by mods wanting to add something extra to how their widgets
 * are displayed on the config screen without having to dive deeply into the guts of the GUIs.
 * To do this just create a class that extends {@link ModConfigScreen}, override {@link ConfigScreen#getControlCreator()}
 * to return a custom implementation (cache the value, don't create a new one each time it's called) and then register
 * your constructor for the {@link net.minecraftforge.fml.ExtensionPoint#CONFIGGUIFACTORY} extension point (the same way
 * that {@link ModConfigScreen#makeConfigGuiExtensionPoint(ModContainer)} works.
 *
 * @author Cadiboo
 */
public class ControlCreator {

    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.RED.getColor() could be null"
    public static final int RED = TextFormatting.RED.getColor();
    @SuppressWarnings("ConstantConditions") // Suppress "TextFormatting.GREEN.getColor() could be null"
    public static final int GREEN = TextFormatting.GREEN.getColor();
    // Copied from TextFieldWidget#enabledColor because it's private
    public static final int TEXT_FIELD_ACTIVE_COLOR = 0xe0e0e0;

    static class Interactor<T> {

        public final ITextComponent title;
        public final T initialValue;
        public ITextComponent label;
        public Widget control;
        public Predicate<T> isValid = $ -> true;
        public Consumer<T> saveValue = $ -> {
        };
        public UpdateVisuals<T> updateVisuals = (a, b) -> {
        };

        Interactor(ITextComponent title, T initialValue) {
            this.title = title;
            this.initialValue = initialValue;
            this.label = title;
        }

        @FunctionalInterface
        interface UpdateVisuals<T> {
            void update(boolean isValid, T newValue);
        }

    }

    public static class ConfigValueInteractor<T> extends Interactor<T> {

        private final ForgeConfigSpec.ConfigValue<T> configValue;
        private final ForgeConfigSpec.ValueSpec configSpec;

        ConfigValueInteractor(ITextComponent title, ConfigValue<T> configValue, ValueSpec configSpec) {
            super(title, configValue.get());
            this.configValue = configValue;
            this.configSpec = configSpec;
            isValid = configSpec::test;
            saveValue = configValue::set;
        }

        public ForgeConfigSpec.ConfigValue<T> getConfigValue() {
            return configValue;
        }

        public ForgeConfigSpec.ValueSpec getConfigSpec() {
            return configSpec;
        }
    }

    public static ControlCreator getDefaultCreator() {
        return DefaultHolder.INSTANCE;
    }

    public <T> void createInteractionWidget(Interactor<T> interactor) {
        T value = interactor.initialValue;
        if (value instanceof Boolean)
            createBooleanInteractionWidget((Interactor<Boolean>) interactor);
        else if (value instanceof Number)
            createNumericInteractionWidget((Interactor<? extends Number>) interactor);
        else if (value instanceof Enum<?>)
            createEnumInteractionWidget((Interactor<Enum>) interactor);
        else if (value instanceof String)
            createStringInteractionWidget((Interactor<String>) interactor);
        else if (value instanceof List<?>)
            createListInteractionWidget((Interactor<List<?>>) interactor);
    }

    public <T> T[] createHolderForUseInLambda(T value) {
        T[] holder = (T[]) Array.newInstance(value.getClass(), 1);
        holder[0] = value;
        return holder;
    }

    public void createBooleanInteractionWidget(Interactor<Boolean> interactor) {
        Boolean[] state = createHolderForUseInLambda(interactor.initialValue);

        interactor.control = createButton(interactor, button -> {
            boolean oldValue = state[0];
            boolean newValue = !oldValue;
            state[0] = newValue;
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            interactor.updateVisuals.update(isValid, newValue);
        });

        interactor.updateVisuals = (isValid, newValue) -> {
            Widget control = interactor.control;
            control.func_238482_a_(new StringTextComponent(Boolean.toString(newValue)));
            if (!isValid)
                control.setFGColor(RED);
            else
                control.setFGColor(newValue ? GREEN : RED);
        };

        interactor.updateVisuals.update(true, interactor.initialValue);
    }

    public <T extends Number> void createNumericInteractionWidget(Interactor<T> interactor) {
        T value = interactor.initialValue;
        if (value instanceof Integer)
            createNumericInteractionWidget((Interactor<Integer>) interactor, Integer::parseInt, getLongestValueStringLength(Integer.MIN_VALUE));
        else if (value instanceof Long)
            createNumericInteractionWidget((Interactor<Long>) interactor, Long::parseLong, getLongestValueStringLength(Long.MIN_VALUE));
        else if (value instanceof Double)
            createNumericInteractionWidget((Interactor<Double>) interactor, Double::parseDouble, getLongestValueStringLength(Double.NEGATIVE_INFINITY));
    }

    public int getLongestValueStringLength(Number value) {
        return Objects.toString(value).length();
    }

    public <T extends Number> void createNumericInteractionWidget(Interactor<T> interactor, Function<String, T> parser, int longestValueStringLength) {
        TextFieldWidget textField = createTextField(interactor);
        interactor.control = textField;

        textField.setMaxStringLength(longestValueStringLength);
        textField.setText(interactor.initialValue.toString());
        textField.setResponder(newText -> {
            T newValue;
            boolean isValid = true;
            try {
                newValue = parser.apply(newText);
            } catch (NumberFormatException ignored) {
                newValue = null;
                isValid = false;
            }
            if (newValue != null)
                isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            interactor.updateVisuals.update(isValid, newValue);
        });

        interactor.updateVisuals = (isValid, newValue) -> {
            TextFieldWidget control = ((TextFieldWidget) interactor.control);
            control.setTextColor(isValid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };

        interactor.updateVisuals.update(true, interactor.initialValue);
    }

    public <T extends Enum<T>> void createEnumInteractionWidget(Interactor<T> interactor) {
        T[] state = createHolderForUseInLambda(interactor.initialValue);
        T[] potential = interactor.initialValue.getDeclaringClass().getEnumConstants();

        interactor.control = createButton(interactor, button -> {
            T oldValue = state[0];
            int oldIndex = ArrayUtils.indexOf(potential, oldValue);
            T newValue;
            if (oldIndex == ArrayUtils.INDEX_NOT_FOUND)
                newValue = (T) oldValue;
            else {
                int newIndex = (oldIndex + 1) % potential.length;
                newValue = potential[newIndex];
            }
            state[0] = newValue;
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            interactor.updateVisuals.update(isValid, newValue);
        });

        interactor.updateVisuals = (isValid, newValue) -> {
            Widget control = interactor.control;
            control.func_238482_a_(new StringTextComponent(newValue.toString()));
            if (!isValid)
                control.setFGColor(RED);
            else {
                int color = getRGBColorForEnum(newValue);
                if (color == Widget.UNSET_FG_COLOR)
                    control.clearFGColor();
                else
                    control.setFGColor(color);
            }
        };

        interactor.updateVisuals.update(true, interactor.initialValue);
    }

    public int getRGBColorForEnum(Enum<?> value) {
        if (value instanceof DyeColor)
            return ((DyeColor) value).getTextColor();
        if (value instanceof TextFormatting && ((TextFormatting) value).isColor())
            return ((TextFormatting) value).getColor();
        return Widget.UNSET_FG_COLOR;
    }

    public void createStringInteractionWidget(Interactor<String> interactor) {
        TextFieldWidget textField = createTextField(interactor);
        interactor.control = textField;

        textField.setMaxStringLength(Integer.MAX_VALUE);
        textField.setText(interactor.initialValue);
        textField.setResponder(newValue -> {
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            interactor.updateVisuals.update(isValid, newValue);
        });

        interactor.updateVisuals = (isValid, newValue) -> {
            TextFieldWidget control = ((TextFieldWidget) interactor.control);
            control.setTextColor(isValid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };

        interactor.updateVisuals.update(true, interactor.initialValue);
    }

    public <T extends List<?>> void createListInteractionWidget(Interactor<T> interactor) {
        T[] state = createHolderForUseInLambda(interactor.initialValue);

        interactor.label = null;
        interactor.control = createButton(interactor, button -> {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            // TODO: Move initialisation to method
            Screen screen = new ListConfigScreen(currentScreen, interactor.title, state[0]) {
                @Override
                public void onModified(List newValueIn) {
                    T newValue = (T) newValueIn;
                    state[0] = newValue;
                    boolean isValid = interactor.isValid.test(newValue);
                    if (isValid)
                        interactor.saveValue.accept(newValue);
                    interactor.updateVisuals.update(isValid, newValue);
                }
            };
            Minecraft.getInstance().displayGuiScreen(screen);
        });

        interactor.updateVisuals = (isValid, newValue) -> {
            Widget control = interactor.control;
            if (!isValid)
                control.setFGColor(RED);
            else
                control.clearFGColor();
        };

        interactor.updateVisuals.update(true, interactor.initialValue);
    }

    public static <T> T copyMutable(T o) {
        // Immutable objects can be returned as is
        if (o instanceof Boolean || o instanceof Number || o instanceof String || o instanceof Enum<?>)
            return o;
        if (o instanceof List<?>)
            return (T) new ArrayList<>((List<?>) o);
        if (o instanceof UnmodifiableConfig)
            return (T) ((AbstractConfig) o).clone();
        return o;
    }

    public Button createButton(Interactor<?> interactor, Button.IPressable iPressable) {
        return new ConfigElementButton(interactor.title, iPressable);
    }

    public TextFieldWidget createTextField(Interactor<?> interactor) {
        return new ConfigElementTextField(interactor.title);
    }

    static class DefaultHolder {
        static final ControlCreator INSTANCE = new ControlCreator();
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

}
