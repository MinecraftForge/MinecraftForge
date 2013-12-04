package za.co.mcportcentral;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

public class MCPCHooks
{
    private static void LogStack()
    {
        if (MCPCConfig.Toggle.logWithStackTraces.value())
        {
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }
    }
    
    public static void logEntityDeath(String msg)
    {
        if (MCPCConfig.Toggle.entityDeathLogging.value())
        {
            MinecraftServer.getServer().logInfo(msg);
            LogStack();
        }
    }
    
    public static void logEntityDespawn(String msg)
    {
        if (MCPCConfig.Toggle.entityDespawnLogging.value())
        {
            MinecraftServer.getServer().getLogAgent().logInfo(msg);
            LogStack();
        }
    }
    
    public static void logEntitySpawn(World world, Entity entity, SpawnReason spawnReason)
    {
        if (MCPCConfig.Toggle.entitySpawnLogging.value())
        {
            MinecraftServer.getServer().getLogAgent().logInfo("Dim: " + world.provider.dimensionId + " Spawning (" + spawnReason +"): " + entity);
            LogStack();
        }
    }
    
    public static void logChunkLoad(ChunkProviderServer provider, String msg, int x, int z, boolean logLoadOnRequest)
    {
        if (MCPCConfig.Toggle.chunkLoadLogging.value())
        {
            MinecraftServer.getServer().getLogAgent().logInfo(msg + " Chunk At " + x + ", " + z + " Dim: " + provider.worldObj.provider.dimensionId);
            if (logLoadOnRequest) logLoadOnRequest(provider);
            LogStack();
        }
        
        
    }
    
    private static void logLoadOnRequest(ChunkProviderServer provider)
    {
        MinecraftServer.getServer().logInfo(
                "Finding Spawn Point: " + provider.worldObj.findingSpawnPoint +
                " Load chunk on request: " + provider.loadChunkOnProvideRequest +
                " Calling Forge Tick: " + MinecraftServer.callingForgeTick + 
                " Load chunk on forge tick: " + MCPCConfig.Toggle.loadChunkOnForgeTick.value() + 
                " Current Tick - Initial Tick: " + (MinecraftServer.currentTick - provider.initialTick)
                );
    }
    
    public static void logChunkUnload(ChunkProviderServer provider, int x, int y, String msg)
    {
        if (MCPCConfig.Toggle.chunkUnloadLogging.value())
        {
            MinecraftServer.getServer().getLogAgent().logInfo(msg + " " + x + ", " + y + " Dim: " + provider.worldObj.provider.dimensionId);
        }
    }
}
