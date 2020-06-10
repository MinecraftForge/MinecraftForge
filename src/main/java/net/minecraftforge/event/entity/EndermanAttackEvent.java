package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@net.minecraftforge.eventbus.api.Cancelable
public class EndermanAttackEvent extends EntityEvent {

    private final PlayerEntity player;
    private final Entity entity;

    public EndermanAttackEvent(EndermanEntity entity, PlayerEntity player)
    {
        super(entity);
        this.player = player;
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

}