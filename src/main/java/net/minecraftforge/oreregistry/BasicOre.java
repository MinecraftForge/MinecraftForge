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

package net.minecraftforge.oreregistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A basic ore represents a set of unified ItemStack resources.
 * If it defined by an {@link OreMaterial}, an {@link OreShape}, and a list of variants.
 *
 * To get an instance, you can add your ore with {@link OreRegistry#addOre(OreMaterial, OreShape, ItemStack)}.
 * If you have no ore you can just retrieve one with {@link OreRegistry#getOre(OreMaterial, OreShape)}.
 *
 * One variant is selected to be "the one" which is always output from recipes and block drops. See {@link #get()}.
 * The variant that gets selected can be manipulated by the Forge Config, see "forge.configgui.oreRegistryPriorityModIds"
 */
public final class BasicOre implements Supplier<ItemStack>
{
    @Nonnull
    private final List<ItemStack> variants = new ArrayList<>();
    @Nonnull
    private final OreMaterial material;
    @Nonnull
    private final OreShape shape;

    BasicOre(@Nonnull OreMaterial material, @Nonnull OreShape shape)
    {
        this.material = material;
        this.shape = shape;
    }

    void addVariant(@Nonnull ItemStack variant)
    {
        Preconditions.checkNotNull(variant, "Variant must not be null");
        Preconditions.checkArgument(!variant.isEmpty(), "Variant must not be empty");
        Preconditions.checkArgument(variant.getMetadata() != OreDictionary.WILDCARD_VALUE, "Product must be an actual ItemStack, not a wildcard.");
        variant = variant.copy();
        variant.setCount(1);
        this.variants.add(variant);
        this.variants.sort(OreSortOrder.INSTANCE);
    }

    @Override
    @Nonnull
    public ItemStack get()
    {
        return variants.get(0).copy();
    }

    @Nonnull
    public List<ItemStack> getVariants()
    {
        return Collections.unmodifiableList(variants);
    }

    @Nonnull
    public OreMaterial getMaterial()
    {
        return material;
    }

    @Nonnull
    public OreShape getShape()
    {
        return shape;
    }

    public static final class OreSortOrder implements Comparator<ItemStack>
    {
        public static final OreSortOrder INSTANCE = new OreSortOrder();

        private OreSortOrder()
        {
        }

        @Override
        public int compare(ItemStack o1, ItemStack o2)
        {
            ResourceLocation registryName1 = o1.getItem().getRegistryName();
            ResourceLocation registryName2 = o2.getItem().getRegistryName();
            if (registryName1 == null || registryName2 == null)
            {
                if (registryName1 != null)
                    return -1;
                else if (registryName2 != null)
                    return 1;
                else
                    return 0;
            }

            List<String> priorities = ForgeModContainer.oreRegistryPriorityModIds;
            int priorityIdx1 = priorities.indexOf(registryName1.getResourceDomain());
            int priorityIdx2 = priorities.indexOf(registryName2.getResourceDomain());
            if (priorityIdx1 == -1)
                priorityIdx1 = Integer.MAX_VALUE;
            if (priorityIdx2 == -1)
                priorityIdx2 = Integer.MAX_VALUE;

            int priorityCompare = Integer.compare(priorityIdx1, priorityIdx2);
            if (priorityCompare != 0)
                return priorityCompare;

            return registryName1.toString().compareTo(registryName2.toString());
        }
    }
}
