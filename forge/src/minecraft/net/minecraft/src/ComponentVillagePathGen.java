package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class ComponentVillagePathGen extends ComponentVillageRoadPiece
{
    private int averageGroundLevel;

    public ComponentVillagePathGen(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.coordBaseMode = par4;
        this.boundingBox = par3StructureBoundingBox;
        this.averageGroundLevel = Math.max(par3StructureBoundingBox.getXSize(), par3StructureBoundingBox.getZSize());
    }

    /**
     * 'Initiates construction of the Structure Component picked, at the current Location of StructGen'
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        boolean var4 = false;
        int var5;
        StructureComponent var6;

        for (var5 = par3Random.nextInt(5); var5 < this.averageGroundLevel - 8; var5 += 2 + par3Random.nextInt(5))
        {
            var6 = this.getNextComponenetNN((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, 0, var5);

            if (var6 != null)
            {
                var5 += Math.max(var6.boundingBox.getXSize(), var6.boundingBox.getZSize());
                var4 = true;
            }
        }

        for (var5 = par3Random.nextInt(5); var5 < this.averageGroundLevel - 8; var5 += 2 + par3Random.nextInt(5))
        {
            var6 = this.getNextComponenetPP((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, 0, var5);

            if (var6 != null)
            {
                var5 += Math.max(var6.boundingBox.getXSize(), var6.boundingBox.getZSize());
                var4 = true;
            }
        }

        if (var4 && par3Random.nextInt(3) > 0)
        {
            switch (this.coordBaseMode)
            {
                case 0:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 1, this.getComponentType());
                    break;

                case 1:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
                    break;

                case 2:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, 1, this.getComponentType());
                    break;

                case 3:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
            }
        }

        if (var4 && par3Random.nextInt(3) > 0)
        {
            switch (this.coordBaseMode)
            {
                case 0:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 3, this.getComponentType());
                    break;

                case 1:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                    break;

                case 2:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, 3, this.getComponentType());
                    break;

                case 3:
                    StructureVillagePieces.getNextStructureComponentVillagePath((ComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
            }
        }
    }

    public static StructureBoundingBox func_35087_a(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 7 * MathHelper.getRandomIntegerInRange(par2Random, 3, 5); var7 >= 7; var7 -= 7)
        {
            StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 3, var7, par6);

            if (StructureComponent.findIntersecting(par1List, var8) == null)
            {
                return var8;
            }
        }

        return null;
    }

    /**
     * 'second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...'
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        for (int var4 = this.boundingBox.minX; var4 <= this.boundingBox.maxX; ++var4)
        {
            for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5)
            {
                if (par3StructureBoundingBox.isVecInside(var4, 64, var5))
                {
                    int var6 = par1World.getTopSolidOrLiquidBlock(var4, var5) - 1;
                    par1World.setBlock(var4, var6, var5, Block.gravel.blockID);
                }
            }
        }

        return true;
    }
}
