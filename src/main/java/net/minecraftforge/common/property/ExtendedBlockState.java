/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class ExtendedBlockState extends BlockStateContainer
{
    private final ImmutableSet<IUnlistedProperty<?>> unlistedProperties;

    public ExtendedBlockState(Block blockIn, IProperty<?>[] properties, IUnlistedProperty<?>[] unlistedProperties)
    {
        super(blockIn, properties, buildUnlistedMap(unlistedProperties));
        ImmutableSet.Builder<IUnlistedProperty<?>> builder = ImmutableSet.builder();
        for(IUnlistedProperty<?> property : unlistedProperties)
        {
            builder.add(property);
        }
        this.unlistedProperties = builder.build();
    }

    public Collection<IUnlistedProperty<?>> getUnlistedProperties()
    {
        return unlistedProperties;
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

    @Override
    @Nonnull
    protected StateImplementation createState(@Nonnull Block block, @Nonnull  ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties)
    {
        if (unlistedProperties == null || unlistedProperties.isEmpty()) return super.createState(block, properties, unlistedProperties);
        return new ExtendedStateImplementation(block, properties, unlistedProperties, null, null);
    }

    protected static class ExtendedStateImplementation extends StateImplementation implements IExtendedBlockState
    {
        private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;
        private IBlockState cleanState;

        protected ExtendedStateImplementation(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, @Nullable ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> table, IBlockState clean)
        {
            super(block, properties, table);
            this.unlistedProperties = unlistedProperties;
            this.cleanState = clean == null ? this : clean;
        }

        @Override
        @Nonnull
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(@Nonnull IProperty<T> property, @Nonnull V value)
        {
            IBlockState clean = super.withProperty(property, value);
            if (clean == this.cleanState) {
                return this;
            }

            if (unlistedProperties.values().stream().noneMatch(Optional::isPresent))
            { // no dynamic properties present, looking up in the normal table
                return clean;
            }

            return new ExtendedStateImplementation(getBlock(), clean.getProperties(), unlistedProperties, ((StateImplementation)clean).getPropertyValueTable(), this.cleanState);
        }

        @Override
        public <V> IExtendedBlockState withProperty(IUnlistedProperty<V> property, V value)
        {
            if(!this.unlistedProperties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot set unlisted property " + property + " as it does not exist in " + getBlock().getBlockState());
            }
            if(!property.isValid(value))
            {
                throw new IllegalArgumentException("Cannot set unlisted property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(getBlock()) + ", it is not an allowed value");
            }
            Map<IUnlistedProperty<?>, Optional<?>> newMap = new HashMap<IUnlistedProperty<?>, Optional<?>>(unlistedProperties);
            newMap.put(property, Optional.ofNullable(value));
            if(Iterables.all(newMap.values(), Predicates.<Optional<?>>equalTo(Optional.empty())))
            { // no dynamic properties, lookup normal state
                return (IExtendedBlockState)cleanState;
            }
            return new ExtendedStateImplementation(getBlock(), getProperties(), ImmutableMap.copyOf(newMap), propertyValueTable, this.cleanState);
        }

        @Override
        public Collection<IUnlistedProperty<?>> getUnlistedNames()
        {
            return Collections.unmodifiableCollection(unlistedProperties.keySet());
        }

        @Override
        public <V>V getValue(IUnlistedProperty<V> property)
        {
            if(!this.unlistedProperties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot get unlisted property " + property + " as it does not exist in " + getBlock().getBlockState());
            }
            return property.getType().cast(this.unlistedProperties.get(property).orElse(null));
        }

        public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties()
        {
            return unlistedProperties;
        }

        @Override
        public IBlockState getClean()
        {
            return cleanState;
        }
    }
}
