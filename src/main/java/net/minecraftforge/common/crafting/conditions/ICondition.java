/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

public interface ICondition
{
    ResourceLocation getID();

    default boolean test(IContext context)
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
        IContext EMPTY = new IContext()
        {
            @Override
            public <T> Tag<Holder<T>> getTag(TagKey<T> key)
            {
                return Tag.empty();
            }
        };

        /**
         * Return the requested tag if available, or an empty tag otherwise.
         */
        <T> Tag<Holder<T>> getTag(TagKey<T> key);
    }
}
