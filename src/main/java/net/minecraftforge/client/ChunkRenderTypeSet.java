/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An immutable ordered set (not implementing {@link java.util.Set}) of chunk {@linkplain RenderType render types}.
 * <p>
 * Considerably speeds up lookups and merges of sets of chunk {@linkplain RenderType render types}.
 * Users should cache their instances of this class whenever possible, as instantiating it is cheap, but not free.
 */
public sealed class ChunkRenderTypeSet implements Iterable<RenderType>
{
    private static final List<RenderType> CHUNK_RENDER_TYPES_LIST = RenderType.chunkBufferLayers();
    private static final RenderType[] CHUNK_RENDER_TYPES = CHUNK_RENDER_TYPES_LIST.toArray(new RenderType[0]);

    private static final ChunkRenderTypeSet NONE = new None();
    private static final ChunkRenderTypeSet ALL = new All();

    public static ChunkRenderTypeSet none()
    {
        return NONE;
    }

    public static ChunkRenderTypeSet all()
    {
        return ALL;
    }

    public static ChunkRenderTypeSet of(RenderType... renderTypes)
    {
        return of(Arrays.asList(renderTypes));
    }

    public static ChunkRenderTypeSet of(Collection<RenderType> renderTypes)
    {
        if (renderTypes.isEmpty())
            return none();
        return of((Iterable<RenderType>) renderTypes);
    }

    private static ChunkRenderTypeSet of(Iterable<RenderType> renderTypes)
    {
        var bits = new BitSet();
        for (RenderType renderType : renderTypes)
        {
            int index = renderType.getChunkLayerId();
            Preconditions.checkArgument(index >= 0, "Attempted to create chunk render type set with a non-chunk render type: " + renderType);
            bits.set(index);
        }
        return new ChunkRenderTypeSet(bits);
    }

    public static ChunkRenderTypeSet union(ChunkRenderTypeSet... sets)
    {
        return union(Arrays.asList(sets));
    }

    public static ChunkRenderTypeSet union(Collection<ChunkRenderTypeSet> sets)
    {
        if (sets.isEmpty())
            return none();
        return union((Iterable<ChunkRenderTypeSet>) sets);
    }

    public static ChunkRenderTypeSet union(Iterable<ChunkRenderTypeSet> sets)
    {
        var bits = new BitSet();
        for (var set : sets)
            bits.or(set.bits);
        return new ChunkRenderTypeSet(bits);
    }

    public static ChunkRenderTypeSet intersection(ChunkRenderTypeSet... sets)
    {
        return intersection(Arrays.asList(sets));
    }

    public static ChunkRenderTypeSet intersection(Collection<ChunkRenderTypeSet> sets)
    {
        if (sets.isEmpty())
            return all();
        return intersection((Iterable<ChunkRenderTypeSet>) sets);
    }

    public static ChunkRenderTypeSet intersection(Iterable<ChunkRenderTypeSet> sets)
    {
        var bits = new BitSet();
        bits.set(0, CHUNK_RENDER_TYPES.length);
        for (var set : sets)
            bits.and(set.bits);
        return new ChunkRenderTypeSet(bits);
    }

    private final BitSet bits;

    private ChunkRenderTypeSet(BitSet bits)
    {
        this.bits = bits;
    }

    public boolean isEmpty()
    {
        return bits.isEmpty();
    }

    public boolean contains(RenderType renderType)
    {
        int id = renderType.getChunkLayerId();
        return id >= 0 && bits.get(id);
    }

    @NotNull
    @Override
    public Iterator<RenderType> iterator()
    {
        return new IteratorImpl();
    }

    public List<RenderType> asList()
    {
        return ImmutableList.copyOf(this);
    }

    private final class IteratorImpl implements Iterator<RenderType>
    {
        private int index = bits.nextSetBit(0);

        @Override
        public boolean hasNext()
        {
            return index >= 0;
        }

        @Override
        public RenderType next()
        {
            var renderType = CHUNK_RENDER_TYPES[index];
            index = bits.nextSetBit(index + 1);
            return renderType;
        }
    }

    private static final class None extends ChunkRenderTypeSet
    {
        private None()
        {
            super(new BitSet());
        }

        @Override
        public boolean isEmpty()
        {
            return true;
        }

        @Override
        public boolean contains(RenderType renderType)
        {
            return false;
        }

        @NotNull
        @Override
        public Iterator<RenderType> iterator()
        {
            return Collections.emptyIterator();
        }

        @Override
        public List<RenderType> asList()
        {
            return List.of();
        }
    }

    private static final class All extends ChunkRenderTypeSet
    {
        private All()
        {
            super(Util.make(new BitSet(), bits -> bits.set(0, CHUNK_RENDER_TYPES.length)));
        }

        @Override
        public boolean isEmpty(){
            return false;
        }

        @Override
        public boolean contains(RenderType renderType)
        {
            return renderType.getChunkLayerId() >= 0; // Could just return true for efficiency purposes, but checking is near-free
        }

        @NotNull
        @Override
        public Iterator<RenderType> iterator()
        {
            return CHUNK_RENDER_TYPES_LIST.iterator();
        }

        @Override
        public List<RenderType> asList()
        {
            return CHUNK_RENDER_TYPES_LIST;
        }
    }
}
