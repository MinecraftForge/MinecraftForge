package net.minecraftforge.client.gui.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Map;

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
public class ConfigScreen extends Screen {

    private final Screen parentScreen;
    // TODO: Fill this with a deep copy of the modConfig
    Map<String, Object> values = new HashMap<>();

    {
        values.put("foo", true);
        values.put("bar", 1);
    }

    public ConfigScreen(Screen parentScreen, ITextComponent titleIn) {
        super(titleIn);
        this.parentScreen = parentScreen;
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        ITextComponent text = new TranslationTextComponent("fml.menu.mods.search");
        field_230712_o_.func_238422_b_(matrixStack, text.func_241878_f(), 0, 0, 0xFFFFFF);
    }

    @Override
    public void func_231175_as__() {
        field_230706_i_.displayGuiScreen(parentScreen);
    }
}
