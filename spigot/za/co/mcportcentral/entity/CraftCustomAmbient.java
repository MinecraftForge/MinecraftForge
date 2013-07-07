package za.co.mcportcentral.entity;

import net.minecraft.entity.Entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftAmbient;
import org.bukkit.entity.EntityType;

import cpw.mods.fml.common.registry.EntityRegistry;

public class CraftCustomAmbient extends CraftAmbient {

    public Class<? extends Entity> entityClass;
    public String entityName;

    public CraftCustomAmbient(CraftServer server, net.minecraft.entity.passive.EntityAmbientCreature entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
    }

    @Override
    public net.minecraft.entity.passive.EntityAmbientCreature getHandle() {
        return (net.minecraft.entity.passive.EntityAmbientCreature) entity;
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