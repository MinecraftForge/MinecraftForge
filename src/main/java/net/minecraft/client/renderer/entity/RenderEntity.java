package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEntity extends Render
{
    private static final String __OBFID = "CL_00000986";

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        renderOffsetAABB(par1Entity.boundingBox, par2 - par1Entity.lastTickPosX, par4 - par1Entity.lastTickPosY, par6 - par1Entity.lastTickPosZ);
        GL11.glPopMatrix();
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return null;
    }
}