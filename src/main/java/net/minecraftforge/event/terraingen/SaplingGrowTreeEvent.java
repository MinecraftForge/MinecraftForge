package net.minecraftforge.event.terraingen;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.Event.HasResult;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

/**
 * SaplingGrowTreeEvent is fired when a spling grows into a tree.<br>
 * This event is fired during sapling growth in
 * BlockSapling#func_149878_d(World, int, int, int, Random).<br>
 * <br>
 * {@link #x} contains the x-coordinate of the growing sapling. <br>
 * {@link #y} contains the y-coordinate of the growing sapling. <br>
 * {@link #z} contains the z-coordinate of the growing sapling. <br>
 * {@link #rand} contains an instance of Random for use. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event has a result. {@link HasResult} <br>
 * This result determines if the sapling is allowed to grow. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
 **/
@HasResult
public class SaplingGrowTreeEvent extends WorldEvent
{
    public final int x;
    public final int y;
    public final int z;
    public final Random rand;
    
    public SaplingGrowTreeEvent(World world, Random rand, int x, int y, int z)
    {
        super(world);
        this.rand = rand;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
