package net.minecraftforge.event.entity.player;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlayerStrVsBlockEvent extends PlayerEvent
{
    public float result = 0F;
    public final Block block;
    public final int meta;
    private boolean handeled = false;
    
    public PlayerStrVsBlockEvent(EntityPlayer player, Block block, int meta)
    {
        super(player);
        this.block = block;
        this.meta = meta;
    }
    
    public void setHandeled()
    {
        handeled = true;
    }
    
    public boolean isHandeled()
    {
        return handeled;
    }
}
