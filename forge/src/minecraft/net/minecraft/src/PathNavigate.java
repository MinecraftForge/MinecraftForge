package net.minecraft.src;

public class PathNavigate
{
    private EntityLiving field_46076_a;
    private World field_46074_b;
    private PathEntity field_46075_c;
    private float field_46073_d;
    private float field_48683_e;
    private boolean field_48681_f = false;
    private int field_48682_g;
    private int field_48688_h;
    private Vec3D field_48689_i = Vec3D.createVectorHelper(0.0D, 0.0D, 0.0D);
    private boolean field_48686_j = true;
    private boolean field_48687_k = false;
    private boolean field_48684_l = false;
    private boolean field_48685_m = false;

    public PathNavigate(EntityLiving par1EntityLiving, World par2World, float par3)
    {
        this.field_46076_a = par1EntityLiving;
        this.field_46074_b = par2World;
        this.field_48683_e = par3;
    }

    public void func_48664_a(boolean par1)
    {
        this.field_48684_l = par1;
    }

    public boolean func_48658_a()
    {
        return this.field_48684_l;
    }

    public void func_48673_b(boolean par1)
    {
        this.field_48687_k = par1;
    }

    public void func_48663_c(boolean par1)
    {
        this.field_48686_j = par1;
    }

    public boolean func_48665_b()
    {
        return this.field_48687_k;
    }

    public void func_48680_d(boolean par1)
    {
        this.field_48681_f = par1;
    }

    public void func_48660_a(float par1)
    {
        this.field_46073_d = par1;
    }

    public void func_48669_e(boolean par1)
    {
        this.field_48685_m = par1;
    }

    public PathEntity func_48671_a(double par1, double par3, double par5)
    {
        return !this.canNavigate() ? null : this.field_46074_b.getEntityPathToXYZ(this.field_46076_a, MathHelper.floor_double(par1), (int)par3, MathHelper.floor_double(par5), this.field_48683_e, this.field_48686_j, this.field_48687_k, this.field_48684_l, this.field_48685_m);
    }

    public boolean func_48666_a(double par1, double par3, double par5, float par7)
    {
        PathEntity var8 = this.func_48671_a((double)MathHelper.floor_double(par1), (double)((int)par3), (double)MathHelper.floor_double(par5));
        return this.func_48678_a(var8, par7);
    }

    public PathEntity func_48679_a(EntityLiving par1EntityLiving)
    {
        return !this.canNavigate() ? null : this.field_46074_b.getPathEntityToEntity(this.field_46076_a, par1EntityLiving, this.field_48683_e, this.field_48686_j, this.field_48687_k, this.field_48684_l, this.field_48685_m);
    }

    public boolean func_48667_a(EntityLiving par1EntityLiving, float par2)
    {
        PathEntity var3 = this.func_48679_a(par1EntityLiving);
        return var3 != null ? this.func_48678_a(var3, par2) : false;
    }

    public boolean func_48678_a(PathEntity par1PathEntity, float par2)
    {
        if (par1PathEntity == null)
        {
            this.field_46075_c = null;
            return false;
        }
        else
        {
            if (!par1PathEntity.func_48647_a(this.field_46075_c))
            {
                this.field_46075_c = par1PathEntity;
            }

            if (this.field_48681_f)
            {
                this.func_48677_l();
            }

            if (this.field_46075_c.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                this.field_46073_d = par2;
                Vec3D var3 = this.func_48661_h();
                this.field_48688_h = this.field_48682_g;
                this.field_48689_i.xCoord = var3.xCoord;
                this.field_48689_i.yCoord = var3.yCoord;
                this.field_48689_i.zCoord = var3.zCoord;
                return true;
            }
        }
    }

    public PathEntity func_48670_c()
    {
        return this.field_46075_c;
    }

    public void onUpdateNavigation()
    {
        ++this.field_48682_g;

        if (!this.noPath())
        {
            if (this.canNavigate())
            {
                this.pathFollow();
            }

            if (!this.noPath())
            {
                Vec3D var1 = this.field_46075_c.getCurrentNodeVec3d(this.field_46076_a);

                if (var1 != null)
                {
                    this.field_46076_a.getMoveHelper().func_48187_a(var1.xCoord, var1.yCoord, var1.zCoord, this.field_46073_d);
                }
            }
        }
    }

