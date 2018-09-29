package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.GuiButton;

import java.util.function.DoubleBinaryOperator;

public class GuiButtonClickConsumer extends GuiButton {
    private final DoubleBinaryOperator onClickAction;

    public GuiButtonClickConsumer(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText, DoubleBinaryOperator onClick) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.onClickAction = onClick;
    }

    @Override
    public void onClick(final double mouseX, final double mouseY) {
        onClickAction.applyAsDouble(mouseX, mouseY);
    }
}
