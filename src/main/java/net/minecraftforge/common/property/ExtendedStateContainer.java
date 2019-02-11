/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.common.property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.AbstractStateHolder;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
// TODO Extended states gotta go, I doubt this works at all
public class ExtendedStateContainer<O, S extends IExtendedState<S>> extends StateContainer<O, S>
{
    private final ImmutableSet<IUnlistedProperty<?>> unlistedProperties;

    public <A extends AbstractStateHolder<O, S>> ExtendedStateContainer(O blockIn, StateContainer.IFactory<O, S, A> stateFactory, net.minecraft.state.IProperty<?>[] properties, IUnlistedProperty<?>[] unlistedProperties)
    {
        super(blockIn, getProxyFactory(unlistedProperties, stateFactory), buildListedMap(properties));// TODO Unlisted properties?, buildUnlistedMap(unlistedProperties));
        ImmutableSet.Builder<IUnlistedProperty<?>> builder = ImmutableSet.builder();
        for(IUnlistedProperty<?> property : unlistedProperties)
        {
            builder.add(property);
        }
        this.unlistedProperties = builder.build();
    }
    
    private static <O, S extends IExtendedState<S>, A extends AbstractStateHolder<O, S>> 
        StateContainer.IFactory<O, S, AbstractStateHolder<O,S>> getProxyFactory(IUnlistedProperty<?>[] unlistedProperties, StateContainer.IFactory<O, S, A> proxy)
    {
        return (o, props) -> {
            if (unlistedProperties == null || unlistedProperties.length == 0) return proxy.create(o, props);
            return new ExtendedStateHolder<O, S>(o, props, buildUnlistedMap(unlistedProperties), null);
        };
    }

    public Collection<IUnlistedProperty<?>> getUnlistedProperties()
    {
        return unlistedProperties;
    }

    private static Map<String, IProperty<?>> buildListedMap(IProperty<?>[] properties)
    {
        return Arrays.stream(properties).collect(Collectors.toMap(IProperty::getName, Function.identity()));
    }
    
    private static ImmutableMap<IUnlistedProperty<?>, Optional<?>> buildUnlistedMap(IUnlistedProperty<?>[] unlistedProperties)
    {
        ImmutableMap.Builder<IUnlistedProperty<?>, Optional<?>> builder = ImmutableMap.builder();
        for(IUnlistedProperty<?> p : unlistedProperties)
        {
            builder.put(p, Optional.empty());
        }
        return builder.build();
    }

    protected static class ExtendedStateHolder<O, S extends IExtendedState<S>> extends AbstractStateHolder<O, S> implements IExtendedState<S>
    {
        private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;
        private S cleanState;

        protected ExtendedStateHolder(O block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, S clean)
        {
            super(block, properties);
            this.unlistedProperties = unlistedProperties;
            this.cleanState = clean == null ? (S) this : clean;
        }

        @Override
        @Nonnull
        public <T extends Comparable<T>, V extends T> S with(@Nonnull IProperty<T> property, @Nonnull V value)
        {
            S clean = super.with(property, value);
            if (clean == this.cleanState) {
                return (S) this;
            }

            if (this == this.cleanState)
            { // no dynamic properties present, looking up in the normal table
                return clean;
            }

            return (S) new ExtendedStateHolder(object, ((IStateHolder)clean).getValues(), unlistedProperties, this.cleanState);
        }

        @Override
        public <V> S withProperty(IUnlistedProperty<V> property, @Nullable V value)
        {
            Optional<?> oldValue = unlistedProperties.get(property);
            if (oldValue == null)
            {
                throw new IllegalArgumentException("Cannot set unlisted property " + property + " as it does not exist in " + this);
            }
            if (Objects.equals(oldValue.orElse(null), value))
            {
                return (S) this;
            }
            if (!property.isValid(value))
            {
                throw new IllegalArgumentException("Cannot set unlisted property " + property + " to " + value + " on object " + object + ", it is not an allowed value");
            }
            boolean clean = true;
            ImmutableMap.Builder<IUnlistedProperty<?>, Optional<?>> builder = ImmutableMap.builder();
            for (Map.Entry<IUnlistedProperty<?>, Optional<?>> entry : unlistedProperties.entrySet())
            {
                IUnlistedProperty<?> key = entry.getKey();
                Optional<?> newValue = key.equals(property) ? Optional.ofNullable(value) : entry.getValue();
                if (newValue.isPresent()) clean = false;
                builder.put(key, newValue);
            }
            if (clean)
            { // no dynamic properties, lookup normal state
                return (S) cleanState;
            }
            return (S) new ExtendedStateHolder(object, getValues(), builder.build(), this.cleanState);
        }

        @Override
        public Collection<IUnlistedProperty<?>> getUnlistedNames()
        {
            return unlistedProperties.keySet();
        }

        @Override
        @Nullable
        public <V> V getValue(IUnlistedProperty<V> property)
        {
            Optional<?> value = unlistedProperties.get(property);
            if (value == null)
            {
                throw new IllegalArgumentException("Cannot get unlisted property " + property + " as it does not exist in " + this);
            }
            return property.getType().cast(value.orElse(null));
        }

        public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties()
        {
            return unlistedProperties;
        }

        @Override
        public S getClean()
        {
            return cleanState;
        }
    }
}
