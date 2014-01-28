package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.gui.IUpdatePlayerListBox;

@SideOnly(Side.CLIENT)
public interface ITickableSound extends ISound, IUpdatePlayerListBox
{
    boolean func_147667_k();
}