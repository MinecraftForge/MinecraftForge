package net.minecraft.src;

public interface IWorldAccess
{
    /**
     * Will mark the block and neighbors that their renderers need an update (could be all the same renderer
     * potentially) Args: x, y, z
     */
    void markBlockNeedsUpdate(int var1, int var2, int var3);

    void func_48180_b(int var1, int var2, int var3);

    /**
     * Called across all registered IWorldAccess instances when a block range is invalidated. Args: minX, minY, minZ,
     * maxX, maxY, maxZ
     */
    void markBlockRangeNeedsUpdate(int var1, int var2, int var3, int var4, int var5, int var6);

    /**
     * Plays the specified sound. Arg: x, y, z, soundName, unknown1, unknown2
     */
    void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    /**
     * Spawns a particle. Arg: particleType, x, y, z, velX, velY, velZ
     */
    void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12);

    /**
     * Start the skin for this entity downloading, if necessary, and increment its reference counter
     */
    void obtainEntitySkin(Entity var1);

    /**
     * Decrement the reference counter for this entity's skin image data
     */
    void releaseEntitySkin(Entity var1);

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    void playRecord(String var1, int var2, int var3, int var4);

    /**
     * In all implementations, this method does nothing.
     */
    void doNothingWithTileEntity(int var1, int var2, int var3, TileEntity var4);

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6);
}
