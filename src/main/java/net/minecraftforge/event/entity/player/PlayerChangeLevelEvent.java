package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is called when a player's level changes through the {@link PlayerEntity#addExperienceLevel(int)} method.
 * The event can be cancelled, and no further processing will be done.
 */
@Cancelable
public class PlayerChangeLevelEvent extends PlayerEvent
{
    private int levels;

    public PlayerChangeLevelEvent(PlayerEntity player, int levels)
    {
        super(player);
        this.levels = levels;
    }

    public int getLevels()
    {
        return this.levels;
    }

    public void setLevels(int levels)
    {
        this.levels = levels;
    }
}
