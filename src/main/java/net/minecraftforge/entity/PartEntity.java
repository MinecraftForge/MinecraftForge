package net.minecraftforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;

/**
 * Created by brandon3055 on 16/12/20
 */
public abstract class PartEntity<T extends Entity> extends Entity {
    private final T parent;

    public PartEntity(T parent) {
        super(parent.getType(), parent.world);
        this.parent = parent;
    }

    public T getParent() {
        return parent;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }
}
