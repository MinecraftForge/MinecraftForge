package net.minecraftforge.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Vec3;
import net.minecraft.src.WorldClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class SkyProviderOverworld extends SkyProvider
{
    public static final SkyProviderOverworld instance = new SkyProviderOverworld();

    private int starGLCallList;

    private int glSkyList;

    private int glSkyList2;

    public SkyProviderOverworld()
    {
        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator var5 = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        byte var7 = 64;
        int var8 = 256 / var7 + 2;
        float var6 = 16.0F;
        int var9;
        int var10;

        for (var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7)
        {
            for (var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7)
            {
                var5.startDrawingQuads();
                var5.addVertex((double) (var9 + 0), (double) var6, (double) (var10 + 0));
                var5.addVertex((double) (var9 + var7), (double) var6, (double) (var10 + 0));
                var5.addVertex((double) (var9 + var7), (double) var6, (double) (var10 + var7));
                var5.addVertex((double) (var9 + 0), (double) var6, (double) (var10 + var7));
                var5.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        var6 = -16.0F;
        var5.startDrawingQuads();

        for (var9 = -var7 * var8; var9 <= var7 * var8; var9 += var7)
        {
            for (var10 = -var7 * var8; var10 <= var7 * var8; var10 += var7)
            {
                var5.addVertex((double) (var9 + var7), (double) var6, (double) (var10 + 0));
                var5.addVertex((double) (var9 + 0), (double) var6, (double) (var10 + 0));
                var5.addVertex((double) (var9 + 0), (double) var6, (double) (var10 + var7));
                var5.addVertex((double) (var9 + var7), (double) var6, (double) (var10 + var7));
            }
        }

        var5.draw();
        GL11.glEndList();
    }

    private void renderStars()
    {
        Random var1 = new Random(10842L);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();

        for (int var3 = 0; var3 < 1500; ++var3)
        {
            double var4 = (double) (var1.nextFloat() * 2.0F - 1.0F);
            double var6 = (double) (var1.nextFloat() * 2.0F - 1.0F);
            double var8 = (double) (var1.nextFloat() * 2.0F - 1.0F);
            double var10 = (double) (0.15F + var1.nextFloat() * 0.1F);
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D)
            {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                double var14 = var4 * 100.0D;
                double var16 = var6 * 100.0D;
                double var18 = var8 * 100.0D;
                double var20 = Math.atan2(var4, var8);
                double var22 = Math.sin(var20);
                double var24 = Math.cos(var20);
                double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                double var28 = Math.sin(var26);
                double var30 = Math.cos(var26);
                double var32 = var1.nextDouble() * Math.PI * 2.0D;
                double var34 = Math.sin(var32);
                double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38)
                {
                    double var39 = 0.0D;
                    double var41 = (double) ((var38 & 2) - 1) * var10;
                    double var43 = (double) ((var38 + 1 & 2) - 1) * var10;
                    double var47 = var41 * var36 - var43 * var34;
                    double var49 = var43 * var36 + var41 * var34;
                    double var53 = var47 * var28 + var39 * var30;
                    double var55 = var39 * var28 - var47 * var30;
                    double var57 = var55 * var22 - var49 * var24;
                    double var61 = var49 * var22 + var55 * var24;
                    var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
                }
            }
        }

        var2.draw();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(float partialTicks, WorldClient world, Minecraft mc)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Vec3 var2 = world.getSkyColor(mc.renderViewEntity, partialTicks);
        float var3 = (float) var2.xCoord;
        float var4 = (float) var2.yCoord;
        float var5 = (float) var2.zCoord;
        float var8;

        if (mc.gameSettings.anaglyph)
        {
            float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
            float var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
            var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
            var3 = var6;
            var4 = var7;
            var5 = var8;
        }

        GL11.glColor3f(var3, var4, var5);
        Tessellator var23 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glColor3f(var3, var4, var5);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.disableStandardItemLighting();
        float[] var24 = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
        float var9;
        float var10;
        float var11;
        float var12;

        if (var24 != null)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            var8 = var24[0];
            var9 = var24[1];
            var10 = var24[2];
            float var13;

            if (mc.gameSettings.anaglyph)
            {
                var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
                var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
                var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
                var8 = var11;
                var9 = var12;
                var10 = var13;
            }

            var23.startDrawing(6);
            var23.setColorRGBA_F(var8, var9, var10, var24[3]);
            var23.addVertex(0.0D, 100.0D, 0.0D);
            byte var26 = 16;
            var23.setColorRGBA_F(var24[0], var24[1], var24[2], 0.0F);

            for (int var27 = 0; var27 <= var26; ++var27)
            {
                var13 = (float) var27 * (float) Math.PI * 2.0F / (float) var26;
                float var14 = MathHelper.sin(var13);
                float var15 = MathHelper.cos(var13);
                var23.addVertex((double) (var14 * 120.0F), (double) (var15 * 120.0F), (double) (-var15 * 40.0F * var24[3]));
            }

            var23.draw();
            GL11.glPopMatrix();
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glPushMatrix();
        var8 = 1.0F - world.getRainStrength(partialTicks);
        var9 = 0.0F;
        var10 = 0.0F;
        var11 = 0.0F;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, var8);
        GL11.glTranslatef(var9, var10, var11);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        var12 = 30.0F;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/terrain/sun.png"));
        var23.startDrawingQuads();
        var23.addVertexWithUV((double) (-var12), 100.0D, (double) (-var12), 0.0D, 0.0D);
        var23.addVertexWithUV((double) var12, 100.0D, (double) (-var12), 1.0D, 0.0D);
        var23.addVertexWithUV((double) var12, 100.0D, (double) var12, 1.0D, 1.0D);
        var23.addVertexWithUV((double) (-var12), 100.0D, (double) var12, 0.0D, 1.0D);
        var23.draw();
        var12 = 20.0F;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/terrain/moon_phases.png"));
        int var28 = world.getMoonPhase(partialTicks);
        int var30 = var28 % 4;
        int var29 = var28 / 4 % 2;
        float var16 = (float) (var30 + 0) / 4.0F;
        float var17 = (float) (var29 + 0) / 2.0F;
        float var18 = (float) (var30 + 1) / 4.0F;
        float var19 = (float) (var29 + 1) / 2.0F;
        var23.startDrawingQuads();
        var23.addVertexWithUV((double) (-var12), -100.0D, (double) var12, (double) var18, (double) var19);
        var23.addVertexWithUV((double) var12, -100.0D, (double) var12, (double) var16, (double) var19);
        var23.addVertexWithUV((double) var12, -100.0D, (double) (-var12), (double) var16, (double) var17);
        var23.addVertexWithUV((double) (-var12), -100.0D, (double) (-var12), (double) var18, (double) var17);
        var23.draw();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        float var20 = world.getStarBrightness(partialTicks) * var8;

        if (var20 > 0.0F)
        {
            GL11.glColor4f(var20, var20, var20, var20);
            GL11.glCallList(this.starGLCallList);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor3f(0.0F, 0.0F, 0.0F);
        double var25 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();

        if (var25 < 0.0D)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 12.0F, 0.0F);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();
            var10 = 1.0F;
            var11 = -((float) (var25 + 65.0D));
            var12 = -var10;
            var23.startDrawingQuads();
            var23.setColorRGBA_I(0, 255);
            var23.addVertex((double) (-var10), (double) var11, (double) var10);
            var23.addVertex((double) var10, (double) var11, (double) var10);
            var23.addVertex((double) var10, (double) var12, (double) var10);
            var23.addVertex((double) (-var10), (double) var12, (double) var10);
            var23.addVertex((double) (-var10), (double) var12, (double) (-var10));
            var23.addVertex((double) var10, (double) var12, (double) (-var10));
            var23.addVertex((double) var10, (double) var11, (double) (-var10));
            var23.addVertex((double) (-var10), (double) var11, (double) (-var10));
            var23.addVertex((double) var10, (double) var12, (double) (-var10));
            var23.addVertex((double) var10, (double) var12, (double) var10);
            var23.addVertex((double) var10, (double) var11, (double) var10);
            var23.addVertex((double) var10, (double) var11, (double) (-var10));
            var23.addVertex((double) (-var10), (double) var11, (double) (-var10));
            var23.addVertex((double) (-var10), (double) var11, (double) var10);
            var23.addVertex((double) (-var10), (double) var12, (double) var10);
            var23.addVertex((double) (-var10), (double) var12, (double) (-var10));
            var23.addVertex((double) (-var10), (double) var12, (double) (-var10));
            var23.addVertex((double) (-var10), (double) var12, (double) var10);
            var23.addVertex((double) var10, (double) var12, (double) var10);
            var23.addVertex((double) var10, (double) var12, (double) (-var10));
            var23.draw();
        }

        if (world.provider.isSkyColored())
        {
            GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
        }
        else
        {
            GL11.glColor3f(var3, var4, var5);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -((float) (var25 - 16.0D)), 0.0F);
        GL11.glCallList(this.glSkyList2);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
    }
}
