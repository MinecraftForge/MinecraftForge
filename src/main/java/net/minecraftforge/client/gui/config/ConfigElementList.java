package net.minecraftforge.client.gui.config;

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
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ConfigElementList extends AbstractOptionList<ConfigElementList.ConfigElement> {

    // From AbstractList#render/func_230430_a_
    private static final int SCROLLBAR_WIDTH = 6;
    public static final int ROW_HEIGHT = 20;

    protected final ConfigScreen configScreen;
    private int maxListLabelWidth;

    public ConfigElementList(ConfigScreen configScreen, Minecraft mcIn) {
        super(mcIn, configScreen.field_230708_k_, configScreen.field_230709_l_, 43, configScreen.field_230709_l_ - 32, ROW_HEIGHT);
        this.configScreen = configScreen;
    }

    @Override
    // getScrollbarPosition
    protected int func_230952_d_() {
        return func_230949_c_();
    }

    @Override
    // getRowWidth
    public int func_230949_c_() {
        return getWidth() - SCROLLBAR_WIDTH;
    }

    public void tick() {
        for (ConfigElement element : this.func_231039_at__())
            element.tick();
    }

    @Override
    // render
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        ConfigElement selectedElement = func_230933_a_(mouseX, mouseY); // getEntryAtPosition
        if (selectedElement != null)
            selectedElement.renderTooltip(matrixStack, mouseX, mouseY, partialTicks);
    }

    public static Button createConfigElementResetButton(String translatedName, Button.IPressable onPress) {
        return new ExtendedButton(0, 0, 30, ROW_HEIGHT, new TranslationTextComponent("controls.reset"), onPress) {
            @Override
            protected IFormattableTextComponent func_230442_c_() {
                return new TranslationTextComponent("narrator.controls.reset", translatedName);
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    public abstract class ConfigElement extends AbstractOptionList.Entry<ConfigElement> {
        public static final int PADDING = 5;
        final String translatedName;
        final String description;
        protected final LinkedList<Widget> widgets = new LinkedList<>();
        @Nullable
        public Button resetButton;
        @Nullable
        public Button undoButton;
        @Deprecated
        protected Runnable reset;
        @Deprecated
        protected BooleanSupplier canReset;

        public ConfigElement(String translatedName, String description) {
            this.translatedName = translatedName;
            this.description = description;
            if (maxListLabelWidth < translatedName.length())
                maxListLabelWidth = translatedName.length();
        }

        @Override
        // getEventListeners
        public List<? extends IGuiEventListener> func_231039_at__() {
            return widgets;
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int p_230432_2_, int elementRenderY, int p_230432_4_, int p_230432_5_, int p_230432_6_, int mouseX, int mouseY, boolean isHovered, float partialTicks) {
            final Iterator<Widget> iterator = widgets.descendingIterator();
            int widgetPos = func_230952_d_() - PADDING; // getScrollbarPosition
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
        }

        public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (description != null && description.length() > 0) {
                // renderTooltip
                List<ITextComponent> list = Arrays.stream(description.split("\n")).map(StringTextComponent::new).collect(Collectors.toList());
                ConfigElementList.this.configScreen.func_243308_b(matrixStack, list, mouseX, mouseY);
            }
        }

        /**
         * Makes the cursor of any {@link TextFieldWidget}s animate.
         */
        public void tick() {
//            // active/enabled
//            resetButton.field_230693_o_ = canReset.getAsBoolean();
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

    /**
     * {@link TextFieldWidget}s don't like to give up focus.
     */
    @Override
    // mouseClicked
    public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
        boolean wasAnythingSelected = super.func_231044_a_(mouseX, mouseY, mouseButton);
        if (!wasAnythingSelected)
            for (ConfigElement element : func_231039_at__()) // getEventListeners
                for (Widget widget : element.widgets)
                    if (widget instanceof TextFieldWidget)
                        ((TextFieldWidget) widget).setFocused2(false);
        return wasAnythingSelected;
    }

    /**
     * {@link TextFieldWidget}s don't like to give up focus.
     */
    @Override
    // clickedHeader
    protected void func_230938_a_(int relativeX, int relativeY) {
        super.func_230938_a_(relativeX, relativeY);
        for (ConfigElement element : func_231039_at__()) // getEventListeners
            for (Widget widget : element.widgets)
                if (widget instanceof TextFieldWidget)
                    ((TextFieldWidget) widget).setFocused2(false);
    }

    /**
     * {@link TextFieldWidget}s don't like to give up focus.
     */
    @Override
    // setListener
    public void func_231035_a_(IGuiEventListener newFocused) {
        ConfigElement oldFocused = func_241217_q_(); // getListener
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
            int width = func_230952_d_() - otherWidthsAndPadding - PADDING * 2;
            Button openScreen = new ExtendedButton(0, 0, width, ROW_HEIGHT, new StringTextComponent(translatedName), b -> field_230668_b_.displayGuiScreen(screenFactory.get()));
            widgets.add(0, openScreen);
        }
    }

}
