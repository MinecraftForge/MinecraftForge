package net.minecraft.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer
{
    private static final String __OBFID = "CL_00000968";

    public void func_147500_a(TileEntityMobSpawner p_147518_1_, double p_147518_2_, double p_147518_4_, double p_147518_6_, float p_147518_8_)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)p_147518_2_ + 0.5F, (float)p_147518_4_, (float)p_147518_6_ + 0.5F);
        func_147517_a(p_147518_1_.func_145881_a(), p_147518_2_, p_147518_4_, p_147518_6_, p_147518_8_);
        GL11.glPopMatrix();
    }

    public static void func_147517_a(MobSpawnerBaseLogic p_147517_0_, double p_147517_1_, double p_147517_3_, double p_147517_5_, float p_147517_7_)
    {
        Entity entity = p_147517_0_.func_98281_h();

        if (entity != null)
        {
            entity.setWorld(p_147517_0_.getSpawnerWorld());
            float f1 = 0.4375F;
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GL11.glRotatef((float)(p_147517_0_.field_98284_d + (p_147517_0_.field_98287_c - p_147517_0_.field_98284_d) * (double)p_147517_7_) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(f1, f1, f1);
            entity.setLocationAndAngles(p_147517_1_, p_147517_3_, p_147517_5_, 0.0F, 0.0F);
            RenderManager.instance.func_147940_a(entity, 0.0D, 0.0D, 0.0D, 0.0F, p_147517_7_);
        }
    }

    public void func_147500_a(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
    {
        this.func_147500_a((TileEntityMobSpawner)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}