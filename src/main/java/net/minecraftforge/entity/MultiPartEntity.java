package net.minecraftforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import java.util.List;

public abstract class MultiPartEntity extends Entity {
    public MultiPartEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }
    public abstract List<PartEntity> getParts();
}