/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

import javax.annotation.Nullable;
import java.util.Map;

public interface ICondition
{
    ResourceLocation getID();

    default boolean test(@Nullable IContext context)
    {
        return test();
    }

    /**
     * @deprecated Use {@linkplain #test(IContext) the other more general overload}.
     */
    @Deprecated(forRemoval = true, since = "1.18.2")
    boolean test();

    interface IContext
    {
        @Nullable
        <T> Map<ResourceLocation, Tag<Holder<T>>> getTags(ResourceKey<? extends Registry<T>> key);
    }
}
