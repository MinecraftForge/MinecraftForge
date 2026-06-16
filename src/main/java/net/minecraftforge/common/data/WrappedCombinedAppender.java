/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.data.tags.BlockItemTagsProvider.CombinedAppender;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class WrappedCombinedAppender implements CombinedAppender {
    private final Function<BlockItemId, ResourceKey<Object>> mapper;
    private final Function<BlockItemTagId, TagKey<Object>> tagMapper;
    private final TagAppender<Object> appender;

    public static WrappedCombinedAppender item(TagAppender<Item> appender) {
        return new WrappedCombinedAppender(appender, BlockItemId::item, BlockItemTagId::item);
    }
    public static WrappedCombinedAppender block(TagAppender<Block> appender) {
        return new WrappedCombinedAppender(appender, BlockItemId::block, BlockItemTagId::block);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> WrappedCombinedAppender(
        TagAppender<T> appender,
        Function<BlockItemId, ResourceKey<T>> mapper,
        Function<BlockItemTagId, TagKey<T>> tagMapper
    ) {
        this.appender = (TagAppender<Object>)appender;
        this.mapper = (Function<BlockItemId, ResourceKey<Object>>)(Function)mapper;
        this.tagMapper = (Function<BlockItemTagId, TagKey<Object>>)(Function)tagMapper;
    }

    @Override
    public WrappedCombinedAppender add(final BlockItemId... ids) {
        return this.addAll(Arrays.stream(ids));
    }

    @Override
    public WrappedCombinedAppender add(final BlockItemTagId... ids) {
        for (var id : ids)
            addTag(id);
        return this;
    }

    @Override
    public WrappedCombinedAppender addAll(final Collection<BlockItemId> ids) {
        return this.addAll(ids.stream());
    }

    @Override
    public WrappedCombinedAppender addAll(Stream<BlockItemId> ids) {
        appender.addAll(ids.map(mapper));
        return this;
    }

    @Override
    public WrappedCombinedAppender addTag(BlockItemTagId id) {
        appender.addTag(tagMapper.apply(id));
        return this;
    }

    public WrappedCombinedAppender addOptional(BlockItemId... ids) {
        for (var id : ids)
            appender.addOptional(mapper.apply(id));
        return this;
    }

    public WrappedCombinedAppender addOptional(BlockItemTagId... ids) {
        for (var id : ids)
            appender.addOptionalTag(tagMapper.apply(id));
        return this;
    }

}
