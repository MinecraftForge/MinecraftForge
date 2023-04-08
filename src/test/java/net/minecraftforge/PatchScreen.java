package net.minecraftforge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PatchScreen extends Screen {
    public PatchScreen(Component p_96550_) {
        super(p_96550_);
    }


    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
    }
}
