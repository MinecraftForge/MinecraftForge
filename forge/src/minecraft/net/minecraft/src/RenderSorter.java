package net.minecraft.src;

import java.util.Comparator;

public class RenderSorter implements Comparator
{
    /** The entity (usually the player) that the camera is inside. */
    private EntityLiving baseEntity;

    public RenderSorter(EntityLiving par1EntityLiving)
    {
        this.baseEntity = par1EntityLiving;
    }

    public int doCompare(WorldRenderer par1WorldRenderer, WorldRenderer par2WorldRenderer)
    {
        boolean var3 = par1WorldRenderer.isInFrustum;
        boolean var4 = par2WorldRenderer.isInFrustum;

        if (var3 && !var4)
        {
            return 1;
        }
        else if (var4 && !var3)
        {
            return -1;
        }
        else
        {
            double var5 = (double)par1WorldRenderer.distanceToEntitySquared(this.baseEntity);
            double var7 = (double)par2WorldRenderer.distanceToEntitySquared(this.baseEntity);
            return var5 < var7 ? 1 : (var5 > var7 ? -1 : (par1WorldRenderer.chunkIndex < par2WorldRenderer.chunkIndex ? 1 : -1));
        }
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.doCompare((WorldRenderer)par1Obj, (WorldRenderer)par2Obj);
    }
}
