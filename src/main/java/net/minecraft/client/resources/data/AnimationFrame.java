package net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AnimationFrame
{
    private final int frameIndex;
    private final int frameTime;
    private static final String __OBFID = "CL_00001104";

    public AnimationFrame(int par1)
    {
        this(par1, -1);
    }

    public AnimationFrame(int par1, int par2)
    {
        this.frameIndex = par1;
        this.frameTime = par2;
    }

    public boolean hasNoTime()
    {
        return this.frameTime == -1;
    }

    public int getFrameTime()
    {
        return this.frameTime;
    }

    public int getFrameIndex()
    {
        return this.frameIndex;
    }
}