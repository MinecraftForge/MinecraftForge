package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

public class DeferredBiomeDecorator extends BiomeDecorator {
    private BiomeDecorator wrapped;

    public DeferredBiomeDecorator(BiomeDecorator wrappedOriginal)
    {
        this.wrapped = wrappedOriginal;
    }

    @Override
    public void decorateChunk(World par1World, Random par2Random, BiomeGenBase biome, int par3, int par4)
    {
        fireCreateEventAndReplace(biome);
        // On first call to decorate, we fire and substitute ourselves, if we haven't already done so
        biome.theBiomeDecorator.decorateChunk(par1World, par2Random, biome, par3, par4);
    }
    public void fireCreateEventAndReplace(BiomeGenBase biome)
    {
        // Copy any configuration from us to the real instance.
        wrapped.bigMushroomsPerChunk = bigMushroomsPerChunk;
        wrapped.cactiPerChunk = cactiPerChunk;
        wrapped.clayPerChunk = clayPerChunk;
        wrapped.deadBushPerChunk = deadBushPerChunk;
        wrapped.flowersPerChunk = flowersPerChunk;
        wrapped.generateLakes = generateLakes;
        wrapped.grassPerChunk = grassPerChunk;
        wrapped.mushroomsPerChunk = mushroomsPerChunk;
        wrapped.reedsPerChunk = reedsPerChunk;
        wrapped.sandPerChunk = sandPerChunk;
        wrapped.sandPerChunk2 = sandPerChunk2;
        wrapped.treesPerChunk = treesPerChunk;
        wrapped.waterlilyPerChunk = waterlilyPerChunk;
        
        BiomeEvent.CreateDecorator event = new BiomeEvent.CreateDecorator(biome, wrapped);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        biome.theBiomeDecorator = event.newBiomeDecorator;
    }
}
