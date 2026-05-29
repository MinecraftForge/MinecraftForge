/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.common.crafting.conditions;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.List;

public class ConditionContext implements ICondition.IContext {
    private final List<Registry.PendingTags<?>> pendingTags;

    public ConditionContext(List<Registry.PendingTags<?>> pendingTags) {
        this.pendingTags = pendingTags;
    }

    @Override
    public <T> Collection<Holder<T>> getTag(TagKey<T> key) {
        for (var entry : this.pendingTags) {
            if (entry.key() != key.registry())
                continue;
            @SuppressWarnings("unchecked")
            var typed = ((Registry.PendingTags<T>)entry);
            return typed.getPending(key);
        }
        return List.of();
    }
}
