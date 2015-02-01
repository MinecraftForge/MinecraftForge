package net.minecraftforge.event.terraingen;

import java.util.Random;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

/**
 * SaplingGrowTreeEvent is fired when a sapling grows into a tree.<br>
 * This event is fired during sapling growth in
 * BlockSapling#func_149878_d(World, BlockPos, Random).<br>
 * <br>
 * {@link #pos} contains the coordinates of the growing sapling. <br>
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
    public final BlockPos pos;
    public final Random rand;

    public SaplingGrowTreeEvent(World world, Random rand, BlockPos pos)
    {
        super(world);
        this.rand = rand;
        this.pos = pos;
    }
}
