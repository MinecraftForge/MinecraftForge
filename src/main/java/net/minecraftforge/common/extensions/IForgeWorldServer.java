package net.minecraftforge.common.extensions;

import java.io.File;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public interface IForgeWorldServer extends IForgeWorld
{
    default WorldServer getWorldServer() { return (WorldServer) this; }
    
    default File getChunkSaveLocation()
    {
        return ((AnvilChunkLoader) getWorldServer().getChunkProvider().chunkLoader).chunkSaveLocation;
    }
}
