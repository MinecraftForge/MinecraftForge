package net.minecraftforge.fml.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

/**
 * Copy of {@link DisconnectedScreen} but with custom button text.
 */
public class MessageDialogScreen extends Screen {
    private final ITextComponent message;
    private final ITextComponent buttonTitle;
    private final Runnable goToNextScreen;
    // Stolen from DisconnectedScreen, change this once it's mapped
    private IBidiRenderer field_243289_b = IBidiRenderer.field_243257_a;
    private int textHeight;

    public MessageDialogScreen(ITextComponent title, ITextComponent message, ITextComponent buttonTitle, Screen nextScreen) {
        this(title, message, buttonTitle, () -> Minecraft.getInstance().displayGuiScreen(nextScreen));
    }

    public MessageDialogScreen(ITextComponent title, ITextComponent message, ITextComponent buttonTitle, Runnable goToNextScreen) {
        super(title);
        this.message = message;
        this.buttonTitle = buttonTitle;
        this.goToNextScreen = goToNextScreen;
    }

    @Override
    // shouldCloseOnEsc
    public boolean func_231178_ax__() {
        return false;
    }

    @Override
    // init
    protected void func_231160_c_() {
        this.field_243289_b = IBidiRenderer.func_243258_a(this.field_230712_o_, this.message, this.field_230708_k_ - 50);
        this.textHeight = this.field_243289_b.func_241862_a() * 9;
        this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 100, Math.min(this.field_230709_l_ / 2 + this.textHeight / 2 + 9, this.field_230709_l_ - 30), 200, 20, buttonTitle, b -> goToNextScreen.run()));
    }

    @Override
    // render
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // drawBackground
        this.func_230446_a_(matrixStack);
        // drawCenteredString
        func_238472_a_(matrixStack, this.field_230712_o_, this.field_230704_d_, this.field_230708_k_ / 2, this.field_230709_l_ / 2 - this.textHeight / 2 - 9 * 2, 11184810);
        this.field_243289_b.func_241863_a(matrixStack, this.field_230708_k_ / 2, this.field_230709_l_ / 2 - this.textHeight / 2);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    }
}
