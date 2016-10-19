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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

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
            builder.put(p, Optional.absent());
        }
        return builder.build();
    }

    @Override
    protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties)
    {
        if (unlistedProperties == null || unlistedProperties.isEmpty()) return super.createState(block, properties, unlistedProperties);
        return new ExtendedStateImplementation(block, properties, unlistedProperties, null);
    }

    protected static class ExtendedStateImplementation extends StateImplementation implements IExtendedBlockState
    {
        private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;
        private Map<Map<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation> normalMap;

        protected ExtendedStateImplementation(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties, ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> table)
        {
            super(block, properties);
            this.unlistedProperties = unlistedProperties;
            this.propertyValueTable = table;
        }

        @Override
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value)
        {
            if (!this.getProperties().containsKey(property))
            {
                throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + getBlock().getBlockState());
            }
            else
            {
                if (!property.getAllowedValues().contains(value))
                {
                    throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.REGISTRY.getNameForObject(getBlock()) + ", it is not an allowed value");
                } else
                {
                    if (this.getProperties().get(property) == value)
                    {
                        return this;
                    }
                    Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap(getProperties());
                    map.put(property, value);
                    if (Iterables.all(unlistedProperties.values(), Predicates.<Optional<?>>equalTo(Optional.absent())))
                    { // no dynamic properties present, looking up in the normal table
                        return normalMap.get(map);
                    }
                    ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> table = propertyValueTable;
                    table = ((StateImplementation) table.get(property, value)).getPropertyValueTable();
                    return new ExtendedStateImplementation(getBlock(), ImmutableMap.copyOf(map), unlistedProperties, table).setMap(this.normalMap);
                }
            }
        }

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
            newMap.put(property, Optional.fromNullable(value));
            if(Iterables.all(newMap.values(), Predicates.<Optional<?>>equalTo(Optional.absent())))
            { // no dynamic properties, lookup normal state
                return (IExtendedBlockState) normalMap.get(getProperties());
            }
            return new ExtendedStateImplementation(getBlock(), getProperties(), ImmutableMap.copyOf(newMap), propertyValueTable).setMap(this.normalMap);
        }

        public Collection<IUnlistedProperty<?>> getUnlistedNames()
        {
            return Collections.unmodifiableCollection(unlistedProperties.keySet());
        }

        public <V>V getValue(IUnlistedProperty<V> property)
        {
            if(!this.unlistedProperties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot get unlisted property " + property + " as it does not exist in " + getBlock().getBlockState());
            }
            return property.getType().cast(this.unlistedProperties.get(property).orNull());
        }

        public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties()
        {
            return unlistedProperties;
        }

        @Override
        public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation> map)
        {
            this.normalMap = map;
            super.buildPropertyValueTable(map);
        }

        private ExtendedStateImplementation setMap(Map<Map<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation> map)
        {
            this.normalMap = map;
            return this;
        }

        public IBlockState getClean()
        {
            return this.normalMap.get(getProperties());
        }
    }
}