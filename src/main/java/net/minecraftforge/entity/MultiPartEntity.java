package net.minecraftforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import java.util.List;
import java.util.function.Supplier;

public class MultiPartEntity implements IMultiPartEntity {
    private final Supplier<List<Entity>> partSupplier;
    public MultiPartEntity(Supplier<List<Entity>> getPartsFunc){
        partSupplier = getPartsFunc;
    }

    @Override
    public List<Entity> getParts() {
        return partSupplier.get();
    }


}
