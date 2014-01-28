package net.minecraft.world.chunk.storage;

import java.io.IOException;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader
{
    // JAVADOC METHOD $$ func_75815_a
    Chunk loadChunk(World var1, int var2, int var3) throws IOException;

    void saveChunk(World var1, Chunk var2) throws MinecraftException, IOException;

    // JAVADOC METHOD $$ func_75819_b
    void saveExtraChunkData(World var1, Chunk var2);

    // JAVADOC METHOD $$ func_75817_a
    void chunkTick();

    // JAVADOC METHOD $$ func_75818_b
    void saveExtraData();
}