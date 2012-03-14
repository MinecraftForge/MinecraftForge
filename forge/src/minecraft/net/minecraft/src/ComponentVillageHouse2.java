package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillageHouse2 extends ComponentVillage
{
    private static final StructurePieceTreasure[] field_46002_a = new StructurePieceTreasure[] {new StructurePieceTreasure(Item.diamond.shiftedIndex, 0, 1, 3, 3), new StructurePieceTreasure(Item.ingotIron.shiftedIndex, 0, 1, 5, 10), new StructurePieceTreasure(Item.ingotGold.shiftedIndex, 0, 1, 3, 5), new StructurePieceTreasure(Item.bread.shiftedIndex, 0, 1, 3, 15), new StructurePieceTreasure(Item.appleRed.shiftedIndex, 0, 1, 3, 15), new StructurePieceTreasure(Item.pickaxeSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.swordSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.plateSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.helmetSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.legsSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.bootsSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Block.obsidian.blockID, 0, 3, 7, 5), new StructurePieceTreasure(Block.sapling.blockID, 0, 3, 7, 5)};
    private int averageGroundLevel = -1;
    private boolean field_46001_c;

    public ComponentVillageHouse2(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {}

    public static ComponentVillageHouse2 findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, 0, 0, 0, 10, 6, 7, par5);
        return canVillageGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentVillageHouse2(par6, par1Random, var7, par5) : null;
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 6 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 9, 4, 6, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 9, 0, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 4, 0, 9, 4, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 0, 9, 5, 6, Block.stairSingle.blockID, Block.stairSingle.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 1, 8, 5, 5, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 0, 2, 3, 0, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 0, 4, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 1, 0, 3, 4, 0, Block.wood.blockID, Block.wood.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 6, 0, 4, 6, Block.wood.blockID, Block.wood.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 3, 3, 1, par3StructureBoundingBox);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 1, 2, 3, 3, 2, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 1, 3, 5, 3, 3, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 5, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 6, 5, 3, 6, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 1, 0, 5, 3, 0, Block.fence.blockID, Block.fence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 1, 0, 9, 3, 0, Block.fence.blockID, Block.fence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 1, 4, 9, 4, 6, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.lavaMoving.blockID, 0, 7, 1, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.lavaMoving.blockID, 0, 8, 1, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, 9, 2, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, 9, 2, 4, par3StructureBoundingBox);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 2, 4, 8, 2, 5, 0, 0, false);
        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 6, 1, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stoneOvenIdle.blockID, 0, 6, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stoneOvenIdle.blockID, 0, 6, 3, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairDouble.blockID, 0, 8, 1, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 2, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 2, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 2, 1, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.pressurePlatePlanks.blockID, 0, 2, 2, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 1, 1, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 3), 2, 1, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 1), 1, 1, 4, par3StructureBoundingBox);
        int var4;
        int var5;

        if (!this.field_46001_c)
        {
            var4 = this.getYWithOffset(1);
            var5 = this.getXWithOffset(5, 5);
            int var6 = this.getZWithOffset(5, 5);

            if (par3StructureBoundingBox.isVecInside(var5, var4, var6))
            {
                this.field_46001_c = true;
                this.createTreasureChestAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 5, 1, 5, field_46002_a, 3 + par2Random.nextInt(6));
            }
        }

        for (var4 = 6; var4 <= 8; ++var4)
        {
            if (this.getBlockIdAtCurrentPosition(par1World, var4, 0, -1, par3StructureBoundingBox) == 0 && this.getBlockIdAtCurrentPosition(par1World, var4, -1, -1, par3StructureBoundingBox) != 0)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairCompactCobblestone.blockID, this.getMetadataWithOffset(Block.stairCompactCobblestone.blockID, 3), var4, 0, -1, par3StructureBoundingBox);
            }
        }

        for (var4 = 0; var4 < 7; ++var4)
        {
            for (var5 = 0; var5 < 10; ++var5)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, var5, 6, var4, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.cobblestone.blockID, 0, var5, -1, var4, par3StructureBoundingBox);
            }
        }

        this.spawnVillagers(par1World, par3StructureBoundingBox, 7, 1, 1, 1);
        return true;
    }

    /**
     * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
     */
    protected int getVillagerType(int par1)
    {
        return 3;
    }
}
