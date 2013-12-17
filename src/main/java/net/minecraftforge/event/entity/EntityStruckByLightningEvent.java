package net.minecraftforge.event.entity;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;

@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent
{
    public final EntityLightningBolt lightning;

    public EntityStruckByLightningEvent(Entity entity, EntityLightningBolt lightning)
    {
        super(entity);
        this.lightning = lightning;
    }
}
