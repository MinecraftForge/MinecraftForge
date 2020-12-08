package net.minecraftforge.event.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityAttributeSetupEvent extends Event implements IModBusEvent {
    private final Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> entityAttributes;

    public EntityAttributeSetupEvent(ModContainer modContainer)
    {
        this.entityAttributes = new HashMap<>();
    }

    public boolean doesEntityTypeHaveAttribute(EntityType<? extends LivingEntity> entity, Attribute attribute)
    {
        return GlobalEntityTypeAttributes.getAttributesForEntity(entity).hasAttribute(attribute);
    }

    public void addAttributeToEntityType(EntityType<? extends LivingEntity> entity, Attribute attribute, double value)
    {
        AttributeModifierMap.MutableAttribute attributes = entityAttributes.computeIfAbsent(entity,
                (entityType) -> new AttributeModifierMap.MutableAttribute());
        attributes.createMutableAttribute(attribute, value);
    }

    public void addAttributeToEntityType(EntityType<? extends LivingEntity> entity, Attribute attribute)
    {
        addAttributeToEntityType(entity, attribute, attribute.getDefaultValue());
    }

    public List<EntityType<? extends LivingEntity>> getTypes()
    {
        return ForgeRegistries.ENTITIES.getValues().stream()
                .filter(GlobalEntityTypeAttributes::doesEntityHaveAttributes)
                .map(entityType -> (EntityType<? extends LivingEntity>) entityType)
                .collect(Collectors.toList());
    }

    public Map<EntityType<? extends LivingEntity>, AttributeModifierMap.MutableAttribute> getEntityAttributes()
    {
        return entityAttributes;
    }
}
