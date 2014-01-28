package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;

public class StructureMineshaftStart extends StructureStart
{
    private static final String __OBFID = "CL_00000450";

    public StructureMineshaftStart() {}

    public StructureMineshaftStart(World par1World, Random par2Random, int par3, int par4)
    {
        super(par3, par4);
        StructureMineshaftPieces.Room room = new StructureMineshaftPieces.Room(0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2);
        this.components.add(room);
        room.buildComponent(room, this.components, par2Random);
        this.updateBoundingBox();
        this.markAvailableHeight(par1World, par2Random, 10);
    }
}