package org.bukkit.craftbukkit.entity;


import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

public class CraftHorse extends CraftAnimals implements Horse {

    public CraftHorse(CraftServer server, net.minecraft.entity.passive.EntityAnimal entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftHorse";
    }

    public EntityType getType() {
        return EntityType.HORSE;
    }
}
