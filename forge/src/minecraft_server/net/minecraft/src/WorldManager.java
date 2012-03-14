package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess
{
    /** Reference to the MinecraftServer object. */
    private MinecraftServer mcServer;

    /** The world itself. */
    private WorldServer world;

    public WorldManager(MinecraftServer par1MinecraftServer, WorldServer par2WorldServer)
    {
        this.mcServer = par1MinecraftServer;
        this.world = par2WorldServer;
    }

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12) {}

    public void obtainEntitySkin(Entity par1Entity)
    {
        this.mcServer.getEntityTracker(this.world.worldProvider.worldType).trackEntity(par1Entity);
    }

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    public void releaseEntitySkin(Entity par1Entity)
    {
        this.mcServer.getEntityTracker(this.world.worldProvider.worldType).untrackEntity(par1Entity);
    }

    /**
     * Plays the specified sound. Arg: x, y, z, soundName, unknown1, unknown2
     */
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {}

    public void markBlockRangeNeedsUpdate(int par1, int par2, int par3, int par4, int par5, int par6) {}

    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        this.mcServer.configManager.markBlockNeedsUpdate(par1, par2, par3, this.world.worldProvider.worldType);
    }

    public void func_48414_b(int par1, int par2, int par3) {}

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    public void playRecord(String par1Str, int par2, int par3, int par4) {}

    /**
     * In all implementations, this method does nothing.
     */
    public void doNothingWithTileEntity(int par1, int par2, int par3, TileEntity par4TileEntity)
    {
        this.mcServer.configManager.sentTileEntityToPlayer(par1, par2, par3, par4TileEntity);
    }

    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        this.mcServer.configManager.func_28171_a(par1EntityPlayer, (double)par3, (double)par4, (double)par5, 64.0D, this.world.worldProvider.worldType, new Packet61DoorChange(par2, par3, par4, par5, par6));
    }
}
