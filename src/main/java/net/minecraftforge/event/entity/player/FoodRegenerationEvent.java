package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event is fired server-side immediately before a player regenerates naturally from the food bar.
 * It allows modification of the amount to regenerate and exhaustion to add after regenerating.
 * 
 * This event is {@link Cancelable}.
 * The current attempt to regenerate is skipped if this event is canceled.
 * This event has no result. {@link HasResult}
 * 
 * {@link regeneratedHealth} contains the health to be regenerated.
 * {@link exhaustion} contains the exhaustion to be added after regeneration.
 * 
 * @author williewillus
 */
@Cancelable
public class FoodRegenerationEvent extends PlayerEvent 
{
    private float regeneratedHealth;   
    private float exhaustion;
    
    public FoodRegenerationEvent(EntityPlayer player, float regeneratedHealth, float exhaustion) 
    {
        super(player);
        this.regeneratedHealth = regeneratedHealth;
        this.exhaustion = exhaustion;
    }

    public float getRegeneratedHealth()
    {
        return regeneratedHealth;
    }

    public void setRegeneratedHealth(float regeneratedHealth)
    {
        this.regeneratedHealth = regeneratedHealth;
    }

    public float getExhaustion()
    {
        return exhaustion;
    }

    public void setExhaustion(float exhaustion)
    {
        this.exhaustion = exhaustion;
    }
}
