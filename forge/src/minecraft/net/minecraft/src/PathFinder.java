package net.minecraft.src;

public class PathFinder
{
    /** Used to find obstacles */
    private IBlockAccess worldMap;

    /** The path being generated */
    private Path path = new Path();

    /** The points in the path */
    private IntHashMap pointMap = new IntHashMap();

    /** Selection of path points to add to the path */
    private PathPoint[] pathOptions = new PathPoint[32];

    /** should the PathFinder go through wodden door blocks */
    private boolean isWoddenDoorAllowed;

    /**
     * should the PathFinder disregard BlockMovement type materials in its path
     */
    private boolean isMovementBlockAllowed;
    private boolean isPathingInWater;

    /** tells the FathFinder to not stop pathing underwater */
    private boolean canEntityDrown;

    public PathFinder(IBlockAccess par1IBlockAccess, boolean par2, boolean par3, boolean par4, boolean par5)
    {
        this.worldMap = par1IBlockAccess;
        this.isWoddenDoorAllowed = par2;
        this.isMovementBlockAllowed = par3;
        this.isPathingInWater = par4;
        this.canEntityDrown = par5;
    }

    /**
     * Creates a path from one entity to another within a minimum distance
     */
    public PathEntity createEntityPathTo(Entity par1Entity, Entity par2Entity, float par3)
    {
        return this.createEntityPathTo(par1Entity, par2Entity.posX, par2Entity.boundingBox.minY, par2Entity.posZ, par3);
    }

    /**
     * Creates a path from an entity to a specified location within a minimum distance
     */
    public PathEntity createEntityPathTo(Entity par1Entity, int par2, int par3, int par4, float par5)
    {
        return this.createEntityPathTo(par1Entity, (double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), par5);
    }

    /**
     * Internal implementation of creating a path from an entity to a point
     */
    private PathEntity createEntityPathTo(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        this.path.clearPath();
        this.pointMap.clearMap();
        boolean var9 = this.isPathingInWater;
        int var10 = MathHelper.floor_double(par1Entity.boundingBox.minY + 0.5D);

        if (this.canEntityDrown && par1Entity.isInWater())
        {
            var10 = (int)par1Entity.boundingBox.minY;

            for (int var11 = this.worldMap.getBlockId(MathHelper.floor_double(par1Entity.posX), var10, MathHelper.floor_double(par1Entity.posZ)); var11 == Block.waterMoving.blockID || var11 == Block.waterStill.blockID; var11 = this.worldMap.getBlockId(MathHelper.floor_double(par1Entity.posX), var10, MathHelper.floor_double(par1Entity.posZ)))
            {
                ++var10;
            }

            var9 = this.isPathingInWater;
            this.isPathingInWater = false;
        }
        else
        {
            var10 = MathHelper.floor_double(par1Entity.boundingBox.minY + 0.5D);
        }

        PathPoint var15 = this.openPoint(MathHelper.floor_double(par1Entity.boundingBox.minX), var10, MathHelper.floor_double(par1Entity.boundingBox.minZ));
        PathPoint var12 = this.openPoint(MathHelper.floor_double(par2 - (double)(par1Entity.width / 2.0F)), MathHelper.floor_double(par4), MathHelper.floor_double(par6 - (double)(par1Entity.width / 2.0F)));
        PathPoint var13 = new PathPoint(MathHelper.floor_float(par1Entity.width + 1.0F), MathHelper.floor_float(par1Entity.height + 1.0F), MathHelper.floor_float(par1Entity.width + 1.0F));
        PathEntity var14 = this.addToPath(par1Entity, var15, var12, var13, par8);
        this.isPathingInWater = var9;
        return var14;
    }

    /**
     * Adds a path from start to end and returns the whole path (args: unused, start, end, unused, maxDistance)
     */
    private PathEntity addToPath(Entity par1Entity, PathPoint par2PathPoint, PathPoint par3PathPoint, PathPoint par4PathPoint, float par5)
    {
        par2PathPoint.totalPathDistance = 0.0F;
        par2PathPoint.distanceToNext = par2PathPoint.distanceTo(par3PathPoint);
        par2PathPoint.distanceToTarget = par2PathPoint.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(par2PathPoint);
        PathPoint var6 = par2PathPoint;

        while (!this.path.isPathEmpty())
        {
            PathPoint var7 = this.path.dequeue();

            if (var7.equals(par3PathPoint))
            {
                return this.createEntityPath(par2PathPoint, par3PathPoint);
            }

            if (var7.distanceTo(par3PathPoint) < var6.distanceTo(par3PathPoint))
            {
                var6 = var7;
            }

            var7.isFirst = true;
            int var8 = this.findPathOptions(par1Entity, var7, par4PathPoint, par3PathPoint, par5);

            for (int var9 = 0; var9 < var8; ++var9)
            {
                PathPoint var10 = this.pathOptions[var9];
                float var11 = var7.totalPathDistance + var7.distanceTo(var10);

                if (!var10.isAssigned() || var11 < var10.totalPathDistance)
                {
                    var10.previous = var7;
                    var10.totalPathDistance = var11;
                    var10.distanceToNext = var10.distanceTo(par3PathPoint);

                    if (var10.isAssigned())
                    {
                        this.path.changeDistance(var10, var10.totalPathDistance + var10.distanceToNext);
                    }
                    else
                    {
                        var10.distanceToTarget = var10.totalPathDistance + var10.distanceToNext;
                        this.path.addPoint(var10);
                    }
                }
            }
        }

        if (var6 == par2PathPoint)
        {
            return null;
        }
        else
        {
            return this.createEntityPath(par2PathPoint, var6);
        }
    }

