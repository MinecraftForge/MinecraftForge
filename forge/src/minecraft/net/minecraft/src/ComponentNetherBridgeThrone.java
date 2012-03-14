package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeThrone extends ComponentNetherBridgePiece
{
    private boolean hasSpawner;

    public ComponentNetherBridgeThrone(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
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
     * Creates and returns a new component piece. Or null if it could not find enough room to place it.
     */
    public static ComponentNetherBridgeThrone createValidComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -2, 0, 0, 7, 8, 9, par5);
        return isAboveGround(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentNetherBridgeThrone(par6, par1Random, var7, par5) : null;
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 2, 0, 6, 7, 7, 0, 0, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 0, 0, 5, 1, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 1, 5, 2, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 3, 2, 5, 3, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 4, 3, 5, 4, 7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 2, 0, 1, 4, 2, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 2, 0, 5, 4, 2, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 2, 1, 5, 3, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 5, 5, 2, 5, 5, 3, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 3, 0, 5, 8, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 5, 3, 6, 5, 8, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 8, 5, 5, 8, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        this.placeBlockAtCurrentPosition(par1World, Block.netherFence.blockID, 0, 1, 6, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.netherFence.blockID, 0, 5, 6, 3, par3StructureBoundingBox);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 6, 3, 0, 6, 8, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 6, 3, 6, 6, 8, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 6, 8, 5, 7, 8, Block.netherFence.blockID, Block.netherFence.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 2, 8, 8, 4, 8, 8, Block.netherFence.blockID, Block.netherFence.blockID, false);
        int var4;
        int var5;

        if (!this.hasSpawner)
        {
            var4 = this.getYWithOffset(5);
            var5 = this.getXWithOffset(3, 5);
            int var6 = this.getZWithOffset(3, 5);

            if (par3StructureBoundingBox.isVecInside(var5, var4, var6))
            {
                this.hasSpawner = true;
                par1World.setBlockWithNotify(var5, var4, var6, Block.mobSpawner.blockID);
                TileEntityMobSpawner var7 = (TileEntityMobSpawner)par1World.getBlockTileEntity(var5, var4, var6);

                if (var7 != null)
                {
                    var7.setMobID("Blaze");
                }
            }
        }

        for (var4 = 0; var4 <= 6; ++var4)
        {
            for (var5 = 0; var5 <= 6; ++var5)
            {
                this.fillCurrentPositionBlocksDownwards(par1World, Block.netherBrick.blockID, 0, var4, -1, var5, par3StructureBoundingBox);
            }
        }

        return true;
    }
}
