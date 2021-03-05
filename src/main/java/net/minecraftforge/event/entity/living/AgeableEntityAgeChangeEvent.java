package net.minecraftforge.event.entity.living;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * This event is fired when an {@link AgeableEntity} change from child to adult or vice-versa. <br>
 * It is fired via {@link ForgeEventFactory#onAgeableEntityAgeChange(AgeableEntity)}.
 * <br>
 * This event not is {@link net.minecraftforge.eventbus.api.Cancelable}.
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class AgeableEntityAgeChangeEvent extends LivingEvent
{
    private final AgeableEntity ageableEntity;

    public AgeableEntityAgeChangeEvent(AgeableEntity entity)
    {
        super(entity);
        this.ageableEntity = entity;
    }

    public AgeableEntity getAgeableEntity() { return ageableEntity; }
}
