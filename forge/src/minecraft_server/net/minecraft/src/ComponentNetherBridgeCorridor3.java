package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeCorridor3 extends ComponentNetherBridgePiece
{
    public ComponentNetherBridgeCorridor3(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        this.getNextComponentNormal((ComponentNetherBridgeStartPiece)par1StructureComponent, par2List, par3Random, 1, 0, true);
    }

    /**
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherBridgeCorridor3 createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 14, 10, par5);
        return isAboveGround(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentNetherBridgeCorridor3(par6, par1Random, var7, par5) : null;
    }

    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        int var4 = this.getMetadataWithOffset(Block.stairsNetherBrick.blockID, 2);

        for (int var5 = 0; var5 <= 9; ++var5)
        {
            int var6 = Math.max(1, 7 - var5);
            int var7 = Math.min(Math.max(var6 + 5, 14 - var5), 13);
            int var8 = var5;
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, var5, 4, var6, var5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, var6 + 1, var5, 3, var7 - 1, var5, 0, 0, false);

            if (var5 <= 6)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairsNetherBrick.blockID, var4, 1, var6 + 1, var5, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairsNetherBrick.blockID, var4, 2, var6 + 1, var5, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairsNetherBrick.blockID, var4, 3, var6 + 1, var5, par3StructureBoundingBox);
            }

            this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, var7, var5, 4, var7, var5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, var6 + 1, var5, 0, var7 - 1, var5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, var6 + 1, var5, 4, var7 - 1, var5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);

            if ((var5 & 1) == 0)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, var6 + 2, var5, 0, var6 + 3, var5, Block.netherFence.blockID, Block.netherFence.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, var6 + 2, var5, 4, var6 + 3, var5, Block.netherFence.blockID, Block.netherFence.blockID, false);
            }

            for (int var9 = 0; var9 <= 4; ++var9)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, var9, -1, var8, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
