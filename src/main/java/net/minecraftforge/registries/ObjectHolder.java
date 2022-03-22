/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.resources.ResourceLocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ObjectHolder can be used to automatically populate public static final fields with entries
 * from the registry. These values can then be referred within mod code directly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectHolder
{
    /**
     * The name of the registry to load registry entries from.
     * This string is parsed as a {@link ResourceLocation} and can contain a namespace.
     *
     * @return The registry name
     */
    String registryName();

    /**
     * If used on a class, this represents a modid only.
     * If used on a field, it represents a name, which can be abbreviated or complete.
     * Abbreviated names derive their modid from an enclosing ObjectHolder at the class level.
     *
     * @return Either a modid or a name based on the rules above
     */
    String value();
}
