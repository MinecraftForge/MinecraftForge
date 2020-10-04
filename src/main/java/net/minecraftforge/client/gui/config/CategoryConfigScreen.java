package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.*;
import net.minecraftforge.client.gui.config.ControlCreator.Interactor;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Range;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A {@link ConfigScreen} for a (sub) category.
 * A category is made when you do "builder.push()" when initialising your config.
 *
 * @author Cadiboo
 */
public class CategoryConfigScreen extends ConfigScreen {
    public static final TextFormatting TOOLTIP_TITLE_COLOR = TextFormatting.GREEN;
    public static final TextFormatting TOOLTIP_COMMENT_COLOR = TextFormatting.YELLOW;
    public static final TextFormatting TOOLTIP_EXTRA_DATA_COLOR = TextFormatting.AQUA;
    public static final TextFormatting TOOLTIP_WORLD_RESTART_REQUIRED_COLOR = TextFormatting.RED;
    protected final ConfigCategoryInfo categoryInfo;

    public CategoryConfigScreen(Screen parentScreen, ITextComponent title, ConfigCategoryInfo categoryInfo) {
        super(parentScreen, title, ConfigScreen.makeSubtitle(parentScreen, title));
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
            TranslationTextComponent comment = new TranslationTextComponent(translationKey);
            if (!translationKey.equals(comment.getString())) {
                comment.func_240701_a_(TOOLTIP_COMMENT_COLOR); // mergeStyle
                // TODO: There can be line breaks in here too but everything's SRG named and I cbf to work out how to split text components
//                configScreen.getFontRenderer().func_238420_b_().func_238362_b_(textLine, tooltipTextWidth, Style.field_240709_b_);
                return Collections.singletonList(comment);
            }
        }
        if (fallbackComment == null)
            return Collections.emptyList();
        return Arrays.stream(fallbackComment.split("\n"))
            .map(StringTextComponent::new)
            .map(s -> s.func_240701_a_(TOOLTIP_COMMENT_COLOR)) // mergeStyle
            .collect(Collectors.toList());
    }

    public static List<ITextProperties> createTooltip(ITextComponent title, String commentTranslationKey, String comment) {
        List<ITextProperties> tooltip = new LinkedList<>();
        tooltip.add(title.func_230532_e_().func_240701_a_(TOOLTIP_TITLE_COLOR));
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

        /**
         * Creates an element for any possible object that is stored in a config.
         */
        public ConfigElement createConfigElement(String key, ConfigCategoryInfo categoryInfo) {
            Object raw = categoryInfo.getValue(key);
            if (raw instanceof UnmodifiableConfig)
                return createCategoryConfigElement(key, categoryInfo, (UnmodifiableConfig) raw);
            else
                return createValueConfigElement(key, categoryInfo, (ConfigValue<?>) raw);
        }

        protected CategoryConfigElement createCategoryConfigElement(String key, ConfigCategoryInfo categoryInfo, UnmodifiableConfig config) {
            UnmodifiableCommentedConfig valueInfo = (UnmodifiableCommentedConfig) categoryInfo.getSpec(key);
            ConfigCategoryInfo valueCategoryInfo = ConfigCategoryInfo.of(
                () -> config.valueMap().keySet(),
                config::get,
                valueInfo::get,
                valueInfo::getComment
            );
            String translationKey = null; // TODO: Can pass path in through categoryInfo, need to also get review on changes to ForgeConfigSpec for comments

            ITextComponent title = translateWithFallback(translationKey, key);
            List<ITextProperties> tooltip = createTooltip(title, translationKey == null ? null : translationKey + ".tooltip", categoryInfo.getCategoryComment(key));

            return new CategoryConfigElement(title, tooltip, valueCategoryInfo);
        }

        protected ConfigElement createValueConfigElement(String key, ConfigCategoryInfo categoryInfo, ConfigValue<?> configValue) {
            ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
            String translationKey = valueInfo.getTranslationKey();

            ITextComponent title = translateWithFallback(translationKey, key);
            List<ITextProperties> tooltip = createTooltip(title, translationKey + ".tooltip", valueInfo.getComment());
            addExtraTooltipInfo(valueInfo, tooltip);

            return createValueElement(configValue, valueInfo, title, tooltip);
        }

        protected void addExtraTooltipInfo(ValueSpec valueInfo, List<ITextProperties> tooltip) {
            String allowedValues = valueInfo.getAllowedValues();
            if (allowedValues != null)
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.allowedValues", allowedValues).func_240701_a_(TOOLTIP_EXTRA_DATA_COLOR));
            Range<?> range = valueInfo.getRange();
            if (range != null)
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.rangeWithDefault", range.toString(), valueInfo.getDefault()).func_240701_a_(TOOLTIP_EXTRA_DATA_COLOR));
            else
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.default", valueInfo.getDefault()).func_240701_a_(TOOLTIP_EXTRA_DATA_COLOR));
            if (valueInfo.needsWorldRestart())
                tooltip.add(new StringTextComponent("[").func_240701_a_(TOOLTIP_WORLD_RESTART_REQUIRED_COLOR)
                    // appendSibling
                    .func_230529_a_(new TranslationTextComponent("forge.configgui.worldRestartRequired").func_240701_a_(TOOLTIP_WORLD_RESTART_REQUIRED_COLOR))
                    .func_230529_a_(new StringTextComponent("]").func_240701_a_(TOOLTIP_WORLD_RESTART_REQUIRED_COLOR))
                );
        }

        protected <T> ConfigElement createValueElement(ConfigValue<T> configValue, ValueSpec valueSpec, ITextComponent title, List<ITextProperties> tooltip) {
            Interactor<T> interactor = tryCreateInteractor(title, configValue, valueSpec);
            if (interactor == null) {
                // title.deepCopy().appendSibling
                ITextComponent label = title.func_230532_e_().func_230529_a_(new StringTextComponent(": " + configValue.get()));
                tooltip.add(new TranslationTextComponent("forge.configgui.tooltip.unsupportedTypeUseConfig"));
                return new ConfigElement(label, title, tooltip);
            } else {
                return new ConfigValueConfigElement(interactor.label, title, tooltip, configValue, valueSpec, interactor);
            }
        }

        @Nullable
        protected <T> Interactor<T> tryCreateInteractor(ITextComponent title, ConfigValue<T> configValue, ValueSpec valueSpec) {
            ControlCreator creator = configScreen.getControlCreator();
            T value = configValue.get();
            Interactor<T> interactor = new Interactor<>(title, value);
            interactor.addData(ControlCreator.CONFIG_VALUE_KEY, configValue);
            interactor.addData(ControlCreator.VALUE_SPEC_KEY, valueSpec);
            interactor.addData(ControlCreator.RANGE_KEY, valueSpec.getRange());
            interactor.addData(ControlCreator.INITIAL_VALUE_KEY, value);
            interactor.addData(ControlCreator.DEFAULT_VALUE_KEY, valueSpec.getDefault());
            interactor.addValidator(valueSpec::test);
            interactor.addSaver(newValue -> {
                configValue.set(newValue);
                configScreen.onChange(valueSpec.needsWorldRestart());
            });
            try {
                creator.createAndInitialiseInteractionWidget(interactor);
                return interactor;
            } catch (Exception e) {
                return null;
            }
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
                    interactor.save(newValue);
                    interactor.onUpdate(newValue);
                }));
                addWidget(resetButton = createConfigElementResetButton(title, button -> {
                    T newValue = ControlCreator.copyMutable(defaultValue);
                    interactor.save(newValue);
                    interactor.onUpdate(newValue);
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
                setMainWidget(configScreen.getControlCreator().createPopupButton(title, () -> new CategoryConfigScreen(configScreen, title, valueCategoryInfo)));
            }
        }
    }

}
