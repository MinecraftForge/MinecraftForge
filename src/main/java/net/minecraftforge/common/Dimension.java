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
import net.minecraft.world.WorldProvider;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Should be created and registered at {@link net.minecraftforge.event.RegistryEvent.Register<Dimension>}
 * If the number of dimensions you need it variable it's probably best to register them at the start of the game and initialize them later
 * @author temp1011 (needed?)
 */
public class Dimension implements IForgeRegistryEntry<Dimension>
{
    private DimensionType type;
    private final ResourceLocation dimID;
    private int ticksWaited;
	/**
	 * default is -2, no dimension will be registered for this
	 */
	private int dimIntID = -2;
    public static final Map<Integer,ResourceLocation> dimensionIntIDMap = new Int2ObjectOpenHashMap<>();
    public static final Map<Integer,DimensionType> dimensionTypeMap = new Int2ObjectOpenHashMap<>();
    public static final IForgeRegistry<Dimension> REGISTRY = ForgeRegistries.DIMENSIONS;

    static void init()
    {
    	REGISTRY.register(new Dimension(DimensionType.OVERWORLD, "minecraft:overworld").setDimIntID(0));
    	REGISTRY.register(new Dimension(DimensionType.NETHER, "minecraft:nether").setDimIntID(-1));
    	REGISTRY.register(new Dimension(DimensionType.THE_END, "minecraft:the_end").setDimIntID(1));
    }

	/**
	 * Create a dimension using a dimension type alreadyd provided.
	 * If you need to create a dimension type as well use {@link #dimensionWithCustomType(String, String, String, Class, boolean)}
	 * @param type the dimension type
	 * @param dimensionName the name of the dimension - Should be supplied in {@link ResourceLocation} form. If not in this form then modid of current mod used instead
	 */
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

    private Dimension(String dimensionName)
	{
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

	private void setType(DimensionType type) {
    	this.type = type;
	}
    
    /* Only required for implementation, don't use this!*/
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
    
    public static ResourceLocation getID(int dimIntID)
    {
    	return dimensionIntIDMap.get(dimIntID);
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
    
    /*Should be internal use only*/
    public int getDimIntID()
    {
    	if(dimIntID==-2)
    	{
    		setDimIntID(((ForgeRegistry<Dimension>) REGISTRY).getID(dimID));
    	}
    	return dimIntID;
    }

	/**
	 * get the int id for a dimension from it's resourcelocation id
	 * may be necessary for some internal fields eg {@link net.minecraft.entity.Entity#dimension}
	 * @param id the {@link ResourceLocation} id
	 * @return the int id (-2 if dimension not found)
	 */
	public static int getDimIntID(ResourceLocation id)
    {
    	Dimension dim = REGISTRY.getValue(id);
    	return dim == null ? -2 : dim.dimIntID;
    }

	/**
	 * Factory method for creating a custom dimension type and a dimension
	 * @param dimensionName the name of the dimension
	 * @param typeName the name of the dimension type
	 * @param typeSuffix suffix for the dimension type
	 * @param provider world provider used
	 * @param keepLoaded whether the dimension type should stay loaded
	 * @return the {@link Dimension} (for chaining)
	 */
    public static Dimension dimensionWithCustomType(String dimensionName, String typeName, String typeSuffix, Class<? extends WorldProvider> provider, boolean keepLoaded)
	{
		Dimension dim = new Dimension(dimensionName);
		DimensionType type = DimensionType.register(typeName, typeSuffix, getDimIntID(dim.dimID), provider, keepLoaded);
		dim.setType(type);
		return dim;
	}

    
    private Dimension setDimIntID(int dimIntID)
    {
    	this.dimIntID=dimIntID;
    	dimensionTypeMap.put(dimIntID, type);
    	dimensionIntIDMap.put(dimIntID,dimID);
    	return this;    	
    }
}
