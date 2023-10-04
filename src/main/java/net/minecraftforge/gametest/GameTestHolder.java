/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as containing game tests that should be registered automatically.
 * All methods annotated with {@link GameTest} or {@link GameTestGenerator} will be registered.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameTestHolder {
    /**
     * The default batch for all tests in this class that do not manually specify one.
     * It it recommended that this includes your mod id.
     * <br/>
     * This is also used when filtering out which batches to run during automation.
     * the filters will be tested against the prefix of the batch name. So it is
     * recommended to be structured in a logical fashion.
     * <pre>
     * Examples:
     * forge.tags.items
     * forge.tags.blocks
     * forge.global_loot_modifiers.block_drops
     * forge.global_loot_modifiers.tool_filter
     * </pre>
     *
     * The filter 'forge.global_loot_modifiers' would execute both the block_drops and tool_filter batches.
     * <p>
     * This will default to your modid if it is on the same class that is annotated with {@link net.minecraftforge.fml.common.Mod @Mod}
     */
    String value() default "";

    /**
     * Used as the default namespace to use for {@link GameTest#template() templates} for any game tests in the class.
     * Defaults to the modid if in a class with @Mod or else defaults to "minecraft"
     */
    String namespace() default "";
}
