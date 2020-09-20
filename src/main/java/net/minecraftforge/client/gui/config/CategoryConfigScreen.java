package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CategoryConfigScreen extends ConfigScreen {
    private final ConfigCategoryInfo categoryInfo;

    public CategoryConfigScreen(Screen parentScreen, ITextComponent title, ConfigCategoryInfo categoryInfo) {
        super(parentScreen, title);
        this.categoryInfo = categoryInfo;
    }

    @Override
    protected ConfigElementList makeConfigElementList() {
        return new CategoryConfigElementList(this, field_230706_i_, categoryInfo);
    }

    public interface ConfigCategoryInfo {

        Collection<String> elements();

        Object getValue(String key);

        Object getSpec(String key);

        /** Special case categories because reasons */
        String getCategoryComment(String key);

        static ConfigCategoryInfo of(Supplier<Collection<String>> elements, Function<String, Object> getValue, Function<String, Object> getSpec) {
            return of(elements, getValue, getSpec, $ -> null);
        }

        static ConfigCategoryInfo of(Supplier<Collection<String>> elements, Function<String, Object> getValue, Function<String, Object> getSpec, Function<String, String> getCategoryComment) {
            return new ConfigCategoryInfo() {

                @Override
                public Collection<String> elements() {
                    return elements.get();
                }

                @Override
                public Object getValue(String key) {
                    return getValue.apply(key);
                }

                @Override
                public Object getSpec(String key) {
                    return getSpec.apply(key);
                }

                @Override
                public String getCategoryComment(String key) {
                    return getCategoryComment.apply(key);
                }
            };
        }
    }

    public static class CategoryConfigElementList extends ConfigElementList {

        public CategoryConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
            super(configScreen, mcIn);
            for (String key : info.elements())
                func_230513_b_(createConfigElement(key, info));
        }

        protected ConfigElement createConfigElement(String key, ConfigCategoryInfo categoryInfo) {
            Object raw = categoryInfo.getValue(key);
            if (raw instanceof UnmodifiableConfig) {
                UnmodifiableConfig value = (UnmodifiableConfig) raw;
                UnmodifiableCommentedConfig valueInfo = (UnmodifiableCommentedConfig) categoryInfo.getSpec(key);
                String description = categoryInfo.getCategoryComment(key);
                ConfigCategoryInfo valueCategoryInfo = ConfigCategoryInfo.of(
                        () -> value.valueMap().keySet(),
                        value::get,
                        valueInfo::get,
                        valueInfo::getComment
                );
                return new CategoryConfigElement(configScreen, key, description == null ? "" : description, valueCategoryInfo);
            } else {
                ForgeConfigSpec.ConfigValue<?> value = (ForgeConfigSpec.ConfigValue<?>) raw;
                ForgeConfigSpec.ValueSpec valueInfo = (ForgeConfigSpec.ValueSpec) categoryInfo.getSpec(key);
                String name = new TranslationTextComponent(valueInfo.getTranslationKey()).getString();
                Object valueValue = value.get();
                ConfigElement element = createValueConfigElement(value, valueInfo, name, valueValue);
                if (element != null)
                    return element;
                return new TitledConfigElement(name + ": " + valueValue, valueInfo.getComment());
            }
        }

        @Nullable
        private ConfigElement createValueConfigElement(ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec valueInfo, String translatedName, Object valueValue) {
            String description = valueInfo.getComment();
            if (valueInfo.needsWorldRestart())
                description += "\n" + TextFormatting.RED + "[" + new TranslationTextComponent("forge.configgui.needsWorldRestart").getString() + "]";
            if (valueValue instanceof Boolean)
                return new ConfigValueConfigElement(
                        translatedName, description, (ForgeConfigSpec.BooleanValue) value, valueInfo,
                        createBoolean((ForgeConfigSpec.BooleanValue) value, translatedName, (Boolean) valueValue),
                        (Boolean) valueValue
                );
            if (valueValue instanceof Integer)
                return new ConfigValueConfigElement(
                        translatedName, description, (ForgeConfigSpec.IntValue) value, valueInfo,
                        createNumericRanged((ForgeConfigSpec.IntValue) value, valueInfo, translatedName, (Integer) valueValue, Integer::parseInt, Integer.MIN_VALUE),
                        (Integer) valueValue
                );
            if (valueValue instanceof Long)
                return new ConfigValueConfigElement(
                        translatedName, description, (ForgeConfigSpec.LongValue) value, valueInfo,
                        createNumericRanged((ForgeConfigSpec.LongValue) value, valueInfo, translatedName, (Long) valueValue, Long::parseLong, Long.MIN_VALUE),
                        (Long) valueValue
                );
            if (valueValue instanceof Double)
                return new ConfigValueConfigElement(
                        translatedName, description, (ForgeConfigSpec.DoubleValue) value, valueInfo,
                        createNumericRanged((ForgeConfigSpec.DoubleValue) value, valueInfo, translatedName, (Double) valueValue, Double::parseDouble, Double.NEGATIVE_INFINITY),
                        (Double) valueValue
                );
            if (valueValue instanceof Enum<?>)
                return new ConfigValueConfigElement(
                        translatedName, description, (ForgeConfigSpec.EnumValue) value, valueInfo,
                        createEnum((ForgeConfigSpec.EnumValue) value, valueInfo, translatedName, (Enum) valueValue),
                        (Enum) valueValue
                );
            if (valueValue instanceof String)
                return new ConfigValueConfigElement(
                        translatedName, description, (ForgeConfigSpec.ConfigValue<String>) value, valueInfo,
                        createString((ForgeConfigSpec.ConfigValue<String>) value, valueInfo, translatedName, (String) valueValue),
                        (String) valueValue
                );
            if (valueValue instanceof List<?>)
                return new PopupConfigElement(translatedName, description, () -> new ListConfigScreen(configScreen, new StringTextComponent(translatedName), (List<?>) valueValue, (List<?>) valueInfo.getDefault()));
            return null;
        }

        static class ValueConfigElementData<T> {
            final Widget widget;
            final Consumer<T> valueSetter;

            ValueConfigElementData(Widget widget, Consumer<T> valueSetter) {
                this.widget = widget;
                this.valueSetter = valueSetter;
            }

        }

        public ValueConfigElementData<Boolean> createBoolean(ForgeConfigSpec.BooleanValue value, String translatedName, Boolean valueValue) {
            ConfigElementControls.ConfigElementWidgetData<Boolean> control = ConfigElementControls.createBooleanButton(newValue -> {
                value.set(newValue);
                configScreen.onChange();
                return true; // Can't have an "invalid" boolean
            }, translatedName);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        public <T extends Comparable<? super T>> ValueConfigElementData<T> createNumericRanged(ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec valueInfo, String translatedName, T valueValue, Function<String, T> parser, T longestValue) {
            @Nullable
            ForgeConfigSpec.Range<T> range = valueInfo.getRange();
            ConfigElementControls.ConfigElementWidgetData<T> control = ConfigElementControls.createNumericTextField(newValue -> {
                if (range != null && !range.test(newValue))
                    return false;
                value.set(newValue);
                configScreen.onChange();
                return true;
            }, translatedName, parser, longestValue);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        private <T extends Enum<T>> ValueConfigElementData<T> createEnum(ForgeConfigSpec.EnumValue<T> value, ForgeConfigSpec.ValueSpec valueInfo, String translatedName, T valueValue) {
            Enum<?>[] potential = Arrays.stream(((Enum<?>) valueValue).getDeclaringClass().getEnumConstants()).filter(valueInfo::test).toArray(Enum<?>[]::new);
            ConfigElementControls.ConfigElementWidgetData<T> control = ConfigElementControls.createEnumButton(newValue -> {
                value.set(newValue);
                configScreen.onChange();
                return true; // Can't have an "invalid" boolean
            }, translatedName, (T[]) potential);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        private ValueConfigElementData<String> createString(ForgeConfigSpec.ConfigValue<String> value, ForgeConfigSpec.ValueSpec valueInfo, String translatedName, String valueValue) {
            ConfigElementControls.ConfigElementWidgetData<String> control = ConfigElementControls.createStringTextField(newValue -> {
                if (!valueInfo.test(newValue))
                    return false;
                value.set(newValue);
                configScreen.onChange();
                return true;
            }, translatedName);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        public class CategoryConfigElement extends PopupConfigElement {
            public CategoryConfigElement(ConfigScreen parentScreen, String translatedName, String description, ConfigCategoryInfo valueCategoryInfo) {
                super(translatedName, description, () -> new CategoryConfigScreen(parentScreen, new StringTextComponent(translatedName), valueCategoryInfo));
            }
        }

        private class ConfigValueConfigElement extends TitledConfigElement {

            private final BooleanSupplier canReset;
            private final BooleanSupplier canUndo;

            public <T> ConfigValueConfigElement(String translatedName, String description, ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec valueInfo, ValueConfigElementData<T> data, T obj) {
                super(translatedName, description);
                widgets.add(0, data.widget);
                T _default = ConfigElementControls.copyMutable((T) valueInfo.getDefault());
                T initial = ConfigElementControls.copyMutable(obj);
                canReset = () -> !value.get().equals(_default);
                canUndo = () -> !value.get().equals(initial);
                widgets.add(resetButton = createConfigElementResetButton(translatedName, button -> data.valueSetter.accept(_default)));
                widgets.add(undoButton = createConfigElementUndoButton(translatedName, button -> data.valueSetter.accept(initial)));
            }

            @Override
            public void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int adjustedItemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                // getRowWidth, Button#getWidth(), Button#getWidth()
                int controlWidth = func_230949_c_() - getMaxListLabelWidth() - resetButton.func_230998_h_() - undoButton.func_230998_h_();
                widgets.getFirst().func_230991_b_(controlWidth); // setWidth
                super.func_230432_a_(matrixStack, index, rowTop, rowLeft, rowWidth, adjustedItemHeight, mouseX, mouseY, isSelected, partialTicks);
            }

            @Override
            public void tick() {
                super.tick();
                // active/enabled
                resetButton.field_230693_o_ = canReset.getAsBoolean();
                undoButton.field_230693_o_ = canUndo.getAsBoolean();
            }

            @Override
            public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                for (Widget widget : widgets)
                    if (mouseX >= widget.field_230690_l_ && mouseY >= widget.field_230691_m_ && mouseX < widget.field_230690_l_ + widget.func_230998_h_() && mouseY < widget.field_230691_m_ + widget.func_238483_d_()) {
                        if (widget == resetButton) {
                            List<ITextProperties> list = Collections.singletonList(new TranslationTextComponent("resets stuff"));
                            GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, getWidth(), getHeight(), -1, configScreen.getFontRenderer());
                        } else if (widget == undoButton) {
                            List<ITextProperties> list = Collections.singletonList(new TranslationTextComponent("undoes stuff"));
                            GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, getWidth(), getHeight(), -1, configScreen.getFontRenderer());
                        }
                        // Don't render the element's tooltip
                        return;
                    }
                super.renderTooltip(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

}
