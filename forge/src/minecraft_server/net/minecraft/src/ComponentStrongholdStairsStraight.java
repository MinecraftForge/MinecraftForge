package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdStairsStraight extends ComponentStronghold
{
    private final EnumDoor field_35345_a;

    public ComponentStrongholdStairsStraight(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.field_35345_a = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
    }

    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
    }

    public static ComponentStrongholdStairsStraight findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 11, 8, par5);
        return canStrongholdGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentStrongholdStairsStraight(par6, par1Random, var7, par5) : null;
    }

    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
        {
            return false;
        }
        else
        {
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 7, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_35345_a, 1, 7, 0);
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.OPENING, 1, 1, 7);
            int var4 = this.getMetadataWithOffset(Block.stairCompactCobblestone.blockID, 2);

            for (int var5 = 0; var5 < 6; ++var5)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairCompactCobblestone.blockID, var4, 1, 6 - var5, 1 + var5, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairCompactCobblestone.blockID, var4, 2, 6 - var5, 1 + var5, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stairCompactCobblestone.blockID, var4, 3, 6 - var5, 1 + var5, par3StructureBoundingBox);

                if (var5 < 5)
                {
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 5 - var5, 1 + var5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 5 - var5, 1 + var5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 5 - var5, 1 + var5, par3StructureBoundingBox);
                }
            }

            return true;
        }
    }
}
