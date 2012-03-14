package net.minecraft.src;

public class Path
{
    /** Contains the points in this path */
    private PathPoint[] pathPoints = new PathPoint[1024];

    /** The number of points in this path */
    private int count = 0;

    /**
     * Adds a point to the path
     */
    public PathPoint addPoint(PathPoint par1PathPoint)
    {
        if (par1PathPoint.index >= 0)
        {
            throw new IllegalStateException("OW KNOWS!");
        }
        else
        {
            if (this.count == this.pathPoints.length)
            {
                PathPoint[] var2 = new PathPoint[this.count << 1];
                System.arraycopy(this.pathPoints, 0, var2, 0, this.count);
                this.pathPoints = var2;
            }

            this.pathPoints[this.count] = par1PathPoint;
            par1PathPoint.index = this.count;
            this.sortBack(this.count++);
            return par1PathPoint;
        }
    }

    /**
     * Clears the path
     */
    public void clearPath()
    {
        this.count = 0;
    }

    /**
     * Returns and removes the first point in the path
     */
    public PathPoint dequeue()
    {
        PathPoint var1 = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;

        if (this.count > 0)
        {
            this.sortForward(0);
        }

        var1.index = -1;
        return var1;
    }

    /**
     * Changes the provided point's distance to target
     */
    public void changeDistance(PathPoint par1PathPoint, float par2)
    {
        float var3 = par1PathPoint.distanceToTarget;
        par1PathPoint.distanceToTarget = par2;

        if (par2 < var3)
        {
            this.sortBack(par1PathPoint.index);
        }
        else
        {
            this.sortForward(par1PathPoint.index);
        }
    }

    /**
     * Sorts a point to the left
     */
    private void sortBack(int par1)
    {
        PathPoint var2 = this.pathPoints[par1];
        int var4;

        for (float var3 = var2.distanceToTarget; par1 > 0; par1 = var4)
        {
            var4 = par1 - 1 >> 1;
            PathPoint var5 = this.pathPoints[var4];

            if (var3 >= var5.distanceToTarget)
            {
                break;
            }

            this.pathPoints[par1] = var5;
            var5.index = par1;
        }

        this.pathPoints[par1] = var2;
        var2.index = par1;
    }

    /**
     * Sorts a point to the right
     */
    private void sortForward(int par1)
    {
        PathPoint var2 = this.pathPoints[par1];
        float var3 = var2.distanceToTarget;

        while (true)
        {
            int var4 = 1 + (par1 << 1);
            int var5 = var4 + 1;

            if (var4 >= this.count)
            {
                break;
            }

            PathPoint var6 = this.pathPoints[var4];
            float var7 = var6.distanceToTarget;
            PathPoint var8;
            float var9;

            if (var5 >= this.count)
            {
                var8 = null;
                var9 = Float.POSITIVE_INFINITY;
            }
            else
            {
                var8 = this.pathPoints[var5];
                var9 = var8.distanceToTarget;
            }

            if (var7 < var9)
            {
                if (var7 >= var3)
                {
                    break;
                }

                this.pathPoints[par1] = var6;
                var6.index = par1;
                par1 = var4;
            }
            else
            {
                if (var9 >= var3)
                {
                    break;
                }

                this.pathPoints[par1] = var8;
                var8.index = par1;
                par1 = var5;
            }
        }

        this.pathPoints[par1] = var2;
        var2.index = par1;
    }

    /**
     * Returns true if this path contains no points
     */
    public boolean isPathEmpty()
    {
        return this.count == 0;
    }
}
