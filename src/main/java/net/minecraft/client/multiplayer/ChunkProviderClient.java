package net.minecraft.client.multiplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ChunkProviderClient implements IChunkProvider
{
    private static final Logger field_147436_a = LogManager.getLogger();
    // JAVADOC FIELD $$ field_73238_a
    private Chunk blankChunk;
    // JAVADOC FIELD $$ field_73236_b
    private LongHashMap chunkMapping = new LongHashMap();
    // JAVADOC FIELD $$ field_73237_c
    private List chunkListing = new ArrayList();
    // JAVADOC FIELD $$ field_73235_d
    private World worldObj;
    private static final String __OBFID = "CL_00000880";

    public ChunkProviderClient(World par1World)
    {
        this.blankChunk = new EmptyChunk(par1World, 0, 0);
        this.worldObj = par1World;
    }

    // JAVADOC METHOD $$ func_73149_a
    public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_73234_b
    public void unloadChunk(int par1, int par2)
    {
        Chunk chunk = this.provideChunk(par1, par2);

        if (!chunk.isEmpty())
        {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        this.chunkListing.remove(chunk);
    }

    // JAVADOC METHOD $$ func_73158_c
    public Chunk loadChunk(int par1, int par2)
    {
        Chunk chunk = new Chunk(this.worldObj, par1, par2);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(par1, par2), chunk);
        this.chunkListing.add(chunk);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
        chunk.isChunkLoaded = true;
        return chunk;
    }

    // JAVADOC METHOD $$ func_73154_d
    public Chunk provideChunk(int par1, int par2)
    {
        Chunk chunk = (Chunk)this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        return chunk == null ? this.blankChunk : chunk;
    }

    // JAVADOC METHOD $$ func_73151_a
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_104112_b
    public void saveExtraData() {}

    // JAVADOC METHOD $$ func_73156_b
    public boolean unloadQueuedChunks()
    {
        long i = System.currentTimeMillis();
        Iterator iterator = this.chunkListing.iterator();

        while (iterator.hasNext())
        {
            Chunk chunk = (Chunk)iterator.next();
            chunk.func_150804_b(System.currentTimeMillis() - i > 5L);
        }

        if (System.currentTimeMillis() - i > 100L)
        {
            field_147436_a.info("Warning: Clientside chunk ticking took {} ms", new Object[] {Long.valueOf(System.currentTimeMillis() - i)});
        }

        return false;
    }

    // JAVADOC METHOD $$ func_73157_c
    public boolean canSave()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_73153_a
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {}

    // JAVADOC METHOD $$ func_73148_d
    public String makeString()
    {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    // JAVADOC METHOD $$ func_73155_a
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        return null;
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return this.chunkListing.size();
    }

    public void recreateStructures(int par1, int par2) {}
}