    private void pathFollow()
    {
        Vec3D var1 = this.func_48661_h();
        int var2 = this.field_46075_c.getCurrentPathLength();

        for (int var3 = this.field_46075_c.getCurrentPathIndex(); var3 < this.field_46075_c.getCurrentPathLength(); ++var3)
        {
            if (this.field_46075_c.getPathPointFromIndex(var3).yCoord != (int)var1.yCoord)
            {
                var2 = var3;
                break;
            }
        }

        float var8 = this.field_46076_a.width * this.field_46076_a.width;
        int var4;

        for (var4 = this.field_46075_c.getCurrentPathIndex(); var4 < var2; ++var4)
        {
            if (var1.squareDistanceTo(this.field_46075_c.func_48646_a(this.field_46076_a, var4)) < (double)var8)
            {
                this.field_46075_c.setCurrentPathIndex(var4 + 1);
            }
        }

        var4 = (int)Math.ceil((double)this.field_46076_a.width);
        int var5 = (int)this.field_46076_a.height + 1;
        int var6 = var4;

        for (int var7 = var2 - 1; var7 >= this.field_46075_c.getCurrentPathIndex(); --var7)
        {
            if (this.func_48662_a(var1, this.field_46075_c.func_48646_a(this.field_46076_a, var7), var4, var5, var6))
            {
                this.field_46075_c.setCurrentPathIndex(var7);
                break;
            }
        }

        if (this.field_48682_g - this.field_48688_h > 100)
        {
            if (var1.squareDistanceTo(this.field_48689_i) < 2.25D)
            {
                this.func_48672_f();
            }

            this.field_48688_h = this.field_48682_g;
            this.field_48689_i.xCoord = var1.xCoord;
            this.field_48689_i.yCoord = var1.yCoord;
            this.field_48689_i.zCoord = var1.zCoord;
        }
    }

    /**
     * If null path or reached the end
     */
    public boolean noPath()
    {
        return this.field_46075_c == null || this.field_46075_c.isFinished();
    }

    public void func_48672_f()
    {
        this.field_46075_c = null;
    }

    private Vec3D func_48661_h()
    {
        return Vec3D.createVector(this.field_46076_a.posX, (double)this.func_48668_i(), this.field_46076_a.posZ);
    }

    private int func_48668_i()
    {
        if (this.field_46076_a.isInWater() && this.field_48685_m)
        {
            int var1 = (int)this.field_46076_a.boundingBox.minY;
            int var2 = this.field_46074_b.getBlockId(MathHelper.floor_double(this.field_46076_a.posX), var1, MathHelper.floor_double(this.field_46076_a.posZ));
            int var3 = 0;

            do
            {
                if (var2 != Block.waterMoving.blockID && var2 != Block.waterStill.blockID)
                {
                    return var1;
                }

                ++var1;
                var2 = this.field_46074_b.getBlockId(MathHelper.floor_double(this.field_46076_a.posX), var1, MathHelper.floor_double(this.field_46076_a.posZ));
                ++var3;
            }
            while (var3 <= 16);

            return (int)this.field_46076_a.boundingBox.minY;
        }
        else
        {
            return (int)(this.field_46076_a.boundingBox.minY + 0.5D);
        }
    }

    /**
     * If on ground or swimming and can swim
     */
    private boolean canNavigate()
    {
        return this.field_46076_a.onGround || this.field_48685_m && this.func_48657_k();
    }

    private boolean func_48657_k()
    {
        return this.field_46076_a.isInWater() || this.field_46076_a.handleLavaMovement();
    }

    private void func_48677_l()
    {
        if (!this.field_46074_b.canBlockSeeTheSky(MathHelper.floor_double(this.field_46076_a.posX), (int)(this.field_46076_a.boundingBox.minY + 0.5D), MathHelper.floor_double(this.field_46076_a.posZ)))
        {
            for (int var1 = 0; var1 < this.field_46075_c.getCurrentPathLength(); ++var1)
            {
                PathPoint var2 = this.field_46075_c.getPathPointFromIndex(var1);

                if (this.field_46074_b.canBlockSeeTheSky(var2.xCoord, var2.yCoord, var2.zCoord))
                {
                    this.field_46075_c.setCurrentPathLength(var1 - 1);
                    return;
                }
            }
        }
    }

