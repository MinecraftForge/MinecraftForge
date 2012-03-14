package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillageHouse1 extends ComponentVillage
{
    private int averageGroundLevel = -1;

    public ComponentVillageHouse1(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {}

    /**
     * Trys to find a valid place to put this component.
     */
    public static ComponentVillageHouse1 findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, 0, 0, 0, 9, 9, 6, par5);
        return canVillageGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentVillageHouse1(par6, par1Random, var7, par5) : null;
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

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 9 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 7, 5, 4, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 8, 0, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 0, 8, 5, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 6, 1, 8, 6, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 7, 2, 8, 7, 3, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        int var4 = this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 3);
        int var5 = this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 2);
        int var6;
        int var7;

        for (var6 = -1; var6 <= 2; ++var6)
        {
            for (var7 = 0; var7 <= 8; ++var7)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, var4, var7, 6 + var6, var6, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, var5, var7, 6 + var6, 5 - var6, par3StructureBoundingBox);
            }
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 0, 1, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 5, 8, 1, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 1, 0, 8, 1, 4, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 1, 0, 7, 1, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 0, 4, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 5, 0, 4, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 2, 5, 8, 4, 5, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 2, 0, 8, 4, 0, Block.cobblestone.blockID, Block.cobblestone.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 1, 0, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 5, 7, 4, 5, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 2, 1, 8, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 0, 7, 4, 0, Block.planks.blockID, Block.planks.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 5, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 6, 2, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 4, 3, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 5, 3, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 6, 3, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 3, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 0, 3, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 2, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 3, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 8, 3, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 2, 2, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 3, 2, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 5, 2, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.thinGlass.blockID, 0, 6, 2, 5, par3StructureBoundingBox);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 4, 1, 7, 4, 1, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 4, 4, 7, 4, 4, Block.planks.blockID, Block.planks.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 3, 4, 7, 3, 4, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 7, 1, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 0), 7, 1, 3, par3StructureBoundingBox);
        var6 = this.getMetadataWithOffset(Block.stairCompactPlanks.blockID, 3);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, var6, 6, 1, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, var6, 5, 1, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, var6, 4, 1, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.stairCompactPlanks.blockID, var6, 3, 1, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 6, 1, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.pressurePlatePlanks.blockID, 0, 6, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 4, 1, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.pressurePlatePlanks.blockID, 0, 4, 2, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.workbench.blockID, 0, 7, 1, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, 1, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, 0, 0, 1, 2, 0, par3StructureBoundingBox);
        this.placeDoorAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 1, 1, 0, this.getMetadataWithOffset(Block.doorWood.blockID, 1));

        if (this.getBlockIdAtCurrentPosition(par1World, 1, 0, -1, par3StructureBoundingBox) == 0 && this.getBlockIdAtCurrentPosition(par1World, 1, -1, -1, par3StructureBoundingBox) != 0)
        {
            this.placeBlockAtCurrentPosition(par1World, Block.stairCompactCobblestone.blockID, this.getMetadataWithOffset(Block.stairCompactCobblestone.blockID, 3), 1, 0, -1, par3StructureBoundingBox);
        }

        for (var7 = 0; var7 < 6; ++var7)
        {
            for (int var8 = 0; var8 < 9; ++var8)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, var8, 9, var7, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.cobblestone.blockID, 0, var8, -1, var7, par3StructureBoundingBox);
            }
        }

        this.spawnVillagers(par1World, par3StructureBoundingBox, 2, 1, 2, 1);
        return true;
    }

    /**
     * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
     */
    protected int getVillagerType(int par1)
    {
        return 1;
    }
}
