/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Objects;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class MultiModelState implements IModelState
{
    private final ImmutableMap<MultiModelPart, ? extends IModelState> states;

    public <M extends IModel, S extends IModelState> MultiModelState(ImmutableList<Pair<M, S>> states)
    {
        ImmutableMap.Builder<MultiModelPart, S> builder = ImmutableMap.builder();
        for(int i = 0; i < states.size(); i++)
        {
            Pair<M, S> pair = states.get(i);
            builder.put(new MultiModelPart(pair.getLeft(), i), pair.getRight());
        }
        this.states = builder.build();
    }

    public static IModelState getPartState(IModelState state, IModel model, int index)
    {
        if(state.apply(Optional.of(new MultiModelPart(model, index))).isPresent())
        {
            return new PartState(state, model, index);
        }
        return state;
    }

    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(part.isPresent())
        {
            if(part.get() instanceof MultiModelPart)
            {
                MultiModelPart key = (MultiModelPart)part.get();
                if(states.containsKey(key))
                {
                    return Optional.of(states.get(key).apply(Optional.empty()).orElse(TRSRTransformation.identity()));
                }
            }
            else if(part.get() instanceof PartPart)
            {
                PartPart partPart = (PartPart)part.get();
                MultiModelPart key = new MultiModelPart(partPart.model, partPart.index);
                if(states.containsKey(key))
                {
                    return states.get(key).apply(partPart.part);
                }
            }
        }
        return Optional.empty();
    }

    private static class PartState implements IModelState
    {
        private final IModelState state;
        private final IModel model;
        private final int index;
        
        public PartState(IModelState state, IModel model, int index)
        {
            this.state = state;
            this.model = model;
            this.index = index;
        }

        @Override
        public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
        {
            Optional<TRSRTransformation> normal = state.apply(part);
            Optional<TRSRTransformation> multi = state.apply(Optional.of(new PartPart(model, index, part)));
            if(normal.isPresent() && multi.isPresent())
            {
                return Optional.of(normal.get().compose(multi.get()));
            }
            if (normal.isPresent()) {
                return normal;
            }
            return multi;
        }
    }

    private static class MultiModelPart implements IModelPart
    {
        private final IModel model;
        private final int index;

        public MultiModelPart(IModel model, int index)
        {
            this.model = model;
            this.index = index;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(model, index);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MultiModelPart  other = (MultiModelPart)obj;
            return Objects.equal(this.model, other.model) && this.index == other.index;
        }
    }

    private static class PartPart implements IModelPart
    {
        private final IModel model;
        private final int index;
        private final Optional<? extends IModelPart> part;

        public PartPart(IModel model, int index, Optional<? extends IModelPart> part)
        {
            this.model = model;
            this.index = index;
            this.part = part;
        }
    }
}
