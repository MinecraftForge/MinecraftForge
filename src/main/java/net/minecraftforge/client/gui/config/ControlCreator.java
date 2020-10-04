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
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
        /** Set this AFTER initialisation */
        public Predicate<T> isValid = $ -> true;
        /** Set this AFTER initialisation */
        public Consumer<T> saveValue = $ -> {};
        /** Set this AFTER initialisation */
        public VisualsUpdater<T> visualsUpdater = (a, b) -> {};

        Interactor(ITextComponent title, T initialValue) {
            this.title = title;
            this.initialValue = Objects.requireNonNull(initialValue, "initialValue cannot be null");
            this.label = title;
        }

        @FunctionalInterface
        interface VisualsUpdater<T> {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
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

    @SuppressWarnings("unchecked")
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
            interactor.visualsUpdater.update(isValid, newValue);
        });

        interactor.visualsUpdater = (isValid, newValue) -> {
            Widget control = interactor.control;
            control.func_238482_a_(new StringTextComponent(Boolean.toString(newValue)));
            if (!isValid)
                control.setFGColor(RED);
            else
                control.setFGColor(newValue ? GREEN : RED);
        };

        interactor.visualsUpdater.update(true, interactor.initialValue);
    }

    @SuppressWarnings("unchecked")
    public <T extends Number> void createNumericInteractionWidget(Interactor<T> interactor) {
        T value = interactor.initialValue;
        Range<? extends Number> range = getRangeForSliderCreation(interactor);
        if (range != null) {
            if (value instanceof Integer)
                createSliderNumericInteractionWidget((Interactor<Integer>) interactor, range, Double::intValue);
            else if (value instanceof Long)
                createSliderNumericInteractionWidget((Interactor<Long>) interactor, range, Double::longValue);
            else if (value instanceof Double)
                createSliderNumericInteractionWidget((Interactor<Double>) interactor, range, Function.identity());
        } else {
            if (value instanceof Integer)
                createTextualNumericInteractionWidget((Interactor<Integer>) interactor, Integer::parseInt, getLongestValueStringLength(Integer.MIN_VALUE));
            else if (value instanceof Long)
                createTextualNumericInteractionWidget((Interactor<Long>) interactor, Long::parseLong, getLongestValueStringLength(Long.MIN_VALUE));
            else if (value instanceof Double)
                createTextualNumericInteractionWidget((Interactor<Double>) interactor, Double::parseDouble, getLongestValueStringLength(Double.NEGATIVE_INFINITY));
        }
    }

    /**
     * @return null if a slider should not be created for the interactor.
     */
    @Nullable
    public <T extends Number, C extends Number & Comparable<? super C>> Range<C> getRangeForSliderCreation(Interactor<T> interactor) {
        if (!(interactor instanceof ConfigValueInteractor))
            return null;
        ConfigValueInteractor<T> configValueInteractor = (ConfigValueInteractor<T>) interactor;
        ValueSpec configSpec = configValueInteractor.getConfigSpec();
        Range<? extends Number> range = configSpec.getRange();
        if (range == null)
            return null;
        double min = range.getMin().doubleValue();
        double max = range.getMax().doubleValue();
        if (max - min > 10)
            return null;
        return (Range<C>) range;
    }

    public int getLongestValueStringLength(Number value) {
        return Objects.toString(value).length();
    }

    public <T extends Number> void createSliderNumericInteractionWidget(Interactor<T> interactor, Range<? extends Number> range, Function<Double, T> converter) {
        Slider.ISlider responder = slider -> {
            Double sliderValue = slider.getValue();
            T newValue = converter.apply(sliderValue);
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            interactor.visualsUpdater.update(isValid, newValue);
        };

        interactor.control = createSlider(interactor, range, responder);

        interactor.visualsUpdater = (isValid, newValue) -> {
            Slider control = ((Slider) interactor.control);
            control.setValue(newValue.doubleValue());
        };

        interactor.visualsUpdater.update(true, interactor.initialValue);
    }

    public <T extends Number> void createTextualNumericInteractionWidget(Interactor<T> interactor, Function<String, T> parser, int longestValueStringLength) {
        TextFieldWidget textField = createTextField(interactor);
        interactor.control = textField;

        textField.setMaxStringLength(longestValueStringLength);
        String[] currentlyRespondingTo = {null};
        textField.setResponder(newText -> {
            if (Objects.equals(currentlyRespondingTo[0], newText))
                // We are being called from inside visualsUpdater, don't recurse & stackoverflow
                return;
            T newValue;
            try {
                newValue = parser.apply(newText);
            } catch (NumberFormatException ignored) {
                ((TextFieldWidget) interactor.control).setTextColor(RED);
                return;
            }
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            currentlyRespondingTo[0] = newText;
            interactor.visualsUpdater.update(isValid, newValue);
            currentlyRespondingTo[0] = null;
        });

        interactor.visualsUpdater = (isValid, newValue) -> {
            TextFieldWidget control = ((TextFieldWidget) interactor.control);
            if (!Objects.equals(textField.getText(), newValue.toString()))
                textField.setText(newValue.toString());
            control.setTextColor(isValid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };

        interactor.visualsUpdater.update(true, interactor.initialValue);
    }

    public <T extends Enum<T>> void createEnumInteractionWidget(Interactor<T> interactor) {
        T[] state = createHolderForUseInLambda(interactor.initialValue);
        T[] potential = interactor.initialValue.getDeclaringClass().getEnumConstants();

        interactor.control = createButton(interactor, button -> {
            T oldValue = state[0];
            int oldIndex = ArrayUtils.indexOf(potential, oldValue);
            T newValue;
            if (oldIndex == ArrayUtils.INDEX_NOT_FOUND)
                newValue = oldValue;
            else {
                int newIndex = (oldIndex + 1) % potential.length;
                newValue = potential[newIndex];
            }
            state[0] = newValue;
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            interactor.visualsUpdater.update(isValid, newValue);
        });

        interactor.visualsUpdater = (isValid, newValue) -> {
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

        interactor.visualsUpdater.update(true, interactor.initialValue);
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
        String[] currentlyRespondingTo = {null};
        textField.setResponder(newValue -> {
            if (Objects.equals(currentlyRespondingTo[0], newValue))
                // We are being called from inside visualsUpdater, don't recurse & stackoverflow
                return;
            boolean isValid = interactor.isValid.test(newValue);
            if (isValid)
                interactor.saveValue.accept(newValue);
            currentlyRespondingTo[0] = newValue;
            interactor.visualsUpdater.update(isValid, newValue);
            currentlyRespondingTo[0] = null;
        });

        interactor.visualsUpdater = (isValid, newValue) -> {
            TextFieldWidget control = ((TextFieldWidget) interactor.control);
            if (!Objects.equals(textField.getText(), newValue))
                textField.setText(newValue);
            control.setTextColor(isValid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        };

        interactor.visualsUpdater.update(true, interactor.initialValue);
    }

    public <T extends List<?>> void createListInteractionWidget(Interactor<T> interactor) {
        T[] state = createHolderForUseInLambda(interactor.initialValue);

        interactor.label = null;
        interactor.control = createButton(interactor, button -> {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            Screen screen = new ListConfigScreen(currentScreen, interactor.title, state[0]) {
                @Override
                public void onModified(List newValueIn) {
                    T newValue = (T) newValueIn;
                    state[0] = newValue;
                    boolean isValid = interactor.isValid.test(newValue);
                    if (isValid)
                        interactor.saveValue.accept(newValue);
                    interactor.visualsUpdater.update(isValid, newValue);
                }
            };
            Minecraft.getInstance().displayGuiScreen(screen);
        });

        interactor.visualsUpdater = (isValid, newValue) -> {
            state[0] = newValue;
            Widget control = interactor.control;
            if (!isValid)
                control.setFGColor(RED);
            else
                control.clearFGColor();
        };

        interactor.visualsUpdater.update(true, interactor.initialValue);
    }

    @SuppressWarnings("unchecked")
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

    public <T extends Number> Slider createSlider(Interactor<T> interactor, Range<? extends Number> range, Slider.ISlider iSlider) {
        T value = interactor.initialValue;
        double min = range.getMin().doubleValue();
        double max = range.getMax().doubleValue();
        boolean showDecimals = value instanceof Double || value instanceof Float;
        // I don't think a slider should be this hard to set up properly, maybe it could do with a refactor?
        StringTextComponent empty = new StringTextComponent("");
        Slider slider = new Slider(0, 0, 0, 0, interactor.title, empty, min, max, value.doubleValue(), showDecimals, true, s -> {});
        if (showDecimals && slider.precision < 2)
            slider.precision = 2;
        slider.dispString = empty;
        slider.updateSlider();
        slider.parent = iSlider;
        return slider;
    }

    static class DefaultHolder {
        static final ControlCreator INSTANCE = new ControlCreator();
    }

    public Button createPopupButton(ITextComponent title, Supplier<? extends Screen> screenSupplier) {
        return new ConfigElementButton(title, b -> Minecraft.getInstance().displayGuiScreen(screenSupplier.get()));
    }

    public static class ConfigElementButton extends ExtendedButton {
        public ConfigElementButton(ITextComponent title, IPressable handler) {
            super(0, 0, 0, 0, title, handler);
        }
    }

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
                if (responderIn != null && lastText != null && !Objects.equals(newText, lastText))
                    responderIn.accept(newText);
                lastText = newText;
            });
        }
    }

}
