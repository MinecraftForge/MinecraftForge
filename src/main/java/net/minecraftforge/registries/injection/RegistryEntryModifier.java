package net.minecraftforge.registries.injection;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegistryEntryModifier<E> implements Merger, Injector<E>
{
    private final RegistryKey<Registry<E>> registryKey;
    private final List<Merger> mergers;
    private final List<Injector<E>> injectors;

    private RegistryEntryModifier(Builder<E> builder)
    {
        this.registryKey = builder.registryKey;
        this.mergers = ImmutableList.copyOf(builder.mergers);
        this.injectors = ImmutableList.copyOf(builder.injectors);
    }

    @Override
    public void inject(RegistryKey<E> entryKey, JsonElement entryData) throws IOException
    {
        for (Injector<E> injector : injectors)
        {
            injector.inject(entryKey, entryData);
        }
    }

    @Override
    public void merge(JsonElement dest, JsonElement src) throws IOException
    {
        for (Merger merger : mergers)
        {
            merger.merge(dest, src);
        }
    }

    public boolean isEmpty()
    {
        return mergers.isEmpty() && injectors.isEmpty();
    }

    public boolean hasMergers()
    {
        return !mergers.isEmpty();
    }

    public boolean hasInjections()
    {
        return !injectors.isEmpty();
    }

    public void injectRaw(RegistryKey<?> rawEntryKey, JsonElement entryData) throws IOException
    {
        RegistryKey<E> typedEntryKey = RegistryKey.getOrCreateKey(registryKey, rawEntryKey.getLocation());

        // Assert that the provided raw key was valid for this modifier.
        if (typedEntryKey != rawEntryKey) throw new IOException("Mismatching registry keys!");

        inject(typedEntryKey, entryData);
    }

    public static <E> Builder<E> builder(RegistryKey<Registry<E>> registryKey)
    {
        return new Builder<>(registryKey);
    }

    public static class Builder<E>
    {
        private final RegistryKey<Registry<E>> registryKey;
        private final List<Merger> mergers = new ArrayList<>();
        private final List<Injector<E>> injectors = new ArrayList<>();

        private Builder(RegistryKey<Registry<E>> registryKey)
        {
            this.registryKey = registryKey;
        }

        public Builder<E> add(Merger merger)
        {
            mergers.add(merger);
            return this;
        }

        public Builder<E> add(Injector<E> injector)
        {
            injectors.add(injector);
            return this;
        }

        public RegistryEntryModifier<E> build()
        {
            return new RegistryEntryModifier<>(this);
        }
    }
}
