
package net.minecraftforge.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.DimensionType;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Dimension extends IForgeRegistryEntry.Impl<Dimension>
{
    private final DimensionType type;
    private final ResourceLocation dimID;
    private int ticksWaited;
    public static final RegistryNamespaced<ResourceLocation, Dimension> REGISTRY = GameData.getWrapper(Dimension.class);
    
    public Dimension(DimensionType type, String dimensionName)
    {
        this.type = type;
        this.ticksWaited = 0;
        dimID = this.setRegistryName(dimensionName).getRegistryName();
        
    }
    
    public static void registerDimensions()
    {
    	registerDimension(new Dimension(DimensionType.NETHER, "minecraft:nether"));
    	registerDimension(new Dimension(DimensionType.OVERWORLD, "minecraft:overworld"));
    	registerDimension(new Dimension(DimensionType.THE_END, "minecraft:the_end"));

    }

    public static void registerDimension(Dimension dimension)
    {
    	REGISTRY.register(dimension.hashCode(), dimension.dimID, dimension);
    }
    
    public DimensionType getType()
    {
    	return type;
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
    
    public int hashCode(ResourceLocation dimID)
    {
    	if(dimID.toString().equals("minecraft:nether"))
    	{
    		return -1;
    	}
    	else if(dimID.toString().equals("minecraft:overworld"))
    	{
    		return 0;
    	}
    	else if(dimID.toString().equals("minecraft:the_end"))
    	{
    		return 1;
    	}
    	else
    	{
    		return dimID.hashCode();
    	}
    	
    }
    
    /*Mods shouldn't call this*/
    public int getDimIntID()
    {
    	return hashCode(this.dimID);
    }
}
