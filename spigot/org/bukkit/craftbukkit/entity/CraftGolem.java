package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Golem;

public class CraftGolem extends CraftCreature implements Golem {
    public CraftGolem(CraftServer server, net.minecraft.entity.monster.EntityGolem entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.monster.EntityGolem getHandle() {
        return (net.minecraft.entity.monster.EntityGolem) entity;
    }

    @Override
    public String toString() {
        return "CraftGolem";
    }
}
