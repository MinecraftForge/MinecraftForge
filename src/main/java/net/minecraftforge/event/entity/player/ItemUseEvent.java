package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Cancelable;

public abstract class ItemUseEvent extends PlayerEvent
{
    public final ItemStack itemInUse;
    public int useDuration;

    public ItemUseEvent(EntityPlayer player, ItemStack inUse, int useTime)
    {
        super(player);
        itemInUse = inUse;
        useDuration = useTime;
    }

    @Cancelable
    public static class Start extends ItemUseEvent
    {
        public Start(EntityPlayer player, ItemStack inUse, int useTime)
        {
            super(player, inUse, useTime);
        }

        @Override
        public boolean isCanceled()
        {
            return super.isCanceled() || this.useDuration <= 0;
        }
    }

    @Cancelable
    public static class During extends ItemUseEvent
    {
        public During(EntityPlayer player, ItemStack inUse, int useTime)
        {
            super(player, inUse, useTime);
        }

        @Override
        public boolean isCanceled()
        {
            return super.isCanceled() || this.useDuration <= 0;
        }
    }

    public static class Finish extends ItemUseEvent
    {
        public ItemStack returnedItem;
        
        public Finish(EntityPlayer player, ItemStack inUse, ItemStack finishedItem)
        {
            super(player, inUse, 0);
            returnedItem = finishedItem;
        }
    }
}
