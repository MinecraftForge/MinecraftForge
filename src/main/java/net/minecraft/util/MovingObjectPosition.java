package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition
{
    // JAVADOC FIELD $$ field_72313_a
    public MovingObjectPosition.MovingObjectType typeOfHit;
    // JAVADOC FIELD $$ field_72311_b
    public int blockX;
    // JAVADOC FIELD $$ field_72312_c
    public int blockY;
    // JAVADOC FIELD $$ field_72309_d
    public int blockZ;
    // JAVADOC FIELD $$ field_72310_e
    public int sideHit;
    // JAVADOC FIELD $$ field_72307_f
    public Vec3 hitVec;
    // JAVADOC FIELD $$ field_72308_g
    public Entity entityHit;
    private static final String __OBFID = "CL_00000610";

    /** Used to determine what sub-segment is hit */
    public int subHit = -1;

    /** Used to add extra hit info */
    public Object hitInfo = null;

    public MovingObjectPosition(int par1, int par2, int par3, int par4, Vec3 par5Vec3)
    {
        this(par1, par2, par3, par4, par5Vec3, true);
    }

    public MovingObjectPosition(int p_i45481_1_, int p_i45481_2_, int p_i45481_3_, int p_i45481_4_, Vec3 p_i45481_5_, boolean p_i45481_6_)
    {
        this.typeOfHit = p_i45481_6_ ? MovingObjectPosition.MovingObjectType.BLOCK : MovingObjectPosition.MovingObjectType.MISS;
        this.blockX = p_i45481_1_;
        this.blockY = p_i45481_2_;
        this.blockZ = p_i45481_3_;
        this.sideHit = p_i45481_4_;
        this.hitVec = p_i45481_5_.myVec3LocalPool.getVecFromPool(p_i45481_5_.xCoord, p_i45481_5_.yCoord, p_i45481_5_.zCoord);
    }

    public MovingObjectPosition(Entity par1Entity)
    {
        this(par1Entity, par1Entity.worldObj.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY, par1Entity.posZ));
    }

    public MovingObjectPosition(Entity p_i45482_1_, Vec3 p_i45482_2_)
    {
        this.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
        this.entityHit = p_i45482_1_;
        this.hitVec = p_i45482_2_;
    }

    public String toString()
    {
        return "HitResult{type=" + this.typeOfHit + ", x=" + this.blockX + ", y=" + this.blockY + ", z=" + this.blockZ + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public static enum MovingObjectType
    {
        MISS,
        BLOCK,
        ENTITY;

        private static final String __OBFID = "CL_00000611";
    }
}