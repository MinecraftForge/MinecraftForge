package net.minecraft.src.forge;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;

public interface IBiomePopulator {

    public void populate(World world, BiomeGenBase biomegenbase, int x, int z);

}
