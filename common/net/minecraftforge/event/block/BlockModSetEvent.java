package net.minecraftforge.event.block;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class BlockModSetEvent extends Event
{
    public final TileEntity callerEntity;
    public final String playerPlacedBy;
    public final Class mod;
    public final World world;
    public final Block oldBlock;
    public final Block newBlock;
    public final int oldMeta;
    public final int newMeta;
    public final int x;
    public final int y;
    public final int z;
    
    public BlockModSetEvent(Class mod, TileEntity callerEntity, String playerPlacedBy, World world, Block oldBlock, int oldMeta, Block newBlock, int newMeta, int x, int y, int z)
    {
        this.callerEntity = callerEntity;
        this.playerPlacedBy = playerPlacedBy;
        this.mod = mod;
        this.world = world;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.oldMeta = oldMeta;
        this.newMeta = newMeta;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
