/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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

import static net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import static net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

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

    public static final Interactor.DataKey<ConfigValue<?>> CONFIG_VALUE_KEY = new Interactor.DataKey<>();
    public static final Interactor.DataKey<ValueSpec> VALUE_SPEC_KEY = new Interactor.DataKey<>();
    public static final Interactor.DataKey<Range<?>> RANGE_KEY = new Interactor.DataKey<>();
    public static final Interactor.DataKey<Object> INITIAL_VALUE_KEY = new Interactor.DataKey<>();
    public static final Interactor.DataKey<Object> DEFAULT_VALUE_KEY = new Interactor.DataKey<>();

    /**
     * A Widget backed by a value of type T + some extra data & listeners for handling changes and displaying the widget.
     */
    public static class Interactor<T> {

        public final ITextComponent title;
        public final T initialValue;
        public ITextComponent label;
        public Widget control;
        private Predicate<T> validator = $ -> true;
        private Consumer<T> saver = $ -> {};
        private BiConsumer<Boolean, T> updateResponder = (a, b) -> {};
        private final Map<DataKey<?>, Object> extraData = new HashMap<>();

        public Interactor(ITextComponent title, T initialValue) {
            this.title = title;
            this.initialValue = Objects.requireNonNull(initialValue, "initialValue cannot be null");
            this.label = title;
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

        public <D> void addData(DataKey<D> key, @Nullable D value) {
            extraData.put(key, value);
        }

        @Nullable
        public <D> D getData(DataKey<D> key) {
            return (D) extraData.get(key);
        }

        /**
         * For making a system similar to a type safe map and the capabilities system.
         * Lets us store arbitrary extra data in a strongly typed manner.
         */
        static class DataKey<T> {
        }

    }

    public static ControlCreator getDefaultCreator() {
        return DefaultHolder.INSTANCE;
    }

    public <T> void createAndInitialiseInteractionWidget(Interactor<T> interactor) {
        createInteractionWidget(interactor);
        interactor.onUpdate(interactor.initialValue);
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
        else
            throw new IllegalStateException("Don't know how to make a widget for " + value);
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
    }

    @SuppressWarnings("unchecked")
    public <T extends Number> void createNumericInteractionWidget(Interactor<T> interactor) {
        T value = interactor.initialValue;
        Range<? extends Number> range = getRangeForSliderCreation(interactor);
        if (range != null) {
            if (value instanceof Integer)
                createSliderNumericInteractionWidget((Interactor<Integer>) interactor, range, d -> Math.round(Math.round(d)));
            else if (value instanceof Long)
                createSliderNumericInteractionWidget((Interactor<Long>) interactor, range, Math::round);
            else if (value instanceof Double)
                createSliderNumericInteractionWidget((Interactor<Double>) interactor, range, Function.identity());
            else
                throw new IllegalStateException("Don't know how to make a widget for Ranged Number " + value);
        } else {
            if (value instanceof Integer)
                createTextualNumericInteractionWidget((Interactor<Integer>) interactor, Integer::parseInt, getLongestValueStringLength(Integer.MIN_VALUE));
            else if (value instanceof Long)
                createTextualNumericInteractionWidget((Interactor<Long>) interactor, Long::parseLong, getLongestValueStringLength(Long.MIN_VALUE));
            else if (value instanceof Double)
                createTextualNumericInteractionWidget((Interactor<Double>) interactor, Double::parseDouble, getLongestValueStringLength(-Double.MAX_VALUE));
            else
                throw new IllegalStateException("Don't know how to make a widget for Non-ranged Number " + value);
        }
    }

    /**
     * @return null if a slider should not be created for the interactor.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Number, C extends Number & Comparable<? super C>> Range<C> getRangeForSliderCreation(Interactor<T> interactor) {
        Range<C> range = (Range<C>) interactor.getData(RANGE_KEY);
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

    public <T extends Number> void createSliderNumericInteractionWidget(Interactor<T> interactor, Range<? extends Number> range, Function<Double, T> converter) {
        Slider.ISlider responder = slider -> {
            Double sliderValue = slider.getValue();
            T newValue = converter.apply(sliderValue);
            boolean isValid = interactor.isValid(newValue);
            if (isValid)
                interactor.save(newValue);
            interactor.onUpdate(isValid, newValue);
        };

        interactor.control = createSlider(interactor, range, responder);

        interactor.addUpdateResponder((isValid, newValue) -> {
            Slider control = ((Slider) interactor.control);
            if (!Objects.equals(control.getValue(), newValue.doubleValue())) {
                control.setValue(newValue.doubleValue());
                control.updateSlider();
            }
        });
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
    }

    public <T extends Enum<T>> void createEnumInteractionWidget(Interactor<T> interactor) {
        T value = interactor.initialValue;
        T[] state = createHolderForUseInLambda(value);
        T[] potential = Arrays.stream(value.getDeclaringClass().getEnumConstants())
            .filter(interactor::isValid)
            .toArray(size -> (T[]) Array.newInstance(value.getClass(), size));

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
    }

    public <T extends List<?>> void createListInteractionWidget(Interactor<T> interactor) {
        T[] state = createHolderForUseInLambda(interactor.initialValue);
        T copiedInitial = copyMutable(interactor.initialValue);
        T defaultValue = (T) interactor.getData(DEFAULT_VALUE_KEY);
        T copiedDefault = defaultValue == null ? null : copyMutable(defaultValue);

        interactor.label = null;
        interactor.control = createButton(interactor, button -> {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            Screen screen = new ListConfigScreen(currentScreen, interactor.title, state[0]) {
                @Override
                public void onModified(List newValueIn) {
                    T newValue = (T) newValueIn;
                    state[0] = newValue;
                    boolean isValid = interactor.isValid(newValue);
                    if (isValid)
                        interactor.save(newValue);
                    interactor.onUpdate(isValid, newValue);
                }

                @Override
                protected void undo() {
                    T newValue = copyMutable(copiedInitial);
                    setupWithNewValue(newValue);
                }

                @Override
                protected boolean canUndo() {
                    return !Objects.equals(value, copiedInitial);
                }

                @Override
                protected void reset() {
                    if (copiedDefault == null)
                        return;
                    T newValue = copyMutable(copiedDefault);
                    setupWithNewValue(newValue);
                }

                @Override
                protected boolean canReset() {
                    return copiedDefault != null && !Objects.equals(value, copiedDefault);
                }

                private void setupWithNewValue(T newValue) {
                    this.value = newValue;
                    interactor.onUpdate(newValue);
                    // Completely recreate everything on this screen
                    // this.init(minecraft, scaledWidth, scaledHeight);
                    this.func_231158_b_(field_230706_i_, field_230708_k_, field_230709_l_);
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
                if (responderIn != null && !Objects.equals(newText, lastText))
                    responderIn.accept(newText);
                lastText = newText;
            });
        }
    }

}
