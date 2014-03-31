package net.minecraftforge.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IKeyBoundItem {
    public void keyPressed(ItemStack stack, EntityPlayer player);

    public String getKeyName();

    public int getKey();
}
