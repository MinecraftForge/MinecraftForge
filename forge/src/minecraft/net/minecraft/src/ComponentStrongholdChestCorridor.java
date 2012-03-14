package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdChestCorridor extends ComponentStronghold
{
    private static final StructurePieceTreasure[] field_40013_a = new StructurePieceTreasure[] {new StructurePieceTreasure(Item.enderPearl.shiftedIndex, 0, 1, 1, 10), new StructurePieceTreasure(Item.diamond.shiftedIndex, 0, 1, 3, 3), new StructurePieceTreasure(Item.ingotIron.shiftedIndex, 0, 1, 5, 10), new StructurePieceTreasure(Item.ingotGold.shiftedIndex, 0, 1, 3, 5), new StructurePieceTreasure(Item.redstone.shiftedIndex, 0, 4, 9, 5), new StructurePieceTreasure(Item.bread.shiftedIndex, 0, 1, 3, 15), new StructurePieceTreasure(Item.appleRed.shiftedIndex, 0, 1, 3, 15), new StructurePieceTreasure(Item.pickaxeSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.swordSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.plateSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.helmetSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.legsSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.bootsSteel.shiftedIndex, 0, 1, 1, 5), new StructurePieceTreasure(Item.appleGold.shiftedIndex, 0, 1, 1, 1)};
    private final EnumDoor doorType;
    private boolean field_40012_c;

    public ComponentStrongholdChestCorridor(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.doorType = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
    }

    public static ComponentStrongholdChestCorridor findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -1, 0, 5, 5, 7, par5);
        return canStrongholdGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentStrongholdChestCorridor(par6, par1Random, var7, par5) : null;
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
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 4, 6, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.doorType, 1, 1, 0);
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.OPENING, 1, 1, 6);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 1, 2, 3, 1, 4, Block.stoneBrick.blockID, Block.stoneBrick.blockID, false);
            this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 5, 3, 1, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 5, 3, 1, 5, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 5, 3, 2, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 5, 3, 2, 4, par3StructureBoundingBox);
            int var4;

            for (var4 = 2; var4 <= 4; ++var4)
            {
                this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 5, 2, 1, var4, par3StructureBoundingBox);
            }

            if (!this.field_40012_c)
            {
                var4 = this.getYWithOffset(2);
                int var5 = this.getXWithOffset(3, 3);
                int var6 = this.getZWithOffset(3, 3);

                if (par3StructureBoundingBox.isVecInside(var5, var4, var6))
                {
                    this.field_40012_c = true;
                    this.createTreasureChestAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 3, 2, 3, field_40013_a, 2 + par2Random.nextInt(2));
                }
            }

            return true;
        }
    }
}
