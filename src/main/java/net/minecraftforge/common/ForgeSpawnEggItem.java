/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common;

import java.util.function.Supplier;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

public class ForgeSpawnEggItem extends SpawnEggItem
{    
    private final Supplier<? extends EntityType<?>> typeSupplier;

    /**
     * Registry-friendly spawn egg constructor.
     * @param typeSupplier A RegistryObject or other entity type supplier to retrieve the spawn egg's associated entity type later
     * @param foregroundColor The foreground color for the egg texture
     * @param backgroundColor The background (spots) color for the egg texture
     * @param properties The item properties for the egg item
     * **/ 
    public ForgeSpawnEggItem(final Supplier<? extends EntityType<?>> typeSupplier, final int backgroundColor, final int foregroundColor, final Item.Properties properties)
    {
       super(null, backgroundColor, foregroundColor, properties);
       this.typeSupplier = java.util.Objects.requireNonNull(typeSupplier);
    }

    @Override
    public EntityType<?> getType(CompoundNBT nbt)
    {
        // run the vanilla logic to allow type overrides via itemstack NBT
        EntityType<?> type = super.getType(nbt);
        // otherwise return our supplied type
        return type != null ? type : this.typeSupplier.get();
    }
    
    public Supplier<? extends EntityType<?>> getTypeSupplier()
    {
        return this.typeSupplier;
    }
}