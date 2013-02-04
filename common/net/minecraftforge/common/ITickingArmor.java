package net.minecraftforge.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * A simple interface to receive ticks from armor items when placed in armor slots.
 *
 * @author cpw
 *
 */
public interface ITickingArmor {
    void onArmorTickUpdate(World worldObj, EntityPlayer player, ItemStack itemStack);
}
