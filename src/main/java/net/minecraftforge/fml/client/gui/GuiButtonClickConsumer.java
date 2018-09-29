package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonClickConsumer extends GuiButton {
    public interface DoubleBiConsumer {
        void applyAsDouble(double x, double y);
    }
    private final DoubleBiConsumer onClickAction;

    public GuiButtonClickConsumer(final int buttonId, final int x, final int y, final String buttonText, DoubleBiConsumer onClick) {
        super(buttonId, x, y, buttonText);
        this.onClickAction = onClick;
    }
    public GuiButtonClickConsumer(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText, DoubleBiConsumer onClick) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.onClickAction = onClick;
    }

    @Override
    public void onClick(final double mouseX, final double mouseY) {
        onClickAction.applyAsDouble(mouseX, mouseY);
    }
}
