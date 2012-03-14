package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class RailLogic
{
    /** Reference to the World object. */
    private World worldObj;
    private int trackX;
    private int trackY;
    private int trackZ;

    /**
     * A boolean value that is true if the rail is powered, and false if its not.
     */
    private final boolean isPoweredRail;
    private List connectedTracks;

    final BlockRail rail;
    private final boolean canMakeSlopes;

    public RailLogic(BlockRail par1BlockRail, World par2World, int par3, int par4, int par5)
    {
        this.rail = par1BlockRail;
        this.connectedTracks = new ArrayList();
        this.worldObj = par2World;
        this.trackX = par3;
        this.trackY = par4;
        this.trackZ = par5;
        int var6 = par2World.getBlockId(par3, par4, par5);
        
        BlockRail target = (BlockRail)Block.blocksList[var6];
        int var7 = target.getBasicRailMetadata(par2World, null, par3, par4, par5);
        isPoweredRail = !target.isFlexibleRail(par2World, par3, par4, par5);
        canMakeSlopes = target.canMakeSlopes(par2World, par3, par4, par5);
        
        this.setConnections(var7);
    }

    private void setConnections(int par1)
    {
        this.connectedTracks.clear();

        if (par1 == 0)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
        }
        else if (par1 == 1)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
        }
        else if (par1 == 2)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY + 1, this.trackZ));
        }
        else if (par1 == 3)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY + 1, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
        }
        else if (par1 == 4)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY + 1, this.trackZ - 1));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
        }
        else if (par1 == 5)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY + 1, this.trackZ + 1));
        }
        else if (par1 == 6)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
        }
        else if (par1 == 7)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ + 1));
        }
        else if (par1 == 8)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX - 1, this.trackY, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
        }
        else if (par1 == 9)
        {
            this.connectedTracks.add(new ChunkPosition(this.trackX + 1, this.trackY, this.trackZ));
            this.connectedTracks.add(new ChunkPosition(this.trackX, this.trackY, this.trackZ - 1));
        }
    }

    /**
     * Neighboring tracks have potentially been broken, so prune the connected track list
     */
    private void refreshConnectedTracks()
    {
        for (int var1 = 0; var1 < this.connectedTracks.size(); ++var1)
        {
            RailLogic var2 = this.getMinecartTrackLogic((ChunkPosition)this.connectedTracks.get(var1));

            if (var2 != null && var2.isConnectedTo(this))
            {
                this.connectedTracks.set(var1, new ChunkPosition(var2.trackX, var2.trackY, var2.trackZ));
            }
            else
            {
                this.connectedTracks.remove(var1--);
            }
        }
    }

    private boolean isMinecartTrack(int par1, int par2, int par3)
    {
        return BlockRail.isRailBlockAt(this.worldObj, par1, par2, par3) ? true : (BlockRail.isRailBlockAt(this.worldObj, par1, par2 + 1, par3) ? true : BlockRail.isRailBlockAt(this.worldObj, par1, par2 - 1, par3));
    }

    private RailLogic getMinecartTrackLogic(ChunkPosition par1ChunkPosition)
    {
        return BlockRail.isRailBlockAt(this.worldObj, par1ChunkPosition.x, par1ChunkPosition.y, par1ChunkPosition.z) ? new RailLogic(this.rail, this.worldObj, par1ChunkPosition.x, par1ChunkPosition.y, par1ChunkPosition.z) : (BlockRail.isRailBlockAt(this.worldObj, par1ChunkPosition.x, par1ChunkPosition.y + 1, par1ChunkPosition.z) ? new RailLogic(this.rail, this.worldObj, par1ChunkPosition.x, par1ChunkPosition.y + 1, par1ChunkPosition.z) : (BlockRail.isRailBlockAt(this.worldObj, par1ChunkPosition.x, par1ChunkPosition.y - 1, par1ChunkPosition.z) ? new RailLogic(this.rail, this.worldObj, par1ChunkPosition.x, par1ChunkPosition.y - 1, par1ChunkPosition.z) : null));
    }

    private boolean isConnectedTo(RailLogic par1RailLogic)
    {
        for (int var2 = 0; var2 < this.connectedTracks.size(); ++var2)
        {
            ChunkPosition var3 = (ChunkPosition)this.connectedTracks.get(var2);

            if (var3.x == par1RailLogic.trackX && var3.z == par1RailLogic.trackZ)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the specified block is in the same railway.
     */
    private boolean isInTrack(int par1, int par2, int par3)
    {
        for (int var4 = 0; var4 < this.connectedTracks.size(); ++var4)
        {
            ChunkPosition var5 = (ChunkPosition)this.connectedTracks.get(var4);

            if (var5.x == par1 && var5.z == par3)
            {
                return true;
            }
        }

        return false;
    }

    private int getAdjacentTracks()
    {
        int var1 = 0;

        if (this.isMinecartTrack(this.trackX, this.trackY, this.trackZ - 1))
        {
            ++var1;
        }

        if (this.isMinecartTrack(this.trackX, this.trackY, this.trackZ + 1))
        {
            ++var1;
        }

        if (this.isMinecartTrack(this.trackX - 1, this.trackY, this.trackZ))
        {
            ++var1;
        }

        if (this.isMinecartTrack(this.trackX + 1, this.trackY, this.trackZ))
        {
            ++var1;
        }

        return var1;
    }

    /**
     * Determines whether or not the track can bend to meet the specified rail
     */
    private boolean canConnectTo(RailLogic par1RailLogic)
    {
        if (this.isConnectedTo(par1RailLogic))
        {
            return true;
        }
        else if (this.connectedTracks.size() == 2)
        {
            return false;
        }
        else if (this.connectedTracks.size() == 0)
        {
            return true;
        }
        else
        {
            ChunkPosition var2 = (ChunkPosition)this.connectedTracks.get(0);
            return par1RailLogic.trackY == this.trackY && var2.y == this.trackY ? true : true;
        }
    }

    /**
     * The specified neighbor has just formed a new connection, so update accordingly
     */
    private void connectToNeighbor(RailLogic par1RailLogic)
    {
        this.connectedTracks.add(new ChunkPosition(par1RailLogic.trackX, par1RailLogic.trackY, par1RailLogic.trackZ));
        boolean var2 = this.isInTrack(this.trackX, this.trackY, this.trackZ - 1);
        boolean var3 = this.isInTrack(this.trackX, this.trackY, this.trackZ + 1);
        boolean var4 = this.isInTrack(this.trackX - 1, this.trackY, this.trackZ);
        boolean var5 = this.isInTrack(this.trackX + 1, this.trackY, this.trackZ);
        byte var6 = -1;

        if (var2 || var3)
        {
            var6 = 0;
        }

        if (var4 || var5)
        {
            var6 = 1;
        }

        if (!this.isPoweredRail)
        {
            if (var3 && var5 && !var2 && !var4)
            {
                var6 = 6;
            }

            if (var3 && var4 && !var2 && !var5)
            {
                var6 = 7;
            }

            if (var2 && var4 && !var3 && !var5)
            {
                var6 = 8;
            }

            if (var2 && var5 && !var3 && !var4)
            {
                var6 = 9;
            }
        }

        if (var6 == 0 && canMakeSlopes)
        {
            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX, this.trackY + 1, this.trackZ - 1))
            {
                var6 = 4;
            }

            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX, this.trackY + 1, this.trackZ + 1))
            {
                var6 = 5;
            }
        }

        if (var6 == 1 && canMakeSlopes)
        {
            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX + 1, this.trackY + 1, this.trackZ))
            {
                var6 = 2;
            }

            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX - 1, this.trackY + 1, this.trackZ))
            {
                var6 = 3;
            }
        }

        if (var6 < 0)
        {
            var6 = 0;
        }

        int var7 = var6;

        if (this.isPoweredRail)
        {
            var7 = this.worldObj.getBlockMetadata(this.trackX, this.trackY, this.trackZ) & 8 | var6;
        }

        this.worldObj.setBlockMetadataWithNotify(this.trackX, this.trackY, this.trackZ, var7);
    }

    /**
     * Determines whether or not the target rail can connect to this rail
     */
    private boolean canConnectFrom(int par1, int par2, int par3)
    {
        RailLogic var4 = this.getMinecartTrackLogic(new ChunkPosition(par1, par2, par3));

        if (var4 == null)
        {
            return false;
        }
        else
        {
            var4.refreshConnectedTracks();
            return var4.canConnectTo(this);
        }
    }

    /**
     * Completely recalculates the track shape based on neighboring tracks and power state
     */
    public void refreshTrackShape(boolean par1, boolean par2)
    {
        boolean var3 = this.canConnectFrom(this.trackX, this.trackY, this.trackZ - 1);
        boolean var4 = this.canConnectFrom(this.trackX, this.trackY, this.trackZ + 1);
        boolean var5 = this.canConnectFrom(this.trackX - 1, this.trackY, this.trackZ);
        boolean var6 = this.canConnectFrom(this.trackX + 1, this.trackY, this.trackZ);
        byte var7 = -1;

        if ((var3 || var4) && !var5 && !var6)
        {
            var7 = 0;
        }

        if ((var5 || var6) && !var3 && !var4)
        {
            var7 = 1;
        }

        if (!this.isPoweredRail)
        {
            if (var4 && var6 && !var3 && !var5)
            {
                var7 = 6;
            }

            if (var4 && var5 && !var3 && !var6)
            {
                var7 = 7;
            }

            if (var3 && var5 && !var4 && !var6)
            {
                var7 = 8;
            }

            if (var3 && var6 && !var4 && !var5)
            {
                var7 = 9;
            }
        }

        if (var7 == -1)
        {
            if (var3 || var4)
            {
                var7 = 0;
            }

            if (var5 || var6)
            {
                var7 = 1;
            }

            if (!this.isPoweredRail)
            {
                if (par1)
                {
                    if (var4 && var6)
                    {
                        var7 = 6;
                    }

                    if (var5 && var4)
                    {
                        var7 = 7;
                    }

                    if (var6 && var3)
                    {
                        var7 = 9;
                    }

                    if (var3 && var5)
                    {
                        var7 = 8;
                    }
                }
                else
                {
                    if (var3 && var5)
                    {
                        var7 = 8;
                    }

                    if (var6 && var3)
                    {
                        var7 = 9;
                    }

                    if (var5 && var4)
                    {
                        var7 = 7;
                    }

                    if (var4 && var6)
                    {
                        var7 = 6;
                    }
                }
            }
        }

        if (var7 == 0 && canMakeSlopes)
        {
            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX, this.trackY + 1, this.trackZ - 1))
            {
                var7 = 4;
            }

            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX, this.trackY + 1, this.trackZ + 1))
            {
                var7 = 5;
            }
        }

        if (var7 == 1 && canMakeSlopes)
        {
            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX + 1, this.trackY + 1, this.trackZ))
            {
                var7 = 2;
            }

            if (BlockRail.isRailBlockAt(this.worldObj, this.trackX - 1, this.trackY + 1, this.trackZ))
            {
                var7 = 3;
            }
        }

        if (var7 < 0)
        {
            var7 = 0;
        }

        this.setConnections(var7);
        int var8 = var7;

        if (this.isPoweredRail)
        {
            var8 = this.worldObj.getBlockMetadata(this.trackX, this.trackY, this.trackZ) & 8 | var7;
        }

        if (par2 || this.worldObj.getBlockMetadata(this.trackX, this.trackY, this.trackZ) != var8)
        {
            this.worldObj.setBlockMetadataWithNotify(this.trackX, this.trackY, this.trackZ, var8);

            for (int var9 = 0; var9 < this.connectedTracks.size(); ++var9)
            {
                RailLogic var10 = this.getMinecartTrackLogic((ChunkPosition)this.connectedTracks.get(var9));

                if (var10 != null)
                {
                    var10.refreshConnectedTracks();

                    if (var10.canConnectTo(this))
                    {
                        var10.connectToNeighbor(this);
                    }
                }
            }
        }
    }

    /**
     * get number of adjacent tracks
     */
    public static int getNAdjacentTracks(RailLogic par0RailLogic)
    {
        return par0RailLogic.getAdjacentTracks();
    }
}
