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
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;

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

    /**
     * Info about how to create a widget for a value.
     */
    static class InteractorSpec<T> {

        public final ITextComponent title;
        public final T initialValue;
        private final Map<Class<?>, Object> extraData = new HashMap<>();

        public InteractorSpec(ITextComponent title, T initialValue) {
            this.title = title;
            this.initialValue = Objects.requireNonNull(initialValue, "initialValue cannot be null");
        }

        public <D> void addData(Class<D> key, @Nullable D value) {
            extraData.put(key, value);
        }

        @Nullable
        public <D> D getData(Class<D> key) {
            return (D) extraData.get(key);
        }
    }

    static class Interactor<T> {

        public final ITextComponent title;
        public final T initialValue;
        public ITextComponent label;
        public Widget control;
        private Predicate<T> validator = $ -> true;
        private Consumer<T> saver = $ -> {};
        private BiConsumer<Boolean, T> updateResponder = (a, b) -> {};

        Interactor(InteractorSpec<T> spec) {
            this.title = spec.title;
            this.initialValue = spec.initialValue;
            this.label = spec.title;
        }

        public void addValidator(Predicate<T> newValidator) {
            validator = validator.and(newValidator);
        }

        public boolean isValid(T newValue) {
            return validator.test(newValue);
        }

        public void addSaver(Consumer<T> newSaver) {
            saver = saver.andThen(newSaver);
        }

        /**
         * Saves a valid new value.
         */
        public void save(T newValue) {
            saver.accept(newValue);
        }

        public void addUpdateResponder(BiConsumer<Boolean, T> newUpdateResponder) {
            updateResponder = updateResponder.andThen(newUpdateResponder);
        }

        public void onUpdate(T newValue) {
            onUpdate(isValid(newValue), newValue);
        }

        /**
         * Responds to value changes.
         * Called to set up the Interactor after initialisation.
         * Called when the user interacts with the Widget directly and changes the value.
         * Called when the user clicks the undo/reset buttons.
         */
        public void onUpdate(boolean isValid, T newValue) {
            updateResponder.accept(isValid, newValue);
        }

    }

    public static ControlCreator getDefaultCreator() {
        return DefaultHolder.INSTANCE;
    }

    public <T> Interactor<T> createAndInitialiseInteractionWidget(InteractorSpec<T> spec) {
        Interactor<T> interactor = createInteractionWidget(spec);
        T value = spec.initialValue;
        interactor.onUpdate(value);
        return interactor;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Interactor<T> createInteractionWidget(InteractorSpec<T> spec) {
        T value = spec.initialValue;
        if (value instanceof Boolean)
            return (Interactor<T>) createBooleanInteractionWidget((InteractorSpec<Boolean>) spec);
        else if (value instanceof Number)
            return (Interactor<T>) createNumericInteractionWidget((InteractorSpec<? extends Number>) spec);
        else if (value instanceof Enum<?>)
            return (Interactor<T>) createEnumInteractionWidget((InteractorSpec<Enum>) spec);
        else if (value instanceof String)
            return (Interactor<T>) createStringInteractionWidget((InteractorSpec<String>) spec);
        else if (value instanceof List<?>)
            return (Interactor<T>) createListInteractionWidget((InteractorSpec<List<?>>) spec);
        throw new IllegalStateException("Don't know how to make a widget for " + value);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] createHolderForUseInLambda(T value) {
        T[] holder = (T[]) Array.newInstance(value.getClass(), 1);
        holder[0] = value;
        return holder;
    }

    public Interactor<Boolean> createBooleanInteractionWidget(InteractorSpec<Boolean> spec) {
        Interactor<Boolean> interactor = new Interactor<>(spec);

        Boolean[] state = createHolderForUseInLambda(spec.initialValue);

        interactor.control = createButton(spec, button -> {
            boolean oldValue = state[0];
            boolean newValue = !oldValue;
            state[0] = newValue;
            boolean isValid = interactor.isValid(newValue);
            if (isValid)
                interactor.save(newValue);
            interactor.onUpdate(isValid, newValue);
        });

        interactor.addUpdateResponder((isValid, newValue) -> {
            Widget control = interactor.control;
            control.func_238482_a_(new StringTextComponent(Boolean.toString(newValue)));
            if (!isValid)
                control.setFGColor(RED);
            else
                control.setFGColor(newValue ? GREEN : RED);
        });

        return interactor;
    }

    @SuppressWarnings("unchecked")
    public <T extends Number> Interactor<T> createNumericInteractionWidget(InteractorSpec<T> spec) {
        T value = spec.initialValue;
        Range<? extends Number> range = getRangeForSliderCreation(spec);
        if (range != null) {
            if (value instanceof Integer)
                return (Interactor<T>) createSliderNumericInteractionWidget((InteractorSpec<Integer>) spec, range, Double::intValue);
            else if (value instanceof Long)
                return (Interactor<T>) createSliderNumericInteractionWidget((InteractorSpec<Long>) spec, range, Double::longValue);
            else if (value instanceof Double)
                return (Interactor<T>) createSliderNumericInteractionWidget((InteractorSpec<Double>) spec, range, Function.identity());
        } else {
            if (value instanceof Integer)
                return (Interactor<T>) createTextualNumericInteractionWidget((InteractorSpec<Integer>) spec, Integer::parseInt, getLongestValueStringLength(Integer.MIN_VALUE));
            else if (value instanceof Long)
                return (Interactor<T>) createTextualNumericInteractionWidget((InteractorSpec<Long>) spec, Long::parseLong, getLongestValueStringLength(Long.MIN_VALUE));
            else if (value instanceof Double)
                return (Interactor<T>) createTextualNumericInteractionWidget((InteractorSpec<Double>) spec, Double::parseDouble, getLongestValueStringLength(Double.NEGATIVE_INFINITY));
        }
        throw new IllegalStateException("Don't know how to make a widget for Number " + value);
    }

    /**
     * @return null if a slider should not be created for the interactor.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Number, C extends Number & Comparable<? super C>> Range<C> getRangeForSliderCreation(InteractorSpec<T> spec) {
        Range<C> range = spec.getData(Range.class);
        if (range == null)
            return null;
        double min = range.getMin().doubleValue();
        double max = range.getMax().doubleValue();
        if (max - min > 10)
            return null;
        return range;
    }

    public int getLongestValueStringLength(Number value) {
        return Objects.toString(value).length();
    }

    public <T extends Number> Interactor<T> createSliderNumericInteractionWidget(InteractorSpec<T> spec, Range<? extends Number> range, Function<Double, T> converter) {
        Interactor<T> interactor = new Interactor<>(spec);

        Slider.ISlider responder = slider -> {
            Double sliderValue = slider.getValue();
            T newValue = converter.apply(sliderValue);
            boolean isValid = interactor.isValid(newValue);
            if (isValid)
                interactor.save(newValue);
            interactor.onUpdate(isValid, newValue);
        };

        interactor.control = createSlider(spec, range, responder);

        interactor.addUpdateResponder((isValid, newValue) -> {
            Slider control = ((Slider) interactor.control);
            control.setValue(newValue.doubleValue());
        });

        return interactor;
    }

    public <T extends Number> Interactor<T> createTextualNumericInteractionWidget(InteractorSpec<T> spec, Function<String, T> parser, int longestValueStringLength) {
        Interactor<T> interactor = new Interactor<>(spec);

        TextFieldWidget textField = createTextField(spec);
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
            boolean isValid = interactor.isValid(newValue);
            if (isValid)
                interactor.save(newValue);
            currentlyRespondingTo[0] = newText;
            interactor.onUpdate(isValid, newValue);
            currentlyRespondingTo[0] = null;
        });

        interactor.addUpdateResponder((isValid, newValue) -> {
            TextFieldWidget control = ((TextFieldWidget) interactor.control);
            if (!Objects.equals(textField.getText(), newValue.toString()))
                textField.setText(newValue.toString());
            control.setTextColor(isValid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        });

        return interactor;
    }

    public <T extends Enum<T>> Interactor<T> createEnumInteractionWidget(InteractorSpec<T> spec) {
        Interactor<T> interactor = new Interactor<>(spec);

        T[] state = createHolderForUseInLambda(spec.initialValue);
        T[] potential = spec.initialValue.getDeclaringClass().getEnumConstants();

        interactor.control = createButton(spec, button -> {
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
            boolean isValid = interactor.isValid(newValue);
            if (isValid)
                interactor.save(newValue);
            interactor.onUpdate(isValid, newValue);
        });

        interactor.addUpdateResponder((isValid, newValue) -> {
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
        });

        return interactor;
    }

    public int getRGBColorForEnum(Enum<?> value) {
        if (value instanceof DyeColor)
            return ((DyeColor) value).getTextColor();
        if (value instanceof TextFormatting && ((TextFormatting) value).isColor())
            return ((TextFormatting) value).getColor();
        return Widget.UNSET_FG_COLOR;
    }

    public Interactor<String> createStringInteractionWidget(InteractorSpec<String> spec) {
        Interactor<String> interactor = new Interactor<>(spec);

        TextFieldWidget textField = createTextField(spec);
        interactor.control = textField;

        textField.setMaxStringLength(Integer.MAX_VALUE);
        String[] currentlyRespondingTo = {null};
        textField.setResponder(newValue -> {
            if (Objects.equals(currentlyRespondingTo[0], newValue))
                // We are being called from inside visualsUpdater, don't recurse & stackoverflow
                return;
            boolean isValid = interactor.isValid(newValue);
            if (isValid)
                interactor.save(newValue);
            currentlyRespondingTo[0] = newValue;
            interactor.onUpdate(isValid, newValue);
            currentlyRespondingTo[0] = null;
        });

        interactor.addUpdateResponder((isValid, newValue) -> {
            TextFieldWidget control = ((TextFieldWidget) interactor.control);
            if (!Objects.equals(textField.getText(), newValue))
                textField.setText(newValue);
            control.setTextColor(isValid ? TEXT_FIELD_ACTIVE_COLOR : RED);
        });

        return interactor;
    }

    public <T extends List<?>> Interactor<T> createListInteractionWidget(InteractorSpec<T> spec) {
        Interactor<T> interactor = new Interactor<>(spec);

        T[] state = createHolderForUseInLambda(spec.initialValue);

        interactor.label = null;
        interactor.control = createButton(spec, button -> {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            Screen screen = new ListConfigScreen(currentScreen, spec.title, state[0]) {
                @Override
                public void onModified(List newValueIn) {
                    T newValue = (T) newValueIn;
                    state[0] = newValue;
                    boolean isValid = interactor.isValid(newValue);
                    if (isValid)
                        interactor.save(newValue);
                    interactor.onUpdate(isValid, newValue);
                }
            };
            Minecraft.getInstance().displayGuiScreen(screen);
        });

        interactor.addUpdateResponder((isValid, newValue) -> {
            state[0] = newValue;
            Widget control = interactor.control;
            if (!isValid)
                control.setFGColor(RED);
            else
                control.clearFGColor();
        });

        return interactor;
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

    public Button createButton(InteractorSpec<?> interactor, Button.IPressable iPressable) {
        return new ConfigElementButton(interactor.title, iPressable);
    }

    public TextFieldWidget createTextField(InteractorSpec<?> interactor) {
        return new ConfigElementTextField(interactor.title);
    }

    public <T extends Number> Slider createSlider(InteractorSpec<T> interactor, Range<? extends Number> range, Slider.ISlider iSlider) {
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
