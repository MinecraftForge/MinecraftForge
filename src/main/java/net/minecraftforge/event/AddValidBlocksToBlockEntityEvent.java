/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import java.util.function.BiConsumer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.RegistryObject;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provided to add new blocks to a {@link BlockEntityType}'s list of valid blocks, used for ticking, rendering, and other purposes.
 * For your own mod's blocks, specify the blocks in the {@link BlockEntityType.Builder}, don't use this event.
 * Blocks added to the valid blocks list are expected to have attached the same {@link BlockEntity} as the parent type in their {@link EntityBlock} implementation.
 *
 * Fired on the Mod bus {@link IModBusEvent} whenever the block registry is baked. Practically, this means it happens after blocks are finished registering during any registry rebuild.
 * The list of forge-added valid blocks is cleared each time the event is about to be fired, before mods receive it.
 */
public class AddValidBlocksToBlockEntityEvent extends Event implements IModBusEvent
{
    private final BiConsumer<BlockEntityType<?>, Block> callback;

    @ApiStatus.Internal
    public AddValidBlocksToBlockEntityEvent(BiConsumer<BlockEntityType<?>, Block> callback)
    {
        this.callback = callback;
    }

    /**
     * Adds a new {@code block} to the valid blocks list for the given {@code type}. It can be used with {@link RegistryObject}
     */
    public void addValidBlock(BlockEntityType<?> type, Block block)
    {
        callback.accept(type, block);
    }
}
