package net.minecraftforge.client.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.*;
import net.minecraftforge.client.gui.config.CategoryConfigScreen.CategoryConfigElementList.CategoryConfigElement;
import net.minecraftforge.client.gui.config.CategoryConfigScreen.CategoryConfigElementList.ConfigValueConfigElement;
import net.minecraftforge.client.gui.config.ListConfigScreen.ListConfigElementList.ListItemConfigElement;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.UnicodeGlyphButton;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A scrolling list for a {@link ConfigScreen}.
 *
 * @author bspkrs
 * @author Cadiboo
 */
public class ConfigElementList extends AbstractOptionList<ConfigElementList.ConfigElement> {

    public static final StringTextComponent EMPTY_STRING = new StringTextComponent("");

    // From AbstractList#render/func_230430_a_
    public static final int SCROLLBAR_WIDTH = 6;

    public final ConfigScreen configScreen;
    protected int maxListLabelWidth;

    public ConfigElementList(ConfigScreen configScreen, Minecraft minecraft) {
        // super(minecraft, width, height, top, bottom, rowHeight);
        super(minecraft, configScreen.field_230708_k_, configScreen.field_230709_l_, 43, configScreen.field_230709_l_ - 32, 20);
        this.configScreen = configScreen;
    }

    public static Button createConfigElementResetButton(ITextComponent title, Button.IPressable onPress) {
        return new UnicodeGlyphButton(0, 0, 20, 0, EMPTY_STRING, GuiUtils.RESET_CHAR, ConfigScreen.GLYPH_SCALE, onPress) {
            @Override
            // getNarrationMessage
            protected IFormattableTextComponent func_230442_c_() {
                return new TranslationTextComponent("narrator.controls.reset", title);
            }
        };
    }

    public static Button createConfigElementUndoButton(ITextComponent title, Button.IPressable onPress) {
        return new UnicodeGlyphButton(0, 0, 20, 0, EMPTY_STRING, GuiUtils.UNDO_CHAR, ConfigScreen.GLYPH_SCALE, onPress) {
            @Override
            // getNarrationMessage
            protected IFormattableTextComponent func_230442_c_() {
                return new TranslationTextComponent("narrator.controls.undo", title);
            }
        };
    }

