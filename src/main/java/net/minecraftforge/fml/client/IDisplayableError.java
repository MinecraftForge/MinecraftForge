package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDisplayableError
{
    @SideOnly(Side.CLIENT)
    GuiScreen createGui();
}
