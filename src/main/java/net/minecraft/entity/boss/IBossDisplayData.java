package net.minecraft.entity.boss;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IChatComponent;

public interface IBossDisplayData
{
    float getMaxHealth();

    float getHealth();

    IChatComponent func_145748_c_();
}