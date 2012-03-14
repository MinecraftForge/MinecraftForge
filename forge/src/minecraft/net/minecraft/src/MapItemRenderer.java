package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;

public class MapItemRenderer
{
    private int[] intArray = new int[16384];
    private int bufferedImage;
    private GameSettings gameSettings;
    private FontRenderer fontRenderer;

    public MapItemRenderer(FontRenderer par1FontRenderer, GameSettings par2GameSettings, RenderEngine par3RenderEngine)
    {
        this.gameSettings = par2GameSettings;
        this.fontRenderer = par1FontRenderer;
        this.bufferedImage = par3RenderEngine.allocateAndSetupTexture(new BufferedImage(128, 128, 2));

        for (int var4 = 0; var4 < 16384; ++var4)
        {
            this.intArray[var4] = 0;
        }
    }

    public void renderMap(EntityPlayer par1EntityPlayer, RenderEngine par2RenderEngine, MapData par3MapData)
    {
        for (int var4 = 0; var4 < 16384; ++var4)
        {
            byte var5 = par3MapData.colors[var4];

            if (var5 / 4 == 0)
            {
                this.intArray[var4] = (var4 + var4 / 128 & 1) * 8 + 16 << 24;
            }
            else
            {
                int var6 = MapColor.mapColorArray[var5 / 4].colorValue;
                int var7 = var5 & 3;
                short var8 = 220;

                if (var7 == 2)
                {
                    var8 = 255;
                }

                if (var7 == 0)
                {
                    var8 = 180;
                }

                int var9 = (var6 >> 16 & 255) * var8 / 255;
                int var10 = (var6 >> 8 & 255) * var8 / 255;
                int var11 = (var6 & 255) * var8 / 255;

                if (this.gameSettings.anaglyph)
                {
                    int var12 = (var9 * 30 + var10 * 59 + var11 * 11) / 100;
                    int var13 = (var9 * 30 + var10 * 70) / 100;
                    int var14 = (var9 * 30 + var11 * 70) / 100;
                    var9 = var12;
                    var10 = var13;
                    var11 = var14;
                }

                this.intArray[var4] = -16777216 | var9 << 16 | var10 << 8 | var11;
            }
        }

        par2RenderEngine.createTextureFromBytes(this.intArray, 128, 128, this.bufferedImage);
        byte var15 = 0;
        byte var16 = 0;
        Tessellator var17 = Tessellator.instance;
        float var18 = 0.0F;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.bufferedImage);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        var17.startDrawingQuads();
        var17.addVertexWithUV((double)((float)(var15 + 0) + var18), (double)((float)(var16 + 128) - var18), -0.009999999776482582D, 0.0D, 1.0D);
        var17.addVertexWithUV((double)((float)(var15 + 128) - var18), (double)((float)(var16 + 128) - var18), -0.009999999776482582D, 1.0D, 1.0D);
        var17.addVertexWithUV((double)((float)(var15 + 128) - var18), (double)((float)(var16 + 0) + var18), -0.009999999776482582D, 1.0D, 0.0D);
        var17.addVertexWithUV((double)((float)(var15 + 0) + var18), (double)((float)(var16 + 0) + var18), -0.009999999776482582D, 0.0D, 0.0D);
        var17.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        par2RenderEngine.bindTexture(par2RenderEngine.getTexture("/misc/mapicons.png"));
        Iterator var19 = par3MapData.playersVisibleOnMap.iterator();

        while (var19.hasNext())
        {
            MapCoord var20 = (MapCoord)var19.next();
            GL11.glPushMatrix();
            GL11.glTranslatef((float)var15 + (float)var20.centerX / 2.0F + 64.0F, (float)var16 + (float)var20.centerZ / 2.0F + 64.0F, -0.02F);
            GL11.glRotatef((float)(var20.iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(4.0F, 4.0F, 3.0F);
            GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
            float var21 = (float)(var20.field_28217_a % 4 + 0) / 4.0F;
            float var23 = (float)(var20.field_28217_a / 4 + 0) / 4.0F;
            float var22 = (float)(var20.field_28217_a % 4 + 1) / 4.0F;
            float var24 = (float)(var20.field_28217_a / 4 + 1) / 4.0F;
            var17.startDrawingQuads();
            var17.addVertexWithUV(-1.0D, 1.0D, 0.0D, (double)var21, (double)var23);
            var17.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)var22, (double)var23);
            var17.addVertexWithUV(1.0D, -1.0D, 0.0D, (double)var22, (double)var24);
            var17.addVertexWithUV(-1.0D, -1.0D, 0.0D, (double)var21, (double)var24);
            var17.draw();
            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -0.04F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        this.fontRenderer.drawString(par3MapData.mapName, var15, var16, -16777216);
        GL11.glPopMatrix();
    }
}
