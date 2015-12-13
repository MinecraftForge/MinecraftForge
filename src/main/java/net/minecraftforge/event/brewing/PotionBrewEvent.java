package net.minecraftforge.event.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;


public class PotionBrewEvent extends Event
{
    private ItemStack[] stacks;
    private TileEntity tile;

    protected PotionBrewEvent(ItemStack[] stacks, TileEntity tile)
    {
        this.stacks = stacks;
        this.tile = tile;
    }

    public ItemStack getItem(int index)
    {
        if (index >= stacks.length) return null;
        return stacks[index];
    }

    public void setItem(int index, ItemStack stack)
    {
        if (index < stacks.length)
        {
            stacks[index] = stack;
        }
    }

    public int getLength()
    {
        return stacks.length;
    }
    
    public TileEntity getTileEntity()
    {
        return this.tile;
    }

    /**
     * PotionBrewEvent.Pre is fired before vanilla brewing takes place.
     * All changes made to the event's array will be made to the TileEntity if the event is canceled.
     * <br>
     * The event is fired during the TileEntityBrewingStand#brewPotions() method invocation.<br>
     * <br>
     * This event offers access to the ItemStack array holding all items in Brewer, as well as the TileEntity, in which the brewing is going to take place.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If the event is not canceled, the vanilla brewing will take place instead of modded brewing.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     * <br>
     * If this event is canceled, and items have been modified, PotionBrewEvent.Post will automatically be fired.
     **/
    @Cancelable
    public static class Pre extends PotionBrewEvent
    {
        public Pre(ItemStack[] stacks, TileEntity tile)
        {
            super(stacks, tile);
        }
    }

    /**
     * PotionBrewEvent.Post is fired when a potion is brewed in the brewing stand.
     * <br>
     * The event is fired during the TileEntityBrewingStand#brewPotions() method invocation.<br>
     * <br>
     * This event offers access to the ItemStack array holding all items in Brewer, as well as the TileEntity, in which the brewing took place.<br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class Post extends PotionBrewEvent
    {
        public Post(ItemStack[] stacks, TileEntity tile)
        {
            super(stacks, tile);
        }
    }
}
