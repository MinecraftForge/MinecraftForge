package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is called when a player's experience level increases through the {@link PlayerEntity#giveExperiencePoints(int)} method.
 * The event can be canceled, and no further processing will be done.
 */
@Cancelable
public class PlayerGiveXpEvent extends PlayerEvent
{
    private final int amount;
    
    public PlayerGiveXpEvent(PlayerEntity player, int amount)
    {
        super(player);
        this.amount = amount;
    }
    
    public int getAmount() {
        return amount;
    }
}
