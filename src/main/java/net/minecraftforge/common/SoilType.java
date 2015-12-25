package net.minecraftforge.common;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;

public class SoilType implements IStringSerializable
{
    private static final Map<String, SoilType> SOIL_TYPES = Maps.newHashMap();
    
    /** Dirt and grass. */
    public static final SoilType Plains = getType("Plains");
    /** Sand, hardened clay and dirt. */
    public static final SoilType Desert = getType("Desert");
    /** Grass, dirt and sand next to water. */
    public static final SoilType Beach = getType("Beach");
    /** Blocks with solid tops. */
    public static final SoilType Cave = getType("Cave");
    /** Water. */
    public static final SoilType Water = getType("Water");
    /** Soul sand. */
    public static final SoilType Nether = getType("Nether");
    /** Farmland. */
    public static final SoilType Crop = getType("Crop");
    
    private final String name;
    
    private SoilType(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return getName();
    }

    /**
     * Getting a custom {@link SoilType}, or an existing one if it has the same name as that one.
     * 
     * <p>If your new plant grows on blocks like any one of them above, never create a new {@link SoilType}. 
     * 
     * <p>Your plant should implement {@link IPlant}
     * and return this custom type in the sets returned by
     * {@link IPlant#getPlantTypes(net.minecraft.world.IBlockAccess, net.minecraft.util.BlockPos) IPlantable.getPlantTypes} and
     * {@link ISeed#getPlantTypes(ItemStack) ISeed.getPlantTypes}.
     * 
     * <p>Your soil should override
     * {@link net.minecraft.block.Block#getSoilTypes(IBlockAccess, BlockPos) Block.getSoilTypes},
     * returning a Set of {@link SoilType}s that represent the soil.
     * 
     * <p>You can create an instance of your plant type in your API and let your/others mods access it. It will be faster than calling this method.
     * @param name the name of the type of plant, you had better follow the style above
     * @return the acquired {@link SoilType}, a new one if not found.
     */
    public static SoilType getType(String name) 
    {
        if (!SOIL_TYPES.containsKey(name))
            SOIL_TYPES.put(name, new SoilType(name));
        return SOIL_TYPES.get(name);
    }

    /**
     * Returns the default behavior for {@link IPlant#canPlantStay} and {@link ISeed#canPlantStay}.
     * @param world The current world.
     * @param pos The position of the plant in the world.
     * @param plantSoils The collection of soil types for this plant.
     * @return Whether the soil has one of the soil types necessary to sustain the plant.
     */
    public static boolean canPlantStayDefault(IBlockAccess world, BlockPos pos, SoilType... plantSoils)
    {
        BlockPos basePos = pos.down();
        Collection<SoilType> baseSoils = world.getBlockState(basePos).getBlock().getSoilTypes(world, basePos);
        
        for (SoilType plantSoil : plantSoils)
            if (baseSoils.contains(plantSoil))
                return true;
        
        return false;
    }
}
