package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Comparator;
import net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public class RenderSorter implements Comparator
{
    // JAVADOC FIELD $$ field_78945_a
    private EntityLivingBase baseEntity;
    private static final String __OBFID = "CL_00000943";

    public RenderSorter(EntityLivingBase par1EntityLivingBase)
    {
        this.baseEntity = par1EntityLivingBase;
    }

    public int compare(WorldRenderer par1WorldRenderer, WorldRenderer par2WorldRenderer)
    {
        if (par1WorldRenderer.isInFrustum && !par2WorldRenderer.isInFrustum)
        {
            return 1;
        }
        else if (par2WorldRenderer.isInFrustum && !par1WorldRenderer.isInFrustum)
        {
            return -1;
        }
        else
        {
            double d0 = (double)par1WorldRenderer.distanceToEntitySquared(this.baseEntity);
            double d1 = (double)par2WorldRenderer.distanceToEntitySquared(this.baseEntity);
            return d0 < d1 ? 1 : (d0 > d1 ? -1 : (par1WorldRenderer.chunkIndex < par2WorldRenderer.chunkIndex ? 1 : -1));
        }
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.compare((WorldRenderer)par1Obj, (WorldRenderer)par2Obj);
    }
}