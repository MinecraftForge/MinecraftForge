package net.minecraftforge.event.world.terraingen;

import java.util.Random;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public abstract class ChunkGenerateRareEvent extends ChunkProviderEvent
{

    public final Random rand;
    public final int worldX;
    public final int worldY;
    public int scarcity;
    
    private boolean handled = false;
   
    public ChunkGenerateRareEvent(IChunkProvider chunkProvider, World world, Random rand, int worldX, int worldY, int scarcity)
    {
        super(chunkProvider, world);
        this.rand = rand;
        this.worldX = worldX;
        this.worldY = worldY;
        this.scarcity = scarcity;
    }

    public boolean isHandled()
    {
        return handled;
    }
    
    public void setHandled()
    {
        handled = true;
    }

    @Cancelable
    public static class ChunkGenerateLakeEvent extends ChunkGenerateRareEvent
    {

        public ChunkGenerateLakeEvent(IChunkProvider chunkProvider, World world, Random rand, int worldX, int worldY, int scarcity)
        {
            super(chunkProvider, world, rand, worldX, worldY, scarcity);
        }
        
    }

    @Cancelable
    public static class ChunkGenerateLavaLakeEvent extends ChunkGenerateRareEvent
    {

        public ChunkGenerateLavaLakeEvent(IChunkProvider chunkProvider, World world, Random rand, int worldX, int worldY, int scarcity)
        {
            super(chunkProvider, world, rand, worldX, worldY, scarcity);
        }
        
    }
    
}
