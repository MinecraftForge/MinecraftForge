package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public interface IChunkProvider
{
    // JAVADOC METHOD $$ func_73149_a
    boolean chunkExists(int var1, int var2);

    // JAVADOC METHOD $$ func_73154_d
    Chunk provideChunk(int var1, int var2);

    // JAVADOC METHOD $$ func_73158_c
    Chunk loadChunk(int var1, int var2);

    // JAVADOC METHOD $$ func_73153_a
    void populate(IChunkProvider var1, int var2, int var3);

    // JAVADOC METHOD $$ func_73151_a
    boolean saveChunks(boolean var1, IProgressUpdate var2);

    // JAVADOC METHOD $$ func_73156_b
    boolean unloadQueuedChunks();

    // JAVADOC METHOD $$ func_73157_c
    boolean canSave();

    // JAVADOC METHOD $$ func_73148_d
    String makeString();

    // JAVADOC METHOD $$ func_73155_a
    List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4);

    ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5);

    int getLoadedChunkCount();

    void recreateStructures(int var1, int var2);

    // JAVADOC METHOD $$ func_104112_b
    void saveExtraData();
}