package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class PositionedSound implements ISound
{
    protected final ResourceLocation field_147664_a;
    protected float field_147662_b = 1.0F;
    protected float field_147663_c = 1.0F;
    protected float field_147660_d;
    protected float field_147661_e;
    protected float field_147658_f;
    protected boolean field_147659_g = false;
    protected int field_147665_h = 0;
    protected ISound.AttenuationType field_147666_i;
    private static final String __OBFID = "CL_00001116";

    protected PositionedSound(ResourceLocation p_i45103_1_)
    {
        this.field_147666_i = ISound.AttenuationType.LINEAR;
        this.field_147664_a = p_i45103_1_;
    }

    public ResourceLocation func_147650_b()
    {
        return this.field_147664_a;
    }

    public boolean func_147657_c()
    {
        return this.field_147659_g;
    }

    public int func_147652_d()
    {
        return this.field_147665_h;
    }

    public float func_147653_e()
    {
        return this.field_147662_b;
    }

    public float func_147655_f()
    {
        return this.field_147663_c;
    }

    public float func_147649_g()
    {
        return this.field_147660_d;
    }

    public float func_147654_h()
    {
        return this.field_147661_e;
    }

    public float func_147651_i()
    {
        return this.field_147658_f;
    }

    public ISound.AttenuationType func_147656_j()
    {
        return this.field_147666_i;
    }
}