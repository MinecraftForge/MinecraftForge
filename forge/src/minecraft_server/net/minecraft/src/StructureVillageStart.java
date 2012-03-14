package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class StructureVillageStart extends StructureStart
{
    private boolean hasMoreThanTwoComponents = false;

    public StructureVillageStart(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        ArrayList var7 = StructureVillagePieces.getStructureVillageWeightedPieceList(par2Random, par5);
        ComponentVillageStartPiece var8 = new ComponentVillageStartPiece(par1World.getWorldChunkManager(), 0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2, var7, par5);
        this.components.add(var8);
        var8.buildComponent(var8, this.components, par2Random);
        ArrayList var9 = var8.field_35387_f;
        ArrayList var10 = var8.field_35389_e;
        int var11;

        while (!var9.isEmpty() || !var10.isEmpty())
        {
            StructureComponent var12;

            if (!var9.isEmpty())
            {
                var11 = par2Random.nextInt(var9.size());
                var12 = (StructureComponent)var9.remove(var11);
                var12.buildComponent(var8, this.components, par2Random);
            }
            else
            {
                var11 = par2Random.nextInt(var10.size());
                var12 = (StructureComponent)var10.remove(var11);
                var12.buildComponent(var8, this.components, par2Random);
            }
        }

        this.updateBoundingBox();
        var11 = 0;
        Iterator var14 = this.components.iterator();

        while (var14.hasNext())
        {
            StructureComponent var13 = (StructureComponent)var14.next();

            if (!(var13 instanceof ComponentVillageRoadPiece))
            {
                ++var11;
            }
        }

        this.hasMoreThanTwoComponents = var11 > 2;
    }

    public boolean isSizeableStructure()
    {
        return this.hasMoreThanTwoComponents;
    }
}
