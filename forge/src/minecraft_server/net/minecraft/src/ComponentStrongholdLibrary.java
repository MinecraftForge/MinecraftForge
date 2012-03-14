package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdLibrary extends ComponentStronghold
{
    private static final StructurePieceTreasure[] field_35335_b = new StructurePieceTreasure[] {new StructurePieceTreasure(Item.book.shiftedIndex, 0, 1, 3, 20), new StructurePieceTreasure(Item.paper.shiftedIndex, 0, 2, 7, 20), new StructurePieceTreasure(Item.map.shiftedIndex, 0, 1, 1, 1), new StructurePieceTreasure(Item.compass.shiftedIndex, 0, 1, 1, 1)};
    protected final EnumDoor field_35337_a;
    private final boolean field_35336_c;

    public ComponentStrongholdLibrary(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.field_35337_a = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
        this.field_35336_c = par3StructureBoundingBox.getYSize() > 6;
    }

    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random) {}

    public static ComponentStrongholdLibrary findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 14, 11, 15, par5);

        if (!canStrongholdGoDeeper(var7) || StructureComponent.findIntersecting(par0List, var7) != null)
        {
            var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 14, 6, 15, par5);

            if (!canStrongholdGoDeeper(var7) || StructureComponent.findIntersecting(par0List, var7) != null)
            {
                return null;
            }
        }

        return new ComponentStrongholdLibrary(par6, par1Random, var7, par5);
    }

    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
        {
            return false;
        }
        else
        {
            byte var4 = 11;

            if (!this.field_35336_c)
            {
                var4 = 6;
            }

            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 13, var4 - 1, 14, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.field_35337_a, 4, 1, 0);
            this.randomlyFillWithBlocks(par1World, par3StructureBoundingBox, par2Random, 0.07F, 2, 1, 1, 11, 4, 13, Block.web.blockID, Block.web.blockID, false);
            int var7;

            for (var7 = 1; var7 <= 13; ++var7)
            {
                if ((var7 - 1) % 4 == 0)
                {
                    this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, var7, 1, 4, var7, Block.planks.blockID, Block.planks.blockID, false);
                    this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 1, var7, 12, 4, var7, Block.planks.blockID, Block.planks.blockID, false);
                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 2, 3, var7, par3StructureBoundingBox);
                    this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, 11, 3, var7, par3StructureBoundingBox);

                    if (this.field_35336_c)
                    {
                        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 6, var7, 1, 9, var7, Block.planks.blockID, Block.planks.blockID, false);
                        this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 6, var7, 12, 9, var7, Block.planks.blockID, Block.planks.blockID, false);
                    }
                }
                else
                {
                    this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, var7, 1, 4, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
                    this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 1, var7, 12, 4, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);

                    if (this.field_35336_c)
                    {
                        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 6, var7, 1, 9, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
                        this.fillWithBlocks(par1World, par3StructureBoundingBox, 12, 6, var7, 12, 9, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
                    }
                }
            }

            for (var7 = 3; var7 < 12; var7 += 2)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 1, var7, 4, 3, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 6, 1, var7, 7, 3, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 1, var7, 10, 3, var7, Block.bookShelf.blockID, Block.bookShelf.blockID, false);
            }

            if (this.field_35336_c)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 5, 1, 3, 5, 13, Block.planks.blockID, Block.planks.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 5, 1, 12, 5, 13, Block.planks.blockID, Block.planks.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 1, 9, 5, 2, Block.planks.blockID, Block.planks.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 5, 12, 9, 5, 13, Block.planks.blockID, Block.planks.blockID, false);
                this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 9, 5, 11, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 8, 5, 11, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, 9, 5, 10, par3StructureBoundingBox);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 3, 6, 2, 3, 6, 12, Block.fence.blockID, Block.fence.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 6, 2, 10, 6, 10, Block.fence.blockID, Block.fence.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 6, 2, 9, 6, 2, Block.fence.blockID, Block.fence.blockID, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 6, 12, 8, 6, 12, Block.fence.blockID, Block.fence.blockID, false);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 9, 6, 11, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 8, 6, 11, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, 9, 6, 10, par3StructureBoundingBox);
                var7 = this.getMetadataWithOffset(Block.ladder.blockID, 3);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 1, 13, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 2, 13, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 3, 13, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 4, 13, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 5, 13, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 6, 13, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.ladder.blockID, var7, 10, 7, 13, par3StructureBoundingBox);
                byte var8 = 7;
                byte var9 = 7;
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 9, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 9, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 8, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 8, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 7, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 7, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 2, 7, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 + 1, 7, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 7, var9 - 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8 - 1, 7, var9 + 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 7, var9 - 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fence.blockID, 0, var8, 7, var9 + 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, var8 - 2, 8, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, var8 + 1, 8, var9, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, var8 - 1, 8, var9 - 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, var8 - 1, 8, var9 + 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, var8, 8, var9 - 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.torchWood.blockID, 0, var8, 8, var9 + 1, par3StructureBoundingBox);
            }

            this.createTreasureChestAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 3, 3, 5, field_35335_b, 1 + par2Random.nextInt(4));

            if (this.field_35336_c)
            {
                this.placeBlockAtCurrentPosition(par1World, 0, 0, 12, 9, 1, par3StructureBoundingBox);
                this.createTreasureChestAtCurrentPosition(par1World, par3StructureBoundingBox, par2Random, 12, 8, 1, field_35335_b, 1 + par2Random.nextInt(4));
            }

            return true;
        }
    }
}
