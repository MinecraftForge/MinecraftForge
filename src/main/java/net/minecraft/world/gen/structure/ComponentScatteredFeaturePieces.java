package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

import net.minecraftforge.common.ChestGenHooks;
import static net.minecraftforge.common.ChestGenHooks.*;

public class ComponentScatteredFeaturePieces
{
    private static final String __OBFID = "CL_00000473";

    public static void func_143045_a()
    {
        MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.DesertPyramid.class, "TeDP");
        MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.JunglePyramid.class, "TeJP");
        MapGenStructureIO.func_143031_a(ComponentScatteredFeaturePieces.SwampHut.class, "TeSH");
    }

    abstract static class Feature extends StructureComponent
        {
            // JAVADOC FIELD $$ field_74939_a
            protected int scatteredFeatureSizeX;
            // JAVADOC FIELD $$ field_74937_b
            protected int scatteredFeatureSizeY;
            // JAVADOC FIELD $$ field_74938_c
            protected int scatteredFeatureSizeZ;
            protected int field_74936_d = -1;
            private static final String __OBFID = "CL_00000479";

            public Feature() {}

            protected Feature(Random par1Random, int par2, int par3, int par4, int par5, int par6, int par7)
            {
                super(0);
                this.scatteredFeatureSizeX = par5;
                this.scatteredFeatureSizeY = par6;
                this.scatteredFeatureSizeZ = par7;
                this.coordBaseMode = par1Random.nextInt(4);

                switch (this.coordBaseMode)
                {
                    case 0:
                    case 2:
                        this.boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par5 - 1, par3 + par6 - 1, par4 + par7 - 1);
                        break;
                    default:
                        this.boundingBox = new StructureBoundingBox(par2, par3, par4, par2 + par7 - 1, par3 + par6 - 1, par4 + par5 - 1);
                }
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                par1NBTTagCompound.setInteger("Width", this.scatteredFeatureSizeX);
                par1NBTTagCompound.setInteger("Height", this.scatteredFeatureSizeY);
                par1NBTTagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
                par1NBTTagCompound.setInteger("HPos", this.field_74936_d);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                this.scatteredFeatureSizeX = par1NBTTagCompound.getInteger("Width");
                this.scatteredFeatureSizeY = par1NBTTagCompound.getInteger("Height");
                this.scatteredFeatureSizeZ = par1NBTTagCompound.getInteger("Depth");
                this.field_74936_d = par1NBTTagCompound.getInteger("HPos");
            }

            protected boolean func_74935_a(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3)
            {
                if (this.field_74936_d >= 0)
                {
                    return true;
                }
                else
                {
                    int j = 0;
                    int k = 0;

                    for (int l = this.boundingBox.minZ; l <= this.boundingBox.maxZ; ++l)
                    {
                        for (int i1 = this.boundingBox.minX; i1 <= this.boundingBox.maxX; ++i1)
                        {
                            if (par2StructureBoundingBox.isVecInside(i1, 64, l))
                            {
                                j += Math.max(par1World.getTopSolidOrLiquidBlock(i1, l), par1World.provider.getAverageGroundLevel());
                                ++k;
                            }
                        }
                    }

                    if (k == 0)
                    {
                        return false;
                    }
                    else
                    {
                        this.field_74936_d = j / k;
                        this.boundingBox.offset(0, this.field_74936_d - this.boundingBox.minY + par3, 0);
                        return true;
                    }
                }
            }
        }

    public static class JunglePyramid extends ComponentScatteredFeaturePieces.Feature
        {
            private boolean field_74947_h;
            private boolean field_74948_i;
            private boolean field_74945_j;
            private boolean field_74946_k;
            // JAVADOC FIELD $$ field_74943_l
            public static final WeightedRandomChestContent[] junglePyramidsChestContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15), new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2), new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20), new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
            // JAVADOC FIELD $$ field_74944_m
            public static final WeightedRandomChestContent[] junglePyramidsDispenserContents = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.arrow, 0, 2, 7, 30)};
            // JAVADOC FIELD $$ field_74942_n
            private static ComponentScatteredFeaturePieces.JunglePyramid.Stones junglePyramidsRandomScatteredStones = new ComponentScatteredFeaturePieces.JunglePyramid.Stones(null);
            private static final String __OBFID = "CL_00000477";

            public JunglePyramid() {}

            public JunglePyramid(Random par1Random, int par2, int par3)
            {
                super(par1Random, par2, 64, par3, 12, 10, 15);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("placedMainChest", this.field_74947_h);
                par1NBTTagCompound.setBoolean("placedHiddenChest", this.field_74948_i);
                par1NBTTagCompound.setBoolean("placedTrap1", this.field_74945_j);
                par1NBTTagCompound.setBoolean("placedTrap2", this.field_74946_k);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_74947_h = par1NBTTagCompound.getBoolean("placedMainChest");
                this.field_74948_i = par1NBTTagCompound.getBoolean("placedHiddenChest");
                this.field_74945_j = par1NBTTagCompound.getBoolean("placedTrap1");
                this.field_74946_k = par1NBTTagCompound.getBoolean("placedTrap2");
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (!this.func_74935_a(par1World, par3StructureBoundingBox, 0))
                {
                    return false;
                }
                else
                {
                    int i = this.func_151555_a(Blocks.stone_stairs, 3);
                    int j = this.func_151555_a(Blocks.stone_stairs, 2);
                    int k = this.func_151555_a(Blocks.stone_stairs, 0);
                    int l = this.func_151555_a(Blocks.stone_stairs, 1);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 1, 2, 9, 2, 2, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 1, 12, 9, 2, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 1, 3, 2, 2, 11, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 1, 3, 9, 2, 11, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 1, 10, 6, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 13, 10, 6, 13, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 3, 2, 1, 6, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 10, 3, 2, 10, 6, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 3, 2, 9, 3, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 6, 2, 9, 6, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 3, 7, 3, 8, 7, 11, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 8, 4, 7, 8, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 3, 1, 3, 8, 2, 11);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 4, 3, 6, 7, 3, 9);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 2, 4, 2, 9, 5, 12);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 4, 6, 5, 7, 6, 9);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 5, 7, 6, 6, 7, 8);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 5, 1, 2, 6, 2, 2);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 5, 2, 12, 6, 2, 12);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 5, 5, 1, 6, 5, 1);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 5, 5, 13, 6, 5, 13);
                    this.func_151550_a(par1World, Blocks.air, 0, 1, 5, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.air, 0, 10, 5, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.air, 0, 1, 5, 9, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.air, 0, 10, 5, 9, par3StructureBoundingBox);
                    int i1;

                    for (i1 = 0; i1 <= 14; i1 += 14)
                    {
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 4, i1, 2, 5, i1, false, par2Random, junglePyramidsRandomScatteredStones);
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 4, i1, 4, 5, i1, false, par2Random, junglePyramidsRandomScatteredStones);
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 4, i1, 7, 5, i1, false, par2Random, junglePyramidsRandomScatteredStones);
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 4, i1, 9, 5, i1, false, par2Random, junglePyramidsRandomScatteredStones);
                    }

                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 6, 0, 6, 6, 0, false, par2Random, junglePyramidsRandomScatteredStones);

                    for (i1 = 0; i1 <= 11; i1 += 11)
                    {
                        for (int j1 = 2; j1 <= 12; j1 += 2)
                        {
                            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, i1, 4, j1, i1, 5, j1, false, par2Random, junglePyramidsRandomScatteredStones);
                        }

                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, i1, 6, 5, i1, 6, 5, false, par2Random, junglePyramidsRandomScatteredStones);
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, i1, 6, 9, i1, 6, 9, false, par2Random, junglePyramidsRandomScatteredStones);
                    }

                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 7, 2, 2, 9, 2, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 7, 2, 9, 9, 2, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, 7, 12, 2, 9, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, 7, 12, 9, 9, 12, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 9, 4, 4, 9, 4, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 9, 4, 7, 9, 4, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 9, 10, 4, 9, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 9, 10, 7, 9, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 9, 7, 6, 9, 7, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 5, 9, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 6, 9, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, j, 5, 9, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, j, 6, 9, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 4, 0, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 5, 0, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 6, 0, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 7, 0, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 4, 1, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 4, 2, 9, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 4, 3, 10, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 7, 1, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 7, 2, 9, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, i, 7, 3, 10, par3StructureBoundingBox);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 9, 4, 1, 9, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, 1, 9, 7, 1, 9, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 10, 7, 2, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 5, 4, 5, 6, 4, 5, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.func_151550_a(par1World, Blocks.stone_stairs, k, 4, 4, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stone_stairs, l, 7, 4, 5, par3StructureBoundingBox);

                    for (i1 = 0; i1 < 4; ++i1)
                    {
                        this.func_151550_a(par1World, Blocks.stone_stairs, j, 5, 0 - i1, 6 + i1, par3StructureBoundingBox);
                        this.func_151550_a(par1World, Blocks.stone_stairs, j, 6, 0 - i1, 6 + i1, par3StructureBoundingBox);
                        this.fillWithAir(par1World, par3StructureBoundingBox, 5, 0 - i1, 7 + i1, 6, 0 - i1, 9 + i1);
                    }

                    this.fillWithAir(par1World, par3StructureBoundingBox, 1, -3, 12, 10, -1, 13);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 1, -3, 1, 3, -1, 13);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 1, -3, 1, 9, -1, 5);

                    for (i1 = 1; i1 <= 13; i1 += 2)
                    {
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, -3, i1, 1, -2, i1, false, par2Random, junglePyramidsRandomScatteredStones);
                    }

                    for (i1 = 2; i1 <= 12; i1 += 2)
                    {
                        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, -1, i1, 3, -1, i1, false, par2Random, junglePyramidsRandomScatteredStones);
                    }

                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, -2, 1, 5, -2, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 7, -2, 1, 9, -2, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 6, -3, 1, 6, -3, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 6, -1, 1, 6, -1, 1, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.func_151550_a(par1World, Blocks.tripwire_hook, this.func_151555_a(Blocks.tripwire_hook, 3) | 4, 1, -3, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire_hook, this.func_151555_a(Blocks.tripwire_hook, 1) | 4, 4, -3, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire, 4, 2, -3, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire, 4, 3, -3, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 7, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 5, -3, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 4, -3, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 3, -3, 1, par3StructureBoundingBox);

                    ChestGenHooks dispenser = ChestGenHooks.getInfo(PYRAMID_JUNGLE_DISPENSER);
                    ChestGenHooks chest = ChestGenHooks.getInfo(PYRAMID_JUNGLE_CHEST);

                    if (!this.field_74945_j)
                    {
                        this.field_74945_j = this.generateStructureDispenserContents(par1World, par3StructureBoundingBox, par2Random, 3, -2, 1, 2, dispenser.getItems(par2Random), dispenser.getCount(par2Random));
                    }

                    this.func_151550_a(par1World, Blocks.vine, 15, 3, -2, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire_hook, this.func_151555_a(Blocks.tripwire_hook, 2) | 4, 7, -3, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire_hook, this.func_151555_a(Blocks.tripwire_hook, 0) | 4, 7, -3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire, 4, 7, -3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire, 4, 7, -3, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.tripwire, 4, 7, -3, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 8, -3, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 9, -3, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 9, -3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 9, -3, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 9, -2, 4, par3StructureBoundingBox);

                    if (!this.field_74946_k)
                    {
                        this.field_74946_k = this.generateStructureDispenserContents(par1World, par3StructureBoundingBox, par2Random, 9, -2, 3, 4, dispenser.getItems(par2Random), dispenser.getCount(par2Random));
                    }

                    this.func_151550_a(par1World, Blocks.vine, 15, 8, -1, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.vine, 15, 8, -2, 3, par3StructureBoundingBox);

                    if (!this.field_74947_h)
                    {
                        this.field_74947_h = this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 8, -3, 3, chest.getItems(par2Random), chest.getCount(par2Random));
                    }

                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 9, -3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 8, -3, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 4, -3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 5, -2, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 5, -1, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 6, -3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 7, -2, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 7, -1, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 8, -3, 5, par3StructureBoundingBox);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, -1, 1, 9, -1, 5, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithAir(par1World, par3StructureBoundingBox, 8, -3, 8, 10, -1, 10);
                    this.func_151550_a(par1World, Blocks.stonebrick, 3, 8, -2, 11, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 3, 9, -2, 11, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.stonebrick, 3, 10, -2, 11, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.lever, BlockLever.func_149819_b(this.func_151555_a(Blocks.lever, 2)), 8, -2, 12, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.lever, BlockLever.func_149819_b(this.func_151555_a(Blocks.lever, 2)), 9, -2, 12, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.lever, BlockLever.func_149819_b(this.func_151555_a(Blocks.lever, 2)), 10, -2, 12, par3StructureBoundingBox);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 8, -3, 8, 8, -3, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 10, -3, 8, 10, -3, 10, false, par2Random, junglePyramidsRandomScatteredStones);
                    this.func_151550_a(par1World, Blocks.mossy_cobblestone, 0, 10, -2, 9, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 8, -2, 9, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 8, -2, 10, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.redstone_wire, 0, 10, -1, 9, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sticky_piston, 1, 9, -2, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sticky_piston, this.func_151555_a(Blocks.sticky_piston, 4), 10, -2, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sticky_piston, this.func_151555_a(Blocks.sticky_piston, 4), 10, -1, 8, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.unpowered_repeater, this.func_151555_a(Blocks.unpowered_repeater, 2), 10, -2, 10, par3StructureBoundingBox);

                    if (!this.field_74948_i)
                    {
                        this.field_74948_i = this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 9, -3, 10, chest.getItems(par2Random), chest.getCount(par2Random));
                    }

                    return true;
                }
            }

            static class Stones extends StructureComponent.BlockSelector
                {
                    private static final String __OBFID = "CL_00000478";

                    private Stones() {}

                    // JAVADOC METHOD $$ func_75062_a
                    public void selectBlocks(Random par1Random, int par2, int par3, int par4, boolean par5)
                    {
                        if (par1Random.nextFloat() < 0.4F)
                        {
                            this.field_151562_a = Blocks.cobblestone;
                        }
                        else
                        {
                            this.field_151562_a = Blocks.mossy_cobblestone;
                        }
                    }

                    Stones(Object par1ComponentScatteredFeaturePieces2)
                    {
                        this();
                    }
                }
        }

    public static class SwampHut extends ComponentScatteredFeaturePieces.Feature
        {
            // JAVADOC FIELD $$ field_82682_h
            private boolean hasWitch;
            private static final String __OBFID = "CL_00000480";

            public SwampHut() {}

            public SwampHut(Random par1Random, int par2, int par3)
            {
                super(par1Random, par2, 64, par3, 7, 5, 9);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("Witch", this.hasWitch);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.hasWitch = par1NBTTagCompound.getBoolean("Witch");
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                if (!this.func_74935_a(par1World, par3StructureBoundingBox, 0))
                {
                    return false;
                }
                else
                {
                    this.func_151556_a(par1World, par3StructureBoundingBox, 1, 1, 1, 5, 1, 7, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 1, 4, 2, 5, 4, 7, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 2, 1, 0, 4, 1, 0, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 2, 2, 2, 3, 3, 2, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 1, 2, 3, 1, 3, 6, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 5, 2, 3, 5, 3, 6, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 2, 2, 7, 4, 3, 7, Blocks.planks, 1, Blocks.planks, 1, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 2, 1, 3, 2, Blocks.log, Blocks.log, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 0, 2, 5, 3, 2, Blocks.log, Blocks.log, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 1, 0, 7, 1, 3, 7, Blocks.log, Blocks.log, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, 5, 0, 7, 5, 3, 7, Blocks.log, Blocks.log, false);
                    this.func_151550_a(par1World, Blocks.fence, 0, 2, 3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 3, 3, 7, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.air, 0, 1, 3, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.air, 0, 5, 3, 4, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.air, 0, 5, 3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.flower_pot, 7, 1, 3, 5, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.crafting_table, 0, 3, 2, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.cauldron, 0, 4, 2, 6, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 1, 2, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.fence, 0, 5, 2, 1, par3StructureBoundingBox);
                    int i = this.func_151555_a(Blocks.oak_stairs, 3);
                    int j = this.func_151555_a(Blocks.oak_stairs, 1);
                    int k = this.func_151555_a(Blocks.oak_stairs, 0);
                    int l = this.func_151555_a(Blocks.oak_stairs, 2);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 0, 4, 1, 6, 4, 1, Blocks.spruce_stairs, i, Blocks.spruce_stairs, i, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 0, 4, 2, 0, 4, 7, Blocks.spruce_stairs, k, Blocks.spruce_stairs, k, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 6, 4, 2, 6, 4, 7, Blocks.spruce_stairs, j, Blocks.spruce_stairs, j, false);
                    this.func_151556_a(par1World, par3StructureBoundingBox, 0, 4, 8, 6, 4, 8, Blocks.spruce_stairs, l, Blocks.spruce_stairs, l, false);
                    int i1;
                    int j1;

                    for (i1 = 2; i1 <= 7; i1 += 5)
                    {
                        for (j1 = 1; j1 <= 5; j1 += 4)
                        {
                            this.func_151554_b(par1World, Blocks.log, 0, j1, -1, i1, par3StructureBoundingBox);
                        }
                    }

                    if (!this.hasWitch)
                    {
                        i1 = this.getXWithOffset(2, 5);
                        j1 = this.getYWithOffset(2);
                        int k1 = this.getZWithOffset(2, 5);

                        if (par3StructureBoundingBox.isVecInside(i1, j1, k1))
                        {
                            this.hasWitch = true;
                            EntityWitch entitywitch = new EntityWitch(par1World);
                            entitywitch.setLocationAndAngles((double)i1 + 0.5D, (double)j1, (double)k1 + 0.5D, 0.0F, 0.0F);
                            entitywitch.onSpawnWithEgg((IEntityLivingData)null);
                            par1World.spawnEntityInWorld(entitywitch);
                        }
                    }

                    return true;
                }
            }
        }

    public static class DesertPyramid extends ComponentScatteredFeaturePieces.Feature
        {
            private boolean[] field_74940_h = new boolean[4];
            // JAVADOC FIELD $$ field_74941_i
            public static final WeightedRandomChestContent[] itemsToGenerateInTemple = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15), new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2), new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20), new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)};
            private static final String __OBFID = "CL_00000476";

            public DesertPyramid() {}

            public DesertPyramid(Random par1Random, int par2, int par3)
            {
                super(par1Random, par2, 64, par3, 21, 15, 21);
            }

            protected void func_143012_a(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143012_a(par1NBTTagCompound);
                par1NBTTagCompound.setBoolean("hasPlacedChest0", this.field_74940_h[0]);
                par1NBTTagCompound.setBoolean("hasPlacedChest1", this.field_74940_h[1]);
                par1NBTTagCompound.setBoolean("hasPlacedChest2", this.field_74940_h[2]);
                par1NBTTagCompound.setBoolean("hasPlacedChest3", this.field_74940_h[3]);
            }

            protected void func_143011_b(NBTTagCompound par1NBTTagCompound)
            {
                super.func_143011_b(par1NBTTagCompound);
                this.field_74940_h[0] = par1NBTTagCompound.getBoolean("hasPlacedChest0");
                this.field_74940_h[1] = par1NBTTagCompound.getBoolean("hasPlacedChest1");
                this.field_74940_h[2] = par1NBTTagCompound.getBoolean("hasPlacedChest2");
                this.field_74940_h[3] = par1NBTTagCompound.getBoolean("hasPlacedChest3");
            }

            // JAVADOC METHOD $$ func_74875_a
            public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
            {
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, Blocks.sandstone, Blocks.sandstone, false);
                int i;

                for (i = 1; i <= 9; ++i)
                {
                    this.func_151549_a(par1World, par3StructureBoundingBox, i, i, i, this.scatteredFeatureSizeX - 1 - i, i, this.scatteredFeatureSizeZ - 1 - i, Blocks.sandstone, Blocks.sandstone, false);
                    this.func_151549_a(par1World, par3StructureBoundingBox, i + 1, i, i + 1, this.scatteredFeatureSizeX - 2 - i, i, this.scatteredFeatureSizeZ - 2 - i, Blocks.air, Blocks.air, false);
                }

                int j;

                for (i = 0; i < this.scatteredFeatureSizeX; ++i)
                {
                    for (j = 0; j < this.scatteredFeatureSizeZ; ++j)
                    {
                        byte b0 = -5;
                        this.func_151554_b(par1World, Blocks.sandstone, 0, i, b0, j, par3StructureBoundingBox);
                    }
                }

                i = this.func_151555_a(Blocks.sandstone_stairs, 3);
                j = this.func_151555_a(Blocks.sandstone_stairs, 2);
                int k1 = this.func_151555_a(Blocks.sandstone_stairs, 0);
                int k = this.func_151555_a(Blocks.sandstone_stairs, 1);
                byte b1 = 1;
                byte b2 = 11;
                this.func_151549_a(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 9, 4, Blocks.sandstone, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 10, 1, 3, 10, 3, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, i, 2, 10, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, j, 2, 10, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, k1, 0, 10, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, k, 4, 10, 2, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 5, 0, 0, this.scatteredFeatureSizeX - 1, 9, 4, Blocks.sandstone, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 4, 10, 1, this.scatteredFeatureSizeX - 2, 10, 3, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, i, this.scatteredFeatureSizeX - 3, 10, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, j, this.scatteredFeatureSizeX - 3, 10, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, k1, this.scatteredFeatureSizeX - 5, 10, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, k, this.scatteredFeatureSizeX - 1, 10, 2, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, 0, 0, 12, 4, 4, Blocks.sandstone, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 1, 0, 11, 3, 4, Blocks.air, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 9, 1, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 9, 2, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 9, 3, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 10, 3, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 11, 3, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 11, 2, 1, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 11, 1, 1, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 1, 8, 3, 3, Blocks.sandstone, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 1, 2, 8, 2, 2, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 12, 1, 1, 16, 3, 3, Blocks.sandstone, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 12, 1, 2, 16, 2, 2, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 5, 4, 5, this.scatteredFeatureSizeX - 6, 4, this.scatteredFeatureSizeZ - 6, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, 4, 9, 11, 4, 11, Blocks.air, Blocks.air, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 8, 1, 8, 8, 3, 8, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 12, 1, 8, 12, 3, 8, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 8, 1, 12, 8, 3, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 12, 1, 12, 12, 3, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 5, 4, 4, 11, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 5, 1, 5, this.scatteredFeatureSizeX - 2, 4, 11, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 6, 7, 9, 6, 7, 11, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 7, 7, 9, this.scatteredFeatureSizeX - 7, 7, 11, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 5, 5, 9, 5, 7, 11, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 6, 5, 9, this.scatteredFeatureSizeX - 6, 7, 11, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151550_a(par1World, Blocks.air, 0, 5, 5, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 5, 6, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 6, 6, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, this.scatteredFeatureSizeX - 6, 5, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, this.scatteredFeatureSizeX - 6, 6, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, this.scatteredFeatureSizeX - 7, 6, 10, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 2, 4, 4, 2, 6, 4, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 3, 4, 4, this.scatteredFeatureSizeX - 3, 6, 4, Blocks.air, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, i, 2, 4, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, i, 2, 3, 4, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, i, this.scatteredFeatureSizeX - 3, 4, 5, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, i, this.scatteredFeatureSizeX - 3, 3, 4, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 1, 1, 3, 2, 2, 3, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 3, 1, 3, this.scatteredFeatureSizeX - 2, 2, 3, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, 0, 1, 1, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, 0, this.scatteredFeatureSizeX - 2, 1, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_slab, 1, 1, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.stone_slab, 1, this.scatteredFeatureSizeX - 2, 2, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, k, 2, 1, 2, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone_stairs, k1, this.scatteredFeatureSizeX - 3, 1, 2, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 4, 3, 5, 4, 3, 18, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 5, 3, 5, this.scatteredFeatureSizeX - 5, 3, 17, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 3, 1, 5, 4, 2, 16, Blocks.air, Blocks.air, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, this.scatteredFeatureSizeX - 6, 1, 5, this.scatteredFeatureSizeX - 5, 2, 16, Blocks.air, Blocks.air, false);
                int l;

                for (l = 5; l <= 17; l += 2)
                {
                    this.func_151550_a(par1World, Blocks.sandstone, 2, 4, 1, l, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 1, 4, 2, l, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, this.scatteredFeatureSizeX - 5, 1, l, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 1, this.scatteredFeatureSizeX - 5, 2, l, par3StructureBoundingBox);
                }

                this.func_151550_a(par1World, Blocks.wool, b1, 10, 0, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 10, 0, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 9, 0, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 11, 0, 9, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 8, 0, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 12, 0, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 7, 0, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 13, 0, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 9, 0, 11, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 11, 0, 11, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 10, 0, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 10, 0, 13, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b2, 10, 0, 10, par3StructureBoundingBox);

                for (l = 0; l <= this.scatteredFeatureSizeX - 1; l += this.scatteredFeatureSizeX - 1)
                {
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 2, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 2, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 2, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 3, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 3, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 3, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 4, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 1, l, 4, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 4, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 5, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 5, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 5, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 6, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 1, l, 6, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 6, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 7, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 7, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 7, 3, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 8, 1, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 8, 2, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 8, 3, par3StructureBoundingBox);
                }

                for (l = 2; l <= this.scatteredFeatureSizeX - 3; l += this.scatteredFeatureSizeX - 3 - 2)
                {
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l - 1, 2, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 2, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l + 1, 2, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l - 1, 3, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 3, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l + 1, 3, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l - 1, 4, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 1, l, 4, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l + 1, 4, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l - 1, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l + 1, 5, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l - 1, 6, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 1, l, 6, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l + 1, 6, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l - 1, 7, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l, 7, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.wool, b1, l + 1, 7, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l - 1, 8, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l, 8, 0, par3StructureBoundingBox);
                    this.func_151550_a(par1World, Blocks.sandstone, 2, l + 1, 8, 0, par3StructureBoundingBox);
                }

                this.func_151556_a(par1World, par3StructureBoundingBox, 8, 4, 0, 12, 6, 0, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151550_a(par1World, Blocks.air, 0, 8, 6, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 12, 6, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 9, 5, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 1, 10, 5, 0, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.wool, b1, 11, 5, 0, par3StructureBoundingBox);
                this.func_151556_a(par1World, par3StructureBoundingBox, 8, -14, 8, 12, -11, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 8, -10, 8, 12, -10, 12, Blocks.sandstone, 1, Blocks.sandstone, 1, false);
                this.func_151556_a(par1World, par3StructureBoundingBox, 8, -9, 8, 12, -9, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 8, -8, 8, 12, -1, 12, Blocks.sandstone, Blocks.sandstone, false);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, -11, 9, 11, -1, 11, Blocks.air, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.stone_pressure_plate, 0, 10, -11, 10, par3StructureBoundingBox);
                this.func_151549_a(par1World, par3StructureBoundingBox, 9, -13, 9, 11, -13, 11, Blocks.tnt, Blocks.air, false);
                this.func_151550_a(par1World, Blocks.air, 0, 8, -11, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 8, -10, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 1, 7, -10, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 7, -11, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 12, -11, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 12, -10, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 1, 13, -10, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 13, -11, 10, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 10, -11, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 10, -10, 8, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 1, 10, -10, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 10, -11, 7, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 10, -11, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.air, 0, 10, -10, 12, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 1, 10, -10, 13, par3StructureBoundingBox);
                this.func_151550_a(par1World, Blocks.sandstone, 2, 10, -11, 13, par3StructureBoundingBox);

                for (l = 0; l < 4; ++l)
                {
                    if (!this.field_74940_h[l])
                    {
                        int i1 = Direction.offsetX[l] * 2;
                        int j1 = Direction.offsetZ[l] * 2;
                        this.field_74940_h[l] = this.generateStructureChestContents(par1World, par3StructureBoundingBox, par2Random, 10 + i1, -11, 10 + j1, ChestGenHooks.getItems(PYRAMID_DESERT_CHEST, par2Random), ChestGenHooks.getCount(PYRAMID_DESERT_CHEST, par2Random));
                    }
                }

                return true;
            }
        }
}