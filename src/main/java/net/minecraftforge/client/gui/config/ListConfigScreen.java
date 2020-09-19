package net.minecraftforge.client.gui.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListConfigScreen extends ConfigScreen {
    private final List list;

    public ListConfigScreen(Screen parentScreen, ITextComponent title, List<?> list) {
        super(parentScreen, title, new StringTextComponent("List"));
        this.list = list;
    }

    @Override
    protected ConfigElementList makeConfigElementList() {
        return new ConfigElementList(this, field_230706_i_) {
            {
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    ConfigElement element = createWidget(i, item);
                    if (element != null)
                        func_230513_b_(element);
                    else
                        func_230513_b_(new TitledConfigElement(Objects.toString(item), "Unknown object "));
                }
            }

            @Nullable
            private ConfigElement createWidget(int index, Object item) {
                if (item instanceof Boolean)
                    return new ConfigElement(item.getClass().getSimpleName() + " list item", "") {
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
                    return new ConfigElement(item.getClass().getSimpleName() + " list item", "") {
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
                    return new ConfigElement(item.getClass().getSimpleName() + " list item", "") {
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
                    return new ConfigElement(item.getClass().getSimpleName() + " list item", "") {
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
                    return new ConfigElement(item.getClass().getSimpleName() + " list item", "") {
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
                if (item instanceof String) {
                    return new ConfigElement(item.getClass().getSimpleName() + " list item", "") {
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
                }
                if (item instanceof List<?>) {
                    return new PopupConfigElement("Nested List", "Nested List", () -> new ListConfigScreen(ListConfigScreen.this, new StringTextComponent("Nested List"), (List<?>) item));
                }
                return null;
            }
        };
    }

    private void set(int index, Object newValue) {
        list.set(index, newValue);
    }

    private void onAddRemove() {
        // Lazy way out
        // Whenever anything changes, recreate from scratch
        // Saves the headache of dealing with indexing of adds/removes
        configElementList = makeConfigElementList();
    }

}
