package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentNetherBridgeEnd extends ComponentNetherBridgePiece
{
    private int fillSeed;

    public ComponentNetherBridgeEnd(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
        this.fillSeed = par2Random.nextInt();
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {}

    public static ComponentNetherBridgeEnd func_40023_a(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -3, 0, 5, 10, 8, par5);
        return isAboveGround(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentNetherBridgeEnd(par6, par1Random, var7, par5) : null;
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        Random var4 = new Random((long)this.fillSeed);
        int var5;
        int var6;
        int var7;

        for (var5 = 0; var5 <= 4; ++var5)
        {
            for (var6 = 3; var6 <= 4; ++var6)
            {
                var7 = var4.nextInt(8);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, var5, var6, 0, var5, var6, var7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            }
        }

        var5 = var4.nextInt(8);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 5, 0, 0, 5, var5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        var5 = var4.nextInt(8);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 0, 4, 5, var5, Block.netherBrick.blockID, Block.netherBrick.blockID, false);

        for (var5 = 0; var5 <= 4; ++var5)
        {
            var6 = var4.nextInt(5);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, var5, 2, 0, var5, 2, var6, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
        }

        for (var5 = 0; var5 <= 4; ++var5)
        {
            for (var6 = 0; var6 <= 1; ++var6)
            {
                var7 = var4.nextInt(3);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, var5, var6, 0, var5, var6, var7, Block.netherBrick.blockID, Block.netherBrick.blockID, false);
            }
        }

        return true;
    }
}
