package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

public class CanHarvestBlockEvent extends PlayerEvent
{
	public boolean result;
    public final Block block;
    
    public CanHarvestBlockEvent(EntityPlayer player, Block block, boolean result)
    {
        super(player);
        this.block = block;
        this.result = result;
    }
}