    private boolean func_48662_a(Vec3D par1Vec3D, Vec3D par2Vec3D, int par3, int par4, int par5)
    {
        int var6 = MathHelper.floor_double(par1Vec3D.xCoord);
        int var7 = MathHelper.floor_double(par1Vec3D.zCoord);
        double var8 = par2Vec3D.xCoord - par1Vec3D.xCoord;
        double var10 = par2Vec3D.zCoord - par1Vec3D.zCoord;
        double var12 = var8 * var8 + var10 * var10;

        if (var12 < 1.0E-8D)
        {
            return false;
        }
        else
        {
            double var14 = 1.0D / Math.sqrt(var12);
            var8 *= var14;
            var10 *= var14;
            par3 += 2;
            par5 += 2;

            if (!this.func_48675_a(var6, (int)par1Vec3D.yCoord, var7, par3, par4, par5, par1Vec3D, var8, var10))
            {
                return false;
            }
            else
            {
                par3 -= 2;
                par5 -= 2;
                double var16 = 1.0D / Math.abs(var8);
                double var18 = 1.0D / Math.abs(var10);
                double var20 = (double)(var6 * 1) - par1Vec3D.xCoord;
                double var22 = (double)(var7 * 1) - par1Vec3D.zCoord;

                if (var8 >= 0.0D)
                {
                    ++var20;
                }

                if (var10 >= 0.0D)
                {
                    ++var22;
                }

                var20 /= var8;
                var22 /= var10;
                int var24 = var8 < 0.0D ? -1 : 1;
                int var25 = var10 < 0.0D ? -1 : 1;
                int var26 = MathHelper.floor_double(par2Vec3D.xCoord);
                int var27 = MathHelper.floor_double(par2Vec3D.zCoord);
                int var28 = var26 - var6;
                int var29 = var27 - var7;

                do
                {
                    if (var28 * var24 <= 0 && var29 * var25 <= 0)
                    {
                        return true;
                    }

                    if (var20 < var22)
                    {
                        var20 += var16;
                        var6 += var24;
                        var28 = var26 - var6;
                    }
                    else
                    {
                        var22 += var18;
                        var7 += var25;
                        var29 = var27 - var7;
                    }
                }
                while (this.func_48675_a(var6, (int)par1Vec3D.yCoord, var7, par3, par4, par5, par1Vec3D, var8, var10));

                return false;
            }
        }
    }

    private boolean func_48675_a(int par1, int par2, int par3, int par4, int par5, int par6, Vec3D par7Vec3D, double par8, double par10)
    {
        int var12 = par1 - par4 / 2;
        int var13 = par3 - par6 / 2;

        if (!this.func_48676_b(var12, par2, var13, par4, par5, par6, par7Vec3D, par8, par10))
        {
            return false;
        }
        else
        {
            for (int var14 = var12; var14 < var12 + par4; ++var14)
            {
                for (int var15 = var13; var15 < var13 + par6; ++var15)
                {
                    double var16 = (double)var14 + 0.5D - par7Vec3D.xCoord;
                    double var18 = (double)var15 + 0.5D - par7Vec3D.zCoord;

                    if (var16 * par8 + var18 * par10 >= 0.0D)
                    {
                        int var20 = this.field_46074_b.getBlockId(var14, par2 - 1, var15);

                        if (var20 <= 0)
                        {
                            return false;
                        }

                        Material var21 = Block.blocksList[var20].blockMaterial;

                        if (var21 == Material.water && !this.field_46076_a.isInWater())
                        {
                            return false;
                        }

                        if (var21 == Material.lava)
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean func_48676_b(int par1, int par2, int par3, int par4, int par5, int par6, Vec3D par7Vec3D, double par8, double par10)
    {
        for (int var12 = par1; var12 < par1 + par4; ++var12)
        {
            for (int var13 = par2; var13 < par2 + par5; ++var13)
            {
                for (int var14 = par3; var14 < par3 + par6; ++var14)
                {
                    double var15 = (double)var12 + 0.5D - par7Vec3D.xCoord;
                    double var17 = (double)var14 + 0.5D - par7Vec3D.zCoord;

                    if (var15 * par8 + var17 * par10 >= 0.0D)
                    {
                        int var19 = this.field_46074_b.getBlockId(var12, var13, var14);

                        if (var19 > 0 && !Block.blocksList[var19].getBlocksMovement(this.field_46074_b, var12, var13, var14))
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
