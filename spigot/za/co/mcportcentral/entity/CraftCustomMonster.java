package za.co.mcportcentral.entity;

import net.minecraft.entity.Entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftMonster;
import org.bukkit.entity.EntityType;

import cpw.mods.fml.common.registry.EntityRegistry;

public class CraftCustomMonster extends CraftMonster {

    public Class<? extends Entity> entityClass;
    public String entityName;

    public CraftCustomMonster(CraftServer server, net.minecraft.entity.monster.EntityMob entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
    }

    @Override
    public net.minecraft.entity.monster.EntityMob getHandle() {
        return (net.minecraft.entity.monster.EntityMob) entity;
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