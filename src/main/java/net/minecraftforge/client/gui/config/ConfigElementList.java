package net.minecraftforge.client.gui.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ConfigElementList extends AbstractOptionList<ConfigElementList.ConfigElement> {
    private final ConfigScreen configScreen;
    private int maxListLabelWidth;

    public ConfigElementList(ConfigScreen configScreen, Minecraft mcIn, ConfigCategoryInfo info) {
        super(mcIn, configScreen.field_230708_k_, configScreen.field_230709_l_, 43, configScreen.field_230709_l_ - 32, 20);
        this.configScreen = configScreen;
        for (String key : info.elements())
            this.func_230513_b_(createConfigElement(key, info));
    }

    private ConfigElement createConfigElement(String key, ConfigCategoryInfo categoryInfo) {
        Object raw = categoryInfo.getValue(key);
        if (raw instanceof UnmodifiableConfig) {
            UnmodifiableConfig value = (UnmodifiableConfig) raw;
            UnmodifiableConfig valueInfo = (UnmodifiableConfig) categoryInfo.getSpec(key);
            ConfigCategoryInfo valueCategoryInfo = ConfigCategoryInfo.of(
                    () -> value.valueMap().keySet(),
                    value::get,
                    valueInfo::get
            );
            return new CategoryConfigElement(configScreen, key, "", valueCategoryInfo);
        } else {
            ConfigValue<?> value = (ConfigValue<?>) raw;
            ValueSpec valueInfo = (ValueSpec) categoryInfo.getSpec(key);
            String name = new TranslationTextComponent(valueInfo.getTranslationKey()).getString();
            return new ConfigElementImpl(name + ": " + value.get(), valueInfo.getComment());
        }
    }

    @Override
    // getScrollbarPosition
    protected int func_230952_d_() {
        return getWidth() - 20;
    }

    @Override
    // getRowWidth
    public int func_230949_c_() {
        return super.func_230949_c_() + 32;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract class ConfigElement extends AbstractOptionList.Entry<ConfigElement> {
        public static final int PADDING = 5;
        final String translatedName;
        final String description;
        protected final LinkedList<Widget> widgets = new LinkedList<>();
        protected final Button btnReset;

        public ConfigElement(String translatedName, String description) {
            this.translatedName = translatedName;
            this.description = description;
            if (maxListLabelWidth < translatedName.length())
                maxListLabelWidth = translatedName.length();
            btnReset = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), button -> {
                System.out.println("Reset " + translatedName);
//            keybinding.setToDefault();
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
        public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int elementRenderY, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
            final Iterator<Widget> iterator = widgets.descendingIterator();
            int widgetPos = (shouldRenderScrollbar() ? func_230952_d_() : getWidth()) - PADDING; // getScrollbarPosition
            while (iterator.hasNext()) {
                final Widget widget = iterator.next();
                widgetPos -= widget.func_230998_h_(); // Width
                widget.field_230690_l_ = widgetPos;
                widget.field_230691_m_ = elementRenderY;
                // Render
                widget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
            }
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

    @OnlyIn(Dist.CLIENT)
    public class ConfigElementImpl extends ConfigElement {
        private final ITextComponent nameComponent;

        private ConfigElementImpl(String translatedName, String description) {
            super(translatedName, description);
            nameComponent = new StringTextComponent(translatedName);
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int elementRenderY, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean p_230432_9_, float partialTicks) {
            super.func_230432_a_(matrixStack, p_230432_2_, elementRenderY, p_230432_4_, p_230432_5_, p_230432_6_, mouseX, mouseY, p_230432_9_, partialTicks);
            field_230668_b_.fontRenderer.func_243248_b(matrixStack, this.nameComponent, (float) (p_230432_4_ + /*90*/ -ConfigElementList.this.maxListLabelWidth), (float) (elementRenderY + p_230432_6_ / 2 - 9 / 2), 0xffffff);
        }

    }

    private class CategoryConfigElement extends ConfigElement {
        public CategoryConfigElement(ConfigScreen parentScreen, String translatedName, String description, ConfigCategoryInfo valueCategoryInfo) {
            super(translatedName, description);
            int otherWidthsAndPadding = widgets.stream().mapToInt(Widget::func_230998_h_).sum();
            // getScrollbarPosition
            int width = (shouldRenderScrollbar() ? func_230952_d_() : getWidth()) - otherWidthsAndPadding - PADDING * 2;
            Button openScreen = new ExtendedButton(0, 0, width, 20, new StringTextComponent(translatedName), button -> {
                ConfigScreen screen = new ConfigScreen(parentScreen, new StringTextComponent(translatedName), valueCategoryInfo);
                field_230668_b_.displayGuiScreen(screen);
            });
            widgets.add(0, openScreen);
        }
    }
}
