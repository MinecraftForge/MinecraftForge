package net.minecraft.src;

import java.util.List;
import java.util.Random;

abstract class ComponentStronghold extends StructureComponent
{
    protected ComponentStronghold(int par1)
    {
        super(par1);
    }

    /**
     * 'builds a door of the enumerated types (empty opening is a door)'
     */
    protected void placeDoor(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox, EnumDoor par4EnumDoor, int par5, int par6, int par7)
    {
        switch (EnumDoorHelper.doorEnum[par4EnumDoor.ordinal()])
        {
            case 1:
            default:
                this.fillWithBlocks(par1World, par3StructureBoundingBox, par5, par6, par7, par5 + 3 - 1, par6 + 3 - 1, par7, 0, 0, false);
                break;

            case 2:
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 2, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.doorWood.blockID, 0, par5 + 1, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.doorWood.blockID, 8, par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
                break;

            case 3:
                this.placeBlockAtCurrentPosition(par1World, 0, 0, par5 + 1, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, 0, 0, par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.fenceIron.blockID, 0, par5 + 2, par6, par7, par3StructureBoundingBox);
                break;

            case 4:
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 1, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 2, par6 + 2, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 2, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, par5 + 2, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.doorSteel.blockID, 0, par5 + 1, par6, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.doorSteel.blockID, 8, par5 + 1, par6 + 1, par7, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.button.blockID, this.getMetadataWithOffset(Block.button.blockID, 4), par5 + 2, par6 + 1, par7 + 1, par3StructureBoundingBox);
                this.placeBlockAtCurrentPosition(par1World, Block.button.blockID, this.getMetadataWithOffset(Block.button.blockID, 3), par5 + 2, par6 + 1, par7 - 1, par3StructureBoundingBox);
        }
    }

    protected EnumDoor getRandomDoor(Random par1Random)
    {
        int var2 = par1Random.nextInt(5);

        switch (var2)
        {
            case 0:
            case 1:
            default:
                return EnumDoor.OPENING;

            case 2:
                return EnumDoor.WOOD_DOOR;

            case 3:
                return EnumDoor.GRATES;

            case 4:
                return EnumDoor.IRON_DOOR;
        }
    }

    /**
     * Gets the next component in any cardinal direction
     */
    protected StructureComponent getNextComponentNormal(ComponentStrongholdStairs2 par1ComponentStrongholdStairs2, List par2List, Random par3Random, int par4, int par5)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.maxZ + 1, this.coordBaseMode, this.getComponentType());

            case 1:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType());

            case 2:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par4, this.boundingBox.minY + par5, this.boundingBox.minZ - 1, this.coordBaseMode, this.getComponentType());

            case 3:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par5, this.boundingBox.minZ + par4, this.coordBaseMode, this.getComponentType());

            default:
                return null;
        }
    }

    /**
     * Gets the next component in the +/- X direction
     */
    protected StructureComponent getNextComponentX(ComponentStrongholdStairs2 par1ComponentStrongholdStairs2, List par2List, Random par3Random, int par4, int par5)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());

            case 1:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());

            case 2:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());

            case 3:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());

            default:
                return null;
        }
    }

    /**
     * Gets the next component in the +/- Z direction
     */
    protected StructureComponent getNextComponentZ(ComponentStrongholdStairs2 par1ComponentStrongholdStairs2, List par2List, Random par3Random, int par4, int par5)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());

            case 1:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());

            case 2:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());

            case 3:
                return StructureStrongholdPieces.getNextValidComponentAccess(par1ComponentStrongholdStairs2, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());

            default:
                return null;
        }
    }

    /**
     * 'returns false if the Structure Bounding Box goes below 10'
     */
    protected static boolean canStrongholdGoDeeper(StructureBoundingBox par0StructureBoundingBox)
    {
        return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
    }
}
