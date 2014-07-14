package net.minecraftforge.event.terraingen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenHills;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

/**
 * BiomeSpecificOreGenEvent is fired whenever biome-specific ore is set to be generated. <br> 
 * This event is fired during the invocation of BiomeGenHills#decorate(World, Random, int, int) for Emeralds by default. <br>
 * <br>
 * {@link #block} contains the Block that is attempting to be generated.<br>
 * <br>
 * This event is not {@link Cancelable}. <br>
 * <br>
 * This event has a result. {@link HasResult}<br>
 * Changing this result to {@link Result#DENY} causes nothing to be generated. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#ORE_GEN_BUS}.<br>
 **/
@HasResult
public class BiomeSpecificOreGenEvent extends OreGenEvent 
{
    public Block block;
    
    public BiomeSpecificOreGenEvent(World world, Random rand, int worldX, int worldZ, Block block)
    {
        super(world, rand, worldX, worldZ);
        this.block = block;
    }
}
