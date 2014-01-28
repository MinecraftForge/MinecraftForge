package net.minecraft.server.management;

import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerPositionComparator implements Comparator
{
    private final ChunkCoordinates theChunkCoordinates;
    private static final String __OBFID = "CL_00001422";

    public PlayerPositionComparator(ChunkCoordinates par1ChunkCoordinates)
    {
        this.theChunkCoordinates = par1ChunkCoordinates;
    }

    public int compare(EntityPlayerMP par1EntityPlayerMP, EntityPlayerMP par2EntityPlayerMP)
    {
        double d0 = par1EntityPlayerMP.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
        double d1 = par2EntityPlayerMP.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
        return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.compare((EntityPlayerMP)par1Obj, (EntityPlayerMP)par2Obj);
    }
}