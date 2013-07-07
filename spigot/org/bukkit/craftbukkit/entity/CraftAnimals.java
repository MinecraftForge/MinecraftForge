package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Animals;

public class CraftAnimals extends CraftAgeable implements Animals {

    public CraftAnimals(CraftServer server, net.minecraft.entity.passive.EntityAnimal entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.passive.EntityAnimal getHandle() {
        return (net.minecraft.entity.passive.EntityAnimal) entity;
    }

    @Override
    public String toString() {
        return "CraftAnimals";
    }
}
