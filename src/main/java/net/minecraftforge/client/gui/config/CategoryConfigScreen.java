package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link ConfigScreen} for a (sub) category.
 * A category is made when you do "builder.push()" when initialising your config.
 */
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

        Collection<String> elements();

        Object getValue(String key);

        Object getSpec(String key);

        /** Special case categories because reasons */
        String getCategoryComment(String key);
    }

    public static class CategoryConfigElementList extends ConfigElementList {

        public CategoryConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
            super(configScreen, mcIn);
            for (String key : info.elements())
                func_230513_b_(createConfigElement(key, info));
        }

        // TODO: This is a mess
        public ConfigElement createConfigElement(String key, ConfigCategoryInfo categoryInfo) {
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
                ITextComponent title = configScreen.getControlCreator().makeTranslationComponent(translationKey, key);
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
                // TODO: Comment can have linebreaks in it
                tooltip.add(configScreen.getControlCreator().makeTranslationComponent(translationKey == null ? null : translationKey + ".tooltip", categoryInfo.getCategoryComment(key)).func_230531_f_().func_240701_a_(TextFormatting.YELLOW));
                return new CategoryConfigElement(title, tooltip, valueCategoryInfo);
            } else {
                ForgeConfigSpec.ConfigValue<?> value = (ForgeConfigSpec.ConfigValue<?>) raw;
                ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
                String translationKey = valueInfo.getTranslationKey();
                ITextComponent title = configScreen.getControlCreator().makeTranslationComponent(translationKey, key);
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
                // TODO: Comment can have linebreaks in it
                tooltip.add(configScreen.getControlCreator().makeTranslationComponent(translationKey == null ? null : translationKey + ".tooltip", valueInfo.getComment()).func_230531_f_().func_240701_a_(TextFormatting.YELLOW));
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
                ValueConfigElementData data = configScreen.getControlCreator().makeElementData(this, title, value, valueInfo, valueValue);
                if (data != null)
                    return new ConfigValueConfigElement(title, tooltip, (ConfigValue) value, valueInfo, data, valueValue);
                // title.deepCopy().appendSibling
                ITextComponent label = title.func_230532_e_().func_230529_a_(new StringTextComponent(": " + valueValue));
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.unsupportedTypeUseConfig"));
                return new ConfigElement(label, title, tooltip);
            }
        }

        static class ValueConfigElementData<T> {
            final Widget widget;
            final Consumer<T> valueSetter;

            ValueConfigElementData(Widget widget, Consumer<T> valueSetter) {
                this.widget = widget;
                this.valueSetter = valueSetter;
            }

        }

        /**
         * Element for a "config value" (a named value in a config).
         * See {@link ConfigElement} for documentation.
         */
        public class ConfigValueConfigElement extends ConfigElement {

            private final BooleanSupplier canReset;
            private final BooleanSupplier canUndo;

            public <T> ConfigValueConfigElement(ITextComponent title, List<ITextComponent> tooltip, ConfigValue<T> value, ValueSpec valueInfo, ValueConfigElementData<T> data, T obj) {
                super(title, title, tooltip);
                widgets.add(0, data.widget);
                T initialValue = configScreen.getControlCreator().copyMutable(obj);
                T defaultValue = configScreen.getControlCreator().copyMutable((T) valueInfo.getDefault());
                canReset = () -> !value.get().equals(defaultValue);
                canUndo = () -> !value.get().equals(initialValue);
                widgets.add(resetButton = createConfigElementResetButton(title, button -> data.valueSetter.accept(defaultValue)));
                widgets.add(undoButton = createConfigElementUndoButton(title, button -> data.valueSetter.accept(initialValue)));
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

        /**
         * Element for a (sub)category in the config.
         * See {@link ConfigElement} for documentation.
         */
        public class CategoryConfigElement extends ConfigElement {
            public CategoryConfigElement(ITextComponent title, List<ITextComponent> tooltip, ConfigCategoryInfo valueCategoryInfo) {
                super(null, title, tooltip);
                this.widgets.add(0, configScreen.getControlCreator().makePopupButton(title, () -> new CategoryConfigScreen(configScreen, title, valueCategoryInfo)));
            }
        }
    }

}
