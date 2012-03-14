package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class StructureStrongholdPieces
{
    private static final StructureStrongholdPieceWeight[] pieceWeightArray = new StructureStrongholdPieceWeight[] {new StructureStrongholdPieceWeight(ComponentStrongholdStraight.class, 40, 0), new StructureStrongholdPieceWeight(ComponentStrongholdPrison.class, 5, 5), new StructureStrongholdPieceWeight(ComponentStrongholdLeftTurn.class, 20, 0), new StructureStrongholdPieceWeight(ComponentStrongholdRightTurn.class, 20, 0), new StructureStrongholdPieceWeight(ComponentStrongholdRoomCrossing.class, 10, 6), new StructureStrongholdPieceWeight(ComponentStrongholdStairsStraight.class, 5, 5), new StructureStrongholdPieceWeight(ComponentStrongholdStairs.class, 5, 5), new StructureStrongholdPieceWeight(ComponentStrongholdCrossing.class, 5, 4), new StructureStrongholdPieceWeight(ComponentStrongholdChestCorridor.class, 5, 4), new StructureStrongholdPieceWeight2(ComponentStrongholdLibrary.class, 10, 2), new StructureStrongholdPieceWeight3(ComponentStrongholdPortalRoom.class, 20, 1)};
    private static List structurePieceList;
    private static Class strongComponentType;
    static int totalWeight = 0;
    private static final StructureStrongholdStones strongholdStones = new StructureStrongholdStones((StructureStrongholdPieceWeight2)null);

    /**
     * 'sets up Arrays with the Structure pieces and their weights'
     */
    public static void prepareStructurePieces()
    {
        structurePieceList = new ArrayList();
        StructureStrongholdPieceWeight[] var0 = pieceWeightArray;
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            StructureStrongholdPieceWeight var3 = var0[var2];
            var3.instancesSpawned = 0;
            structurePieceList.add(var3);
        }

        strongComponentType = null;
    }

    private static boolean canAddStructurePieces()
    {
        boolean var0 = false;
        totalWeight = 0;
        StructureStrongholdPieceWeight var2;

        for (Iterator var1 = structurePieceList.iterator(); var1.hasNext(); totalWeight += var2.pieceWeight)
        {
            var2 = (StructureStrongholdPieceWeight)var1.next();

            if (var2.instancesLimit > 0 && var2.instancesSpawned < var2.instancesLimit)
            {
                var0 = true;
            }
        }

        return var0;
    }

    /**
     * 'translates the PieceWeight class to the Component class'
     */
    private static ComponentStronghold getStrongholdComponentFromWeightedPiece(Class par0Class, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        Object var8 = null;

        if (par0Class == ComponentStrongholdStraight.class)
        {
            var8 = ComponentStrongholdStraight.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdPrison.class)
        {
            var8 = ComponentStrongholdPrison.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdLeftTurn.class)
        {
            var8 = ComponentStrongholdLeftTurn.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdRightTurn.class)
        {
            var8 = ComponentStrongholdRightTurn.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdRoomCrossing.class)
        {
            var8 = ComponentStrongholdRoomCrossing.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdStairsStraight.class)
        {
            var8 = ComponentStrongholdStairsStraight.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdStairs.class)
        {
            var8 = ComponentStrongholdStairs.getStrongholdStairsComponent(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdCrossing.class)
        {
            var8 = ComponentStrongholdCrossing.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdChestCorridor.class)
        {
            var8 = ComponentStrongholdChestCorridor.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdLibrary.class)
        {
            var8 = ComponentStrongholdLibrary.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }
        else if (par0Class == ComponentStrongholdPortalRoom.class)
        {
            var8 = ComponentStrongholdPortalRoom.findValidPlacement(par1List, par2Random, par3, par4, par5, par6, par7);
        }

        return (ComponentStronghold)var8;
    }

    private static ComponentStronghold getNextComponent(ComponentStrongholdStairs2 par0ComponentStrongholdStairs2, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (!canAddStructurePieces())
        {
            return null;
        }
        else
        {
            if (strongComponentType != null)
            {
                ComponentStronghold var8 = getStrongholdComponentFromWeightedPiece(strongComponentType, par1List, par2Random, par3, par4, par5, par6, par7);
                strongComponentType = null;

                if (var8 != null)
                {
                    return var8;
                }
            }

            int var13 = 0;

            while (var13 < 5)
            {
                ++var13;
                int var9 = par2Random.nextInt(totalWeight);
                Iterator var10 = structurePieceList.iterator();

                while (var10.hasNext())
                {
                    StructureStrongholdPieceWeight var11 = (StructureStrongholdPieceWeight)var10.next();
                    var9 -= var11.pieceWeight;

                    if (var9 < 0)
                    {
                        if (!var11.canSpawnMoreStructuresOfType(par7) || var11 == par0ComponentStrongholdStairs2.field_35038_a)
                        {
                            break;
                        }

                        ComponentStronghold var12 = getStrongholdComponentFromWeightedPiece(var11.pieceClass, par1List, par2Random, par3, par4, par5, par6, par7);

                        if (var12 != null)
                        {
                            ++var11.instancesSpawned;
                            par0ComponentStrongholdStairs2.field_35038_a = var11;

                            if (!var11.canSpawnMoreStructures())
                            {
                                structurePieceList.remove(var11);
                            }

                            return var12;
                        }
                    }
                }
            }

            StructureBoundingBox var14 = ComponentStrongholdCorridor.func_35051_a(par1List, par2Random, par3, par4, par5, par6);

            if (var14 != null && var14.minY > 1)
            {
                return new ComponentStrongholdCorridor(par7, par2Random, var14, par6);
            }
            else
            {
                return null;
            }
        }
    }

    private static StructureComponent getNextValidComponent(ComponentStrongholdStairs2 par0ComponentStrongholdStairs2, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 50)
        {
            return null;
        }
        else if (Math.abs(par3 - par0ComponentStrongholdStairs2.getBoundingBox().minX) <= 112 && Math.abs(par5 - par0ComponentStrongholdStairs2.getBoundingBox().minZ) <= 112)
        {
            ComponentStronghold var8 = getNextComponent(par0ComponentStrongholdStairs2, par1List, par2Random, par3, par4, par5, par6, par7 + 1);

            if (var8 != null)
            {
                par1List.add(var8);
                par0ComponentStrongholdStairs2.field_35037_b.add(var8);
            }

            return var8;
        }
        else
        {
            return null;
        }
    }

    static StructureComponent getNextValidComponentAccess(ComponentStrongholdStairs2 par0ComponentStrongholdStairs2, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        return getNextValidComponent(par0ComponentStrongholdStairs2, par1List, par2Random, par3, par4, par5, par6, par7);
    }

    static Class setComponentType(Class par0Class)
    {
        strongComponentType = par0Class;
        return par0Class;
    }

    static StructureStrongholdStones getStrongholdStones()
    {
        return strongholdStones;
    }
}
