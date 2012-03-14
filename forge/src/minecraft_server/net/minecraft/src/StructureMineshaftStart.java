package net.minecraft.src;

import java.util.Random;

public class StructureMineshaftStart extends StructureStart
{
    public StructureMineshaftStart(World par1World, Random par2Random, int par3, int par4)
    {
        ComponentMineshaftRoom var5 = new ComponentMineshaftRoom(0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
        this.components.add(var5);
        var5.buildComponent(var5, this.components, par2Random);
        this.updateBoundingBox();
        this.markAvailableHeight(par1World, par2Random, 10);
    }
}
