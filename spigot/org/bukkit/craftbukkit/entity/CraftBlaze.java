package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;

public class CraftBlaze extends CraftMonster implements Blaze {
    public CraftBlaze(CraftServer server, net.minecraft.entity.monster.EntityBlaze entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityBlaze getHandle() {
        return (net.minecraft.entity.monster.EntityBlaze) entity;
    }

    @Override
    public String toString() {
        return "CraftBlaze";
    }

    public EntityType getType() {
        return EntityType.BLAZE;
    }
}
