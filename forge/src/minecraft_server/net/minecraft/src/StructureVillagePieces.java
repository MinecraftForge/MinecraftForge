package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StructureVillagePieces
{
    public static ArrayList getStructureVillageWeightedPieceList(Random par0Random, int par1)
    {
        ArrayList var2 = new ArrayList();
        var2.add(new StructureVillagePieceWeight(ComponentVillageHouse4_Garden.class, 4, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 4 + par1 * 2)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageChurch.class, 20, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 1 + par1)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageHouse1.class, 20, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 2 + par1)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageWoodHut.class, 3, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 5 + par1 * 3)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageHall.class, 15, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 2 + par1)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageField.class, 3, MathHelper.getRandomIntegerInRange(par0Random, 1 + par1, 4 + par1)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageField2.class, 3, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 4 + par1 * 2)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageHouse2.class, 15, MathHelper.getRandomIntegerInRange(par0Random, 0, 1 + par1)));
        var2.add(new StructureVillagePieceWeight(ComponentVillageHouse3.class, 8, MathHelper.getRandomIntegerInRange(par0Random, 0 + par1, 3 + par1 * 2)));
        Iterator var3 = var2.iterator();

        while (var3.hasNext())
        {
            if (((StructureVillagePieceWeight)var3.next()).villagePiecesLimit == 0)
            {
                var3.remove();
            }
        }

        return var2;
    }

    private static int getAvailablePieceWeight(ArrayList par0ArrayList)
    {
        boolean var1 = false;
        int var2 = 0;
        StructureVillagePieceWeight var4;

        for (Iterator var3 = par0ArrayList.iterator(); var3.hasNext(); var2 += var4.villagePieceWeight)
        {
            var4 = (StructureVillagePieceWeight)var3.next();

            if (var4.villagePiecesLimit > 0 && var4.villagePiecesSpawned < var4.villagePiecesLimit)
            {
                var1 = true;
            }
        }

        return var1 ? var2 : -1;
    }

    private static ComponentVillage getVillageComponentFromWeightedPiece(StructureVillagePieceWeight par0StructureVillagePieceWeight, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        Class var8 = par0StructureVillagePieceWeight.villagePieceClass;
        Object var9 = null;

        if (var8 == ComponentVillageHouse4_Garden.class)
        {
            var9 = ComponentVillageHouse4_Garden.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageChurch.class)
        {
            var9 = ComponentVillageChurch.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageHouse1.class)
        {
            var9 = ComponentVillageHouse1.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageWoodHut.class)
        {
            var9 = ComponentVillageWoodHut.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageHall.class)
        {
            var9 = ComponentVillageHall.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageField.class)
        {
            var9 = ComponentVillageField.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageField2.class)
        {
            var9 = ComponentVillageField2.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageHouse2.class)
        {
            var9 = ComponentVillageHouse2.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (var8 == ComponentVillageHouse3.class)
        {
            var9 = ComponentVillageHouse3.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }

        return (ComponentVillage)var9;
    }

    private static ComponentVillage getNextVillageComponent(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        int var8 = getAvailablePieceWeight(par0ComponentVillageStartPiece.structureVillageWeightedPieceList);

        if (var8 <= 0)
        {
            return null;
        }
        else
        {
            int var9 = 0;

            while (var9 < 5)
            {
                ++var9;
                int var10 = par2Random.nextInt(var8);
                Iterator var11 = par0ComponentVillageStartPiece.structureVillageWeightedPieceList.iterator();

                while (var11.hasNext())
                {
                    StructureVillagePieceWeight var12 = (StructureVillagePieceWeight)var11.next();
                    var10 -= var12.villagePieceWeight;

                    if (var10 < 0)
                    {
                        if (!var12.canSpawnMoreVillagePiecesOfType(par7) || var12 == par0ComponentVillageStartPiece.structVillagePieceWeight && par0ComponentVillageStartPiece.structureVillageWeightedPieceList.size() > 1)
                        {
                            break;
                        }

                        ComponentVillage var13 = getVillageComponentFromWeightedPiece(var12, par1List, par2Random, par3, par4, par5, par6, par7);

                        if (var13 != null)
                        {
                            ++var12.villagePiecesSpawned;
                            par0ComponentVillageStartPiece.structVillagePieceWeight = var12;

                            if (!var12.canSpawnMoreVillagePieces())
                            {
                                par0ComponentVillageStartPiece.structureVillageWeightedPieceList.remove(var12);
                            }

                            return var13;
                        }
                    }
                }
            }

            StructureBoundingBox var14 = ComponentVillageTorch.findValidPlacement(par1List, par2Random, par3, par4, par5, par6);

            if (var14 != null)
            {
                return new ComponentVillageTorch(par7, par2Random, var14, par6);
            }
            else
            {
                return null;
            }
        }
    }

    private static StructureComponent getNextVillageStructureComponent(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 50)
        {
            return null;
        }
        else if (Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) <= 112 && Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) <= 112)
        {
            ComponentVillage var8 = getNextVillageComponent(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7 + 1);

            if (var8 != null)
            {
                int var9 = (var8.boundingBox.minX + var8.boundingBox.maxX) / 2;
                int var10 = (var8.boundingBox.minZ + var8.boundingBox.maxZ) / 2;
                int var11 = var8.boundingBox.maxX - var8.boundingBox.minX;
                int var12 = var8.boundingBox.maxZ - var8.boundingBox.minZ;
                int var13 = var11 > var12 ? var11 : var12;

                if (par0ComponentVillageStartPiece.getWorldChunkMngr().areBiomesViable(var9, var10, var13 / 2 + 4, MapGenVillage.villageSpawnBiomes))
                {
                    par1List.add(var8);
                    par0ComponentVillageStartPiece.field_35389_e.add(var8);
                    return var8;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    private static StructureComponent getNextComponentVillagePath(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 3 + par0ComponentVillageStartPiece.terrainType)
        {
            return null;
        }
        else if (Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) <= 112 && Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) <= 112)
        {
            StructureBoundingBox var8 = ComponentVillagePathGen.func_35378_a(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6);

            if (var8 != null && var8.minY > 10)
            {
                ComponentVillagePathGen var9 = new ComponentVillagePathGen(par7, par2Random, var8, par6);
                int var10 = (var9.boundingBox.minX + var9.boundingBox.maxX) / 2;
                int var11 = (var9.boundingBox.minZ + var9.boundingBox.maxZ) / 2;
                int var12 = var9.boundingBox.maxX - var9.boundingBox.minX;
                int var13 = var9.boundingBox.maxZ - var9.boundingBox.minZ;
                int var14 = var12 > var13 ? var12 : var13;

                if (par0ComponentVillageStartPiece.getWorldChunkMngr().areBiomesViable(var10, var11, var14 / 2 + 4, MapGenVillage.villageSpawnBiomes))
                {
                    par1List.add(var9);
                    par0ComponentVillageStartPiece.field_35387_f.add(var9);
                    return var9;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    static StructureComponent getNextStructureComponent(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        return getNextVillageStructureComponent(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7);
    }

    static StructureComponent getNextStructureComponentVillagePath(ComponentVillageStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        return getNextComponentVillagePath(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7);
    }
}
