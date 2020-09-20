package net.minecraftforge.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListConfigScreen extends ConfigScreen {
    private List list;
    private final List<?> initialValue;
    private final List<?> defaultValue;

    public ListConfigScreen(Screen parentScreen, ITextComponent title, List<?> initialValue, List<?> defaultValue) {
        super(parentScreen, title, new StringTextComponent("List"));
        list = this.initialValue = initialValue;
        this.defaultValue = defaultValue;
    }

    @Override
    protected void reset() {
        list = ConfigElementControls.copyMutable(defaultValue);
    }

    @Override
    protected void undo() {
        list = ConfigElementControls.copyMutable(defaultValue);
    }

    @Override
    protected boolean canReset() {
        return !Objects.equals(list, defaultValue);
    }

    @Override
    protected boolean canUndo() {
        return !Objects.equals(list, initialValue);
    }

    @Override
    protected ConfigElementList makeConfigElementList() {
        return new ListConfigElementList(this, field_230706_i_);
    }

    public class ListConfigElementList extends ConfigElementList {

        public ListConfigElementList(ConfigScreen configScreen, Minecraft mcIn) {
            super(configScreen, mcIn);
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                ConfigElement element = createWidget(i, item);
                if (element != null)
                    func_230513_b_(element);
                else
                    func_230513_b_(new TitledConfigElement(Objects.toString(item), "Unknown object "));
            }
        }

        class ListItemConfigElement extends ConfigElement {

            public ListItemConfigElement(int index, Object item) {
                super(item.getClass().getSimpleName() + " list item", "");
                Button addBelowButton = new ExtendedButton(0, 0, 20, 20, new StringTextComponent("+"), b -> {
                    list.add(index + 1, ConfigElementControls.copyMutable(list.get(index)));
                    onAddRemove(index + 1);
                });
                addBelowButton.setFGColor(ConfigElementControls.GREEN);
                Button removeButton = new ExtendedButton(0, 0, 20, 20, new StringTextComponent("-"), b -> {
                    list.remove(index);
                    onAddRemove(index);
                });
                removeButton.setFGColor(ConfigElementControls.RED);
                widgets.add(addBelowButton);
                widgets.add(removeButton);
            }
        }

        @Nullable
        private ConfigElement createWidget(int index, Object item) {
            if (item instanceof Boolean)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementControls.ConfigElementWidgetData<Boolean> control = ConfigElementControls.createBooleanButton(newValue -> {
                            set(index, newValue);
                            ListConfigScreen.this.onChange();
                            return true; // Can't have an "invalid" boolean
                        }, translatedName);
                        control.valueSetter.setup((Boolean) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Integer)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementControls.ConfigElementWidgetData<Integer> control = ConfigElementControls.createNumericTextField(newValue -> {
                            set(index, newValue);
                            ListConfigScreen.this.onChange();
                            return true;
                        }, translatedName, Integer::parseInt, Integer.MIN_VALUE);
                        control.valueSetter.setup((Integer) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Long)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementControls.ConfigElementWidgetData<Long> control = ConfigElementControls.createNumericTextField(newValue -> {
                            set(index, newValue);
                            ListConfigScreen.this.onChange();
                            return true;
                        }, translatedName, Long::parseLong, Long.MIN_VALUE);
                        control.valueSetter.setup((Long) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Double)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementControls.ConfigElementWidgetData<Double> control = ConfigElementControls.createNumericTextField(newValue -> {
                            set(index, newValue);
                            ListConfigScreen.this.onChange();
                            return true;
                        }, translatedName, Double::parseDouble, Double.NEGATIVE_INFINITY);
                        control.valueSetter.setup((Double) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof Enum<?>)
                return new ListItemConfigElement(index, item) {
                    {
                        Enum[] potential = Arrays.stream(((Enum<?>) item).getDeclaringClass().getEnumConstants()).toArray(Enum<?>[]::new);
                        ConfigElementControls.ConfigElementWidgetData<Enum> control = ConfigElementControls.createEnumButton(newValue -> {
                            set(index, newValue);
                            ListConfigScreen.this.onChange();
                            return true;
                        }, translatedName, potential);
                        control.valueSetter.setup((Enum) item);
//                            canReset = () -> item !=
//                            reset = data.reset(valueInfo);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof String)
                return new ListItemConfigElement(index, item) {
                    {
                        ConfigElementControls.ConfigElementWidgetData<String> control = ConfigElementControls.createStringTextField(newValue -> {
                            set(index, newValue);
                            ListConfigScreen.this.onChange();
                            return true;
                        }, translatedName);
                        control.valueSetter.setup((String) item);
                        widgets.add(0, control.widget);
                    }
                };
            if (item instanceof List<?>)
                return new PopupConfigElement("Nested List", "Nested List", () -> new ListConfigScreen(ListConfigScreen.this, new StringTextComponent("Nested List"), (List<?>) item, (List<?>) item));
            return null;
        }

    }

    private void set(int index, Object newValue) {
        list.set(index, newValue);
    }

    private void onAddRemove(int index) {
        // Lazy way out
        // Whenever anything changes, recreate from scratch
        // Saves the headache of dealing with indexing of adds/removes
        field_230705_e_.remove(configElementList); // children.remove
        double scrollAmount = configElementList.func_230966_l_(); // getScrollAmount
        if (index == list.size() - 1)
            // If we're at the bottom, scroll even further because if it's an add
            // we want to scroll to the new bottom
            scrollAmount += this.configElementList.getItemHeight();
        configElementList = makeConfigElementList();
        configElementList.func_230932_a_(scrollAmount); // getScrollAmount
        field_230705_e_.add(configElementList); // children.add
    }

}
