package net.minecraftforge.event.block;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraftforge.event.Cancelable;

/**
 * This event is used to check if the block can stay/be placed on certain block(used by catus, flowers, torches)
 * 
 * @author battlefield
 *
 */

@Cancelable
public class CanBlockStayCheckEvent extends BlockEvent{

	public ItemStack blockBelow;
	public boolean canBePlaced = false;
	
	public CanBlockStayCheckEvent(Block block, ItemStack blockBelow) {
		super(block);
		this.blockBelow = blockBelow;
	}
	
	public void setCanPlace(boolean b)
	{
		canBePlaced = b;
	}
	
	public boolean canBePlaced()
	{
		return canBePlaced;
	}

}