    public static int correctWidgetBound(Widget widget, int widthOrHeight) {
        // TextFields have a 2px border on each side that is not included in their width/height
        if (widget instanceof TextFieldWidget)
            return widthOrHeight - 4;
        return widthOrHeight;
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

    /**
     * The base class for anything that can be displayed in a row on a {@link ConfigElementList}.
     * This contains the undo and reset buttons, breaking encapsulation a bit, but it allows simple implementations of
     * the undo and reset buttons on the {@link ConfigScreen} that this belongs to.
     * <p>
     * Examples:
     * 1. Text only element (might be used for displaying an error message e.g. "Could not generate a widget for this
     * config value, please edit this in the config file")
     * - Has a label to display the text
     * - Has a title for use in the narrator
     * - May have widgets
     * - May have a tooltip
     * <p>
     * 2. {@link ConfigValueConfigElement Element for a "config value" (a named value in a config)}
     * - Has a label to display its name
     * - Has a title (its name) for use in the narrator
     * - Has a widget to interact with (view and change) its value
     * - Has buttons to undo changes to the value and reset the value to default
     * - Has a tooltip containing info about the config value (e.g. its comment)
     * <p>
     * 3. {@link CategoryConfigElement Element for a (sub)category in the config}
     * - Does not have a label (it displays its name on its main button)
     * - Has a title (its name) for use in the narrator
     * - Has a widget to take you to a new screen to interact with (view and change) the values in this category
     * - Has no undo/reset buttons
     * - Has a tooltip containing info about the category (e.g. its comment) TODO: Forge currently doesn't provide translation keys for Categories
     * <p>
     * 4. Element for a List "config value" (a named value (that is a List) in a config)
     * - Does not have a label (it displays its name on its main button)
     * - Has a title (it's name) for use in the narrator
     * - Has a widget to take you to a new screen to interact with (view and change) the items in this list
     * - Has buttons to undo changes to the value and reset the value to default
     * - Has a tooltip containing info about the config value (e.g. its comment)
     * 5. {@link ListItemConfigElement Element for an item inside a List}
     * - Has a label to display its index
     * - Has a title (its index) for use in the narrator
     * - Has a widget to take you to a new screen to interact with (view and change) its value (it might open a new screen, see "Element for a List")
     * - Has buttons to add a value to the list below it or remove itself from the list
     * - No tooltip
     */
    public class ConfigElement extends AbstractOptionList.Entry<ConfigElement> {
        public static final int PADDING = 5;
        protected final LinkedList<Widget> widgets = new LinkedList<>();
        /**
         * The TEXT to display before any widgets.
         * E.g. this element's name.
         * Will be null for list/category elements (they display this info on their main button).
         * A non-null value for this increments {@link #maxListLabelWidth}.
         */
        @Nullable
        final ITextProperties label;
        /** The title of this element. Describes what this is. Used for narration messages. */
        final ITextProperties title;
        /** Will be null/empty for list item entries who have nothing useful to display. */
        @Nullable
        final List<? extends ITextProperties> tooltip;
        @Nullable
        public Button undoButton;
        @Nullable
        public Button resetButton;

        public ConfigElement(@Nullable ITextProperties label, ITextProperties title, @Nullable List<? extends ITextProperties> tooltip) {
            this.label = label;
            this.title = title;
            this.tooltip = tooltip;
            if (label != null)
                maxListLabelWidth = Math.max(maxListLabelWidth, configScreen.getFontRenderer().func_238414_a_(label)); // getStringPropertyWidth
        }

        @Override
        // getEventListeners
        public List<? extends IGuiEventListener> func_231039_at__() {
            return widgets;
        }

        @Override
        // render
        public void func_230432_a_(MatrixStack matrixStack, int index, int rowTop, int rowLeft, int rowWidth, int adjustedItemHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            if (label != null)
                configScreen.getFontRenderer().func_238418_a_(label, rowLeft, (rowTop + getItemHeight() / 2 - configScreen.getFontRenderer().FONT_HEIGHT / 2), Integer.MAX_VALUE, 0xFFFFFF);
            // Expand the main widget to fill all free space
            // If the label is not null, the free space starts at rowLeft + maxListLabelWidth, otherwise it starts at rowLeft
            Widget main = getMainWidget();
            if (main != null) {
                int otherWidthsAndPadding = widgets.stream().mapToInt(Widget::func_230998_h_).sum(); // getHeight
                otherWidthsAndPadding -= main.func_230998_h_(); // The main widget was included in the sum, subtract it // getHeight
                int width = func_230952_d_() - otherWidthsAndPadding - PADDING * 2; // getScrollbarPosition
                if (label != null)
                    width -= getMaxListLabelWidth() + PADDING;
                main.func_230991_b_(correctWidgetBound(main, width)); // setWidth
            }
            // Position widgets with the first item on the left and the last on the right
            Iterator<Widget> iterator = widgets.descendingIterator();
            int widgetPos = func_230952_d_() - PADDING; // getScrollbarPosition
            while (iterator.hasNext()) {
                Widget widget = iterator.next();
                widgetPos -= widget.func_230998_h_(); // getWidth
                widget.field_230690_l_ = widgetPos; // x
                widget.field_230691_m_ = rowTop; // y
                widget.setHeight(correctWidgetBound(widget, getItemHeight()));
                if (widget instanceof TextFieldWidget) {
                    // TextFields have a 2px border on each side that has to be accounted for
                    widget.field_230690_l_ -= 2; // x
                    widget.field_230691_m_ += 2; // y
                }
                // render
                widget.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
            }
        }

        public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            for (Widget widget : widgets) {
                if (resetButton != null && widget == resetButton && widget.func_230449_g_()) { // isHovered
                    List<ITextProperties> list = Collections.singletonList(new TranslationTextComponent("forge.configgui.resetToDefault.tooltip"));
                    GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, getWidth(), getHeight(), -1, configScreen.getFontRenderer());
                    return; // Stop main tooltip being rendered
                }
                if (undoButton != null && widget == undoButton && widget.func_230449_g_()) { // isHovered
                    List<ITextProperties> list = Collections.singletonList(new TranslationTextComponent("forge.configgui.undoChanges.tooltip"));
                    GuiUtils.drawHoveringText(matrixStack, list, mouseX, mouseY, getWidth(), getHeight(), -1, configScreen.getFontRenderer());
                    return; // Stop main tooltip being rendered
                }
            }
            if (tooltip != null && !tooltip.isEmpty())
                GuiUtils.drawHoveringText(matrixStack, tooltip, mouseX, mouseY, getWidth(), getHeight(), -1, configScreen.getFontRenderer());
        }

        /**
         * Makes the cursor of any {@link TextFieldWidget}s animate.
         */
        public void tick() {
            for (Widget widget : widgets)
                if (widget instanceof TextFieldWidget)
                    ((TextFieldWidget) widget).tick();
        }

        public Widget getMainWidget() {
            return widgets.isEmpty() ? null : widgets.getFirst();
        }

        @Override
        // mouseClicked
        public boolean func_231044_a_(double mouseX, double mouseY, int mouseBtn) {
            for (Widget widget : widgets)
                if (widget.func_231044_a_(mouseX, mouseY, mouseBtn))
                    return true;
            return false;
        }

        @Override
        // mouseReleased
        public boolean func_231048_c_(double mouseX, double mouseY, int mouseBtn) {
            for (Widget widget : widgets)
                if (widget.func_231048_c_(mouseX, mouseY, mouseBtn))
                    return true;
            return false;
        }

    }

}
