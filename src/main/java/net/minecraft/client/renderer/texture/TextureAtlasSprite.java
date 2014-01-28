package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.IIcon;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TextureAtlasSprite implements IIcon
{
    private final String iconName;
    protected List framesTextureData = Lists.newArrayList();
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    private boolean field_147966_k;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;
    private static final String __OBFID = "CL_00001062";

    protected TextureAtlasSprite(String par1Str)
    {
        this.iconName = par1Str;
    }

    public void initSprite(int par1, int par2, int par3, int par4, boolean par5)
    {
        this.originX = par3;
        this.originY = par4;
        this.rotated = par5;
        float f = (float)(0.009999999776482582D / (double)par1);
        float f1 = (float)(0.009999999776482582D / (double)par2);
        this.minU = (float)par3 / (float)((double)par1) + f;
        this.maxU = (float)(par3 + this.width) / (float)((double)par1) - f;
        this.minV = (float)par4 / (float)par2 + f1;
        this.maxV = (float)(par4 + this.height) / (float)par2 - f1;

        if (this.field_147966_k)
        {
            float f2 = 8.0F / (float)par1;
            float f3 = 8.0F / (float)par2;
            this.minU += f2;
            this.maxU -= f2;
            this.minV += f3;
            this.maxV -= f3;
        }
    }

    public void copyFrom(TextureAtlasSprite par1TextureAtlasSprite)
    {
        this.originX = par1TextureAtlasSprite.originX;
        this.originY = par1TextureAtlasSprite.originY;
        this.width = par1TextureAtlasSprite.width;
        this.height = par1TextureAtlasSprite.height;
        this.rotated = par1TextureAtlasSprite.rotated;
        this.minU = par1TextureAtlasSprite.minU;
        this.maxU = par1TextureAtlasSprite.maxU;
        this.minV = par1TextureAtlasSprite.minV;
        this.maxV = par1TextureAtlasSprite.maxV;
    }

    // JAVADOC METHOD $$ func_130010_a
    public int getOriginX()
    {
        return this.originX;
    }

    // JAVADOC METHOD $$ func_110967_i
    public int getOriginY()
    {
        return this.originY;
    }

    // JAVADOC METHOD $$ func_94211_a
    public int getIconWidth()
    {
        return this.width;
    }

    // JAVADOC METHOD $$ func_94216_b
    public int getIconHeight()
    {
        return this.height;
    }

    // JAVADOC METHOD $$ func_94209_e
    public float getMinU()
    {
        return this.minU;
    }

    // JAVADOC METHOD $$ func_94212_f
    public float getMaxU()
    {
        return this.maxU;
    }

    // JAVADOC METHOD $$ func_94214_a
    public float getInterpolatedU(double par1)
    {
        float f = this.maxU - this.minU;
        return this.minU + f * (float)par1 / 16.0F;
    }

    // JAVADOC METHOD $$ func_94206_g
    public float getMinV()
    {
        return this.minV;
    }

    // JAVADOC METHOD $$ func_94210_h
    public float getMaxV()
    {
        return this.maxV;
    }

    // JAVADOC METHOD $$ func_94207_b
    public float getInterpolatedV(double par1)
    {
        float f = this.maxV - this.minV;
        return this.minV + f * ((float)par1 / 16.0F);
    }

    public String getIconName()
    {
        return this.iconName;
    }

    public void updateAnimation()
    {
        ++this.tickCounter;

        if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter))
        {
            int i = this.animationMetadata.getFrameIndex(this.frameCounter);
            int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % j;
            this.tickCounter = 0;
            int k = this.animationMetadata.getFrameIndex(this.frameCounter);

            if (i != k && k >= 0 && k < this.framesTextureData.size())
            {
                TextureUtil.func_147955_a((int[][])this.framesTextureData.get(k), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }

    public int[][] func_147965_a(int p_147965_1_)
    {
        return (int[][])this.framesTextureData.get(p_147965_1_);
    }

    public int getFrameCount()
    {
        return this.framesTextureData.size();
    }

    public void setIconWidth(int par1)
    {
        this.width = par1;
    }

    public void setIconHeight(int par1)
    {
        this.height = par1;
    }

    public void func_147964_a(BufferedImage[] p_147964_1_, AnimationMetadataSection p_147964_2_, boolean p_147964_3_)
    {
        this.resetSprite();
        this.field_147966_k = p_147964_3_;
        int i = p_147964_1_[0].getWidth();
        int j = p_147964_1_[0].getHeight();
        this.width = i;
        this.height = j;

        if (p_147964_3_)
        {
            this.width += 16;
            this.height += 16;
        }

        int[][] aint = new int[p_147964_1_.length][];
        int k;

        for (k = 0; k < p_147964_1_.length; ++k)
        {
            BufferedImage bufferedimage = p_147964_1_[k];

            if (bufferedimage != null)
            {
                if (k > 0 && (bufferedimage.getWidth() != i >> k || bufferedimage.getHeight() != j >> k))
                {
                    throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", new Object[] {Integer.valueOf(k), Integer.valueOf(bufferedimage.getWidth()), Integer.valueOf(bufferedimage.getHeight()), Integer.valueOf(i >> k), Integer.valueOf(j >> k)}));
                }

                aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
                bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
            }
        }

        if (p_147964_2_ == null)
        {
            if (j != i)
            {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }

            this.func_147961_a(aint);
            this.framesTextureData.add(this.func_147960_a(aint, i, j));
        }
        else
        {
            k = j / i;
            int j1 = i;
            int l = i;
            this.height = this.width;
            int i1;

            if (p_147964_2_.getFrameCount() > 0)
            {
                Iterator iterator = p_147964_2_.getFrameIndexSet().iterator();

                while (iterator.hasNext())
                {
                    i1 = ((Integer)iterator.next()).intValue();

                    if (i1 >= k)
                    {
                        throw new RuntimeException("invalid frameindex " + i1);
                    }

                    this.allocateFrameTextureData(i1);
                    this.framesTextureData.set(i1, this.func_147960_a(func_147962_a(aint, j1, l, i1), j1, l));
                }

                this.animationMetadata = p_147964_2_;
            }
            else
            {
                ArrayList arraylist = Lists.newArrayList();

                for (i1 = 0; i1 < k; ++i1)
                {
                    this.framesTextureData.add(this.func_147960_a(func_147962_a(aint, j1, l, i1), j1, l));
                    arraylist.add(new AnimationFrame(i1, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(arraylist, this.width, this.height, p_147964_2_.getFrameTime());
            }
        }
    }

    public void func_147963_d(int p_147963_1_)
    {
        ArrayList arraylist = Lists.newArrayList();

        for (int j = 0; j < this.framesTextureData.size(); ++j)
        {
            final int[][] aint = (int[][])this.framesTextureData.get(j);

            if (aint != null)
            {
                try
                {
                    arraylist.add(TextureUtil.func_147949_a(p_147963_1_, this.width, aint));
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
                    crashreportcategory.addCrashSection("Frame index", Integer.valueOf(j));
                    crashreportcategory.addCrashSectionCallable("Frame sizes", new Callable()
                    {
                        private static final String __OBFID = "CL_00001063";
                        public String call()
                        {
                            StringBuilder stringbuilder = new StringBuilder();
                            int[][] aint1 = aint;
                            int k = aint1.length;

                            for (int l = 0; l < k; ++l)
                            {
                                int[] aint2 = aint1[l];

                                if (stringbuilder.length() > 0)
                                {
                                    stringbuilder.append(", ");
                                }

                                stringbuilder.append(aint2 == null ? "null" : Integer.valueOf(aint2.length));
                            }

                            return stringbuilder.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }

        this.setFramesTextureData(arraylist);
    }

    private void func_147961_a(int[][] p_147961_1_)
    {
        int[] aint1 = p_147961_1_[0];
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1;

        for (i1 = 0; i1 < aint1.length; ++i1)
        {
            if ((aint1[i1] & -16777216) != 0)
            {
                j += aint1[i1] >> 16 & 255;
                k += aint1[i1] >> 8 & 255;
                l += aint1[i1] >> 0 & 255;
                ++i;
            }
        }

        if (i != 0)
        {
            j /= i;
            k /= i;
            l /= i;

            for (i1 = 0; i1 < aint1.length; ++i1)
            {
                if ((aint1[i1] & -16777216) == 0)
                {
                    aint1[i1] = j << 16 | k << 8 | l;
                }
            }
        }
    }

    private int[][] func_147960_a(int[][] p_147960_1_, int p_147960_2_, int p_147960_3_)
    {
        if (!this.field_147966_k)
        {
            return p_147960_1_;
        }
        else
        {
            int[][] aint1 = new int[p_147960_1_.length][];

            for (int k = 0; k < p_147960_1_.length; ++k)
            {
                int[] aint2 = p_147960_1_[k];

                if (aint2 != null)
                {
                    int[] aint3 = new int[(p_147960_2_ + 16 >> k) * (p_147960_3_ + 16 >> k)];
                    System.arraycopy(aint2, 0, aint3, 0, aint2.length);
                    aint1[k] = TextureUtil.func_147948_a(aint3, p_147960_2_ >> k, p_147960_3_ >> k, 8 >> k);
                }
            }

            return aint1;
        }
    }

    private void allocateFrameTextureData(int par1)
    {
        if (this.framesTextureData.size() <= par1)
        {
            for (int j = this.framesTextureData.size(); j <= par1; ++j)
            {
                this.framesTextureData.add((Object)null);
            }
        }
    }

    private static int[][] func_147962_a(int[][] p_147962_0_, int p_147962_1_, int p_147962_2_, int p_147962_3_)
    {
        int[][] aint1 = new int[p_147962_0_.length][];

        for (int l = 0; l < p_147962_0_.length; ++l)
        {
            int[] aint2 = p_147962_0_[l];

            if (aint2 != null)
            {
                aint1[l] = new int[(p_147962_1_ >> l) * (p_147962_2_ >> l)];
                System.arraycopy(aint2, p_147962_3_ * aint1[l].length, aint1[l], 0, aint1[l].length);
            }
        }

        return aint1;
    }

    public void clearFramesTextureData()
    {
        this.framesTextureData.clear();
    }

    public boolean hasAnimationMetadata()
    {
        return this.animationMetadata != null;
    }

    public void setFramesTextureData(List par1List)
    {
        this.framesTextureData = par1List;
    }

    private void resetSprite()
    {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
    }

    public String toString()
    {
        return "TextureAtlasSprite{name=\'" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }

    /**
     * The result of this function determines is the below 'load' function is called, and the 
     * default vanilla loading code is bypassed completely.
     * @param manager
     * @param location
     * @return True to use your own custom load code and bypass vanilla loading.
     */
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
    {
        return false;
    }
    
    /**
     * Load the specified resource as this sprite's data.
     * Returning false from this function will prevent this icon from being stitched onto the master texture.
     * @param manager Main resource manager
     * @param location File resource location
     * @return False to prevent this Icon from being stitched
     */
    public boolean load(IResourceManager manager, ResourceLocation location)
    {
        return true;
    }
}