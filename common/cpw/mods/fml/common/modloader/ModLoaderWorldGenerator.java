package cpw.mods.fml.common.modloader;

import java.util.Random;

import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.ChunkProviderHell;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;

import cpw.mods.fml.common.IWorldGenerator;

public class ModLoaderWorldGenerator implements IWorldGenerator
{
    private BaseModProxy mod;

    public ModLoaderWorldGenerator(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, Object... additionalData)
    {
        World w = (World) additionalData[0];
        IChunkProvider cp = (IChunkProvider) additionalData[1];

        if (cp instanceof ChunkProviderGenerate)
        {
            mod.generateSurface(w, random, chunkX << 4, chunkZ << 4);
        }
        else if (cp instanceof ChunkProviderHell)
        {
            mod.generateNether(w, random, chunkX << 4, chunkZ << 4);
        }
    }

}
