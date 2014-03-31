package net.minecraftforge.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @author BalthezarOi
 * @CC-License Created under the CC BY-NC-ND license by BalthezarOi
 */
public interface IKeyBoundItem {
    public void keyPressed(ItemStack stack, EntityPlayer player);

    public String getKeyName();

    public int getKey();
}
