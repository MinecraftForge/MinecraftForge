package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTntMinecart extends RenderMinecart
{
    private static final String __OBFID = "CL_00001029";

    protected void func_147910_a(EntityMinecartTNT p_147912_1_, float p_147912_2_, Block p_147912_3_, int p_147912_4_)
    {
        int j = p_147912_1_.func_94104_d();

        if (j > -1 && (float)j - p_147912_2_ + 1.0F < 10.0F)
        {
            float f1 = 1.0F - ((float)j - p_147912_2_ + 1.0F) / 10.0F;

            if (f1 < 0.0F)
            {
                f1 = 0.0F;
            }

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }

            f1 *= f1;
            f1 *= f1;
            float f2 = 1.0F + f1 * 0.3F;
            GL11.glScalef(f2, f2, f2);
        }

        super.func_147910_a(p_147912_1_, p_147912_2_, p_147912_3_, p_147912_4_);

        if (j > -1 && j / 5 % 2 == 0)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)j - p_147912_2_ + 1.0F) / 100.0F) * 0.8F);
            GL11.glPushMatrix();
            this.field_94145_f.func_147800_a(Blocks.tnt, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    protected void func_147910_a(EntityMinecart p_147910_1_, float p_147910_2_, Block p_147910_3_, int p_147910_4_)
    {
        this.func_147910_a((EntityMinecartTNT)p_147910_1_, p_147910_2_, p_147910_3_, p_147910_4_);
    }
}