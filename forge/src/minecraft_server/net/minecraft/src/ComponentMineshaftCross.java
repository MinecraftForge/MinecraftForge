package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentMineshaftCross extends StructureComponent
{
    private final int corridorDirection;
    private final boolean isMultipleFloors;

    public ComponentMineshaftCross(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.corridorDirection = par4;
        this.boundingBox = par3StructureBoundingBox;
        this.isMultipleFloors = par3StructureBoundingBox.getYSize() > 3;
    }

    public static StructureBoundingBox findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5)
    {
        StructureBoundingBox var6 = new StructureBoundingBox(par2, par3, par4, par2, par3 + 2, par4);

        if (par1Random.nextInt(4) == 0)
        {
            var6.maxY += 4;
        }

        switch (par5)
        {
            case 0:
                var6.minX = par2 - 1;
                var6.maxX = par2 + 3;
                var6.maxZ = par4 + 4;
                break;

            case 1:
                var6.minX = par2 - 4;
                var6.minZ = par4 - 1;
                var6.maxZ = par4 + 3;
                break;

            case 2:
                var6.minX = par2 - 1;
                var6.maxX = par2 + 3;
                var6.minZ = par4 - 4;
                break;

            case 3:
                var6.maxX = par2 + 4;
                var6.minZ = par4 - 1;
                var6.maxZ = par4 + 3;
        }

        return StructureComponent.findIntersecting(par0List, var6) != null ? null : var6;
    }

    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        int var4 = this.getComponentType();

        switch (this.corridorDirection)
        {
            case 0:
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, var4);
                break;

            case 1:
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, var4);
                break;

            case 2:
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, var4);
                break;

            case 3:
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, var4);
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, var4);
        }

        if (this.isMultipleFloors)
        {
            if (par3Random.nextBoolean())
            {
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, 2, var4);
            }

            if (par3Random.nextBoolean())
            {
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, 1, var4);
            }

            if (par3Random.nextBoolean())
            {
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, 3, var4);
            }

            if (par3Random.nextBoolean())
            {
                StructureMineshaftPieces.getNextComponent(par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, 0, var4);
            }
        }
    }

    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
        {
            return false;
        }
        else
        {
            if (this.isMultipleFloors)
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, 0, 0, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, 0, 0, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, 0, 0, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, 0, 0, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, 0, 0, false);
            }
            else
            {
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, 0, 0, false);
                this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, 0, 0, false);
            }

            this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Block.planks.blockID, 0, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Block.planks.blockID, 0, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Block.planks.blockID, 0, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Block.planks.blockID, 0, false);

            for (int var4 = this.boundingBox.minX; var4 <= this.boundingBox.maxX; ++var4)
            {
                for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5)
                {
                    int var6 = this.getBlockIdAtCurrentPosition(par1World, var4, this.boundingBox.minY - 1, var5, par3StructureBoundingBox);

                    if (var6 == 0)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Block.planks.blockID, 0, var4, this.boundingBox.minY - 1, var5, par3StructureBoundingBox);
                    }
                }
            }

            return true;
        }
    }
}
