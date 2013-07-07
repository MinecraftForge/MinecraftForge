package za.co.mcportcentral.entity;

import net.minecraft.entity.Entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftWaterMob;
import org.bukkit.entity.EntityType;

import cpw.mods.fml.common.registry.EntityRegistry;

public class CraftCustomWaterMob extends CraftWaterMob {

    public Class<? extends Entity> entityClass;
    public String entityName;

    public CraftCustomWaterMob(CraftServer server, net.minecraft.entity.passive.EntityWaterMob entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
    }

    @Override
    public net.minecraft.entity.passive.EntityWaterMob getHandle() {
        return (net.minecraft.entity.passive.EntityWaterMob) entity;
    }

    @Override
    public String toString() {
        return this.entityName;
    }

    public EntityType getType() {
        EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
    }
}