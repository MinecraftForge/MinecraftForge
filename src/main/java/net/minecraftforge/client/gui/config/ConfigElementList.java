package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.config.ConfigElementControls.ConfigElementWidgetData;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ConfigElementList extends AbstractOptionList<ConfigElementList.ConfigElement> {
    private final ConfigScreen configScreen;
    private int maxListLabelWidth;

    public ConfigElementList(ConfigScreen configScreen, Minecraft mcIn) {
        super(mcIn, configScreen.field_230708_k_, configScreen.field_230709_l_, 43, configScreen.field_230709_l_ - 32, 20);
        this.configScreen = configScreen;
    }

    public ConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
        this(configScreen, mcIn);
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
            ConfigValue<?> value = (ConfigValue<?>) raw;
            ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
            String name = new TranslationTextComponent(valueInfo.getTranslationKey()).getString();
            Object valueValue = value.get();
            ConfigElement element = createValueConfigElement(value, valueInfo, name, valueValue);
            if (element != null)
                return element;
            return new TitledConfigElement(name + ": " + valueValue, valueInfo.getComment());
        }
    }

    @Nullable
    private ConfigElement createValueConfigElement(ConfigValue<?> value, ValueSpec valueInfo, String translatedName, Object valueValue) {
        if (valueValue instanceof Boolean)
            return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                {
                    ValueConfigElementData<Boolean> data = createBoolean((BooleanValue) value, translatedName, (Boolean) valueValue);
                    canReset = data.canReset((BooleanValue) value, valueInfo);
                    reset = data.reset(valueInfo);
                    widgets.add(0, data.widget);
                }
            };
        if (valueValue instanceof Integer)
            return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                {
                    ValueConfigElementData<Integer> data = createNumericRanged((IntValue) value, valueInfo, translatedName, (Integer) valueValue, Integer::parseInt, Integer.MIN_VALUE);
                    canReset = data.canReset((IntValue) value, valueInfo);
                    reset = data.reset(valueInfo);
                    widgets.add(0, data.widget);
                }
            };
        if (valueValue instanceof Long)
            return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                {
                    ValueConfigElementData<Long> data = createNumericRanged((LongValue) value, valueInfo, translatedName, (Long) valueValue, Long::parseLong, Long.MIN_VALUE);
                    canReset = data.canReset((LongValue) value, valueInfo);
                    reset = data.reset(valueInfo);
                    widgets.add(0, data.widget);
                }
            };
        if (valueValue instanceof Double)
            return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                {
                    ValueConfigElementData<Double> data = createNumericRanged((DoubleValue) value, valueInfo, translatedName, (Double) valueValue, Double::parseDouble, Double.NEGATIVE_INFINITY);
                    canReset = data.canReset((DoubleValue) value, valueInfo);
                    reset = data.reset(valueInfo);
                    widgets.add(0, data.widget);
                }
            };
        if (valueValue instanceof Enum<?>)
            return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                {
                    ValueConfigElementData<?> data = createEnum((EnumValue) value, valueInfo, translatedName, (Enum) valueValue);
                    canReset = data.canReset((EnumValue) value, valueInfo);
                    reset = data.reset(valueInfo);
                    widgets.add(0, data.widget);
                }
            };
        if (valueValue instanceof String)
            return new TitledConfigElement(translatedName, valueInfo.getComment()) {
                {
                    ValueConfigElementData<String> data = createString((ConfigValue<String>) value, valueInfo, translatedName, (String) valueValue);
                    canReset = data.canReset((ConfigValue<String>) value, valueInfo);
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

        public Runnable reset(ValueSpec valueInfo) {
            return () -> valueSetter.accept((T) valueInfo.getDefault());
        }

        public BooleanSupplier canReset(ConfigValue<T> value, ValueSpec valueInfo) {
            return () -> !value.get().equals(valueInfo.getDefault());
        }
    }

    public ValueConfigElementData<Boolean> createBoolean(BooleanValue value, String translatedName, Boolean valueValue) {
        ConfigElementWidgetData<Boolean> control = ConfigElementControls.createBooleanButton(newValue -> {
            value.set(newValue);
            configScreen.onChange();
            return true; // Can't have an "invalid" boolean
        }, translatedName);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    public <T extends Comparable<? super T>> ValueConfigElementData<T> createNumericRanged(ConfigValue<T> value, ValueSpec valueInfo, String translatedName, T valueValue, Function<String, T> parser, T longestValue) {
        @Nullable
        Range<T> range = valueInfo.getRange();
        ConfigElementWidgetData<T> control = ConfigElementControls.createNumericTextField(newValue -> {
            if (range != null && !range.test(newValue))
                return false;
            value.set(newValue);
            configScreen.onChange();
            return true;
        }, translatedName, parser, longestValue);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    private <T extends Enum<T>> ValueConfigElementData<T> createEnum(EnumValue<T> value, ValueSpec valueInfo, String translatedName, T valueValue) {
        Enum<?>[] potential = Arrays.stream(((Enum<?>) valueValue).getDeclaringClass().getEnumConstants()).filter(valueInfo::test).toArray(Enum<?>[]::new);
        ConfigElementWidgetData<T> control = ConfigElementControls.createEnumButton(newValue -> {
            value.set(newValue);
            configScreen.onChange();
            return true; // Can't have an "invalid" boolean
        }, translatedName, (T[]) potential);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    private ValueConfigElementData<String> createString(ConfigValue<String> value, ValueSpec valueInfo, String translatedName, String valueValue) {
        ConfigElementWidgetData<String> control = ConfigElementControls.createStringTextField(newValue -> {
            if (!valueInfo.test(newValue))
                return false;
            value.set(newValue);
            configScreen.onChange();
            return true;
        }, translatedName);
        control.valueSetter.setup(valueValue);
        return new ValueConfigElementData<>(control.widget, control.valueSetter::setTo);
    }

    @Override
    // getScrollbarPosition
    protected int func_230952_d_() {
        return func_230949_c_();
    }

    @Override
    // getRowWidth
    public int func_230949_c_() {
        return getWidth() - 20;
    }

    public void tick() {
        for (ConfigElement element : this.func_231039_at__())
            element.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public abstract class ConfigElement extends AbstractOptionList.Entry<ConfigElement> {
        public static final int PADDING = 5;
        final String translatedName;
        final String description;
        protected final LinkedList<Widget> widgets = new LinkedList<>();
        protected final Button btnReset;
        protected Runnable reset;
        protected BooleanSupplier canReset;

        public ConfigElement(String translatedName, String description) {
            this.translatedName = translatedName;
            this.description = description;
            if (maxListLabelWidth < translatedName.length())
                maxListLabelWidth = translatedName.length();
            reset = () -> System.out.println("Reset " + translatedName);
            canReset = () -> false;
            btnReset = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), button -> {
                reset.run();
            }) {
                @Override
                protected IFormattableTextComponent func_230442_c_() {
                    return new TranslationTextComponent("narrator.controls.reset", translatedName);
                }
            };
            widgets.add(btnReset);
        }

        @Override
        public List<? extends IGuiEventListener> func_231039_at__() {
            return widgets;
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int elementRenderY, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
            final Iterator<Widget> iterator = widgets.descendingIterator();
            int widgetPos = (shouldRenderScrollbar() ? func_230952_d_() : getWidth()) - PADDING; // getScrollbarPosition
            while (iterator.hasNext()) {
                final Widget widget = iterator.next();
                widgetPos -= widget.func_230998_h_(); // Width
                widget.field_230690_l_ = widgetPos;
                widget.field_230691_m_ = elementRenderY;
                if (widget instanceof TextFieldWidget) {
                    widget.field_230690_l_ -= 2;
                    widget.field_230691_m_ += 2;
                }
                // Render
                widget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
            }
            // TODO: Doesn't work well
            if (description.length() > 0 && this.func_231047_b_(mouseX, mouseY)) {
                // renderTooltip
                List<ITextComponent> list = Arrays.stream(description.split("\n")).map(StringTextComponent::new).collect(Collectors.toList());
                ConfigElementList.this.configScreen.func_243308_b(matrixStack, list, mouseX, mouseY);
            }
        }

        public void tick() {
            // active/enabled
            btnReset.field_230693_o_ = canReset.getAsBoolean();
            for (Widget widget : widgets)
                if (widget instanceof TextFieldWidget)
                    ((TextFieldWidget) widget).tick();
        }

//        @Override
//        // mouseClicked
//        public boolean func_231044_a_(double mouseX, double mouseY, int mouseBtn) {
//            for (Widget widget : widgets)
//                if (widget.func_231044_a_(mouseX, mouseY, mouseBtn))
//                    return true;
//            return false;
//        }
//
//        @Override
//        // mouseReleased
//        public boolean func_231048_c_(double mouseX, double mouseY, int mouseBtn) {
//            for (Widget widget : widgets)
//                if (widget.func_231048_c_(mouseX, mouseY, mouseBtn))
//                    return true;
//            return false;
//        }

    }

    public boolean shouldRenderScrollbar() {
        // Copied from func_230955_e_/getMaxScroll
        int maxScroll = func_230945_b_() - (field_230673_j_ - field_230672_i_ - 4);
        return maxScroll > 0;
    }

    @Override
    public boolean func_231044_a_(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        boolean wasAnythingSelected = super.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_);
        if (!wasAnythingSelected) {
            for (ConfigElement element : func_231039_at__())
                for (Widget widget : element.widgets)
                    if (widget instanceof TextFieldWidget)
                        ((TextFieldWidget) widget).setFocused2(false);
        }
        return wasAnythingSelected;
    }

    @Override
    protected void func_230938_a_(int p_230938_1_, int p_230938_2_) {
        super.func_230938_a_(p_230938_1_, p_230938_2_);
        for (ConfigElement element : func_231039_at__())
            for (Widget widget : element.widgets)
                if (widget instanceof TextFieldWidget)
                    ((TextFieldWidget) widget).setFocused2(false);
    }

    @Override
    public void func_231035_a_(IGuiEventListener newFocused) {
        final ConfigElement oldFocused = func_241217_q_();
        if (oldFocused != null && oldFocused != newFocused)
            for (Widget widget : oldFocused.widgets)
                if (widget instanceof TextFieldWidget)
                    ((TextFieldWidget) widget).setFocused2(false);
        super.func_231035_a_(newFocused);
    }

    @OnlyIn(Dist.CLIENT)
    public class TitledConfigElement extends ConfigElement {
        private final ITextComponent nameComponent;

        public TitledConfigElement(String translatedName, String description) {
            super(translatedName, description);
            nameComponent = new StringTextComponent(translatedName);
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int elementRenderY, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
            super.func_230432_a_(matrixStack, p_230432_2_, elementRenderY, p_230432_4_, p_230432_5_, p_230432_6_, mouseX, mouseY, isHovered, partialTicks);
            field_230668_b_.fontRenderer.func_243248_b(matrixStack, this.nameComponent, p_230432_4_, (float) (elementRenderY + p_230432_6_ / 2 - 9 / 2), 0xffffff);
        }

    }

    /**
     * Opens a new Screen
     */
    public class PopupConfigElement extends ConfigElement {
        public PopupConfigElement(String translatedName, String description, Supplier<ConfigScreen> screenFactory) {
            super(translatedName, description);
            int otherWidthsAndPadding = widgets.stream().mapToInt(Widget::func_230998_h_).sum();
            // getScrollbarPosition
            int width = (shouldRenderScrollbar() ? func_230952_d_() : getWidth()) - otherWidthsAndPadding - PADDING * 2;
            Button openScreen = new ExtendedButton(0, 0, width, 20, new StringTextComponent(translatedName), b -> {
                // DO NOT use this, it crashes Java somehow. Probably a bug in the compiler?
                // "https://gist.github.com/Cadiboo/6d1bd4b3b97c284e743a2253b81c3e5e#file-log"
                field_230668_b_.displayGuiScreen(screenFactory.get());
//                Minecraft.getInstance().displayGuiScreen(screenFactory.get());
            });
            widgets.add(0, openScreen);
        }
    }

    public class CategoryConfigElement extends PopupConfigElement {
        public CategoryConfigElement(ConfigScreen parentScreen, String translatedName, String description, ConfigCategoryInfo valueCategoryInfo) {
            super(translatedName, description, () -> new CategoryConfigScreen(parentScreen, new StringTextComponent(translatedName), valueCategoryInfo));
        }
    }
}
