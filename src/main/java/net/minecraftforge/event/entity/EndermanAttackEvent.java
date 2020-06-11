package net.minecraftforge.event.entity;

import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event gets fired whenever a player look an Enderman in the eye<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * If this event is canceled, the enderman will not attack the player who looks into the eyes<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 *
 */
@Cancelable
public class EndermanAttackEvent extends LivingEvent {

    private final PlayerEntity player;
    private final EndermanEntity entity;

    public EndermanAttackEvent(EndermanEntity entity, PlayerEntity player)
    {
        super(entity);
        this.player = player;
        this.entity = entity;
    }


    @Override
    public EndermanEntity getEntity() {
        return entity;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

}