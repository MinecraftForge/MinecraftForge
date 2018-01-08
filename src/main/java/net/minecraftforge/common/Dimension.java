package net.minecraftforge.common;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.DimensionType;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Dimension extends IForgeRegistryEntry.Impl<Dimension>
{
    private final DimensionType type;
    private int ticksWaited;
    public static final RegistryNamespaced<ResourceLocation, Dimension> REGISTRY = GameData.getWrapper(Dimension.class);
    
    public Dimension(DimensionType type)
    {
        this.type = type;
        this.ticksWaited = 0;
    }
    
    public static void registerDimensions()
    {
    	registerDimension(-1,"minecraft:nether", new Dimension(DimensionType.NETHER));
    	registerDimension(0,"minecraft:overworld", new Dimension(DimensionType.OVERWORLD));
    	registerDimension(1,"minecraft:the_end", new Dimension(DimensionType.THE_END));

    }
    /*DO NOT CALL THIS, CALL REGISTER DIMENSION IN DIMENSIONMANAGER INSTEAD*/
    public static void registerDimension(int intID, String id, Dimension dimension)
    {
    	REGISTRY.register(intID, new ResourceLocation(id), dimension);
    }
    
    public DimensionType getType()
    {
    	return type;
    }
    
    public int getTicksWaited()
    {
    	return ticksWaited;
    }
}
