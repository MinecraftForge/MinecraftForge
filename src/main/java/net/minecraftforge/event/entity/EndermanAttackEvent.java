package net.minecraftforge.event.entity;

import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@net.minecraftforge.eventbus.api.Cancelable
public class EndermanAttackEvent extends EntityEvent {

    private final PlayerEntity player;

    public EndermanAttackEvent(EndermanEntity entity, PlayerEntity player)
    {
        super(entity);
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

}