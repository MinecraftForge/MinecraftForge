package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeCorridor4 extends ComponentNetherBridgePiece
{
    public ComponentNetherBridgeCorridor4(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        byte var4 = 1;

        if (this.coordBaseMode == 1 || this.coordBaseMode == 2)
        {
            var4 = 5;
        }

        this.getNextComponentX((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 0, var4, par3Random.nextInt(8) > 0);
        this.getNextComponentZ((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 0, var4, par3Random.nextInt(8) > 0);
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherBridgeCorridor4 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -3, 0, 0, 9, 7, 9, par5);
        return isAboveGround(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentNetherBridgeCorridor4(par6, par1Random, var7, par5) : null;
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 8, 1, 8, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 8, 5, 8, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 6, 0, 8, 6, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 2, 5, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 2, 0, 8, 5, 0, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 3, 0, 1, 4, 0, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 3, 0, 7, 4, 0, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 4, 8, 2, 8, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 4, 2, 2, 4, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 1, 4, 7, 2, 4, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, 8, 8, 3, 8, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, 6, 0, 3, 7, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 3, 6, 8, 3, 7, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, 4, 0, 5, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 8, 3, 4, 8, 5, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 3, 5, 2, 5, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 3, 5, 7, 5, 5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 4, 5, 1, 5, 5, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 7, 4, 5, 7, 5, 5, Block.netherFence.blockID, Block.netherFence.blockID, false);

        for (int var4 = 0; var4 <= 5; ++var4)
        {
            for (int var5 = 0; var5 <= 8; ++var5)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, var5, -1, var4, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
