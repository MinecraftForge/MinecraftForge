package za.co.mcportcentral.entity;

import net.minecraft.entity.Entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;

import cpw.mods.fml.common.registry.EntityRegistry;

public class CraftCustomLivingEntity extends CraftLivingEntity {

    public Class<? extends Entity> entityClass;
    public String entityName;

    public CraftCustomLivingEntity(CraftServer server, net.minecraft.entity.EntityLiving entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
    }

    @Override
    public net.minecraft.entity.EntityLiving getHandle() {
        return (net.minecraft.entity.EntityLiving) entity;
    }

    @Override
    public String toString() {
        return this.entityName;
    }

    public Class<? extends Entity> getEntityClass()
    {
        return this.entityClass;
    }

    public EntityType getType() {
        EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
    }
}