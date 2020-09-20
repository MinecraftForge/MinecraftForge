package net.minecraftforge.client.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * This class standardises the layout/look/feel of a config screen.
 * It passes necessary events through to it's {@link ConfigElementList}.
 * <p>
 * TODO:
 * - Stop tooltip rendering partially off screen
 * - Tooltips containing extra info about the item
 * - Fix list item control sizing (rowWidth - maxListLabelWidth - otherWidgetsWidth)
 * - Translucent scissored list
 * - Copy values
 * - Reset working properly for lists & categories
 * - Undo changes working properly for lists & categories
 * - Do I need to deal with sub-configs?
 * - Requires world restart (display screen on change)
 */
public abstract class ConfigScreen extends Screen {

    private final Screen parentScreen;
    @Nullable
    private final ITextComponent subTitle;
    protected Button resetButton;
    protected Button undoButton;
    protected ConfigElementList configElementList;

    public ConfigScreen(Screen parentScreen, ITextComponent title) {
        this(parentScreen, title, null);
    }

    public ConfigScreen(Screen parentScreen, ITextComponent title, @Nullable ITextComponent subTitle) {
        super(title);
        this.parentScreen = parentScreen;
        this.subTitle = subTitle;
    }

    @Override
    // init
    public void func_231158_b_(Minecraft minecraft, int scaledWidth, int scaledHeight) {
        super.func_231158_b_(minecraft, scaledWidth, scaledHeight);
        // Tick right after init so everything's set up properly for the first render
        this.func_231023_e_(); // tick
    }

    @Override
    // init
    protected void func_231160_c_() {
        super.func_231160_c_();
        configElementList = makeConfigElementList();
        // children.add
        field_230705_e_.add(configElementList);
        int padding = 10;
        int left = padding;
        int footerTop = configElementList.getBottom();
        int footerHeight = field_230709_l_ - footerTop; // height
        int y = footerTop + footerHeight / 2 - 20 / 2;
        int buttonSize = (field_230708_k_ - padding * 2) / 3; // width
        // Add the undo button
        undoButton = func_230480_a_(new Button(left, y, buttonSize, 20, new TranslationTextComponent("undo.config"), button -> undo()));
        // Add the reset button
        resetButton = func_230480_a_(new Button(left + buttonSize, y, buttonSize, 20, new TranslationTextComponent("reset.config"), button -> reset()));
        // Add the "done" button
        func_230480_a_(new Button(left + buttonSize * 2, y, buttonSize, 20, DialogTexts.field_240632_c_, button -> field_230706_i_.displayGuiScreen(parentScreen)));
    }

    protected abstract ConfigElementList makeConfigElementList();

    @Override
    // render
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // renderBackground(matrixStack)
        func_230446_a_(matrixStack);
        // configEntryList.render()
        configElementList.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);

        // drawCenteredString(matrixStack, fontRenderer, title, width / 2, 8, 0xFFFFFF)
        func_238472_a_(matrixStack, field_230712_o_, field_230704_d_, this.field_230708_k_ / 2, 8, 0xFFFFFF);
        if (subTitle != null)
            func_238472_a_(matrixStack, field_230712_o_, subTitle, this.field_230708_k_ / 2, 8 + field_230712_o_.FONT_HEIGHT + 8, 0xA0A0A0);

        // Renders our widgets for us
        // super.render(matrixStack, mouseX, mouseY, partialTicks);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    // tick
    public void func_231023_e_() {
        this.configElementList.tick();
        boolean anyResettable = false;
        boolean anyUndoable = false;
        for (ConfigElementList.ConfigElement element : this.configElementList.func_231039_at__()) {
            Button btnReset = element.resetButton;
            Button btnUndo = element.undoButton;
            // btn != null && btn.visible && btn.active/enabled
            anyResettable |= btnReset != null && btnReset.field_230694_p_ && btnReset.field_230693_o_;
            anyUndoable |= btnUndo != null && btnUndo.field_230694_p_ && btnUndo.field_230693_o_;
            if (anyResettable && anyUndoable)
                // We've checked as far as we need to
                break;
        }
        // active/enabled
        this.resetButton.field_230693_o_ = anyResettable;
        this.undoButton.field_230693_o_ = anyUndoable;
    }

    /**
     * Called when the value of a config element on a {@link ConfigElementList} changes.
     */
    public void onChange() {
        if (parentScreen instanceof ConfigScreen)
            ((ConfigScreen) parentScreen).onChange();
    }

    /**
     * Called to set all of the current screen's config elements to their default values.
     */
    protected void reset() {
//        for (ConfigElementList.ConfigElement element : this.configElementList.func_231039_at__())
//            element.reset.run();
    }

    /**
     * Called to set all of the current screen's config elements to their initial values.
     */
    protected void undo() {
//        for (ConfigElementList.ConfigElement element : this.configElementList.func_231039_at__())
//            element.undo.run();
    }

    @Override
    // closeScreen
    public void func_231175_as__() {
        field_230706_i_.displayGuiScreen(parentScreen);
    }
}
