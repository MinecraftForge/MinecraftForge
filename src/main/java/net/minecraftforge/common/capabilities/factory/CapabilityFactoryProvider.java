package net.minecraftforge.common.capabilities.factory;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CapabilityFactoryProvider<I, G extends ICapabilityFactoryManager<I>> implements ICapabilityProvider {
    private CapabilityFactoryManager<I> manager;
    private Map<CapabilityFactoryManager.Key, LazyOptional<?>> caps = new HashMap<>();

    protected CapabilityFactoryProvider(CapabilityFactoryManager<I> manager) {
        this.manager = manager;
    }

    public LazyOptional<?> getCap(Capability<?> cap, Direction dir) {
        var key = new CapabilityFactoryManager.Key(cap, dir);
        var ret = caps.get(key);
        if (ret == null)
            ret = create(key);
        return ret;
    }

    private LazyOptional<?> create(CapabilityFactoryManager.Key key) {
        LazyOptional<?> ret = null;
        var inst = manager.factories.get(key);
        if (inst != null)
            ret = init(inst);
        else if (key.side() != null)
            ret = create(new CapabilityFactoryManager.Key(key.cap(), null));

        if (ret == null)
            ret = LazyOptional.empty();

        caps.put(key, ret);

        return ret;
    }

    private LazyOptional<?> init(CapabilityFactoryManager.Instance<I> inst) {
        var ret = inst.func().apply((I) this);
        for (var exp : inst.exposed()) {
            for (var dir : exp.dirs())
                caps.put(new CapabilityFactoryManager.Key(exp.cap(), dir), ret);
        }
        return ret;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCap(cap, side).cast();
    }
}
