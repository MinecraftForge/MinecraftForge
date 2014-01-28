package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Comparator;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class EntitySorter implements Comparator
{
    // JAVADOC FIELD $$ field_78949_a
    private double entityPosX;
    // JAVADOC FIELD $$ field_78947_b
    private double entityPosY;
    // JAVADOC FIELD $$ field_78948_c
    private double entityPosZ;
    private static final String __OBFID = "CL_00000944";

    public EntitySorter(Entity par1Entity)
    {
        this.entityPosX = -par1Entity.posX;
        this.entityPosY = -par1Entity.posY;
        this.entityPosZ = -par1Entity.posZ;
    }

    public int compare(WorldRenderer par1WorldRenderer, WorldRenderer par2WorldRenderer)
    {
        double d0 = (double)par1WorldRenderer.posXPlus + this.entityPosX;
        double d1 = (double)par1WorldRenderer.posYPlus + this.entityPosY;
        double d2 = (double)par1WorldRenderer.posZPlus + this.entityPosZ;
        double d3 = (double)par2WorldRenderer.posXPlus + this.entityPosX;
        double d4 = (double)par2WorldRenderer.posYPlus + this.entityPosY;
        double d5 = (double)par2WorldRenderer.posZPlus + this.entityPosZ;
        return (int)((d0 * d0 + d1 * d1 + d2 * d2 - (d3 * d3 + d4 * d4 + d5 * d5)) * 1024.0D);
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.compare((WorldRenderer)par1Obj, (WorldRenderer)par2Obj);
    }
}