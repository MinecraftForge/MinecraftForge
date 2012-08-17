package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.Tessellator;
import net.minecraft.src.WorldClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class SkyProviderEnd extends SkyProvider
{
    public static final SkyProviderEnd instance = new SkyProviderEnd();

    @Override
    @SideOnly(Side.CLIENT)
    public void render(float partialTicks, WorldClient world, Minecraft mc)
    {
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.disableStandardItemLighting();
        GL11.glDepthMask(false);
        mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/misc/tunnel.png"));
        Tessellator var21 = Tessellator.instance;

        for (int var22 = 0; var22 < 6; ++var22)
        {
            GL11.glPushMatrix();

            if (var22 == 1)
            {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 2)
            {
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 3)
            {
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 4)
            {
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (var22 == 5)
            {
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            var21.startDrawingQuads();
            var21.setColorOpaque_I(2631720);
            var21.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
            var21.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
            var21.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
            var21.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
            var21.draw();
            GL11.glPopMatrix();
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
