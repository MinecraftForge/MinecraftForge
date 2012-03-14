package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentStrongholdPortalRoom extends ComponentStronghold
{
    private boolean field_40316_a;

    public ComponentStrongholdPortalRoom(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
    }

    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        if (par1StructureComponent != null)
        {
            ((ComponentStrongholdStairs2)par1StructureComponent).field_40317_b = this;
        }
    }

    public static ComponentStrongholdPortalRoom findValidPlacement(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -4, -1, 0, 11, 8, 16, par5);
        return canStrongholdGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentStrongholdPortalRoom(par6, par1Random, var7, par5) : null;
    }

    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 10, 7, 15, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.GRATES, 4, 1, 0);
        byte var4 = 6;
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, var4, 1, 1, var4, 14, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 9, var4, 1, 9, var4, 14, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, var4, 1, 8, var4, 2, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 2, var4, 14, 8, var4, 14, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 2, 1, 4, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 8, 1, 1, 9, 1, 4, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 1, 1, 1, 1, 1, 3, Block.lavaMoving.blockID, Block.lavaMoving.blockID, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 9, 1, 1, 9, 1, 3, Block.lavaMoving.blockID, Block.lavaMoving.blockID, false);
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 3, 1, 8, 7, 1, 12, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 4, 1, 9, 6, 1, 11, Block.lavaMoving.blockID, Block.lavaMoving.blockID, false);
        int var5;

        for (var5 = 3; var5 < 14; var5 += 2)
        {
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 3, var5, 0, 4, var5, Block.fenceIron.blockID, Block.fenceIron.blockID, false);
            this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 3, var5, 10, 4, var5, Block.fenceIron.blockID, Block.fenceIron.blockID, false);
        }

        for (var5 = 2; var5 < 9; var5 += 2)
        {
            this.fillWithBlocks(par1World, par3StructureBoundingBox, var5, 3, 15, var5, 4, 15, Block.fenceIron.blockID, Block.fenceIron.blockID, false);
        }

        var5 = this.getMetadataWithOffset(Block.stairsStoneBrickSmooth.blockID, 3);
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 1, 5, 6, 1, 7, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 2, 6, 6, 2, 7, false, par2Random, StructureStrongholdPieces.getStrongholdStones());
        this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 4, 3, 7, 6, 3, 7, false, par2Random, StructureStrongholdPieces.getStrongholdStones());

        for (int var6 = 4; var6 <= 6; ++var6)
        {
            this.placeBlockAtCurrentPosition(par1World, Block.stairsStoneBrickSmooth.blockID, var5, var6, 1, 4, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stairsStoneBrickSmooth.blockID, var5, var6, 2, 5, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stairsStoneBrickSmooth.blockID, var5, var6, 3, 6, par3StructureBoundingBox);
        }

        byte var14 = 2;
        byte var7 = 0;
        byte var8 = 3;
        byte var9 = 1;

        switch (this.coordBaseMode)
        {
            case 0:
                var14 = 0;
                var7 = 2;
                break;

            case 1:
                var14 = 1;
                var7 = 3;
                var8 = 0;
                var9 = 2;

            case 2:
            default:
                break;

            case 3:
                var14 = 3;
                var7 = 1;
                var8 = 0;
                var9 = 2;
        }

        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var14 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var14 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var14 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var7 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var7 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var7 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var8 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var8 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var8 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var9 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var9 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Block.endPortalFrame.blockID, var9 + (par2Random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 11, par3StructureBoundingBox);

        if (!this.field_40316_a)
        {
            int var13 = this.getYWithOffset(3);
            int var10 = this.getXWithOffset(5, 6);
            int var11 = this.getZWithOffset(5, 6);

            if (par3StructureBoundingBox.isVecInside(var10, var13, var11))
            {
                this.field_40316_a = true;
                par1World.setBlockWithNotify(var10, var13, var11, Block.mobSpawner.blockID);
                TileEntityMobSpawner var12 = (TileEntityMobSpawner)par1World.getBlockTileEntity(var10, var13, var11);

                if (var12 != null)
                {
                    var12.setMobID("Silverfish");
                }
            }
        }

        return true;
    }
}
