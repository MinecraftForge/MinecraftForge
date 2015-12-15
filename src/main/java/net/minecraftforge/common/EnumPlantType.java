package net.minecraftforge.common;

import java.util.Collection;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.EnumHelper;

public enum EnumPlantType
{
    /** Dirt and grass. */
    Plains,
    /** Sand, hardened clay and dirt. */
    Desert,
    /** Grass, dirt and sand next to water. */
    Beach,
    /** Blocks with solid tops. */
    Cave,
    /** Water. */
    Water,
    /** Soul sand. */
    Nether,
    /** Farmland. */
    Crop;

    /**
     * Getting a custom {@link EnumPlantType}, or an existing one if it has the same name as that one. Your plant should implement {@link IPlantable}
     * and return this custom type in {@link IPlantable#getPlantTypes(net.minecraft.world.IBlockAccess, net.minecraft.util.BlockPos)}.
     * 
     * <p>If your new plant grows on blocks like any one of them above, never create a new {@link EnumPlantType}. 
     * This enumeration is only functioning in 
     * {@link net.minecraft.block.Block#canSustainPlant(net.minecraft.world.IBlockAccess, net.minecraft.util.BlockPos, net.minecraft.util.EnumFacing, IPlantable)},
     * which you are supposed to override this function in your new block and create a new plant type to grow on that block.
     * 
     * <p>You can create an instance of your plant type in your API and let your/others mods access it. It will be faster than calling this method.
     * @param name the name of the type of plant, you had better follow the style above
     * @return the acquired {@EnumPlantType}, a new one if not found.
     */
    public static EnumPlantType getPlantType(String name) 
    {
        for (EnumPlantType t : values()) 
        { 
            if (t.name().equalsIgnoreCase(name)) return t;
        }
        return EnumHelper.addEnum(EnumPlantType.class, name, new Class[0], new Object[0]);
    }

    /**
     * Returns the default behavior for {@link IPlantable#canPlantStay} and {@link ISeed#canPlantStay}.
     * @param world The current world.
     * @param pos The position of the plant in the world.
     * @param plantSoils The collection of soil types for this plant.
     * @return Whether the soil has one of the soil types necessary to sustain the plant.
     */
    public static boolean canPlantStayDefault(IBlockAccess world, BlockPos pos, Collection<EnumPlantType> plantSoils)
    {
        BlockPos basePos = pos.down();
        Collection<EnumPlantType> baseSoils = world.getBlockState(basePos).getBlock().getSoilTypes(world, basePos);
        
        for (EnumPlantType plantSoil : plantSoils)
            if (baseSoils.contains(plantSoil))
                return true;
        
        return false;
    }

    /**
     * Returns the default behavior for {@link IPlantable#canPlantStay} and {@link ISeed#canPlantStay}.
     * @param world The current world.
     * @param pos The position of the plant in the world.
     * @param plant The {@link IPlantable} to check.
     * @param state The block state of the plant.
     * @return Whether the soil has one of the soil types necessary to sustain the plant.
     */
    public static boolean canPlantStayDefault(IBlockAccess world, BlockPos pos, IPlantable plant, IBlockState state)
    {
        return canPlantStayDefault(world, pos, plant.getSoilTypes(world, pos, state));
    }

    /**
     * Returns the default behavior for {@link IPlantable#canPlantStay} and {@link ISeed#canPlantStay}.
     * @param world The current world.
     * @param pos The position of the plant in the world.
     * @param seed The {@link ISeed} to check.
     * @param stack The stack of seeds.
     * @return Whether the soil has one of the soil types necessary to sustain the plant.
     */
    public static boolean canPlantStayDefault(IBlockAccess world, BlockPos pos, ISeed seed, ItemStack stack)
    {
        return canPlantStayDefault(world, pos, seed.getSoilTypes(stack));
    }
}
