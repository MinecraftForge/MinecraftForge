package net.minecraftforge.fml.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class DefaultConfigScreen extends Screen {
    protected Screen previousScreen;
    protected ForgeConfigSpec configSpec;

    public DefaultConfigScreen(ITextComponent titleIn, Screen previousScreen, ForgeConfigSpec forgeConfigSpec) {
        super(titleIn);
        this.previousScreen = previousScreen;
        this.configSpec = forgeConfigSpec;
    }

    @Override
    protected void init() {
        super.init();
        Map<String, Object> specValues = configSpec.getSpec().valueMap();
        Map<String, Object> valueValues = configSpec.getValues().valueMap();
        ;
        int spacing = 30;
        for (String key : specValues.keySet()) {
            Object value = specValues.get(key);
            if (value instanceof ForgeConfigSpec.ValueSpec) {
                ForgeConfigSpec.ValueSpec valueSpec = (ForgeConfigSpec.ValueSpec) value;
                Object specValue = valueValues.get(key);
                if (specValue instanceof ForgeConfigSpec.BooleanValue) {
                    ForgeConfigSpec.BooleanValue booleanValue = (ForgeConfigSpec.BooleanValue) specValue;
                    Boolean b = booleanValue.get();
                }
            }
        }
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        renderBackground();
        drawCenteredString(font, title.getFormattedText(), width / 2, 6, 0xffffff);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void onClose() {
        minecraft.displayGuiScreen(previousScreen);
    }
}
