package net.minecraft.client.gui.mco;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public abstract class ScreenWithCallback extends GuiScreen
{
    private static final String __OBFID = "CL_00000812";

    abstract void func_146735_a(Object var1);
}