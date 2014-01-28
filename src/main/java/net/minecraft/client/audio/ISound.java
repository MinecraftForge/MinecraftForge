package net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public interface ISound
{
    ResourceLocation func_147650_b();

    boolean func_147657_c();

    int func_147652_d();

    float func_147653_e();

    float func_147655_f();

    float func_147649_g();

    float func_147654_h();

    float func_147651_i();

    ISound.AttenuationType func_147656_j();

    @SideOnly(Side.CLIENT)
    public static enum AttenuationType
    {
        NONE(0),
        LINEAR(2);
        private final int field_148589_c;

        private static final String __OBFID = "CL_00001126";

        private AttenuationType(int p_i45110_3_)
        {
            this.field_148589_c = p_i45110_3_;
        }

        public int func_148586_a()
        {
            return this.field_148589_c;
        }
    }
}