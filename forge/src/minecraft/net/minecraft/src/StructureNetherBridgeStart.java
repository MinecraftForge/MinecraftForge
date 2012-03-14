package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;

class StructureNetherBridgeStart extends StructureStart
{
    public StructureNetherBridgeStart(World par1World, Random par2Random, int par3, int par4)
    {
        ComponentNetherBridgeStartPiece var5 = new ComponentNetherBridgeStartPiece(par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
        this.components.add(var5);
        var5.buildComponent(var5, this.components, par2Random);
        ArrayList var6 = var5.field_40034_d;

        while (!var6.isEmpty())
        {
            int var7 = par2Random.nextInt(var6.size());
            StructureComponent var8 = (StructureComponent)var6.remove(var7);
            var8.buildComponent(var5, this.components, par2Random);
        }

        this.updateBoundingBox();
        this.setRandomHeight(par1World, par2Random, 48, 70);
    }
}
