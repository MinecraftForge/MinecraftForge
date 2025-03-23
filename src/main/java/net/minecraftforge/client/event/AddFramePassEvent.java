/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Fired after all vanilla frame passes are added into the pass list.
 *
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable}, and does not
 * {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain net.minecraftforge.common.MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide#CLIENT logical client}.
 */
public class AddFramePassEvent extends Event {
    private final FrameGraphBuilder builder;
    public final LevelTargetBundle bundle;

    private final Set<String> addedNames = new HashSet<>();

    @ApiStatus.Internal
    public AddFramePassEvent(FrameGraphBuilder builder, LevelTargetBundle bundle) {
        this.builder = builder;
        this.bundle = bundle;
    }

    /**
     * Adds a frame pass to the end of the pass list.
     *
     * @param rl Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @return Reference to pass to add modder rendering to.
     * @throws IllegalArgumentException If the name is a duplicate.
     */
    public FramePass createPass(ResourceLocation rl) {
        String name = rl.toString();
        if (!this.addedNames.add(name)) // false if the set didn't change
            throw new IllegalArgumentException("Cannot create a frame pass with a duplicate name: " + name);

        return this.builder.addPass(name);
    }

    /**
     * Adds a frame pass relative to a different pass.
     *
     * @param rl    Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @param other Use vanilla name of frame pass to order against. See
     *              {@linkplain net.minecraft.client.renderer.LevelRenderer}.
     * @param order BEFORE to come before, AFTER to come after.
     * @return Reference to pass to add modder rendering to.
     * @throws IllegalArgumentException If the name is a duplicate or the other pass name is an empty string or does not
     *                                  exist in the frame graph builder.
     */
    public FramePass createPass(ResourceLocation rl, String other, Order order) {
        // ensure valid other pass name
        if (other.isBlank())
            throw new IllegalArgumentException("Cannot create an ordered path against a frame pass with an empty name!");

        // ensure non-duplicate pass name
        String name = rl.toString();
        if (!this.addedNames.add(name)) // false if the set didn't change
            throw new IllegalArgumentException("Cannot create a frame pass with a duplicate name: " + name);

        return this.builder.addOrderedPass(name, other, order.ordinal());
    }

    /**
     * Adds a frame pass relative to a different pass.
     *
     * <p>If you are targeting a modded pass, ensure that your mod is loaded {@code AFTER} the target mod as a
     * dependency in {@code mods.toml}, or use a lower
     * {@linkplain net.minecraftforge.eventbus.api.EventPriority event priority}.</p>
     *
     * @param rl    Resource location for frame pass name.
     * @param other Use mod's RL of frame pass to order against.
     * @param order BEFORE to come before, AFTER to come after.
     * @return Reference to pass to add modder rendering to.
     * @throws IllegalArgumentException If the name is a duplicate or the other pass does not exist in the frame graph
     *                                  builder.
     */
    public FramePass createPass(ResourceLocation rl, ResourceLocation other, Order order) {
        return this.createPass(rl, other.toString(), order);
    }

    /** @return If this event's builder has a pass with the given name. */
    public boolean hasPass(ResourceLocation name) {
        return this.hasPass(rlToString(name));
    }

    /**
     * @return If this event's builder has a pass with the given name.
     * @see #hasPass(ResourceLocation)
     */
    public boolean hasPass(String name) {
        return this.builder.indexOfPass(name) > 0;
    }

    /**
     * Gets the string of a given resource location, removing the namespace if it is the
     * {@linkplain ResourceLocation#DEFAULT_NAMESPACE default namespace}. (i.e. {@code minecraft:foo} becomes
     * {@code foo}).
     *
     * @param rl The location to convert to string
     * @return The string representation
     */
    private static String rlToString(ResourceLocation rl) {
        return ResourceLocation.DEFAULT_NAMESPACE.equals(rl.getNamespace()) ? rl.getPath() : rl.toString();
    }

    /**
     * When {@linkplain #createPass(ResourceLocation, String, Order) creating an ordered pass}, this enum dictates
     * whether it should be placed before or after the declared existing pass.
     */
    public enum Order {
        BEFORE, AFTER
    }
}