package org.bukkit.craftbukkit.entity;


import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

public class CraftSkeleton extends CraftMonster implements Skeleton {

    public CraftSkeleton(CraftServer server, net.minecraft.entity.monster.EntitySkeleton entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntitySkeleton getHandle() {
        return (net.minecraft.entity.monster.EntitySkeleton) entity;
    }

    @Override
    public String toString() {
        return "CraftSkeleton";
    }

    public EntityType getType() {
        return EntityType.SKELETON;
    }

    public SkeletonType getSkeletonType() {
        return SkeletonType.getType(getHandle().getSkeletonType());
    }

    public void setSkeletonType(SkeletonType type) {
        Validate.notNull(type);
        getHandle().setSkeletonType(type.getId());
    }
}
