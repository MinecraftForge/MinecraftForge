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

package net.minecraftforge.common;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;


public class Dimension implements IForgeRegistryEntry<Dimension>
{
    private final DimensionType type;
    private final ResourceLocation dimID;
    private int ticksWaited;
    private int dimIntID;
    public static Map<Integer,DimensionType> dimensionTypeMap = new Int2ObjectOpenHashMap<DimensionType>();
    
    public Dimension(DimensionType type, String dimensionName)
    {
        this.type = type;
        this.ticksWaited = 0;
        if(dimensionName.indexOf(":")==-1)
        {
        	dimID = new ResourceLocation(Loader.instance().activeModContainer().getModId().toLowerCase(),dimensionName);
        }
        else
        {
        	dimID = new ResourceLocation(dimensionName);
        }
    }
    
    static void init()
    {
    	ForgeRegistries.DIMENSIONS.register(new Dimension(DimensionType.OVERWORLD, "minecraft:overworld").setDimIntID(0));
    	ForgeRegistries.DIMENSIONS.register(new Dimension(DimensionType.NETHER, "minecraft:nether").setDimIntID(-1));
    	ForgeRegistries.DIMENSIONS.register(new Dimension(DimensionType.THE_END, "minecraft:the_end").setDimIntID(1));
    }
    
    public final Dimension setRegistryName(ResourceLocation dontUse)
    {
    	return this;
    }	
    
    public ResourceLocation getRegistryName()
    {
    	return dimID;
    }
    
    public Class<Dimension> getRegistryType()
    {
    	return Dimension.class;
    }
    
    public DimensionType getType()
    {
    	return type;
    }
    
    public static DimensionType getType(int dimIntID)
    {
    	return dimensionTypeMap.get(dimIntID);
    }
    
    public int getTicksWaited()
    {
    	return ticksWaited;
    }
    
    public void setTicksWaited(int ticksWaited)
    {
    	this.ticksWaited = ticksWaited;
    }
    
    public void addTicksWaited(int ticks)
    {
    	ticksWaited += ticks;
    }
    
    public String getName()
    {
    	return dimID.toString();
    }
    
    public ResourceLocation getID()
    {
    	return dimID;
    }
    
    /*Mods shouldn't call this*/
    public int getDimIntID()
    {
    	return dimIntID;
    }
    
    public Dimension setDimIntID(int dimIntID)
    {
    	this.dimIntID=dimIntID;
    	dimensionTypeMap.put(dimIntID, type);
    	return this;
    	
    }
}
