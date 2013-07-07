package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Witch;
import org.bukkit.entity.EntityType;

public class CraftWitch extends CraftMonster implements Witch {
    public CraftWitch(CraftServer server, net.minecraft.entity.monster.EntityWitch entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityWitch getHandle() {
        return (net.minecraft.entity.monster.EntityWitch) entity;
    }

    @Override
    public String toString() {
        return "CraftWitch";
    }

    public EntityType getType() {
        return EntityType.WITCH;
    }
}
