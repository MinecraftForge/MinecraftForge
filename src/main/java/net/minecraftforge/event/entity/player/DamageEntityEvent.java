package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

/**
 * DamageEntityEvent is fired after a player has hurt an Entity.<br>
 * This event is fired after a player has hurt an Entity in
 * {@link Player#attack(Entity)}.<br>
 * <br>
 * {@link #target} contains the Entity that was damaged by the player. <br>
 * <br>
 * {@link #amount} contains the final amount of damage that will be dealt by the player. <br>
 * <br>
 * {@link #strength} contains the attack strength of the player. <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class DamageEntityEvent extends PlayerEvent
{
    private final Entity target;
    private final float amount;
    private final float strength;
    public DamageEntityEvent(Player player, Entity target, float amount, float strength)
    {
        super(player);
        this.target = target;
        this.amount = amount;
        this.strength = strength;
    }

    public Entity getTarget()
    {
        return target;
    }

    public float getAmount()
    {
        return amount;
    }

    public float getStrength() {
        return strength;
    }
}
