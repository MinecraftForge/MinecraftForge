package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;

public class CraftGiant extends CraftMonster implements Giant {

    public CraftGiant(CraftServer server, net.minecraft.entity.monster.EntityGiantZombie entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityGiantZombie getHandle() {
        return (net.minecraft.entity.monster.EntityGiantZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftGiant";
    }

    public EntityType getType() {
        return EntityType.GIANT;
    }
}
