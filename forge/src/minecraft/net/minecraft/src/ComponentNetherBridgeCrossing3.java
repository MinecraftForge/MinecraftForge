package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeCrossing3 extends ComponentNetherBridgePiece
{
    public ComponentNetherBridgeCrossing3(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    protected ComponentNetherBridgeCrossing3(Random par1Random, int par2, int par3)
    {
        super(0);
        this.coordBaseMode = par1Random.nextInt(4);

        switch (this.coordBaseMode)
        {
            case 0:
            case 2:
                this.boundingBox = new StructureBoundingBox(par2, 64, par3, par2 + 19 - 1, 73, par3 + 19 - 1);
                break;

            default:
                this.boundingBox = new StructureBoundingBox(par2, 64, par3, par2 + 19 - 1, 73, par3 + 19 - 1);
        }
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        this.getNextComponentNormal((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 8, 3, false);
        this.getNextComponentX((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 3, 8, false);
        this.getNextComponentZ((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 3, 8, false);
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherBridgeCrossing3 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -8, -3, 0, 19, 10, 19, par5);
        return isAboveGround(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentNetherBridgeCrossing3(par6, par1Random, var7, par5) : null;
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 3, 0, 11, 4, 18, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, 7, 18, 4, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 5, 0, 10, 7, 18, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 8, 18, 7, 10, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 5, 0, 7, 5, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 5, 11, 7, 5, 18, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 11, 5, 0, 11, 5, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 11, 5, 11, 11, 5, 18, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 7, 7, 5, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 11, 5, 7, 18, 5, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 11, 7, 5, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 11, 5, 11, 18, 5, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 2, 0, 11, 2, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 2, 13, 11, 2, 18, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 0, 0, 11, 1, 3, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 0, 15, 11, 1, 18, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        int var4;
        int var5;

        for (var4 = 7; var4 <= 11; ++var4)
        {
            for (var5 = 0; var5 <= 2; ++var5)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, var4, -1, var5, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, var4, -1, 18 - var5, par3StructureBoundingBox);
            }
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 7, 5, 2, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 13, 2, 7, 18, 2, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 7, 3, 1, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 15, 0, 7, 18, 1, 11, Block.netherBrick.blockID, Block.netherBrick.blockID, false);

        for (var4 = 0; var4 <= 2; ++var4)
        {
            for (var5 = 7; var5 <= 11; ++var5)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, var4, -1, var5, par3StructureBoundingBox);
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, 18 - var4, -1, var5, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