    /**
     * populates pathOptions with available points and returns the number of options found (args: unused1, currentPoint,
     * unused2, targetPoint, maxDistance)
     */
    private int findPathOptions(Entity par1Entity, PathPoint par2PathPoint, PathPoint par3PathPoint, PathPoint par4PathPoint, float par5)
    {
        int var6 = 0;
        byte var7 = 0;

        if (this.getVerticalOffset(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord + 1, par2PathPoint.zCoord, par3PathPoint) == 1)
        {
            var7 = 1;
        }

        PathPoint var8 = this.getSafePoint(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord, par2PathPoint.zCoord + 1, par3PathPoint, var7);
        PathPoint var9 = this.getSafePoint(par1Entity, par2PathPoint.xCoord - 1, par2PathPoint.yCoord, par2PathPoint.zCoord, par3PathPoint, var7);
        PathPoint var10 = this.getSafePoint(par1Entity, par2PathPoint.xCoord + 1, par2PathPoint.yCoord, par2PathPoint.zCoord, par3PathPoint, var7);
        PathPoint var11 = this.getSafePoint(par1Entity, par2PathPoint.xCoord, par2PathPoint.yCoord, par2PathPoint.zCoord - 1, par3PathPoint, var7);

        if (var8 != null && !var8.isFirst && var8.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var8;
        }

        if (var9 != null && !var9.isFirst && var9.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var9;
        }

        if (var10 != null && !var10.isFirst && var10.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var10;
        }

        if (var11 != null && !var11.isFirst && var11.distanceTo(par4PathPoint) < par5)
        {
            this.pathOptions[var6++] = var11;
        }

        return var6;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint, int par6)
    {
        PathPoint var7 = null;
        int var8 = this.getVerticalOffset(par1Entity, par2, par3, par4, par5PathPoint);

        if (var8 == 2)
        {
            return this.openPoint(par2, par3, par4);
        }
        else
        {
            if (var8 == 1)
            {
                var7 = this.openPoint(par2, par3, par4);
            }

            if (var7 == null && par6 > 0 && var8 != -3 && var8 != -4 && this.getVerticalOffset(par1Entity, par2, par3 + par6, par4, par5PathPoint) == 1)
            {
                var7 = this.openPoint(par2, par3 + par6, par4);
                par3 += par6;
            }

            if (var7 != null)
            {
                int var9 = 0;
                int var10 = 0;

                while (par3 > 0)
                {
                    var10 = this.getVerticalOffset(par1Entity, par2, par3 - 1, par4, par5PathPoint);

                    if (this.isPathingInWater && var10 == -1)
                    {
                        return null;
                    }

                    if (var10 != 1)
                    {
                        break;
                    }

                    ++var9;

                    if (var9 >= 4)
                    {
                        return null;
                    }

                    --par3;

                    if (par3 > 0)
                    {
                        var7 = this.openPoint(par2, par3, par4);
                    }
                }

                if (var10 == -2)
                {
                    return null;
                }
            }

            return var7;
        }
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    private final PathPoint openPoint(int par1, int par2, int par3)
    {
        int var4 = PathPoint.makeHash(par1, par2, par3);
        PathPoint var5 = (PathPoint)this.pointMap.lookup(var4);

        if (var5 == null)
        {
            var5 = new PathPoint(par1, par2, par3);
            this.pointMap.addKey(var4, var5);
        }

        return var5;
    }

    /**
     * Given an x y z, returns a vertical offset needed to search to find a block to stand on
     */
    private int getVerticalOffset(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint)
    {
        boolean var6 = false;

        for (int var7 = par2; var7 < par2 + par5PathPoint.xCoord; ++var7)
        {
            for (int var8 = par3; var8 < par3 + par5PathPoint.yCoord; ++var8)
            {
                for (int var9 = par4; var9 < par4 + par5PathPoint.zCoord; ++var9)
                {
                    int var10 = this.worldMap.getBlockId(var7, var8, var9);

                    if (var10 > 0)
                    {
                        if (var10 == Block.trapdoor.blockID)
                        {
                            var6 = true;
                        }
                        else if (var10 != Block.waterMoving.blockID && var10 != Block.waterStill.blockID)
                        {
                            if (!this.isWoddenDoorAllowed && var10 == Block.doorWood.blockID)
                            {
                                return 0;
                            }
                        }
                        else
                        {
                            if (this.isPathingInWater)
                            {
                                return -1;
                            }

                            var6 = true;
                        }

                        Block var11 = Block.blocksList[var10];

                        if (!var11.getBlocksMovement(this.worldMap, var7, var8, var9) && (!this.isMovementBlockAllowed || var10 != Block.doorWood.blockID))
                        {
                            if (var10 == Block.fence.blockID || var10 == Block.fenceGate.blockID)
                            {
                                return -3;
                            }

                            if (var10 == Block.trapdoor.blockID)
                            {
                                return -4;
                            }

                            Material var12 = var11.blockMaterial;

                            if (var12 != Material.lava)
                            {
                                return 0;
                            }

                            if (!par1Entity.handleLavaMovement())
                            {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return var6 ? 2 : 1;
    }

    /**
     * Returns a new PathEntity for a given start and end point
     */
    private PathEntity createEntityPath(PathPoint par1PathPoint, PathPoint par2PathPoint)
    {
        int var3 = 1;
        PathPoint var4;

        for (var4 = par2PathPoint; var4.previous != null; var4 = var4.previous)
        {
            ++var3;
        }

        PathPoint[] var5 = new PathPoint[var3];
        var4 = par2PathPoint;
        --var3;

        for (var5[var3] = par2PathPoint; var4.previous != null; var5[var3] = var4)
        {
            var4 = var4.previous;
            --var3;
        }

        return new PathEntity(var5);
    }
}
