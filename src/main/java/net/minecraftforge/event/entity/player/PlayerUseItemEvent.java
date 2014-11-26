package net.minecraftforge.event.entity.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class PlayerUseItemEvent extends PlayerEvent
{
    public final ItemStack item;
    public int duration;

    private PlayerUseItemEvent(EntityPlayer player, ItemStack item, int duration)
    {
        super(player);
        this.item = item;
        this.duration = duration;
    }

    /**
     * Fired when a player starts 'using' an item, typically when they hold right mouse.
     * Examples:
     *   Drawing a bow
     *   Eating Food
     *   Drinking Potions/Milk
     *   Guarding with a sword
     *
     * Cancel the event, or set the duration or <= 0 to prevent it from processing.
     *
     */
    @Cancelable
    public static class Start extends PlayerUseItemEvent
    {
        public Start(EntityPlayer player, ItemStack item, int duration)
        {
            super(player, item, duration);
        }
    }

    /**
     * Fired every tick that a player is 'using' an item, see {@link Start} for info.
     *
     * Cancel the event, or set the duration or <= 0 to cause the player to stop using the item.
     *
     */
    @Cancelable
    public static class Tick extends PlayerUseItemEvent
    {
        public Tick(EntityPlayer player, ItemStack item, int duration)
        {
            super(player, item, duration);
        }
    }

    /**
     * Fired when a player stops using an item without the use duration timing out. 
     * Example:
     *   Stop eating 1/2 way through
     *   Stop defending with sword
     *   Stop drawing bow. This case would fire the arrow
     *   
     * Duration on this event is how long the item had left in it's count down before 'finishing'
     *
     * Canceling this event will prevent the Item from being notified that it has stopped being used, 
     * The only vanilla item this would effect are bows, and it would cause them NOT to fire there arrow.
     */
    @Cancelable
    public static class Stop extends PlayerUseItemEvent
    {
        public Stop(EntityPlayer player, ItemStack item, int duration)
        {
            super(player, item, duration);
        }
    }

    /**
     * Fired after an item has fully finished being used.
     * The item has been notified that it was used, and the item/result stacks reflect after that state.
     * This means that when this is fired for a Potion, the potion effect has already been applied.
     * 
     * If you wish to cancel those effects, you should cancel one of the above events.
     * 
     * The result item stack is the stack that is placed in the player's inventory in replacement of the stack that is currently being used.
     *
     */
    public static class Finish extends PlayerUseItemEvent
    {
        public ItemStack result;
        public Finish(EntityPlayer player, ItemStack item, int duration, ItemStack result)
        {
            super(player, item, duration);
            this.result = result;
        }
    }
}
