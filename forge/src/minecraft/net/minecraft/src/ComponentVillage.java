package net.minecraft.src;

import java.util.List;
import java.util.Random;

abstract class ComponentVillage extends StructureComponent
{
    /** The number of villagers that have been spawned in this component. */
    private int villagersSpawned;

    protected ComponentVillage(int par1)
    {
        super(par1);
    }

    /**
     * Gets the next village component, with the bounding box shift -1 in the X and Z direction.
     */
    protected StructureComponent getNextComponenetNN(ComponentVillageStartPiece par1ComponentVillageStartPiece, List par2List, Random par3Random, int par4, int par5)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());

            case 1:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());

            case 2:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 1, this.getComponentType());

            case 3:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.minZ - 1, 2, this.getComponentType());

            default:
                return null;
        }
    }

    /**
     * Gets the next village component, with the bounding box shift +1 in the X and Z direction.
     */
    protected StructureComponent getNextComponenetPP(ComponentVillageStartPiece par1ComponentVillageStartPiece, List par2List, Random par3Random, int par4, int par5)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());

            case 1:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());

            case 2:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + par4, this.boundingBox.minZ + par5, 3, this.getComponentType());

            case 3:
                return StructureVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + par4, this.boundingBox.maxZ + 1, 0, this.getComponentType());

            default:
                return null;
        }
    }

    protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox)
    {
        int var3 = 0;
        int var4 = 0;

        for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5)
        {
            for (int var6 = this.boundingBox.minX; var6 <= this.boundingBox.maxX; ++var6)
            {
                if (par2StructureBoundingBox.isVecInside(var6, 64, var5))
                {
                    var3 += Math.max(par1World.getTopSolidOrLiquidBlock(var6, var5), par1World.worldProvider.getAverageGroundLevel());
                    ++var4;
                }
            }
        }

        if (var4 == 0)
        {
            return -1;
        }
        else
        {
            return var3 / var4;
        }
    }

    protected static boolean canVillageGoDeeper(StructureBoundingBox par0StructureBoundingBox)
    {
        return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
    }

    /**
     * Spawns a number of villagers in this component. Parameters: world, component bounding box, x offset, y offset, z
     * offset, number of villagers
     */
    protected void spawnVillagers(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3, int par4, int par5, int par6)
    {
        if (this.villagersSpawned < par6)
        {
            for (int var7 = this.villagersSpawned; var7 < par6; ++var7)
            {
                int var8 = this.getXWithOffset(par3 + var7, par5);
                int var9 = this.getYWithOffset(par4);
                int var10 = this.getZWithOffset(par3 + var7, par5);

                if (!par2StructureBoundingBox.isVecInside(var8, var9, var10))
                {
                    break;
                }

                ++this.villagersSpawned;
                EntityVillager var11 = new EntityVillager(par1World, this.getVillagerType(var7));
                var11.setLocationAndAngles((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
                par1World.spawnEntityInWorld(var11);
            }
        }
    }

    /**
     * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
     */
    protected int getVillagerType(int par1)
    {
        return 0;
    }
}
