package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.widget.button.Button;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sub-interface with generic parameter to allow using correct class in lambda
 */
public interface IPressHandler<W extends Button> extends Button.IPressable {

    @ParametersAreNonnullByDefault
    @SuppressWarnings("unchecked")
    @Override
    default void onPress(Button p_onPress_1_) {
        onPress2((W) p_onPress_1_);
    }

    /**
     * @param button {@link Button} instance
     */
    void onPress2(W button);
}
