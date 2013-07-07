package za.co.mcportcentral.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftTameableAnimal;
import org.bukkit.entity.EntityType;

public class CraftCustomTameableAnimal extends CraftTameableAnimal {

    public Class<? extends Entity> entityClass;
    public String entityName;

    public CraftCustomTameableAnimal(CraftServer server, net.minecraft.entity.passive.EntityTameable entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.getCustomEntityTypeName(entityClass);
        if (entityName == null)
            entityName = entity.getEntityName();
    }

    @Override
    public net.minecraft.entity.passive.EntityTameable getHandle() {
        return (net.minecraft.entity.passive.EntityTameable) entity;
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
