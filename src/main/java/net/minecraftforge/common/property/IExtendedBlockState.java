package net.minecraftforge.common.property;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public interface IExtendedBlockState extends IBlockState
{
    Collection<IUnlistedProperty<?>> getUnlistedNames();

    <V>V getValue(IUnlistedProperty<V> property);

    <V>IExtendedBlockState withProperty(IUnlistedProperty<V> property, V value);

    ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties();
    
    IBlockState getClean();
}
