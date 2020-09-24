package net.minecraftforge.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.config.ConfigElementControls.ConfigElementWidgetData;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Popup {@link ConfigScreen} that displays and modifies a {@link List}.
 *
 * @author Cadiboo
 */
public abstract class ListConfigScreen extends ConfigScreen {
    protected final List<?> initialValue;
    protected final List<?> defaultValue;
    protected List actualValue;

    public ListConfigScreen(ConfigScreen configScreen, ITextComponent title, List<?> initialValue, List<?> defaultValue) {
        super(configScreen, title, new StringTextComponent("List"));
        actualValue = configScreen.getControlCreator().copyMutable(this.initialValue = initialValue);
        this.defaultValue = defaultValue;
    }

    @Override
    protected void undo() {
        actualValue = copyMutable(initialValue);
        onModified(actualValue);
        onAddRemove(0);
    }

    @Override
    protected void reset() {
        actualValue = copyMutable(defaultValue);
        onModified(actualValue);
        onAddRemove(0);
    }

    @Override
    protected boolean canUndo() {
        return !Objects.equals(actualValue, initialValue);
    }

    @Override
    protected boolean canReset() {
        return !Objects.equals(actualValue, defaultValue);
    }

    protected <T> T copyMutable(T value) {
        return ((ConfigScreen) parentScreen).getControlCreator().copyMutable(value);
    }

    public abstract void onModified(List newValue);

    @Override
    protected ConfigElementList makeConfigElementList() {
        return new ListConfigElementList(this, field_230706_i_);
    }

    protected void setAndNotify(int index, Object newValue) {
        actualValue.set(index, newValue);
        onModified(actualValue);
    }

    protected void onAddRemove(int index) {
        // TODO: Don't take the lazy way out
        // Lazy way out
        // Whenever anything changes, recreate from scratch
        // Saves the headache of dealing with indexing of adds/removes
        field_230705_e_.remove(configElementList); // children.remove
        double scrollAmount = configElementList.func_230966_l_(); // getScrollAmount
        if (index == actualValue.size() - 1)
            // If we're at the bottom, scroll even further because if it's an add
            // we want to scroll to the new bottom
            scrollAmount += this.configElementList.getItemHeight();
        configElementList = makeConfigElementList();
        configElementList.func_230932_a_(scrollAmount); // getScrollAmount
        field_230705_e_.add(configElementList); // children.add
    }

    public class ListConfigElementList extends ConfigElementList {

        public ListConfigElementList(ConfigScreen configScreen, Minecraft mcIn) {
            super(configScreen, mcIn);
            for (int i = 0; i < actualValue.size(); i++) {
                Object item = actualValue.get(i);
                ConfigElement element = createWidget(i, item);
                if (element != null)
                    func_230513_b_(element);
                else {
                    ITextComponent title = new StringTextComponent(i + " (" + (item == null ? "null" : item.getClass().getSimpleName()) + ")");
                    func_230513_b_(new ConfigElement(title, title, Collections.singletonList(new TranslationTextComponent("forge.configgui.tooltip.unsupportedTypeUseConfig"))));
                }
            }
        }

        @Nullable
        protected ConfigElement createWidget(int index, Object item) {
            // TODO: Move to ConfigElementControls
            if (item instanceof Boolean)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementWidgetData<Boolean> control = configScreen.getControlCreator().createBooleanButton(newValue -> {
                            setAndNotify(index, newValue);
                            return true; // Can't have an "invalid" boolean
                        }, title);
                        control.valueSetter.setup((Boolean) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Integer)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementWidgetData<Integer> control = configScreen.getControlCreator().createNumericTextField(newValue -> {
                            setAndNotify(index, newValue);
                            return true;
                        }, title, Integer::parseInt, Integer.MIN_VALUE);
                        control.valueSetter.setup((Integer) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Long)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementWidgetData<Long> control = configScreen.getControlCreator().createNumericTextField(newValue -> {
                            setAndNotify(index, newValue);
                            return true;
                        }, title, Long::parseLong, Long.MIN_VALUE);
                        control.valueSetter.setup((Long) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Double)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementWidgetData<Double> control = configScreen.getControlCreator().createNumericTextField(newValue -> {
                            setAndNotify(index, newValue);
                            return true;
                        }, title, Double::parseDouble, Double.NEGATIVE_INFINITY);
                        control.valueSetter.setup((Double) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Enum<?>)
                return new ListItemConfigElement(index, item) {
                    {
                        Enum[] potential = Arrays.stream(((Enum<?>) item).getDeclaringClass().getEnumConstants()).toArray(Enum<?>[]::new);
                        ConfigElementWidgetData<Enum> control = configScreen.getControlCreator().createEnumButton(newValue -> {
                            setAndNotify(index, newValue);
                            return true;
                        }, title, potential);
                        control.valueSetter.setup((Enum) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof String)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementWidgetData<String> control = configScreen.getControlCreator().createStringTextField(newValue -> {
                            setAndNotify(index, newValue);
                            return true;
                        }, title);
                        control.valueSetter.setup((String) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof List<?>)
                return new ListItemConfigElement(index, item) {
                    {
                        // TODO: "Nested List"?
                        this.widgets.add(0, configScreen.getControlCreator().makePopupButton(title, () -> new ListConfigScreen(ListConfigScreen.this, new StringTextComponent("Nested List"), (List<?>) item, (List<?>) item) {
                            @Override
                            public void onModified(List newValue) {
                                // Pass up to parent
                                ListConfigScreen.this.onModified(newValue);
                            }
                        }));
                    }
                };
            return null;
        }

        /**
         * Element for an item inside a List.
         * See {@link ConfigElement} for documentation.
         */
        class ListItemConfigElement extends ConfigElement {

            public ListItemConfigElement(int index, Object item) {
                super(new StringTextComponent(Integer.toString(index)), new StringTextComponent(Integer.toString(index)), Collections.emptyList());
                Button addBelowButton = new ExtendedButton(0, 0, 20, 20, new StringTextComponent("+"), b -> {
                    actualValue.add(index + 1, copyMutable(actualValue.get(index)));
                    onModified(actualValue);
                    onAddRemove(index + 1);
                });
                addBelowButton.setFGColor(ConfigElementControls.GREEN);
                Button removeButton = new ExtendedButton(0, 0, 20, 20, new StringTextComponent("-"), b -> {
                    actualValue.remove(index);
                    onModified(actualValue);
                    onAddRemove(index);
                });
                removeButton.setFGColor(ConfigElementControls.RED);
                widgets.add(addBelowButton);
                widgets.add(removeButton);
            }
        }

    }

}
