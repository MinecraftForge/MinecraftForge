package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.DimensionManager;

public class WorldServer extends World
{
    public ChunkProviderServer chunkProviderServer;

    /** Set to true when an op is building or this dimension != 0 */
    public boolean disableSpawnProtection = false;

    /** Whether or not level saving is enabled */
    public boolean levelSaving;
    private MinecraftServer mcServer;

    /** Maps ids to entity instances */
    private IntHashMap entityInstanceIdMap;
    public EntityTracker entityTracker;
    public PlayerManager playerManager;

    public WorldServer(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings)
    {
        super(par2ISaveHandler, par3Str, par5WorldSettings, WorldProvider.getProviderForDimension(par4));
        this.mcServer = par1MinecraftServer;

        if (this.entityInstanceIdMap == null)
        {
            this.entityInstanceIdMap = new IntHashMap();
        }
        DimensionManager.setWorld(par4, this);
        playerManager = new PlayerManager(mcServer, par4, mcServer.propertyManagerObj.getIntProperty("view-distance", 10));
        entityTracker = new EntityTracker(mcServer, par4);
    }

    public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2)
    {
        if (!this.mcServer.spawnPeacefulMobs && (par1Entity instanceof EntityAnimal || par1Entity instanceof EntityWaterMob))
        {
            par1Entity.setEntityDead();
        }

        if (!this.mcServer.field_44002_p && par1Entity instanceof INpc)
        {
            par1Entity.setEntityDead();
        }

        if (par1Entity.riddenByEntity == null || !(par1Entity.riddenByEntity instanceof EntityPlayer))
        {
            super.updateEntityWithOptionalForce(par1Entity, par2);
        }
    }

    public void func_12017_b(Entity par1Entity, boolean par2)
    {
        super.updateEntityWithOptionalForce(par1Entity, par2);
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        IChunkLoader var1 = this.saveHandler.getChunkLoader(this.worldProvider);
        this.chunkProviderServer = new ChunkProviderServer(this, var1, this.worldProvider.getChunkProvider());
        return this.chunkProviderServer;
    }

    /**
     * get a list of tileEntity's
     */
    public List getTileEntityList(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        ArrayList var7 = new ArrayList();
        for(int x = (par1 >> 4); x <= (par4 >> 4); x++)
        {
            for(int z = (par3 >> 4); z <= (par6 >> 4); z++) 
            {
                Chunk chunk = getChunkFromChunkCoords(x, z);
                if (chunk != null)
                {
                    for(Object obj : chunk.chunkTileEntityMap.values())
                    {
                        TileEntity entity = (TileEntity)obj;
                        if (!entity.isInvalid())
                        {
                            if (entity.xCoord >= par1 && entity.yCoord >= par2 && entity.zCoord >= par3 && 
                                entity.xCoord <= par4 && entity.yCoord <= par5 && entity.zCoord <= par6)
                            {
                                var7.add(entity);
                            }
                        }
                    }
                }
            }
        }

        return var7;
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4)
    {
        int var5 = MathHelper.abs(par2 - this.worldInfo.getSpawnX());
        int var6 = MathHelper.abs(par4 - this.worldInfo.getSpawnZ());

        if (var5 > var6)
        {
            var6 = var5;
        }

        return var6 > 16 || this.mcServer.configManager.isOp(par1EntityPlayer.username);
    }

    /**
     * generates a spawn point for this world
     */
    protected void generateSpawnPoint()
    {
        if (this.entityInstanceIdMap == null)
        {
            this.entityInstanceIdMap = new IntHashMap();
        }

        super.generateSpawnPoint();
    }

    protected void obtainEntitySkin(Entity par1Entity)
    {
        super.obtainEntitySkin(par1Entity);
        this.entityInstanceIdMap.addKey(par1Entity.entityId, par1Entity);
        Entity[] var2 = par1Entity.getParts();

        if (var2 != null)
        {
            for (int var3 = 0; var3 < var2.length; ++var3)
            {
                this.entityInstanceIdMap.addKey(var2[var3].entityId, var2[var3]);
            }
        }
    }

    protected void releaseEntitySkin(Entity par1Entity)
    {
        super.releaseEntitySkin(par1Entity);
        this.entityInstanceIdMap.removeObject(par1Entity.entityId);
        Entity[] var2 = par1Entity.getParts();

        if (var2 != null)
        {
            for (int var3 = 0; var3 < var2.length; ++var3)
            {
                this.entityInstanceIdMap.removeObject(var2[var3].entityId);
            }
        }
    }

    public Entity func_6158_a(int par1)
    {
        return (Entity)this.entityInstanceIdMap.lookup(par1);
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity par1Entity)
    {
        if (super.addWeatherEffect(par1Entity))
        {
            this.mcServer.configManager.sendPacketToPlayersAroundPoint(par1Entity.posX, par1Entity.posY, par1Entity.posZ, 512.0D, this.worldProvider.worldType, new Packet71Weather(par1Entity));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity par1Entity, byte par2)
    {
        Packet38EntityStatus var3 = new Packet38EntityStatus(par1Entity.entityId, par2);
        this.mcServer.getEntityTracker(this.worldProvider.worldType).sendPacketToTrackedPlayersAndTrackedEntity(par1Entity, var3);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9)
    {
        Explosion var10 = new Explosion(this, par1Entity, par2, par4, par6, par8);
        var10.isFlaming = par9;
        var10.doExplosionA();
        var10.doExplosionB(false);
        this.mcServer.configManager.sendPacketToPlayersAroundPoint(par2, par4, par6, 64.0D, this.worldProvider.worldType, new Packet60Explosion(par2, par4, par6, par8, var10.destroyedBlockPositions));
        return var10;
    }

    /**
     * plays a given note at x, y, z. args: x, y, z, instrument, note
     */
    public void playNoteAt(int par1, int par2, int par3, int par4, int par5)
    {
        super.playNoteAt(par1, par2, par3, par4, par5);
        this.mcServer.configManager.sendPacketToPlayersAroundPoint((double)par1, (double)par2, (double)par3, 64.0D, this.worldProvider.worldType, new Packet54PlayNoteBlock(par1, par2, par3, par4, par5));
    }

    public void func_30006_w()
    {
        this.saveHandler.func_22093_e();
    }

    /**
     * update's all weather states.
     */
    protected void updateWeather()
    {
        boolean var1 = this.isRaining();
        super.updateWeather();

        if (var1 != this.isRaining())
        {
            if (var1)
            {
                this.mcServer.configManager.sendPacketToAllPlayers(new Packet70Bed(2, 0));
            }
            else
            {
                this.mcServer.configManager.sendPacketToAllPlayers(new Packet70Bed(1, 0));
            }
        }
    }
}
