package net.minecraftforge.client.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.config.ConfigElementList.ConfigElement;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * This class standardises the layout/look/feel of a config screen.
 * It passes necessary events through to it's {@link ConfigElementList}.
 * <p>
 * TODO:
 * - Translucent scissored list
 * - Do I need to deal with sub-configs?
 * - Config Screen titles
 */
public abstract class ConfigScreen extends Screen {

    protected final Screen parentScreen;
    @Nullable
    protected final ITextComponent subTitle;
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
        int buttonHeight = 20;
        int buttonSize = (field_230708_k_ - padding * 2) / 3; // width
        int footerTop = configElementList.getBottom();
        int footerHeight = field_230709_l_ - footerTop; // height
        int y = footerTop + footerHeight / 2 - buttonHeight / 2;
        // Add the undo button
        undoButton = func_230480_a_(new Button(left, y, buttonSize, buttonHeight, new TranslationTextComponent("forge.configgui.undoAllChanges"), button -> undo()));
        // Add the reset button
        resetButton = func_230480_a_(new Button(left + buttonSize, y, buttonSize, buttonHeight, new TranslationTextComponent("forge.configgui.resetAllToDefault"), button -> reset()));
        // Add the "done" button
        func_230480_a_(new Button(left + buttonSize * 2, y, buttonSize, buttonHeight, DialogTexts.field_240632_c_, button -> func_231175_as__()));
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
        func_238472_a_(matrixStack, field_230712_o_, field_230704_d_, field_230708_k_ / 2, 8, 0xFFFFFF);
        if (subTitle != null)
            func_238472_a_(matrixStack, field_230712_o_, subTitle, field_230708_k_ / 2, 8 + field_230712_o_.FONT_HEIGHT + 8, 0xA0A0A0);

        // Renders our widgets for us
        // super.render(matrixStack, mouseX, mouseY, partialTicks);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        if (undoButton.func_230449_g_()) // isHovered
            GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(new TranslationTextComponent("forge.configgui.undoAllChanges.tooltip")), mouseX, mouseY, field_230708_k_, field_230709_l_, -1, getFontRenderer()); // width, height
        if (resetButton.func_230449_g_()) // isHovered
            GuiUtils.drawHoveringText(matrixStack, Collections.singletonList(new TranslationTextComponent("forge.configgui.resetAllToDefault.tooltip")), mouseX, mouseY, field_230708_k_, field_230709_l_, -1, getFontRenderer()); // width, height
    }

    @Override
    // tick
    public void func_231023_e_() {
        configElementList.tick();
        // active/enabled
        resetButton.field_230693_o_ = canReset();
        undoButton.field_230693_o_ = canUndo();
    }

    protected boolean canReset() {
        for (ConfigElement element : configElementList.func_231039_at__()) {
            Button resetButton = element.resetButton;
            // btn != null && btn.visible && btn.active/enabled
            if (resetButton != null && resetButton.field_230694_p_ && resetButton.field_230693_o_)
                return true;
        }
        return false;
    }

    protected boolean canUndo() {
        for (ConfigElement element : configElementList.func_231039_at__()) {
            Button undoButton = element.undoButton;
            // btn != null && btn.visible && btn.active/enabled
            if (undoButton != null && undoButton.field_230694_p_ && undoButton.field_230693_o_)
                return true;
        }
        return false;
    }

    /**
     * Called when the value of a config element on a {@link ConfigElementList} changes.
     */
    public void onChange(boolean requiresWorldRestart) {
        if (parentScreen instanceof ConfigScreen)
            ((ConfigScreen) parentScreen).onChange(requiresWorldRestart);
    }

    /**
     * Called to set all of the current screen's config elements to their default values.
     */
    protected void reset() {
        for (ConfigElementList.ConfigElement element : configElementList.func_231039_at__())
            if (element.resetButton != null)
                element.resetButton.func_230930_b_();
    }

    /**
     * Called to set all of the current screen's config elements to their initial values.
     */
    protected void undo() {
        for (ConfigElementList.ConfigElement element : configElementList.func_231039_at__())
            if (element.undoButton != null)
                element.undoButton.func_230930_b_();
    }

    @Override
    // closeScreen
    public void func_231175_as__() {
        field_230706_i_.displayGuiScreen(parentScreen);
    }

    public FontRenderer getFontRenderer() {
        return field_230712_o_;
    }

    public ConfigElementControls getControlCreator() {
        if (parentScreen instanceof ConfigScreen)
            return ((ConfigScreen) parentScreen).getControlCreator();
        return ConfigElementControls.getDefaultCreator();
    }
}
