package net.minecraft.src.forge;

import net.minecraft.src.*;
import net.minecraft.src.forge.packets.PacketMissingMods;

public class GuiMissingMods extends GuiScreen
{
    PacketMissingMods packet;
    public GuiMissingMods(PacketMissingMods pkt)
    {
        packet = pkt;
    }

    public void initGui()
    {
        controlList.clear();
        controlList.add(new GuiButton(0, width / 2 - 100, height - 60, StringTranslate.getInstance().translateKey("gui.toMenu")));
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.id == 0)
        {
            mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "The server requires you to have the following mods:", width / 2, 50, 0xffffff);
        int y = 0;
        for (String mod : packet.Mods)
        {
            drawCenteredString(fontRenderer, mod, width / 2, 80 + y++ * 10, 0xffffff);
        }
        super.drawScreen(i, j, f);
    }
}
