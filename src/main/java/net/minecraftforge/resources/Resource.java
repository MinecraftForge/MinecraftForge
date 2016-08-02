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
package net.minecraftforge.resources;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is used to register ingots, nuggets, gems and other stuff like this, so that other mods can use this stuff
 * and don't have to register a new stuff.
 */
public class Resource {

    /** The unique identification name for this resource. */
    protected final String resourceName;
    /** The color of this resource*/
    protected int color = 0xFFFFFFFF;


    /**
     * The productions of the resource.
     * The string is the type of production like: ingot, nugget, block, ore, fluid, gas, gem.
     * The item stack is the production.
     */
    static BiMap<String, ItemStack> productions = HashBiMap.create();

    public Resource(String resourceName)
    {
        this.resourceName = resourceName.toLowerCase(Locale.ENGLISH);
    }

    public String getName()
    {
        return resourceName;
    }

    public Resource setColor(int color) {
        this.color = color;
        return this;
    }

    public int getColor() { return color; }

    public boolean registerProduction(String productionType, ItemStack production)
    {
        if(productions.containsKey(productionType)){
            return false;
        }
        productions.put(productionType, production);
        return true;
    }

    public ItemStack getProduction(String productionType) { return productions.get(productionType); }
}
