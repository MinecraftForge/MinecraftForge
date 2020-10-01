package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.*;
import net.minecraftforge.client.gui.config.ControlCreator.ConfigValueInteractor;
import net.minecraftforge.client.gui.config.ControlCreator.Interactor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraftforge.client.gui.config.ControlCreator.ConfigElementButton;

/**
 * A {@link ConfigScreen} for a (sub) category.
 * A category is made when you do "builder.push()" when initialising your config.
 *
 * @author Cadiboo
 */
public class CategoryConfigScreen extends ConfigScreen {
    protected final ConfigCategoryInfo categoryInfo;

    public CategoryConfigScreen(Screen parentScreen, ITextComponent title, ConfigCategoryInfo categoryInfo) {
        super(parentScreen, title);
        this.categoryInfo = categoryInfo;
    }

    static ITextComponent translateWithFallback(@Nullable String translationKey, String fallback) {
        if (translationKey == null)
            return new StringTextComponent(fallback);
        ITextComponent title = new TranslationTextComponent(translationKey);
        if (translationKey.equals(title.getString()))
            return new StringTextComponent(fallback);
        return title;
    }

    static Collection<? extends ITextProperties> translateCommentWithFallback(@Nullable String translationKey, @Nullable String fallbackComment) {
        // Need to handle line breaks (\n) properly
        if (translationKey != null) {
            translationKey += ".tooltip";
            TranslationTextComponent comment = new TranslationTextComponent(translationKey);
            if (!translationKey.equals(comment.getString())) {
                comment.func_240701_a_(TextFormatting.YELLOW); // mergeStyle
                // TODO: There can be line breaks in here too but everything's SRG named and I cbf to work out how to split text components
//                configScreen.getFontRenderer().func_238420_b_().func_238362_b_(textLine, tooltipTextWidth, Style.field_240709_b_);
                return Collections.singletonList(comment);
            }
        }
        if (fallbackComment == null)
            return Collections.emptyList();
        return Arrays.stream(fallbackComment.split("\n"))
            .map(StringTextComponent::new)
            .map(s -> s.func_240701_a_(TextFormatting.YELLOW)) // mergeStyle
            .collect(Collectors.toList());
    }

    public static List<ITextProperties> createTooltip(ITextComponent title, String commentTranslationKey, String comment) {
        List<ITextProperties> tooltip = new LinkedList<>();
        tooltip.add(title.func_230532_e_().func_240701_a_(TextFormatting.GREEN));
        tooltip.addAll(translateCommentWithFallback(commentTranslationKey, comment));
        return tooltip;
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
                List<ITextProperties> tooltip = createTooltip(title, translationKey, categoryInfo.getCategoryComment(key));

                return new CategoryConfigElement(title, tooltip, valueCategoryInfo);
            } else {
                ForgeConfigSpec.ConfigValue<?> value = (ForgeConfigSpec.ConfigValue<?>) raw;
                ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
                String translationKey = valueInfo.getTranslationKey();

                ITextComponent title = translateWithFallback(translationKey, key);
                List<ITextProperties> tooltip = createTooltip(title, translationKey, valueInfo.getComment());
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

                return createValueElement(value, valueInfo, title, tooltip);
            }
        }

        protected <T> ConfigElement createValueElement(ConfigValue<T> configValue, ValueSpec valueSpec, ITextComponent title, List<ITextProperties> tooltip) {
            Interactor<T> interactor = tryCreateInteractor(title, configValue, valueSpec);
            if (interactor == null) {
                // title.deepCopy().appendSibling
                ITextComponent label = title.func_230532_e_().func_230529_a_(new StringTextComponent(": " + configValue.get()));
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.unsupportedTypeUseConfig"));
                return new ConfigElement(label, title, tooltip);
            } else {
                // Don't need to explicitly check range, this is done as part of the test :)
                interactor.isValid = valueSpec::test;
                interactor.saveValue = newValue -> {
                    configValue.set(newValue);
                    configScreen.onChange(valueSpec.needsWorldRestart());
                };
                return new ConfigValueConfigElement(interactor.label, interactor.title, tooltip, configValue, valueSpec, interactor);
            }
        }

        @Nullable
        protected <T> Interactor<T> tryCreateInteractor(ITextComponent title, ConfigValue<T> configValue, ValueSpec valueSpec) {
            ControlCreator creator = configScreen.getControlCreator();
            Interactor<T> interactor = new ConfigValueInteractor<>(title, configValue, valueSpec);
            try {
                creator.createInteractionWidget(interactor);
            } catch (Exception e) {
                return null;
            }
            if (interactor.control == null)
                return null;
            return interactor;
        }

        /**
         * Element for a "config value" (a named value in a config).
         * See {@link ConfigElement} for documentation.
         */
        public class ConfigValueConfigElement extends ConfigElement {

            protected final BooleanSupplier canUndo;
            protected final BooleanSupplier canReset;

            public <T> ConfigValueConfigElement(ITextComponent label, ITextComponent title, List<? extends ITextProperties> tooltip, ConfigValue<T> value, ValueSpec valueInfo, Interactor<T> interactor) {
                super(label, title, tooltip);
                setMainWidget(interactor.control);
                T initialValue = ControlCreator.copyMutable(interactor.initialValue);
                T defaultValue = ControlCreator.copyMutable((T) valueInfo.getDefault());
                canUndo = () -> !value.get().equals(initialValue);
                canReset = () -> !value.get().equals(defaultValue);
                addWidget(undoButton = createConfigElementUndoButton(title, button -> {
                    T newValue = ControlCreator.copyMutable(initialValue);
                    interactor.saveValue.accept(newValue);
                    interactor.updateVisuals.update(true, newValue);
                }));
                addWidget(resetButton = createConfigElementResetButton(title, button -> {
                    T newValue = ControlCreator.copyMutable(defaultValue);
                    interactor.saveValue.accept(newValue);
                    interactor.updateVisuals.update(true, newValue);
                }));
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
                if (label != null && mouseX >= widget.field_230690_l_ && mouseY >= widget.field_230691_m_ && mouseX < widget.field_230690_l_ + widget.func_230998_h_() && mouseY < widget.field_230691_m_ + widget.func_238483_d_())
                    return; // Don't render the main tooltip if we're trying to change the value
                super.renderTooltip(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        /**
         * Element for a (sub)category in the config.
         * See {@link ConfigElement} for documentation.
         */
        public class CategoryConfigElement extends ConfigElement {
            public CategoryConfigElement(ITextComponent title, List<ITextProperties> tooltip, ConfigCategoryInfo valueCategoryInfo) {
                super(null, title, tooltip);
                setMainWidget(new ConfigElementButton(title, b -> Minecraft.getInstance().displayGuiScreen(new CategoryConfigScreen(configScreen, title, valueCategoryInfo))));
            }
        }
    }

}
