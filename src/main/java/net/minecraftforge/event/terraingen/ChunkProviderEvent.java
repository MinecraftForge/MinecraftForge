package net.minecraftforge.event.terraingen;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEvent extends Event
{

    public final IChunkProvider chunkProvider;

    public ChunkProviderEvent(IChunkProvider chunkProvider)
    {
        this.chunkProvider = chunkProvider;
    }

    /**
     * This event is fired when a chunks blocks are replaced by a biomes top and
     * filler blocks.
     *
     * You can set the result to DENY to prevent the default replacement.
     */
    @HasResult
    public static class ReplaceBiomeBlocks extends ChunkProviderEvent
    {
        public final int x;
        public final int z;
        public final ChunkPrimer primer;
        public final World world; // CAN BE NULL

        public ReplaceBiomeBlocks(IChunkProvider chunkProvider, int x, int z, ChunkPrimer primer, World world)
        {
            super(chunkProvider);
            this.x = x;
            this.z = z;
            this.primer = primer;
            this.world = world;
        }

    }

    /**
     * This event is fired before a chunks terrain noise field is initialized.
     *
     * You can set the result to DENY to substitute your own noise field.
     */
    @HasResult
    public static class InitNoiseField extends ChunkProviderEvent
    {
        public double[] noisefield;
        public final int posX;
        public final int posY;
        public final int posZ;
        public final int sizeX;
        public final int sizeY;
        public final int sizeZ;

        public InitNoiseField(IChunkProvider chunkProvider, double[] noisefield, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ)
        {
            super(chunkProvider);
            this.noisefield = noisefield;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }

    }
}
