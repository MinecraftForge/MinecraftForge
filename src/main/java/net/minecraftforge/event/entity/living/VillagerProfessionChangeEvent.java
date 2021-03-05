package net.minecraftforge.event.entity.living;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * This event is fired when a {@link VillagerEntity} changes profession. <br>
 * It is fired via {@link ForgeEventFactory#onVillagerProfessionChange(VillagerEntity, VillagerProfession)}.
 * <br>
 * {@link #oldProfession} contains the {@link VillagerProfession} the villager had before changing. <br>
 * <br>
 * This event not is {@link net.minecraftforge.eventbus.api.Cancelable}.
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class VillagerProfessionChangeEvent extends LivingEvent
{
    private final VillagerEntity villager;
    private final VillagerProfession oldProfession;

    public VillagerProfessionChangeEvent(VillagerEntity entity, VillagerProfession oldProfession)
    {
        super(entity);
        this.villager = entity;
        this.oldProfession = oldProfession;
    }

    public VillagerEntity getVillager() { return villager; }

    public VillagerProfession getOldProfession() { return oldProfession; }
}
