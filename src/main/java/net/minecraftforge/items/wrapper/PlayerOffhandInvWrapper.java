package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class PlayerOffhandInvWrapper extends RangedWrapper
{
    public PlayerOffhandInvWrapper(InventoryPlayer inv)
    {
        super(new InvWrapper(inv), inv.mainInventory.length + inv.armorInventory.length,
                inv.mainInventory.length + inv.armorInventory.length + inv.offHandInventory.length);
    }
}
