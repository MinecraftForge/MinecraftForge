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

package net.minecraftforge.fluids;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.NonNullList;

public class FluidDictionary
{
    private static final Map<Fluid, NonNullList<String>> FLUID_TO_NAMES = Maps.<Fluid, NonNullList<String>>newHashMap();
    private static final Map<String, NonNullList<Fluid>> NAME_TO_FLUIDS =  Maps.<String, NonNullList<Fluid>>newHashMap();
    
    public static void registerFluid(Fluid fluid, String name)
    {
        name = name.toLowerCase();
        if (FLUID_TO_NAMES.containsKey(fluid))
        {
            FLUID_TO_NAMES.get(fluid).add(name);
        }
        else
        {
            FLUID_TO_NAMES.put(fluid, NonNullList.<String>withSize(1, name));
        }
        if (NAME_TO_FLUIDS.containsKey(name))
        {
            NAME_TO_FLUIDS.get(name).add(fluid);
        }
        else
        {
            NAME_TO_FLUIDS.put(name, NonNullList.<Fluid>withSize(1, fluid));
        }
    }
    
    public static NonNullList<String> getNames(Fluid fluid)
    {
        NonNullList<String> ret = NonNullList.<String>create();
        if (FLUID_TO_NAMES.containsKey(fluid))
        {
            ret.addAll(FLUID_TO_NAMES.get(fluid));
        }
        return ret;
    }
    
    public static NonNullList<Fluid> getFluids(String name)
    {
        NonNullList<Fluid> ret = NonNullList.<Fluid>create();
        if (NAME_TO_FLUIDS.containsKey(name = name.toLowerCase()))
        {
            ret.addAll(NAME_TO_FLUIDS.get(name));
        }
        return ret;
    }
    
    @Nullable
    public static Fluid getFirstFluid(String name)
    {
        if (nameExists(name))
        {
            return NAME_TO_FLUIDS.get(name.toLowerCase()).get(0);
        }
        return null;
    }
    
    public static boolean nameExists(String name)
    {
        return NAME_TO_FLUIDS.containsKey(name.toLowerCase());
    }
    
    public static NonNullList<String> getAllNames()
    {
        NonNullList<String> ret = NonNullList.<String>create();
        ret.addAll(NAME_TO_FLUIDS.keySet());
        return ret;
    }
    
    @Nullable
    public static FluidStack createFluidStack(String name, int amount)
    {
        Fluid fluid = getFirstFluid(name.toLowerCase());
        return fluid == null ? null : new FluidStack(fluid, amount);
    }
}
