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
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.UnicodeGlyphButton;

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

    protected final ConfigScreen configScreen;
    private int maxListLabelWidth;

    public ConfigElementList(ConfigScreen configScreen, Minecraft minecraft) {
        // super(minecraft, width, height, top, bottom, rowHeight);
        super(minecraft, configScreen.field_230708_k_, configScreen.field_230709_l_, 43, configScreen.field_230709_l_ - 32, 20);
        this.configScreen = configScreen;
    }

    public int getMaxListLabelWidth() {
        return maxListLabelWidth;
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
        return new UnicodeGlyphButton(0, 0, 20, 0, ConfigElementControls.EMPTY_STRING, GuiUtils.RESET_CHAR, 1, onPress) {
            @Override
            // getNarrationMessage
            protected IFormattableTextComponent func_230442_c_() {
                return new TranslationTextComponent("narrator.controls.reset", translatedName);
            }
        };
    }

    public static Button createConfigElementUndoButton(String translatedName, Button.IPressable onPress) {
        return new UnicodeGlyphButton(0, 0, 20, 0, ConfigElementControls.EMPTY_STRING, GuiUtils.UNDO_CHAR, 1, onPress) {
            @Override
            // getNarrationMessage
            protected IFormattableTextComponent func_230442_c_() {
                return new TranslationTextComponent("narrator.controls.undo", translatedName);
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

        public ConfigElement(String translatedName, String description) {
            this.translatedName = translatedName;
            this.description = description;
            int length = configScreen.getFontRenderer().getStringWidth(translatedName);
            if (maxListLabelWidth < length)
                maxListLabelWidth = length;
        }

        @Override
        // getEventListeners
        public List<? extends IGuiEventListener> func_231039_at__() {
            return widgets;
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int adjustedItemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            Iterator<Widget> iterator = widgets.descendingIterator();
            int widgetPos = func_230952_d_() - PADDING; // getScrollbarPosition
            while (iterator.hasNext()) {
                Widget widget = iterator.next();
                widgetPos -= widget.func_230998_h_(); // getWidth
                widget.field_230690_l_ = widgetPos; // x
                widget.field_230691_m_ = rowTop; // y
                widget.setHeight(getItemHeight());
                if (widget instanceof TextFieldWidget) {
                    widget.field_230690_l_ -= 2; // x
                    widget.field_230691_m_ += 2; // y
                    widget.setHeight(widget.func_238483_d_() - 4); // getHeight
                }
                // Render
                widget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (description != null && description.length() > 0) {
                List<ITextComponent> list = Arrays.stream(description.split("\n"))
                        .map(StringTextComponent::new)
                        .collect(Collectors.toList());
                GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, getWidth(), getHeight(), -1, configScreen.getFontRenderer());
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

    public int getItemHeight() {
        return field_230669_c_;
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
        public void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int adjustedItemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            super.func_230432_a_(matrixStack, index, rowTop, rowLeft, rowWidth, adjustedItemHeight, mouseX, mouseY, isSelected, partialTicks);
            field_230668_b_.fontRenderer.func_243248_b(matrixStack, this.nameComponent, rowLeft, (float) (rowTop + adjustedItemHeight / 2 - 9 / 2), 0xffffff);
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
            Button openScreen = new ExtendedButton(0, 0, width, 0, new StringTextComponent(translatedName), b -> field_230668_b_.displayGuiScreen(screenFactory.get()));
            widgets.add(0, openScreen);
        }
    }

}
