package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public abstract class StructureComponent
{
    protected StructureBoundingBox boundingBox;
    // JAVADOC FIELD $$ field_74885_f
    protected int coordBaseMode;
    // JAVADOC FIELD $$ field_74886_g
    protected int componentType;
    private static final String __OBFID = "CL_00000511";

    public StructureComponent() {}

    protected StructureComponent(int par1)
    {
        this.componentType = par1;
        this.coordBaseMode = -1;
    }

    public NBTTagCompound func_143010_b()
    {
        if (MapGenStructureIO.func_143036_a(this) == null) // Friendlier error then the Null Stirng error below.
        {
            throw new RuntimeException("StructureComponent \"" + this.getClass().getName() + "\" missing ID Mapping, Modder see MapGenStructureIO");
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.func_143036_a(this));
        nbttagcompound.setTag("BB", this.boundingBox.func_151535_h());
        nbttagcompound.setInteger("O", this.coordBaseMode);
        nbttagcompound.setInteger("GD", this.componentType);
        this.func_143012_a(nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void func_143012_a(NBTTagCompound var1);

    public void func_143009_a(World par1World, NBTTagCompound par2NBTTagCompound)
    {
        if (par2NBTTagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(par2NBTTagCompound.getIntArray("BB"));
        }

        this.coordBaseMode = par2NBTTagCompound.getInteger("O");
        this.componentType = par2NBTTagCompound.getInteger("GD");
        this.func_143011_b(par2NBTTagCompound);
    }

    protected abstract void func_143011_b(NBTTagCompound var1);

    // JAVADOC METHOD $$ func_74861_a
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {}

    // JAVADOC METHOD $$ func_74875_a
    public abstract boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3);

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    // JAVADOC METHOD $$ func_74877_c
    public int getComponentType()
    {
        return this.componentType;
    }

    // JAVADOC METHOD $$ func_74883_a
    public static StructureComponent findIntersecting(List par0List, StructureBoundingBox par1StructureBoundingBox)
    {
        Iterator iterator = par0List.iterator();
        StructureComponent structurecomponent;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            structurecomponent = (StructureComponent)iterator.next();
        }
        while (structurecomponent.getBoundingBox() == null || !structurecomponent.getBoundingBox().intersectsWith(par1StructureBoundingBox));

        return structurecomponent;
    }

    public ChunkPosition func_151553_a()
    {
        return new ChunkPosition(this.boundingBox.getCenterX(), this.boundingBox.getCenterY(), this.boundingBox.getCenterZ());
    }

    // JAVADOC METHOD $$ func_74860_a
    protected boolean isLiquidInStructureBoundingBox(World par1World, StructureBoundingBox par2StructureBoundingBox)
    {
        int i = Math.max(this.boundingBox.minX - 1, par2StructureBoundingBox.minX);
        int j = Math.max(this.boundingBox.minY - 1, par2StructureBoundingBox.minY);
        int k = Math.max(this.boundingBox.minZ - 1, par2StructureBoundingBox.minZ);
        int l = Math.min(this.boundingBox.maxX + 1, par2StructureBoundingBox.maxX);
        int i1 = Math.min(this.boundingBox.maxY + 1, par2StructureBoundingBox.maxY);
        int j1 = Math.min(this.boundingBox.maxZ + 1, par2StructureBoundingBox.maxZ);
        int k1;
        int l1;

        for (k1 = i; k1 <= l; ++k1)
        {
            for (l1 = k; l1 <= j1; ++l1)
            {
                if (par1World.func_147439_a(k1, j, l1).func_149688_o().isLiquid())
                {
                    return true;
                }

                if (par1World.func_147439_a(k1, i1, l1).func_149688_o().isLiquid())
                {
                    return true;
                }
            }
        }

        for (k1 = i; k1 <= l; ++k1)
        {
            for (l1 = j; l1 <= i1; ++l1)
            {
                if (par1World.func_147439_a(k1, l1, k).func_149688_o().isLiquid())
                {
                    return true;
                }

                if (par1World.func_147439_a(k1, l1, j1).func_149688_o().isLiquid())
                {
                    return true;
                }
            }
        }

        for (k1 = k; k1 <= j1; ++k1)
        {
            for (l1 = j; l1 <= i1; ++l1)
            {
                if (par1World.func_147439_a(i, l1, k1).func_149688_o().isLiquid())
                {
                    return true;
                }

                if (par1World.func_147439_a(l, l1, k1).func_149688_o().isLiquid())
                {
                    return true;
                }
            }
        }

        return false;
    }

    protected int getXWithOffset(int par1, int par2)
    {
        switch (this.coordBaseMode)
        {
            case 0:
            case 2:
                return this.boundingBox.minX + par1;
            case 1:
                return this.boundingBox.maxX - par2;
            case 3:
                return this.boundingBox.minX + par2;
            default:
                return par1;
        }
    }

    protected int getYWithOffset(int par1)
    {
        return this.coordBaseMode == -1 ? par1 : par1 + this.boundingBox.minY;
    }

    protected int getZWithOffset(int par1, int par2)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return this.boundingBox.minZ + par2;
            case 1:
            case 3:
                return this.boundingBox.minZ + par1;
            case 2:
                return this.boundingBox.maxZ - par2;
            default:
                return par2;
        }
    }

    protected int func_151555_a(Block p_151555_1_, int p_151555_2_)
    {
        if (p_151555_1_ == Blocks.rail)
        {
            if (this.coordBaseMode == 1 || this.coordBaseMode == 3)
            {
                if (p_151555_2_ == 1)
                {
                    return 0;
                }

                return 1;
            }
        }
        else if (p_151555_1_ != Blocks.wooden_door && p_151555_1_ != Blocks.iron_door)
        {
            if (p_151555_1_ != Blocks.stone_stairs && p_151555_1_ != Blocks.oak_stairs && p_151555_1_ != Blocks.nether_brick_stairs && p_151555_1_ != Blocks.stone_brick_stairs && p_151555_1_ != Blocks.sandstone_stairs)
            {
                if (p_151555_1_ == Blocks.ladder)
                {
                    if (this.coordBaseMode == 0)
                    {
                        if (p_151555_2_ == 2)
                        {
                            return 3;
                        }

                        if (p_151555_2_ == 3)
                        {
                            return 2;
                        }
                    }
                    else if (this.coordBaseMode == 1)
                    {
                        if (p_151555_2_ == 2)
                        {
                            return 4;
                        }

                        if (p_151555_2_ == 3)
                        {
                            return 5;
                        }

                        if (p_151555_2_ == 4)
                        {
                            return 2;
                        }

                        if (p_151555_2_ == 5)
                        {
                            return 3;
                        }
                    }
                    else if (this.coordBaseMode == 3)
                    {
                        if (p_151555_2_ == 2)
                        {
                            return 5;
                        }

                        if (p_151555_2_ == 3)
                        {
                            return 4;
                        }

                        if (p_151555_2_ == 4)
                        {
                            return 2;
                        }

                        if (p_151555_2_ == 5)
                        {
                            return 3;
                        }
                    }
                }
                else if (p_151555_1_ == Blocks.stone_button)
                {
                    if (this.coordBaseMode == 0)
                    {
                        if (p_151555_2_ == 3)
                        {
                            return 4;
                        }

                        if (p_151555_2_ == 4)
                        {
                            return 3;
                        }
                    }
                    else if (this.coordBaseMode == 1)
                    {
                        if (p_151555_2_ == 3)
                        {
                            return 1;
                        }

                        if (p_151555_2_ == 4)
                        {
                            return 2;
                        }

                        if (p_151555_2_ == 2)
                        {
                            return 3;
                        }

                        if (p_151555_2_ == 1)
                        {
                            return 4;
                        }
                    }
                    else if (this.coordBaseMode == 3)
                    {
                        if (p_151555_2_ == 3)
                        {
                            return 2;
                        }

                        if (p_151555_2_ == 4)
                        {
                            return 1;
                        }

                        if (p_151555_2_ == 2)
                        {
                            return 3;
                        }

                        if (p_151555_2_ == 1)
                        {
                            return 4;
                        }
                    }
                }
                else if (p_151555_1_ != Blocks.tripwire_hook && !(p_151555_1_ instanceof BlockDirectional))
                {
                    if (p_151555_1_ == Blocks.piston || p_151555_1_ == Blocks.sticky_piston || p_151555_1_ == Blocks.lever || p_151555_1_ == Blocks.dispenser)
                    {
                        if (this.coordBaseMode == 0)
                        {
                            if (p_151555_2_ == 2 || p_151555_2_ == 3)
                            {
                                return Facing.oppositeSide[p_151555_2_];
                            }
                        }
                        else if (this.coordBaseMode == 1)
                        {
                            if (p_151555_2_ == 2)
                            {
                                return 4;
                            }

                            if (p_151555_2_ == 3)
                            {
                                return 5;
                            }

                            if (p_151555_2_ == 4)
                            {
                                return 2;
                            }

                            if (p_151555_2_ == 5)
                            {
                                return 3;
                            }
                        }
                        else if (this.coordBaseMode == 3)
                        {
                            if (p_151555_2_ == 2)
                            {
                                return 5;
                            }

                            if (p_151555_2_ == 3)
                            {
                                return 4;
                            }

                            if (p_151555_2_ == 4)
                            {
                                return 2;
                            }

                            if (p_151555_2_ == 5)
                            {
                                return 3;
                            }
                        }
                    }
                }
                else if (this.coordBaseMode == 0)
                {
                    if (p_151555_2_ == 0 || p_151555_2_ == 2)
                    {
                        return Direction.rotateOpposite[p_151555_2_];
                    }
                }
                else if (this.coordBaseMode == 1)
                {
                    if (p_151555_2_ == 2)
                    {
                        return 1;
                    }

                    if (p_151555_2_ == 0)
                    {
                        return 3;
                    }

                    if (p_151555_2_ == 1)
                    {
                        return 2;
                    }

                    if (p_151555_2_ == 3)
                    {
                        return 0;
                    }
                }
                else if (this.coordBaseMode == 3)
                {
                    if (p_151555_2_ == 2)
                    {
                        return 3;
                    }

                    if (p_151555_2_ == 0)
                    {
                        return 1;
                    }

                    if (p_151555_2_ == 1)
                    {
                        return 2;
                    }

                    if (p_151555_2_ == 3)
                    {
                        return 0;
                    }
                }
            }
            else if (this.coordBaseMode == 0)
            {
                if (p_151555_2_ == 2)
                {
                    return 3;
                }

                if (p_151555_2_ == 3)
                {
                    return 2;
                }
            }
            else if (this.coordBaseMode == 1)
            {
                if (p_151555_2_ == 0)
                {
                    return 2;
                }

                if (p_151555_2_ == 1)
                {
                    return 3;
                }

                if (p_151555_2_ == 2)
                {
                    return 0;
                }

                if (p_151555_2_ == 3)
                {
                    return 1;
                }
            }
            else if (this.coordBaseMode == 3)
            {
                if (p_151555_2_ == 0)
                {
                    return 2;
                }

                if (p_151555_2_ == 1)
                {
                    return 3;
                }

                if (p_151555_2_ == 2)
                {
                    return 1;
                }

                if (p_151555_2_ == 3)
                {
                    return 0;
                }
            }
        }
        else if (this.coordBaseMode == 0)
        {
            if (p_151555_2_ == 0)
            {
                return 2;
            }

            if (p_151555_2_ == 2)
            {
                return 0;
            }
        }
        else
        {
            if (this.coordBaseMode == 1)
            {
                return p_151555_2_ + 1 & 3;
            }

            if (this.coordBaseMode == 3)
            {
                return p_151555_2_ + 3 & 3;
            }
        }

        return p_151555_2_;
    }

    protected void func_151550_a(World p_151550_1_, Block p_151550_2_, int p_151550_3_, int p_151550_4_, int p_151550_5_, int p_151550_6_, StructureBoundingBox p_151550_7_)
    {
        int i1 = this.getXWithOffset(p_151550_4_, p_151550_6_);
        int j1 = this.getYWithOffset(p_151550_5_);
        int k1 = this.getZWithOffset(p_151550_4_, p_151550_6_);

        if (p_151550_7_.isVecInside(i1, j1, k1))
        {
            p_151550_1_.func_147465_d(i1, j1, k1, p_151550_2_, p_151550_3_, 2);
        }
    }

    protected Block func_151548_a(World p_151548_1_, int p_151548_2_, int p_151548_3_, int p_151548_4_, StructureBoundingBox p_151548_5_)
    {
        int l = this.getXWithOffset(p_151548_2_, p_151548_4_);
        int i1 = this.getYWithOffset(p_151548_3_);
        int j1 = this.getZWithOffset(p_151548_2_, p_151548_4_);
        return !p_151548_5_.isVecInside(l, i1, j1) ? Blocks.air : p_151548_1_.func_147439_a(l, i1, j1);
    }

    // JAVADOC METHOD $$ func_74878_a
    protected void fillWithAir(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3, int par4, int par5, int par6, int par7, int par8)
    {
        for (int k1 = par4; k1 <= par7; ++k1)
        {
            for (int l1 = par3; l1 <= par6; ++l1)
            {
                for (int i2 = par5; i2 <= par8; ++i2)
                {
                    this.func_151550_a(par1World, Blocks.air, 0, l1, k1, i2, par2StructureBoundingBox);
                }
            }
        }
    }

    protected void func_151549_a(World p_151549_1_, StructureBoundingBox p_151549_2_, int p_151549_3_, int p_151549_4_, int p_151549_5_, int p_151549_6_, int p_151549_7_, int p_151549_8_, Block p_151549_9_, Block p_151549_10_, boolean p_151549_11_)
    {
        for (int k1 = p_151549_4_; k1 <= p_151549_7_; ++k1)
        {
            for (int l1 = p_151549_3_; l1 <= p_151549_6_; ++l1)
            {
                for (int i2 = p_151549_5_; i2 <= p_151549_8_; ++i2)
                {
                    if (!p_151549_11_ || this.func_151548_a(p_151549_1_, l1, k1, i2, p_151549_2_).func_149688_o() != Material.field_151579_a)
                    {
                        if (k1 != p_151549_4_ && k1 != p_151549_7_ && l1 != p_151549_3_ && l1 != p_151549_6_ && i2 != p_151549_5_ && i2 != p_151549_8_)
                        {
                            this.func_151550_a(p_151549_1_, p_151549_10_, 0, l1, k1, i2, p_151549_2_);
                        }
                        else
                        {
                            this.func_151550_a(p_151549_1_, p_151549_9_, 0, l1, k1, i2, p_151549_2_);
                        }
                    }
                }
            }
        }
    }

    protected void func_151556_a(World p_151556_1_, StructureBoundingBox p_151556_2_, int p_151556_3_, int p_151556_4_, int p_151556_5_, int p_151556_6_, int p_151556_7_, int p_151556_8_, Block p_151556_9_, int p_151556_10_, Block p_151556_11_, int p_151556_12_, boolean p_151556_13_)
    {
        for (int i2 = p_151556_4_; i2 <= p_151556_7_; ++i2)
        {
            for (int j2 = p_151556_3_; j2 <= p_151556_6_; ++j2)
            {
                for (int k2 = p_151556_5_; k2 <= p_151556_8_; ++k2)
                {
                    if (!p_151556_13_ || this.func_151548_a(p_151556_1_, j2, i2, k2, p_151556_2_).func_149688_o() != Material.field_151579_a)
                    {
                        if (i2 != p_151556_4_ && i2 != p_151556_7_ && j2 != p_151556_3_ && j2 != p_151556_6_ && k2 != p_151556_5_ && k2 != p_151556_8_)
                        {
                            this.func_151550_a(p_151556_1_, p_151556_11_, p_151556_12_, j2, i2, k2, p_151556_2_);
                        }
                        else
                        {
                            this.func_151550_a(p_151556_1_, p_151556_9_, p_151556_10_, j2, i2, k2, p_151556_2_);
                        }
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_74882_a
    protected void fillWithRandomizedBlocks(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3, int par4, int par5, int par6, int par7, int par8, boolean par9, Random par10Random, StructureComponent.BlockSelector par11StructurePieceBlockSelector)
    {
        for (int k1 = par4; k1 <= par7; ++k1)
        {
            for (int l1 = par3; l1 <= par6; ++l1)
            {
                for (int i2 = par5; i2 <= par8; ++i2)
                {
                    if (!par9 || this.func_151548_a(par1World, l1, k1, i2, par2StructureBoundingBox).func_149688_o() != Material.field_151579_a)
                    {
                        par11StructurePieceBlockSelector.selectBlocks(par10Random, l1, k1, i2, k1 == par4 || k1 == par7 || l1 == par3 || l1 == par6 || i2 == par5 || i2 == par8);
                        this.func_151550_a(par1World, par11StructurePieceBlockSelector.func_151561_a(), par11StructurePieceBlockSelector.getSelectedBlockMetaData(), l1, k1, i2, par2StructureBoundingBox);
                    }
                }
            }
        }
    }

    protected void func_151551_a(World p_151551_1_, StructureBoundingBox p_151551_2_, Random p_151551_3_, float p_151551_4_, int p_151551_5_, int p_151551_6_, int p_151551_7_, int p_151551_8_, int p_151551_9_, int p_151551_10_, Block p_151551_11_, Block p_151551_12_, boolean p_151551_13_)
    {
        for (int k1 = p_151551_6_; k1 <= p_151551_9_; ++k1)
        {
            for (int l1 = p_151551_5_; l1 <= p_151551_8_; ++l1)
            {
                for (int i2 = p_151551_7_; i2 <= p_151551_10_; ++i2)
                {
                    if (p_151551_3_.nextFloat() <= p_151551_4_ && (!p_151551_13_ || this.func_151548_a(p_151551_1_, l1, k1, i2, p_151551_2_).func_149688_o() != Material.field_151579_a))
                    {
                        if (k1 != p_151551_6_ && k1 != p_151551_9_ && l1 != p_151551_5_ && l1 != p_151551_8_ && i2 != p_151551_7_ && i2 != p_151551_10_)
                        {
                            this.func_151550_a(p_151551_1_, p_151551_12_, 0, l1, k1, i2, p_151551_2_);
                        }
                        else
                        {
                            this.func_151550_a(p_151551_1_, p_151551_11_, 0, l1, k1, i2, p_151551_2_);
                        }
                    }
                }
            }
        }
    }

    protected void func_151552_a(World p_151552_1_, StructureBoundingBox p_151552_2_, Random p_151552_3_, float p_151552_4_, int p_151552_5_, int p_151552_6_, int p_151552_7_, Block p_151552_8_, int p_151552_9_)
    {
        if (p_151552_3_.nextFloat() < p_151552_4_)
        {
            this.func_151550_a(p_151552_1_, p_151552_8_, p_151552_9_, p_151552_5_, p_151552_6_, p_151552_7_, p_151552_2_);
        }
    }

    protected void func_151547_a(World p_151547_1_, StructureBoundingBox p_151547_2_, int p_151547_3_, int p_151547_4_, int p_151547_5_, int p_151547_6_, int p_151547_7_, int p_151547_8_, Block p_151547_9_, boolean p_151547_10_)
    {
        float f = (float)(p_151547_6_ - p_151547_3_ + 1);
        float f1 = (float)(p_151547_7_ - p_151547_4_ + 1);
        float f2 = (float)(p_151547_8_ - p_151547_5_ + 1);
        float f3 = (float)p_151547_3_ + f / 2.0F;
        float f4 = (float)p_151547_5_ + f2 / 2.0F;

        for (int k1 = p_151547_4_; k1 <= p_151547_7_; ++k1)
        {
            float f5 = (float)(k1 - p_151547_4_) / f1;

            for (int l1 = p_151547_3_; l1 <= p_151547_6_; ++l1)
            {
                float f6 = ((float)l1 - f3) / (f * 0.5F);

                for (int i2 = p_151547_5_; i2 <= p_151547_8_; ++i2)
                {
                    float f7 = ((float)i2 - f4) / (f2 * 0.5F);

                    if (!p_151547_10_ || this.func_151548_a(p_151547_1_, l1, k1, i2, p_151547_2_).func_149688_o() != Material.field_151579_a)
                    {
                        float f8 = f6 * f6 + f5 * f5 + f7 * f7;

                        if (f8 <= 1.05F)
                        {
                            this.func_151550_a(p_151547_1_, p_151547_9_, 0, l1, k1, i2, p_151547_2_);
                        }
                    }
                }
            }
        }
    }

    // JAVADOC METHOD $$ func_74871_b
    protected void clearCurrentPositionBlocksUpwards(World par1World, int par2, int par3, int par4, StructureBoundingBox par5StructureBoundingBox)
    {
        int l = this.getXWithOffset(par2, par4);
        int i1 = this.getYWithOffset(par3);
        int j1 = this.getZWithOffset(par2, par4);

        if (par5StructureBoundingBox.isVecInside(l, i1, j1))
        {
            while (!par1World.func_147437_c(l, i1, j1) && i1 < 255)
            {
                par1World.func_147465_d(l, i1, j1, Blocks.air, 0, 2);
                ++i1;
            }
        }
    }

    protected void func_151554_b(World p_151554_1_, Block p_151554_2_, int p_151554_3_, int p_151554_4_, int p_151554_5_, int p_151554_6_, StructureBoundingBox p_151554_7_)
    {
        int i1 = this.getXWithOffset(p_151554_4_, p_151554_6_);
        int j1 = this.getYWithOffset(p_151554_5_);
        int k1 = this.getZWithOffset(p_151554_4_, p_151554_6_);

        if (p_151554_7_.isVecInside(i1, j1, k1))
        {
            while ((p_151554_1_.func_147437_c(i1, j1, k1) || p_151554_1_.func_147439_a(i1, j1, k1).func_149688_o().isLiquid()) && j1 > 1)
            {
                p_151554_1_.func_147465_d(i1, j1, k1, p_151554_2_, p_151554_3_, 2);
                --j1;
            }
        }
    }

    // JAVADOC METHOD $$ func_74879_a
    protected boolean generateStructureChestContents(World par1World, StructureBoundingBox par2StructureBoundingBox, Random par3Random, int par4, int par5, int par6, WeightedRandomChestContent[] par7ArrayOfWeightedRandomChestContent, int par8)
    {
        int i1 = this.getXWithOffset(par4, par6);
        int j1 = this.getYWithOffset(par5);
        int k1 = this.getZWithOffset(par4, par6);

        if (par2StructureBoundingBox.isVecInside(i1, j1, k1) && par1World.func_147439_a(i1, j1, k1) != Blocks.chest)
        {
            par1World.func_147465_d(i1, j1, k1, Blocks.chest, 0, 2);
            TileEntityChest tileentitychest = (TileEntityChest)par1World.func_147438_o(i1, j1, k1);

            if (tileentitychest != null)
            {
                WeightedRandomChestContent.generateChestContents(par3Random, par7ArrayOfWeightedRandomChestContent, tileentitychest, par8);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    // JAVADOC METHOD $$ func_74869_a
    protected boolean generateStructureDispenserContents(World par1World, StructureBoundingBox par2StructureBoundingBox, Random par3Random, int par4, int par5, int par6, int par7, WeightedRandomChestContent[] par8ArrayOfWeightedRandomChestContent, int par9)
    {
        int j1 = this.getXWithOffset(par4, par6);
        int k1 = this.getYWithOffset(par5);
        int l1 = this.getZWithOffset(par4, par6);

        if (par2StructureBoundingBox.isVecInside(j1, k1, l1) && par1World.func_147439_a(j1, k1, l1) != Blocks.dispenser)
        {
            par1World.func_147465_d(j1, k1, l1, Blocks.dispenser, this.func_151555_a(Blocks.dispenser, par7), 2);
            TileEntityDispenser tileentitydispenser = (TileEntityDispenser)par1World.func_147438_o(j1, k1, l1);

            if (tileentitydispenser != null)
            {
                WeightedRandomChestContent.func_150706_a(par3Random, par8ArrayOfWeightedRandomChestContent, tileentitydispenser, par9);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected void placeDoorAtCurrentPosition(World par1World, StructureBoundingBox par2StructureBoundingBox, Random par3Random, int par4, int par5, int par6, int par7)
    {
        int i1 = this.getXWithOffset(par4, par6);
        int j1 = this.getYWithOffset(par5);
        int k1 = this.getZWithOffset(par4, par6);

        if (par2StructureBoundingBox.isVecInside(i1, j1, k1))
        {
            ItemDoor.func_150924_a(par1World, i1, j1, k1, par7, Blocks.wooden_door);
        }
    }

    public abstract static class BlockSelector
        {
            protected Block field_151562_a;
            protected int selectedBlockMetaData;
            private static final String __OBFID = "CL_00000512";

            protected BlockSelector()
            {
                this.field_151562_a = Blocks.air;
            }

            // JAVADOC METHOD $$ func_75062_a
            public abstract void selectBlocks(Random var1, int var2, int var3, int var4, boolean var5);

            public Block func_151561_a()
            {
                return this.field_151562_a;
            }

            public int getSelectedBlockMetaData()
            {
                return this.selectedBlockMetaData;
            }
        }
}