package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdRightTurn extends ComponentStrongholdLeftTurn
{
    public ComponentStrongholdRightTurn(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1, par2Random, par3StructureBoundingBox, par4);
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        if (this.coordBaseMode != 2 && this.coordBaseMode != 3)
        {
            this.getNextComponentX((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
        }
        else
        {
            this.getNextComponentZ((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
        }
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
        {
            return false;
        }
        else
        {
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 4, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.doorType, 1, 1, 0);

            if (this.coordBaseMode != 2 && this.coordBaseMode != 3)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 1, 0, 3, 3, 0, 0, false);
            }
            else
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 1, 1, 4, 3, 3, 0, 0, false);
            }

            return true;
        }
    }
}
