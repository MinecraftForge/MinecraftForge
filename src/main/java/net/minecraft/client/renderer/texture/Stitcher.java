package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class Stitcher
{
    private final int field_147971_a;
    private final Set setStitchHolders = new HashSet(256);
    private final List stitchSlots = new ArrayList(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final boolean forcePowerOf2;
    // JAVADOC FIELD $$ field_94323_h
    private final int maxTileDimension;
    private static final String __OBFID = "CL_00001054";

    public Stitcher(int p_i45095_1_, int p_i45095_2_, boolean p_i45095_3_, int p_i45095_4_, int p_i45095_5_)
    {
        this.field_147971_a = p_i45095_5_;
        this.maxWidth = p_i45095_1_;
        this.maxHeight = p_i45095_2_;
        this.forcePowerOf2 = p_i45095_3_;
        this.maxTileDimension = p_i45095_4_;
    }

    public int getCurrentWidth()
    {
        return this.currentWidth;
    }

    public int getCurrentHeight()
    {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite par1TextureAtlasSprite)
    {
        Stitcher.Holder holder = new Stitcher.Holder(par1TextureAtlasSprite, this.field_147971_a);

        if (this.maxTileDimension > 0)
        {
            holder.setNewDimension(this.maxTileDimension);
        }

        this.setStitchHolders.add(holder);
    }

    public void doStitch()
    {
        Stitcher.Holder[] aholder = (Stitcher.Holder[])this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
        Arrays.sort(aholder);
        Stitcher.Holder[] aholder1 = aholder;
        int i = aholder.length;

        for (int j = 0; j < i; ++j)
        {
            Stitcher.Holder holder = aholder1[j];

            if (!this.allocateSlot(holder))
            {
                String s = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution texturepack?", new Object[] {holder.getAtlasSprite().getIconName(), Integer.valueOf(holder.getAtlasSprite().getIconWidth()), Integer.valueOf(holder.getAtlasSprite().getIconHeight())});
                throw new StitcherException(holder, s);
            }
        }

        if (this.forcePowerOf2)
        {
            this.currentWidth = MathHelper.func_151236_b(this.currentWidth);
            this.currentHeight = MathHelper.func_151236_b(this.currentHeight);
        }
    }

    public List getStichSlots()
    {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.stitchSlots.iterator();

        while (iterator.hasNext())
        {
            Stitcher.Slot slot = (Stitcher.Slot)iterator.next();
            slot.getAllStitchSlots(arraylist);
        }

        ArrayList arraylist1 = Lists.newArrayList();
        Iterator iterator1 = arraylist.iterator();

        while (iterator1.hasNext())
        {
            Stitcher.Slot slot1 = (Stitcher.Slot)iterator1.next();
            Stitcher.Holder holder = slot1.getStitchHolder();
            TextureAtlasSprite textureatlassprite = holder.getAtlasSprite();
            textureatlassprite.initSprite(this.currentWidth, this.currentHeight, slot1.getOriginX(), slot1.getOriginY(), holder.isRotated());
            arraylist1.add(textureatlassprite);
        }

        return arraylist1;
    }

    private static int func_147969_b(int p_147969_0_, int p_147969_1_)
    {
        return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
    }

    // JAVADOC METHOD $$ func_94310_b
    private boolean allocateSlot(Stitcher.Holder par1StitchHolder)
    {
        for (int i = 0; i < this.stitchSlots.size(); ++i)
        {
            if (((Stitcher.Slot)this.stitchSlots.get(i)).addSlot(par1StitchHolder))
            {
                return true;
            }

            par1StitchHolder.rotate();

            if (((Stitcher.Slot)this.stitchSlots.get(i)).addSlot(par1StitchHolder))
            {
                return true;
            }

            par1StitchHolder.rotate();
        }

        return this.expandAndAllocateSlot(par1StitchHolder);
    }

    // JAVADOC METHOD $$ func_94311_c
    private boolean expandAndAllocateSlot(Stitcher.Holder par1StitchHolder)
    {
        int i = Math.min(par1StitchHolder.getWidth(), par1StitchHolder.getHeight());
        boolean flag = this.currentWidth == 0 && this.currentHeight == 0;
        boolean flag1;
        int j;

        if (this.forcePowerOf2)
        {
            j = MathHelper.func_151236_b(this.currentWidth);
            int k = MathHelper.func_151236_b(this.currentHeight);
            int l = MathHelper.func_151236_b(this.currentWidth + i);
            int i1 = MathHelper.func_151236_b(this.currentHeight + i);
            boolean flag2 = l <= this.maxWidth;
            boolean flag3 = i1 <= this.maxHeight;

            if (!flag2 && !flag3)
            {
                return false;
            }

            boolean flag4 = j != l;
            boolean flag5 = k != i1;

            if (flag4 ^ flag5)
            {
                flag1 = flag5 && flag3; //Forge: Bug fix: Attempt to fill all downward space before expanding width
            }
            else
            {
                flag1 = flag2 && j <= k;
            }
        }
        else
        {
            boolean flag6 = this.currentWidth + i <= this.maxWidth;
            boolean flag7 = this.currentHeight + i <= this.maxHeight;

            if (!flag6 && !flag7)
            {
                return false;
            }

            flag1 = flag6 && (flag || this.currentWidth <= this.currentHeight);
        }

        j = Math.max(par1StitchHolder.getWidth(), par1StitchHolder.getHeight());

        if (MathHelper.func_151236_b((flag1 ? this.currentHeight : this.currentWidth) + j) > (flag1 ? this.maxHeight : this.maxWidth))
        {
            return false;
        }
        else
        {
            Stitcher.Slot slot;

            if (flag1)
            {
                if (par1StitchHolder.getWidth() > par1StitchHolder.getHeight())
                {
                    par1StitchHolder.rotate();
                }

                if (this.currentHeight == 0)
                {
                    this.currentHeight = par1StitchHolder.getHeight();
                }

                slot = new Stitcher.Slot(this.currentWidth, 0, par1StitchHolder.getWidth(), this.currentHeight);
                this.currentWidth += par1StitchHolder.getWidth();
            }
            else
            {
                slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, par1StitchHolder.getHeight());
                this.currentHeight += par1StitchHolder.getHeight();
            }

            slot.addSlot(par1StitchHolder);
            this.stitchSlots.add(slot);
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Slot
        {
            private final int originX;
            private final int originY;
            private final int width;
            private final int height;
            private List subSlots;
            private Stitcher.Holder holder;
            private static final String __OBFID = "CL_00001056";

            public Slot(int par1, int par2, int par3, int par4)
            {
                this.originX = par1;
                this.originY = par2;
                this.width = par3;
                this.height = par4;
            }

            public Stitcher.Holder getStitchHolder()
            {
                return this.holder;
            }

            public int getOriginX()
            {
                return this.originX;
            }

            public int getOriginY()
            {
                return this.originY;
            }

            public boolean addSlot(Stitcher.Holder par1StitchHolder)
            {
                if (this.holder != null)
                {
                    return false;
                }
                else
                {
                    int i = par1StitchHolder.getWidth();
                    int j = par1StitchHolder.getHeight();

                    if (i <= this.width && j <= this.height)
                    {
                        if (i == this.width && j == this.height)
                        {
                            this.holder = par1StitchHolder;
                            return true;
                        }
                        else
                        {
                            if (this.subSlots == null)
                            {
                                this.subSlots = new ArrayList(1);
                                this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
                                int k = this.width - i;
                                int l = this.height - j;

                                if (l > 0 && k > 0)
                                {
                                    int i1 = Math.max(this.height, k);
                                    int j1 = Math.max(this.width, l);

                                    if (i1 >= j1)
                                    {
                                        this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                                        this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                                    }
                                    else
                                    {
                                        this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                                        this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                                    }
                                }
                                else if (k == 0)
                                {
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                                }
                                else if (l == 0)
                                {
                                    this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                                }
                            }

                            Iterator iterator = this.subSlots.iterator();
                            Stitcher.Slot slot;

                            do
                            {
                                if (!iterator.hasNext())
                                {
                                    return false;
                                }

                                slot = (Stitcher.Slot)iterator.next();
                            }
                            while (!slot.addSlot(par1StitchHolder));

                            return true;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            }

            // JAVADOC METHOD $$ func_94184_a
            public void getAllStitchSlots(List par1List)
            {
                if (this.holder != null)
                {
                    par1List.add(this);
                }
                else if (this.subSlots != null)
                {
                    Iterator iterator = this.subSlots.iterator();

                    while (iterator.hasNext())
                    {
                        Stitcher.Slot slot = (Stitcher.Slot)iterator.next();
                        slot.getAllStitchSlots(par1List);
                    }
                }
            }

            public String toString()
            {
                return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
            }
        }

    @SideOnly(Side.CLIENT)
    public static class Holder implements Comparable
        {
            private final TextureAtlasSprite theTexture;
            private final int width;
            private final int height;
            private final int field_147968_d;
            private boolean rotated;
            private float scaleFactor = 1.0F;
            private static final String __OBFID = "CL_00001055";

            public Holder(TextureAtlasSprite p_i45094_1_, int p_i45094_2_)
            {
                this.theTexture = p_i45094_1_;
                this.width = p_i45094_1_.getIconWidth();
                this.height = p_i45094_1_.getIconHeight();
                this.field_147968_d = p_i45094_2_;
                this.rotated = Stitcher.func_147969_b(this.height, p_i45094_2_) > Stitcher.func_147969_b(this.width, p_i45094_2_);
            }

            public TextureAtlasSprite getAtlasSprite()
            {
                return this.theTexture;
            }

            public int getWidth()
            {
                return this.rotated ? Stitcher.func_147969_b((int)((float)this.height * this.scaleFactor), this.field_147968_d) : Stitcher.func_147969_b((int)((float)this.width * this.scaleFactor), this.field_147968_d);
            }

            public int getHeight()
            {
                return this.rotated ? Stitcher.func_147969_b((int)((float)this.width * this.scaleFactor), this.field_147968_d) : Stitcher.func_147969_b((int)((float)this.height * this.scaleFactor), this.field_147968_d);
            }

            public void rotate()
            {
                this.rotated = !this.rotated;
            }

            public boolean isRotated()
            {
                return this.rotated;
            }

            public void setNewDimension(int par1)
            {
                if (this.width > par1 && this.height > par1)
                {
                    this.scaleFactor = (float)par1 / (float)Math.min(this.width, this.height);
                }
            }

            public String toString()
            {
                return "Holder{width=" + this.width + ", height=" + this.height + '}';
            }

            public int compareTo(Stitcher.Holder par1StitchHolder)
            {
                int i;

                if (this.getHeight() == par1StitchHolder.getHeight())
                {
                    if (this.getWidth() == par1StitchHolder.getWidth())
                    {
                        if (this.theTexture.getIconName() == null)
                        {
                            return par1StitchHolder.theTexture.getIconName() == null ? 0 : -1;
                        }

                        return this.theTexture.getIconName().compareTo(par1StitchHolder.theTexture.getIconName());
                    }

                    i = this.getWidth() < par1StitchHolder.getWidth() ? 1 : -1;
                }
                else
                {
                    i = this.getHeight() < par1StitchHolder.getHeight() ? 1 : -1;
                }

                return i;
            }

            public int compareTo(Object par1Obj)
            {
                return this.compareTo((Stitcher.Holder)par1Obj);
            }
        }
}