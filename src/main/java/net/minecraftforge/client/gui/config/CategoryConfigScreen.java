package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.common.ForgeConfigSpec.*;

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
                ConfigCategoryInfo valueCategoryInfo = ConfigCategoryInfo.of(
                        () -> value.valueMap().keySet(),
                        value::get,
                        valueInfo::get,
                        valueInfo::getComment
                );
                String translationKey = null; // TODO: Can pass path in through categoryInfo, need to also get review on changes to ForgeConfigSpec for comments
                ITextComponent title = configScreen.makeTranslationComponent(translationKey, key);
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
                // TODO: Comment can have linebreaks in it
                tooltip.add(configScreen.makeTranslationComponent(translationKey == null ? null : translationKey + ".tooltip", categoryInfo.getCategoryComment(key)).func_230531_f_().func_240701_a_(TextFormatting.YELLOW));
                return new ConfigElement(null, title, tooltip) {
                    {
                        this.widgets.add(0, configScreen.makePopupButton(title, () -> new CategoryConfigScreen(configScreen, title, valueCategoryInfo)));
                    }
                };
            } else {
                ForgeConfigSpec.ConfigValue<?> value = (ForgeConfigSpec.ConfigValue<?>) raw;
                ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
                String translationKey = valueInfo.getTranslationKey();
                ITextComponent title = configScreen.makeTranslationComponent(translationKey, key);
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
                // TODO: Comment can have linebreaks in it
                tooltip.add(configScreen.makeTranslationComponent(translationKey == null ? null : translationKey + ".tooltip", valueInfo.getComment()).func_230531_f_().func_240701_a_(TextFormatting.YELLOW));
                Range<?> range = valueInfo.getRange();
                if (range != null)
                    tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.rangeWithDefault", range.getMin(), range.getMax(), valueInfo.getDefault()).func_240701_a_(TextFormatting.AQUA));
                else
                    tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.default", valueInfo.getDefault()).func_240701_a_(TextFormatting.AQUA));
                if (valueInfo.needsWorldRestart())
                    tooltip.add(new StringTextComponent("[").func_240701_a_(TextFormatting.RED)
                            // appendSibling
                            .func_230529_a_(new TranslationTextComponent("forge.configgui.needsWorldRestart").func_240701_a_(TextFormatting.RED))
                            .func_230529_a_(new StringTextComponent("[").func_240701_a_(TextFormatting.RED))
                    );
                Object valueValue = value.get();
                ConfigElement element = createValueConfigElement(title, tooltip, value, valueInfo, valueValue);
                if (element != null)
                    return element;
                // title.deepCopy().appendSibling
                ITextComponent label = title.func_230532_e_().func_230529_a_(new StringTextComponent(": " + valueValue));
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.unsupportedTypeUseConfig"));
                return new ConfigElement(label, title, tooltip);
            }
        }

        @Nullable
        private ConfigElement createValueConfigElement(ITextComponent title, List<ITextComponent> tooltip, ForgeConfigSpec.ConfigValue<?> value, ValueSpec valueInfo, Object valueValue) {
            if (valueValue instanceof Boolean)
                return new ConfigValueConfigElement(
                        title, tooltip, (BooleanValue) value, valueInfo,
                        createBoolean((BooleanValue) value, title, (Boolean) valueValue),
                        (Boolean) valueValue
                );
            if (valueValue instanceof Integer)
                return new ConfigValueConfigElement(
                        title, tooltip, (IntValue) value, valueInfo,
                        createNumericRanged((IntValue) value, valueInfo, title, (Integer) valueValue, Integer::parseInt, Integer.MIN_VALUE),
                        (Integer) valueValue
                );
            if (valueValue instanceof Long)
                return new ConfigValueConfigElement(
                        title, tooltip, (LongValue) value, valueInfo,
                        createNumericRanged((LongValue) value, valueInfo, title, (Long) valueValue, Long::parseLong, Long.MIN_VALUE),
                        (Long) valueValue
                );
            if (valueValue instanceof Double)
                return new ConfigValueConfigElement(
                        title, tooltip, (DoubleValue) value, valueInfo,
                        createNumericRanged((DoubleValue) value, valueInfo, title, (Double) valueValue, Double::parseDouble, Double.NEGATIVE_INFINITY),
                        (Double) valueValue
                );
            if (valueValue instanceof Enum<?>)
                return new ConfigValueConfigElement(
                        title, tooltip, (EnumValue) value, valueInfo,
                        createEnum((EnumValue) value, valueInfo, title, (Enum) valueValue),
                        (Enum) valueValue
                );
            if (valueValue instanceof String)
                return new ConfigValueConfigElement(
                        title, tooltip, (ForgeConfigSpec.ConfigValue<String>) value, valueInfo,
                        createString((ForgeConfigSpec.ConfigValue<String>) value, valueInfo, title, (String) valueValue),
                        (String) valueValue
                );
            if (valueValue instanceof List<?>)
                return new ListConfigValueConfigElement(title, tooltip, (ForgeConfigSpec.ConfigValue<List>) value, valueInfo, (List) valueValue);
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

        public ValueConfigElementData<Boolean> createBoolean(BooleanValue value, ITextComponent title, Boolean valueValue) {
            ConfigElementControls.ConfigElementWidgetData<Boolean> control = ConfigElementControls.createBooleanButton(newValue -> {
                value.set(newValue);
                configScreen.onChange();
                return true; // Can't have an "invalid" boolean
            }, title);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        public <T extends Comparable<? super T>> ValueConfigElementData<T> createNumericRanged(ForgeConfigSpec.ConfigValue<T> value, ValueSpec valueInfo, ITextComponent title, T valueValue, Function<String, T> parser, T longestValue) {
            @Nullable
            ForgeConfigSpec.Range<T> range = valueInfo.getRange();
            ConfigElementControls.ConfigElementWidgetData<T> control = ConfigElementControls.createNumericTextField(newValue -> {
                if (range != null && !range.test(newValue))
                    return false;
                value.set(newValue);
                configScreen.onChange();
                return true;
            }, title, parser, longestValue);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        private <T extends Enum<T>> ValueConfigElementData<T> createEnum(ForgeConfigSpec.EnumValue<T> value, ValueSpec valueInfo, ITextComponent title, T valueValue) {
            Enum<?>[] potential = Arrays.stream(((Enum<?>) valueValue).getDeclaringClass().getEnumConstants()).filter(valueInfo::test).toArray(Enum<?>[]::new);
            ConfigElementControls.ConfigElementWidgetData<T> control = ConfigElementControls.createEnumButton(newValue -> {
                value.set(newValue);
                configScreen.onChange();
                return true; // We already filtered "potential" to have only valid values
            }, title, (T[]) potential);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        private ValueConfigElementData<String> createString(ForgeConfigSpec.ConfigValue<String> value, ValueSpec valueInfo, ITextComponent title, String valueValue) {
            ConfigElementControls.ConfigElementWidgetData<String> control = ConfigElementControls.createStringTextField(newValue -> {
                if (!valueInfo.test(newValue))
                    return false;
                value.set(newValue);
                configScreen.onChange();
                return true;
            }, title);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        // TODO: Clean this up and merge it with ConfigValueConfigElement
        public class ListConfigValueConfigElement extends ConfigElementList.ConfigElement {
            private final BooleanSupplier canReset;
            private final BooleanSupplier canUndo;

            public ListConfigValueConfigElement(ITextComponent title, List<ITextComponent> tooltip, ConfigValue<List> configValue, ValueSpec valueInfo, List obj) {
                super(null, title, tooltip);
                List initialValue = ConfigElementControls.copyMutable(obj);
                List defaultValue = ConfigElementControls.copyMutable((List) valueInfo.getDefault());
                canReset = () -> !configValue.get().equals(defaultValue);
                canUndo = () -> !configValue.get().equals(initialValue);
                widgets.add(0, configScreen.makePopupButton(title, () -> new ListConfigScreen(configScreen, title, ConfigElementControls.copyMutable(configValue.get()), defaultValue) {
                    @Override
                    public void onModified(List newValue) {
                        configValue.set(newValue);
                        onChange();
                    }
                }));
                widgets.add(resetButton = createConfigElementResetButton(title, button -> configValue.set(defaultValue)));
                widgets.add(undoButton = createConfigElementUndoButton(title, button -> configValue.set(initialValue)));
            }

            @Override
            public void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int adjustedItemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                // getRowWidth, Button#getWidth(), Button#getWidth()
                int controlWidth = func_230949_c_() - resetButton.func_230998_h_() - undoButton.func_230998_h_();
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
        }

        private class ConfigValueConfigElement extends ConfigElement {

            private final BooleanSupplier canReset;
            private final BooleanSupplier canUndo;

            public <T> ConfigValueConfigElement(ITextComponent title, List<ITextComponent> tooltip, ForgeConfigSpec.ConfigValue<T> value, ValueSpec valueInfo, ValueConfigElementData<T> data, T obj) {
                super(title, title, tooltip);
                widgets.add(0, data.widget);
                T defaultValue = ConfigElementControls.copyMutable((T) valueInfo.getDefault());
                T initialValue = ConfigElementControls.copyMutable(obj);
                canReset = () -> !value.get().equals(defaultValue);
                canUndo = () -> !value.get().equals(initialValue);
                widgets.add(resetButton = createConfigElementResetButton(title, button -> data.valueSetter.accept(defaultValue)));
                widgets.add(undoButton = createConfigElementUndoButton(title, button -> data.valueSetter.accept(initialValue)));
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
                if (getMainWidget().func_230449_g_()) // isHovered
                    return; // Don't render the main tooltip if we're trying to change the value
                super.renderTooltip(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

}
