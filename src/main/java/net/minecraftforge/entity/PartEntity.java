package net.minecraftforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class PartEntity extends Entity {
    private final Entity parent;

    public PartEntity(Entity parent, World worldIn) {
        super(parent.getType(), worldIn);
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

    @Override
    @Nonnull
    public IPacket<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }
}
