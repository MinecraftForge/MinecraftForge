package net.minecraftforge.event.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

import java.util.Map;

public class EntityAttributeSetupEvent extends Event implements IModBusEvent {
    private final Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> entityAttributes;

    public EntityAttributeSetupEvent(Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> entityAttributes)
    {
        this.entityAttributes = entityAttributes;
    }

    public Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> getEntityAttributes()
    {
        return entityAttributes;
    }
}
