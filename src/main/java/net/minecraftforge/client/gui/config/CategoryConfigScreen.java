package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static class CategoryConfigElementList extends ConfigElementList {

        public CategoryConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
            super(configScreen, mcIn);
            for (String key : info.elements())
                this.func_230513_b_(createConfigElement(key, info));
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
            if (valueValue instanceof Boolean)
                return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                    {
                        ValueConfigElementData<Boolean> data = createBoolean((ForgeConfigSpec.BooleanValue) value, translatedName, (Boolean) valueValue);
                        canReset = data.canReset((ForgeConfigSpec.BooleanValue) value, valueInfo);
                        reset = data.reset(valueInfo);
                        widgets.add(0, data.widget);
                    }
                };
            if (valueValue instanceof Integer)
                return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                    {
                        ValueConfigElementData<Integer> data = createNumericRanged((ForgeConfigSpec.IntValue) value, valueInfo, translatedName, (Integer) valueValue, Integer::parseInt, Integer.MIN_VALUE);
                        canReset = data.canReset((ForgeConfigSpec.IntValue) value, valueInfo);
                        reset = data.reset(valueInfo);
                        widgets.add(0, data.widget);
                    }
                };
            if (valueValue instanceof Long)
                return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                    {
                        ValueConfigElementData<Long> data = createNumericRanged((ForgeConfigSpec.LongValue) value, valueInfo, translatedName, (Long) valueValue, Long::parseLong, Long.MIN_VALUE);
                        canReset = data.canReset((ForgeConfigSpec.LongValue) value, valueInfo);
                        reset = data.reset(valueInfo);
                        widgets.add(0, data.widget);
                    }
                };
            if (valueValue instanceof Double)
                return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                    {
                        ValueConfigElementData<Double> data = createNumericRanged((ForgeConfigSpec.DoubleValue) value, valueInfo, translatedName, (Double) valueValue, Double::parseDouble, Double.NEGATIVE_INFINITY);
                        canReset = data.canReset((ForgeConfigSpec.DoubleValue) value, valueInfo);
                        reset = data.reset(valueInfo);
                        widgets.add(0, data.widget);
                    }
                };
            if (valueValue instanceof Enum<?>)
                return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                    {
                        ValueConfigElementData<?> data = createEnum((ForgeConfigSpec.EnumValue) value, valueInfo, translatedName, (Enum) valueValue);
                        canReset = data.canReset((ForgeConfigSpec.EnumValue) value, valueInfo);
                        reset = data.reset(valueInfo);
                        widgets.add(0, data.widget);
                    }
                };
            if (valueValue instanceof String)
                return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                    {
                        ValueConfigElementData<String> data = createString((ForgeConfigSpec.ConfigValue<String>) value, valueInfo, translatedName, (String) valueValue);
                        canReset = data.canReset((ForgeConfigSpec.ConfigValue<String>) value, valueInfo);
                        reset = data.reset(valueInfo);
                        widgets.add(0, data.widget);
                    }
                };
            if (valueValue instanceof List<?>)
                return new PopupConfigElement(translatedName, valueInfo.getComment(), () -> new ListConfigScreen(configScreen, new StringTextComponent(translatedName), (List<?>) valueValue));
            return null;
        }

        static class ValueConfigElementData<T> {
            final Widget widget;
            final Consumer<T> valueSetter;

            ValueConfigElementData(Widget widget, Consumer<T> valueSetter) {
                this.widget = widget;
                this.valueSetter = valueSetter;
            }

            public Runnable reset(ForgeConfigSpec.ValueSpec valueInfo) {
                return () -> valueSetter.accept((T) valueInfo.getDefault());
            }

            public BooleanSupplier canReset(ForgeConfigSpec.ConfigValue<T> value, ForgeConfigSpec.ValueSpec valueInfo) {
                return () -> !value.get().equals(valueInfo.getDefault());
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
    }

}
