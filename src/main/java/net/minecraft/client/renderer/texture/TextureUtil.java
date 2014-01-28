package net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TextureUtil
{
    private static final Logger field_147959_c = LogManager.getLogger();
    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
    public static final int[] missingTextureData = missingTexture.getTextureData();
    private static int field_147958_e = -1;
    private static int field_147956_f = -1;
    private static final int[] field_147957_g;
    private static final String __OBFID = "CL_00001067";

    public static int glGenTextures()
    {
        return GL11.glGenTextures();
    }

    public static void func_147942_a(int p_147942_0_)
    {
        GL11.glDeleteTextures(p_147942_0_);
    }

    public static int uploadTextureImage(int par0, BufferedImage par1BufferedImage)
    {
        return uploadTextureImageAllocate(par0, par1BufferedImage, false, false);
    }

    public static void uploadTexture(int par0, int[] par1ArrayOfInteger, int par2, int par3)
    {
        bindTexture(par0);
        func_147947_a(0, par1ArrayOfInteger, par2, par3, 0, 0, false, false, false);
    }

    public static int[][] func_147949_a(int p_147949_0_, int p_147949_1_, int[][] p_147949_2_)
    {
        int[][] aint1 = new int[p_147949_0_ + 1][];
        aint1[0] = p_147949_2_[0];

        if (p_147949_0_ > 0)
        {
            boolean flag = false;
            int k;

            for (k = 0; k < p_147949_2_.length; ++k)
            {
                if (p_147949_2_[0][k] >> 24 == 0)
                {
                    flag = true;
                    break;
                }
            }

            for (k = 1; k <= p_147949_0_; ++k)
            {
                if (p_147949_2_[k] != null)
                {
                    aint1[k] = p_147949_2_[k];
                }
                else
                {
                    int[] aint2 = aint1[k - 1];
                    int[] aint3 = new int[aint2.length >> 2];
                    int l = p_147949_1_ >> k;
                    int i1 = aint3.length / l;
                    int j1 = l << 1;

                    for (int k1 = 0; k1 < l; ++k1)
                    {
                        for (int l1 = 0; l1 < i1; ++l1)
                        {
                            int i2 = 2 * (k1 + l1 * j1);
                            aint3[k1 + l1 * l] = func_147943_a(aint2[i2 + 0], aint2[i2 + 1], aint2[i2 + 0 + j1], aint2[i2 + 1 + j1], flag);
                        }
                    }

                    aint1[k] = aint3;
                }
            }
        }

        return aint1;
    }

    private static int func_147943_a(int p_147943_0_, int p_147943_1_, int p_147943_2_, int p_147943_3_, boolean p_147943_4_)
    {
        if (!p_147943_4_)
        {
            int i2 = func_147944_a(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 24);
            int j2 = func_147944_a(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 16);
            int k2 = func_147944_a(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 8);
            int l2 = func_147944_a(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 0);
            return i2 << 24 | j2 << 16 | k2 << 8 | l2;
        }
        else
        {
            field_147957_g[0] = p_147943_0_;
            field_147957_g[1] = p_147943_1_;
            field_147957_g[2] = p_147943_2_;
            field_147957_g[3] = p_147943_3_;
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            int i1;

            for (i1 = 0; i1 < 4; ++i1)
            {
                if (field_147957_g[i1] >> 24 != 0)
                {
                    f += (float)Math.pow((double)((float)(field_147957_g[i1] >> 24 & 255) / 255.0F), 2.2D);
                    f1 += (float)Math.pow((double)((float)(field_147957_g[i1] >> 16 & 255) / 255.0F), 2.2D);
                    f2 += (float)Math.pow((double)((float)(field_147957_g[i1] >> 8 & 255) / 255.0F), 2.2D);
                    f3 += (float)Math.pow((double)((float)(field_147957_g[i1] >> 0 & 255) / 255.0F), 2.2D);
                }
            }

            f /= 4.0F;
            f1 /= 4.0F;
            f2 /= 4.0F;
            f3 /= 4.0F;
            i1 = (int)(Math.pow((double)f, 0.45454545454545453D) * 255.0D);
            int j1 = (int)(Math.pow((double)f1, 0.45454545454545453D) * 255.0D);
            int k1 = (int)(Math.pow((double)f2, 0.45454545454545453D) * 255.0D);
            int l1 = (int)(Math.pow((double)f3, 0.45454545454545453D) * 255.0D);

            if (i1 < 96)
            {
                i1 = 0;
            }

            return i1 << 24 | j1 << 16 | k1 << 8 | l1;
        }
    }

    private static int func_147944_a(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_, int p_147944_4_)
    {
        float f = (float)Math.pow((double)((float)(p_147944_0_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f1 = (float)Math.pow((double)((float)(p_147944_1_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f2 = (float)Math.pow((double)((float)(p_147944_2_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f3 = (float)Math.pow((double)((float)(p_147944_3_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f4 = (float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D);
        return (int)((double)f4 * 255.0D);
    }

    public static void func_147955_a(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_, int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_)
    {
        for (int i1 = 0; i1 < p_147955_0_.length; ++i1)
        {
            int[] aint1 = p_147955_0_[i1];
            func_147947_a(i1, aint1, p_147955_1_ >> i1, p_147955_2_ >> i1, p_147955_3_ >> i1, p_147955_4_ >> i1, p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
        }
    }

    private static void func_147947_a(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_)
    {
        int j1 = 4194304 / p_147947_2_;
        func_147954_b(p_147947_6_, p_147947_8_);
        setTextureClamped(p_147947_7_);
        int i2;

        for (int k1 = 0; k1 < p_147947_2_ * p_147947_3_; k1 += p_147947_2_ * i2)
        {
            int l1 = k1 / p_147947_2_;
            i2 = Math.min(j1, p_147947_3_ - l1);
            int j2 = p_147947_2_ * i2;
            copyToBufferPos(p_147947_1_, k1, j2);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, p_147947_0_, p_147947_4_, p_147947_5_ + l1, p_147947_2_, i2, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, dataBuffer);
        }
    }

    public static int uploadTextureImageAllocate(int par0, BufferedImage par1BufferedImage, boolean par2, boolean par3)
    {
        allocateTexture(par0, par1BufferedImage.getWidth(), par1BufferedImage.getHeight());
        return uploadTextureImageSub(par0, par1BufferedImage, 0, 0, par2, par3);
    }

    public static void allocateTexture(int par0, int par1, int par2)
    {
        func_147946_a(par0, 0, par1, par2, 1.0F);
    }

    public static void func_147946_a(int p_147946_0_, int p_147946_1_, int p_147946_2_, int p_147946_3_, float p_147946_4_)
    {
        func_147942_a(p_147946_0_);
        bindTexture(p_147946_0_);

        if (OpenGlHelper.field_148825_d)
        {
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, p_147946_4_);
        }

        if (p_147946_1_ > 0)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, p_147946_1_);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, (float)p_147946_1_);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
        }

        for (int i1 = 0; i1 <= p_147946_1_; ++i1)
        {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i1, GL11.GL_RGBA, p_147946_2_ >> i1, p_147946_3_ >> i1, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)null);
        }
    }

    public static int uploadTextureImageSub(int par0, BufferedImage par1BufferedImage, int par2, int par3, boolean par4, boolean par5)
    {
        bindTexture(par0);
        uploadTextureImageSubImpl(par1BufferedImage, par2, par3, par4, par5);
        return par0;
    }

    private static void uploadTextureImageSubImpl(BufferedImage par0BufferedImage, int par1, int par2, boolean par3, boolean par4)
    {
        int k = par0BufferedImage.getWidth();
        int l = par0BufferedImage.getHeight();
        int i1 = 4194304 / k;
        int[] aint = new int[i1 * k];
        func_147951_b(par3);
        setTextureClamped(par4);

        for (int j1 = 0; j1 < k * l; j1 += k * i1)
        {
            int k1 = j1 / k;
            int l1 = Math.min(i1, l - k1);
            int i2 = k * l1;
            par0BufferedImage.getRGB(0, k1, k, l1, aint, 0, k);
            copyToBuffer(aint, i2);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, par1, par2 + k1, k, l1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, dataBuffer);
        }
    }

    private static void setTextureClamped(boolean par0)
    {
        if (par0)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        }
        else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }
    }

    private static void func_147951_b(boolean p_147951_0_)
    {
        func_147954_b(p_147951_0_, false);
    }

    public static void func_147950_a(boolean p_147950_0_, boolean p_147950_1_)
    {
        field_147958_e = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        field_147956_f = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
        func_147954_b(p_147950_0_, p_147950_1_);
    }

    public static void func_147945_b()
    {
        if (field_147958_e >= 0 && field_147956_f >= 0)
        {
            func_147952_b(field_147958_e, field_147956_f);
            field_147958_e = -1;
            field_147956_f = -1;
        }
    }

    private static void func_147952_b(int p_147952_0_, int p_147952_1_)
    {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147952_0_);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, p_147952_1_);
    }

    private static void func_147954_b(boolean p_147954_0_, boolean p_147954_1_)
    {
        if (p_147954_0_)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }
        else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
    }

    private static void copyToBuffer(int[] par0ArrayOfInteger, int par1)
    {
        copyToBufferPos(par0ArrayOfInteger, 0, par1);
    }

    private static void copyToBufferPos(int[] par0ArrayOfInteger, int par1, int par2)
    {
        int[] aint1 = par0ArrayOfInteger;

        if (Minecraft.getMinecraft().gameSettings.anaglyph)
        {
            aint1 = updateAnaglyph(par0ArrayOfInteger);
        }

        dataBuffer.clear();
        dataBuffer.put(aint1, par1, par2);
        dataBuffer.position(0).limit(par2);
    }

    static void bindTexture(int par0)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, par0);
    }

    public static int[] readImageData(IResourceManager par0ResourceManager, ResourceLocation par1ResourceLocation) throws IOException
    {
        BufferedImage bufferedimage = ImageIO.read(par0ResourceManager.getResource(par1ResourceLocation).getInputStream());
        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        return aint;
    }

    public static int[] updateAnaglyph(int[] par0ArrayOfInteger)
    {
        int[] aint1 = new int[par0ArrayOfInteger.length];

        for (int i = 0; i < par0ArrayOfInteger.length; ++i)
        {
            int j = par0ArrayOfInteger[i] >> 24 & 255;
            int k = par0ArrayOfInteger[i] >> 16 & 255;
            int l = par0ArrayOfInteger[i] >> 8 & 255;
            int i1 = par0ArrayOfInteger[i] & 255;
            int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
            int k1 = (k * 30 + l * 70) / 100;
            int l1 = (k * 30 + i1 * 70) / 100;
            aint1[i] = j << 24 | j1 << 16 | k1 << 8 | l1;
        }

        return aint1;
    }

    public static int[] func_147948_a(int[] p_147948_0_, int p_147948_1_, int p_147948_2_, int p_147948_3_)
    {
        int l = p_147948_1_ + 2 * p_147948_3_;
        int i1;
        int j1;

        for (i1 = p_147948_2_ - 1; i1 >= 0; --i1)
        {
            j1 = i1 * p_147948_1_;
            int k1 = p_147948_3_ + (i1 + p_147948_3_) * l;
            int l1;

            for (l1 = 0; l1 < p_147948_3_; l1 += p_147948_1_)
            {
                int i2 = Math.min(p_147948_1_, p_147948_3_ - l1);
                System.arraycopy(p_147948_0_, j1 + p_147948_1_ - i2, p_147948_0_, k1 - l1 - i2, i2);
            }

            System.arraycopy(p_147948_0_, j1, p_147948_0_, k1, p_147948_1_);

            for (l1 = 0; l1 < p_147948_3_; l1 += p_147948_1_)
            {
                System.arraycopy(p_147948_0_, j1, p_147948_0_, k1 + p_147948_1_ + l1, Math.min(p_147948_1_, p_147948_3_ - l1));
            }
        }

        for (i1 = 0; i1 < p_147948_3_; i1 += p_147948_2_)
        {
            j1 = Math.min(p_147948_2_, p_147948_3_ - i1);
            System.arraycopy(p_147948_0_, (p_147948_3_ + p_147948_2_ - j1) * l, p_147948_0_, (p_147948_3_ - i1 - j1) * l, l * j1);
        }

        for (i1 = 0; i1 < p_147948_3_; i1 += p_147948_2_)
        {
            j1 = Math.min(p_147948_2_, p_147948_3_ - i1);
            System.arraycopy(p_147948_0_, p_147948_3_ * l, p_147948_0_, (p_147948_2_ + p_147948_3_ + i1) * l, l * j1);
        }

        return p_147948_0_;
    }

    public static void func_147953_a(int[] p_147953_0_, int p_147953_1_, int p_147953_2_)
    {
        int[] aint1 = new int[p_147953_1_];
        int k = p_147953_2_ / 2;

        for (int l = 0; l < k; ++l)
        {
            System.arraycopy(p_147953_0_, l * p_147953_1_, aint1, 0, p_147953_1_);
            System.arraycopy(p_147953_0_, (p_147953_2_ - 1 - l) * p_147953_1_, p_147953_0_, l * p_147953_1_, p_147953_1_);
            System.arraycopy(aint1, 0, p_147953_0_, (p_147953_2_ - 1 - l) * p_147953_1_, p_147953_1_);
        }
    }

    static
    {
        int var0 = -16777216;
        int var1 = -524040;
        int[] var2 = new int[] { -524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] var3 = new int[] { -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int var4 = var2.length;

        for (int var5 = 0; var5 < 16; ++var5)
        {
            System.arraycopy(var5 < var4 ? var2 : var3, 0, missingTextureData, 16 * var5, var4);
            System.arraycopy(var5 < var4 ? var3 : var2, 0, missingTextureData, 16 * var5 + var4, var4);
        }

        missingTexture.updateDynamicTexture();
        field_147957_g = new int[4];
    }
}