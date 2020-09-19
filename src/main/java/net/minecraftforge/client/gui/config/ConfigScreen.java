package net.minecraftforge.client.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * This class needs to handle holding state for the displayed config.
 * It should be the "master" class that everything passes through.
 * Duties:
 * - To load the stuff for the config file
 * - To display a message to the user when the config file on the system is changed
 * and the displayed version needs to be updated (display a list of the user's changes
 * and wait for them to click reload, maybe in the future allow them to merge their changes)
 * - To manage the scrollable screen displaying the widgets
 */
public abstract class ConfigScreen extends Screen {

    private final Screen parentScreen;
    @Nullable
    private final ITextComponent subTitle;
    protected Button resetButton;
//    protected Button undoButton;
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
    protected void func_231160_c_() {
        super.func_231160_c_();
        configElementList = makeConfigElementList();
        // children.add
        field_230705_e_.add(configElementList);
        resetButton = func_230480_a_(new Button(field_230708_k_ / 2 - 155, field_230709_l_ - 29, 150, 20, new TranslationTextComponent("reset.config"), (p_213125_1_) -> {
            resetConfig();
        }));
        // Add the "done" button
        func_230480_a_(new Button(field_230708_k_ / 2 - 155 + 160, field_230709_l_ - 29, 150, 20, DialogTexts.field_240632_c_, b -> field_230706_i_.displayGuiScreen(parentScreen)));
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
    public void func_231023_e_() {
        this.configElementList.tick();
        boolean anyResettable = false;
        for (ConfigElementList.ConfigElement element : this.configElementList.func_231039_at__()) {
            if (!element.btnReset.field_230693_o_)
                continue;
            anyResettable = true;
            break;
        }
        // active/enabled
        this.resetButton.field_230693_o_ = anyResettable;
    }

    public void onChange() {
        if (parentScreen instanceof ConfigScreen)
            ((ConfigScreen) parentScreen).onChange();
    }

    protected void resetConfig() {
//        // Delegate resetting to the parent screen if we are a sub-screen (list/sub config)
//        if (parentScreen instanceof ConfigScreen) {
//            ((ConfigScreen) parentScreen).resetConfig();
//        } else {
//            // TODO: Reset
//        }
        for (ConfigElementList.ConfigElement element : this.configElementList.func_231039_at__())
            element.reset.run();
    }

    @Override
    // closeScreen
    public void func_231175_as__() {
        field_230706_i_.displayGuiScreen(parentScreen);
    }
}
