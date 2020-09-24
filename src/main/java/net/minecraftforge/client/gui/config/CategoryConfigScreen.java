package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
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
                ITextComponent title = makeTitle(valueInfo.getTranslationKey(), key);
                Object valueValue = value.get();
                ConfigElement element = createValueConfigElement(value, valueInfo, title, valueValue);
                if (element != null)
                    return element;
                // TODO: title.getString()
                return new TitledConfigElement(title.getString() + ": " + valueValue, valueInfo.getComment());
            }
        }

        private ITextComponent makeTitle(String translationKey, String fallback) {
            if (translationKey == null)
                return new StringTextComponent(fallback);
            ITextComponent title = new TranslationTextComponent(translationKey);
            if (translationKey.equals(title.getString()))
                return new StringTextComponent(fallback);
            return title;
        }

        @Nullable
        private ConfigElement createValueConfigElement(ForgeConfigSpec.ConfigValue<?> value, ForgeConfigSpec.ValueSpec valueInfo, ITextComponent title, Object valueValue) {
            String description = valueInfo.getComment();
            if (valueInfo.needsWorldRestart())
                description += "\n" + TextFormatting.RED + "[" + new TranslationTextComponent("forge.configgui.needsWorldRestart").getString() + "]";
            if (valueValue instanceof Boolean)
                return new ConfigValueConfigElement(
                        title, description, (ForgeConfigSpec.BooleanValue) value, valueInfo,
                        createBoolean((ForgeConfigSpec.BooleanValue) value, title, (Boolean) valueValue),
                        (Boolean) valueValue
                );
            if (valueValue instanceof Integer)
                return new ConfigValueConfigElement(
                        title, description, (ForgeConfigSpec.IntValue) value, valueInfo,
                        createNumericRanged((ForgeConfigSpec.IntValue) value, valueInfo, title, (Integer) valueValue, Integer::parseInt, Integer.MIN_VALUE),
                        (Integer) valueValue
                );
            if (valueValue instanceof Long)
                return new ConfigValueConfigElement(
                        title, description, (ForgeConfigSpec.LongValue) value, valueInfo,
                        createNumericRanged((ForgeConfigSpec.LongValue) value, valueInfo, title, (Long) valueValue, Long::parseLong, Long.MIN_VALUE),
                        (Long) valueValue
                );
            if (valueValue instanceof Double)
                return new ConfigValueConfigElement(
                        title, description, (ForgeConfigSpec.DoubleValue) value, valueInfo,
                        createNumericRanged((ForgeConfigSpec.DoubleValue) value, valueInfo, title, (Double) valueValue, Double::parseDouble, Double.NEGATIVE_INFINITY),
                        (Double) valueValue
                );
            if (valueValue instanceof Enum<?>)
                return new ConfigValueConfigElement(
                        title, description, (ForgeConfigSpec.EnumValue) value, valueInfo,
                        createEnum((ForgeConfigSpec.EnumValue) value, valueInfo, title, (Enum) valueValue),
                        (Enum) valueValue
                );
            if (valueValue instanceof String)
                return new ConfigValueConfigElement(
                        title, description, (ForgeConfigSpec.ConfigValue<String>) value, valueInfo,
                        createString((ForgeConfigSpec.ConfigValue<String>) value, valueInfo, title, (String) valueValue),
                        (String) valueValue
                );
            if (valueValue instanceof List<?>) {
                // TODO: Clean this up and merge it with ConfigValueConfigElement
                ConfigValue<List<?>> configValue = (ForgeConfigSpec.ConfigValue<List<?>>) value;
                // TODO: title.getString()
                return new PopupConfigElement(title.getString(), description, () -> new ListConfigScreen(configScreen, title, (List<?>) valueValue, (List<?>) valueInfo.getDefault())) {
                    private final BooleanSupplier canReset;
                    private final BooleanSupplier canUndo;

                    {
                        List _default = ConfigElementControls.copyMutable((List) valueInfo.getDefault());
                        List initial = ConfigElementControls.copyMutable((List) valueValue);
                        canReset = () -> !value.get().equals(_default);
                        canUndo = () -> !value.get().equals(initial);
                        widgets.add(resetButton = createConfigElementResetButton(title, button -> configValue.set(_default)));
                        widgets.add(undoButton = createConfigElementUndoButton(title, button -> configValue.set(initial)));
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
                };
            }
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

        public ValueConfigElementData<Boolean> createBoolean(ForgeConfigSpec.BooleanValue value, ITextComponent title, Boolean valueValue) {
            ConfigElementControls.ConfigElementWidgetData<Boolean> control = ConfigElementControls.createBooleanButton(newValue -> {
                value.set(newValue);
                configScreen.onChange();
                return true; // Can't have an "invalid" boolean
            }, title);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        public <T extends Comparable<? super T>> ValueConfigElementData<T> createNumericRanged(ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec valueInfo, ITextComponent title, T valueValue, Function<String, T> parser, T longestValue) {
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

        private <T extends Enum<T>> ValueConfigElementData<T> createEnum(ForgeConfigSpec.EnumValue<T> value, ForgeConfigSpec.ValueSpec valueInfo, ITextComponent title, T valueValue) {
            Enum<?>[] potential = Arrays.stream(((Enum<?>) valueValue).getDeclaringClass().getEnumConstants()).filter(valueInfo::test).toArray(Enum<?>[]::new);
            ConfigElementControls.ConfigElementWidgetData<T> control = ConfigElementControls.createEnumButton(newValue -> {
                value.set(newValue);
                configScreen.onChange();
                return true; // We already filtered "potential" to have only valid values
            }, title, (T[]) potential);
            control.valueSetter.setup(valueValue);
            return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
        }

        private ValueConfigElementData<String> createString(ForgeConfigSpec.ConfigValue<String> value, ForgeConfigSpec.ValueSpec valueInfo, ITextComponent title, String valueValue) {
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

        public class CategoryConfigElement extends PopupConfigElement {
            public CategoryConfigElement(ConfigScreen parentScreen, String translatedName, String description, ConfigCategoryInfo valueCategoryInfo) {
                super(translatedName, description, () -> new CategoryConfigScreen(parentScreen, new StringTextComponent(translatedName), valueCategoryInfo));
            }
        }

        private class ConfigValueConfigElement extends TitledConfigElement {

            private final BooleanSupplier canReset;
            private final BooleanSupplier canUndo;

            public <T> ConfigValueConfigElement(ITextComponent title, String description, ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec valueInfo, ValueConfigElementData<T> data, T obj) {
                super(title.getString(), description); // TODO: FIX title.getString()
                widgets.add(0, data.widget);
                T _default = ConfigElementControls.copyMutable((T) valueInfo.getDefault());
                T initial = ConfigElementControls.copyMutable(obj);
                canReset = () -> !value.get().equals(_default);
                canUndo = () -> !value.get().equals(initial);
                widgets.add(resetButton = createConfigElementResetButton(title, button -> data.valueSetter.accept(_default)));
                widgets.add(undoButton = createConfigElementUndoButton(title, button -> data.valueSetter.accept(initial)));
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
