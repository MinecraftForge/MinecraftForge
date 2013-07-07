package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

public class CraftComplexPart extends CraftEntity implements ComplexEntityPart {
    public CraftComplexPart(CraftServer server, net.minecraft.entity.boss.EntityDragonPart entity) {
        super(server, entity);
    }

    public ComplexLivingEntity getParent() {
        // MCPC+ start - Fix twilight Hydra crashes
        org.bukkit.entity.Entity result = getParentEntity();
        return (result instanceof ComplexLivingEntity) ? (ComplexLivingEntity)result : null;
    }

    private org.bukkit.entity.Entity getParentEntity() {
        return ((net.minecraft.entity.Entity)getHandle().entityDragonObj).getBukkitEntity();
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent cause) {
        getParentEntity().setLastDamageCause(cause);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return getParentEntity().getLastDamageCause();
        // MCPC+ end
    }

    @Override
    public net.minecraft.entity.boss.EntityDragonPart getHandle() {
        return (net.minecraft.entity.boss.EntityDragonPart) entity;
    }

    @Override
    public String toString() {
        return "CraftComplexPart";
    }

    public EntityType getType() {
        return EntityType.COMPLEX_PART;
    }
}
