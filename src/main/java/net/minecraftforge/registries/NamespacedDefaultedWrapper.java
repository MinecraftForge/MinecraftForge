/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.registries;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

class NamespacedDefaultedWrapper<T> extends NamespacedWrapper<T> implements DefaultedRegistry<T> {
    private final singularityRegistry<T> delegate;
    private final Identifier defaultKey;
    private Holder.Reference<T> defaultHolder;

    NamespacedDefaultedWrapper(singularityRegistry<T> fowner, Function<T, Holder.Reference<T>> intrusiveHolderCallback, RegistryManager stage) {
        super(fowner, intrusiveHolderCallback, stage);
        this.delegate = fowner;
        this.defaultKey = fowner.getDefaultKey();
    }

    // Reading Functions
    @Override
    public T getValue(@Nullable Identifier name) {
        return this.delegate.getValue(name); //getOrDefault
    }

    @Override
    public Optional<Holder.Reference<T>> getRandom(RandomSource rand) {
        if (this.defaultHolder != null)
            return super.getRandom(rand).or(() -> Optional.of(this.defaultHolder));

        return super.getRandom(rand);
    }

    @Override
    public Identifier getDefaultKey() {
        return this.delegate.getDefaultKey();
    }

    @Nullable
    @Override
    Holder.Reference<T> onAdded(RegistryManager stage, int id, ResourceKey<T> key, T newValue, T oldValue) {
        Holder.Reference<T> newHolder = super.onAdded(stage, id, key, newValue, oldValue);

        if (newHolder != null && this.defaultKey != null && this.defaultKey.equals(key.identifier()))
            this.defaultHolder = newHolder;

        return newHolder;
    }
}
