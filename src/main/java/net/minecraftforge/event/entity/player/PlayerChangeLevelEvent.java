package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * PlayerChangeLevelEvent is fired when a player's level changes through either {@link EntityPlayer#addExperienceLevel(int)} or
 * {@link EntityPlayer#removeExperienceLevel(int)}<br />
 * {@link #getLevel} contains the levels to be added to the player's total, with negative for remove.<br />
 * This event is {@link Cancelable}. If it is canceled, then no further processing will be done.
 */
@Cancelable
public class PlayerChangeLevelEvent extends PlayerEvent
{
    private final int levels;

    public PlayerChangeLevelEvent(EntityPlayer player, int levels)
    {
        super(player);
        this.levels = levels;
    }

    public int getLevels()
    {
        return this.levels;
    }
}
