package net.minecraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public abstract class TaskLongRunning implements Runnable
{
    protected GuiScreenLongRunningTask field_148419_b;
    private static final String __OBFID = "CL_00000784";

    public void func_148412_a(GuiScreenLongRunningTask p_148412_1_)
    {
        this.field_148419_b = p_148412_1_;
    }

    public void func_148416_a(String p_148416_1_)
    {
        this.field_148419_b.func_146905_a(p_148416_1_);
    }

    public void func_148417_b(String p_148417_1_)
    {
        this.field_148419_b.func_146906_b(p_148417_1_);
    }

    public Minecraft func_148413_b()
    {
        return this.field_148419_b.func_146903_h();
    }

    public boolean func_148418_c()
    {
        return this.field_148419_b.func_146904_i();
    }

    public void func_148414_a() {}

    public void func_148415_a(GuiButton p_148415_1_) {}

    public void func_148411_d() {}
}