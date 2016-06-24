package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * PlayerAddXpEvent is fired when a player's Xp increases through {@link EntityPlayer#addExperience(int)}<br />
 * {@link #getAmount} contains the amount to be added to the player's total.<br />
 * This event is {@link Cancelable}. If it is canceled, then no further processing will be done.
 */
@Cancelable
public class PlayerAddXpEvent extends PlayerEvent
{
    private final int amount;

    public PlayerAddXpEvent(EntityPlayer player, int amount)
    {
        super(player);
        this.amount = amount;
    }

    public int getAmount()
    {
        return this.amount;
    }
}
