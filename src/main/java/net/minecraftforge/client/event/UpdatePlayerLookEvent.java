package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class UpdatePlayerLookEvent extends PlayerEvent
{
    private double mouseTurnSpeed;

    public UpdatePlayerLookEvent(PlayerEntity player, double speed)
    {
        super(player);
        this.mouseTurnSpeed = speed;
    }

    public double getMouseTurnSpeed()
    {
        return mouseTurnSpeed;
    }

    public void setMouseTurnSpeed(double speed)
    {
        mouseTurnSpeed = speed;
    }
}
