package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class PlayerDestroyItemEvent extends PlayerEvent
{
    public final int blockID;
	public final int damage;
    public PlayerDestroyItemEvent(EntityPlayer player, int blockID, int damage)
    {
        super(player);
        this.blockID = blockID;
		this.damage = damage;
    }

}
