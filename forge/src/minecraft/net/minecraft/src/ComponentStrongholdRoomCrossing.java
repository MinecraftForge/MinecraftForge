package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdRoomCrossing extends ComponentStronghold
{
    private static final StructurePieceTreasure[] field_35061_c = new StructurePieceTreasure[] {new StructurePieceTreasure(Item.ingotIron.shiftedIndex, 0, 1, 5, 10), new StructurePieceTreasure(Item.ingotGold.shiftedIndex, 0, 1, 3, 5), new StructurePieceTreasure(Item.redstone.shiftedIndex, 0, 4, 9, 5), new StructurePieceTreasure(Item.coal.shiftedIndex, 0, 3, 8, 10), new StructurePieceTreasure(Item.bread.shiftedIndex, 0, 1, 3, 15), new StructurePieceTreasure(Item.appleRed.shiftedIndex, 0, 1, 3, 15), new StructurePieceTreasure(Item.pickaxeSteel.shiftedIndex, 0, 1, 1, 1)};
    protected final EnumDoor doorType;
    protected final int field_35060_b;

    public ComponentStrongholdRoomCrossing(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.doorType = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
        this.field_35060_b = par2Random.nextInt(5);
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 4, 1);
        this.getNextComponentX((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 4);
        this.getNextComponentZ((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 4);
    }

    public static ComponentStrongholdRoomCrossing findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 11, 7, 11, par5);
        return canStrongholdGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentStrongholdRoomCrossing(par6, par1Random, var7, par5) : null;
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
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 10, 6, 10, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.doorType, 4, 1, 0);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 1, 10, 6, 3, 10, 0, 0, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 1, 4, 0, 3, 6, 0, 0, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 1, 4, 10, 3, 6, 0, 0, false);
            int var4;

            switch (this.field_35060_b)
            {
                case 0:
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 5, 1, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 5, 2, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 5, 3, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 4, 3, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 6, 3, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 5, 3, 4, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 5, 3, 6, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 4, 1, 4, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 4, 1, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 4, 1, 6, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 6, 1, 4, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 6, 1, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 6, 1, 6, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 5, 1, 4, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stairSingle.blockID, 0, 5, 1, 6, par3StructureBoundingBox);
                    break;

                case 1:
                    for (var4 = 0; var4 < 5; ++var4)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 1, 3 + var4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 7, 1, 3 + var4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3 + var4, 1, 3, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3 + var4, 1, 7, par3StructureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 5, 1, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 5, 2, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 5, 3, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.waterMoving.blockID, 0, 5, 4, 5, par3StructureBoundingBox);
                    break;

                case 2:
                    for (var4 = 1; var4 <= 9; ++var4)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 1, 3, var4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 9, 3, var4, par3StructureBoundingBox);
                    }

                    for (var4 = 1; var4 <= 9; ++var4)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, var4, 3, 1, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, var4, 3, 9, par3StructureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 5, 1, 4, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 5, 1, 6, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 5, 3, 4, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 5, 3, 6, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 4, 1, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 6, 1, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 4, 3, 5, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 6, 3, 5, par3StructureBoundingBox);

                    for (var4 = 1; var4 <= 3; ++var4)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 4, var4, 4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 6, var4, 4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 4, var4, 6, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.cobblestone.blockID, 0, 6, var4, 6, par3StructureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 5, 3, 5, par3StructureBoundingBox);

                    for (var4 = 2; var4 <= 8; ++var4)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 2, 3, var4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 3, 3, var4, par3StructureBoundingBox);

                        if (var4 <= 3 || var4 >= 7)
                        {
                            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 4, 3, var4, par3StructureBoundingBox);
                            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 5, 3, var4, par3StructureBoundingBox);
                            this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 6, 3, var4, par3StructureBoundingBox);
                        }

                        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 7, 3, var4, par3StructureBoundingBox);
                        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 3, var4, par3StructureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, this.getMetadataWithOffset(Block.ladder.blockID, 4), 9, 1, 3, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, this.getMetadataWithOffset(Block.ladder.blockID, 4), 9, 2, 3, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, this.getMetadataWithOffset(Block.ladder.blockID, 4), 9, 3, 3, par3StructureBoundingBox);
                    this.createTreasureChestAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 3, 4, 8, field_35061_c, 1 + par2Random.nextInt(4));
            }

            return true;
        }
    }
}
