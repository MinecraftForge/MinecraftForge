package net.minecraftforge.server.timings;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Objects;

public class ForgeRegistryObjectHolder<T> {
    public final T object;
    public final BlockPos pos;
    public final RegistryKey<World> dimension;

    public ForgeRegistryObjectHolder(T object, BlockPos pos, RegistryKey<World> dimension) {
        this.object = object;
        this.pos = pos;
        this.dimension = dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgeRegistryObjectHolder<?> that = (ForgeRegistryObjectHolder<?>) o;
        return object.equals(that.object) && pos.equals(that.pos) && dimension.equals(that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, pos, dimension);
    }
}
