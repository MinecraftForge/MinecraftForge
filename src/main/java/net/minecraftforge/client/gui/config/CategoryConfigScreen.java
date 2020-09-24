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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {@link ConfigScreen} for a (sub) category.
 * A category is made when you do "builder.push()" when initialising your config.
 */
public class CategoryConfigScreen extends ConfigScreen {
    protected final ConfigCategoryInfo categoryInfo;

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

    static ITextComponent translateWithFallback(@Nullable String translationKey, String fallback) {
        if (translationKey == null)
            return new StringTextComponent(fallback);
        ITextComponent title = new TranslationTextComponent(translationKey);
        if (translationKey.equals(title.getString()))
            return new StringTextComponent(fallback);
        return title;
    }

    static Collection<? extends ITextComponent> translateCommentWithFallback(String translationKey, String fallbackComment) {
        // Need to handle linebreaks properly
        if (translationKey != null) {
            translationKey += ".tooltip";
            TranslationTextComponent comment = new TranslationTextComponent(translationKey);
            if (!translationKey.equals(comment.getString())) {
                // TODO: There can be line breaks in here too but everything's SRG named and I cbf to work out how to split text components
//                configScreen.getFontRenderer().func_238420_b_().func_238362_b_(textLine, tooltipTextWidth, Style.field_240709_b_);
                return Collections.singletonList(comment);
            }
        }
        return Arrays.stream(fallbackComment.split("\n"))
                .map(StringTextComponent::new)
                .map(s -> s.func_240701_a_(TextFormatting.YELLOW)) // mergeStyle
                .collect(Collectors.toList());
    }

    public static class CategoryConfigElementList extends ConfigElementList {

        public CategoryConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
            super(configScreen, mcIn);
            for (String key : info.elements())
                func_230513_b_(createConfigElement(key, info));
        }

        // TODO: This is a bit of a mess
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

                ITextComponent title = translateWithFallback(translationKey, key);
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
                tooltip.addAll(translateCommentWithFallback(translationKey, categoryInfo.getCategoryComment(key)));

                return new CategoryConfigElement(title, tooltip, valueCategoryInfo);
            } else {
                ForgeConfigSpec.ConfigValue<?> value = (ForgeConfigSpec.ConfigValue<?>) raw;
                ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
                String translationKey = valueInfo.getTranslationKey();

                ITextComponent title = translateWithFallback(translationKey, key);
                List<ITextComponent> tooltip = new LinkedList<>();
                tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
                tooltip.addAll(translateCommentWithFallback(translationKey, valueInfo.getComment()));
                Range<?> range = valueInfo.getRange();
                if (range != null)
                    tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.rangeWithDefault", range.getMin(), range.getMax(), valueInfo.getDefault()).func_240701_a_(TextFormatting.AQUA));
                else
                    tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.default", valueInfo.getDefault()).func_240701_a_(TextFormatting.AQUA));
                if (valueInfo.needsWorldRestart())
                    tooltip.add(new StringTextComponent("[").func_240701_a_(TextFormatting.RED)
                            // appendSibling
                            .func_230529_a_(new TranslationTextComponent("forge.configgui.worldRestartRequired").func_240701_a_(TextFormatting.RED))
                            .func_230529_a_(new StringTextComponent("]").func_240701_a_(TextFormatting.RED))
                    );

                Object valueValue = value.get();
                ValueConfigElementData data = configScreen.getControlCreator().makeElementData(this, title, value, valueInfo, valueValue);
                if (data != null)
                    // TODO: Provide a way to properly pass a null label
                    return new ConfigValueConfigElement(valueValue instanceof List ? null : title, title, tooltip, (ConfigValue) value, valueInfo, data, valueValue);
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

            protected final BooleanSupplier canUndo;
            protected final BooleanSupplier canReset;

            public <T> ConfigValueConfigElement(ITextComponent label, ITextComponent title, List<ITextComponent> tooltip, ConfigValue<T> value, ValueSpec valueInfo, ValueConfigElementData<T> data, T obj) {
                super(label, title, tooltip);
                widgets.add(0, data.widget);
                T initialValue = configScreen.getControlCreator().copyMutable(obj);
                T defaultValue = configScreen.getControlCreator().copyMutable((T) valueInfo.getDefault());
                canUndo = () -> !value.get().equals(initialValue);
                canReset = () -> !value.get().equals(defaultValue);
                widgets.add(undoButton = createConfigElementUndoButton(title, button -> data.valueSetter.accept(initialValue)));
                widgets.add(resetButton = createConfigElementResetButton(title, button -> data.valueSetter.accept(defaultValue)));
            }

            @Override
            public void tick() {
                super.tick();
                // active/enabled
                undoButton.field_230693_o_ = canUndo.getAsBoolean();
                resetButton.field_230693_o_ = canReset.getAsBoolean();
            }

            @Override
            public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                Widget widget = getMainWidget();
                // Directly check if it's hovered (all the methods to check also check extra stuff e.g. isFocussed which we don't want)
                if (mouseX >= widget.field_230690_l_ && mouseY >= widget.field_230691_m_ && mouseX < widget.field_230690_l_ + widget.func_230998_h_() && mouseY < widget.field_230691_m_ + widget.func_238483_d_())
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
