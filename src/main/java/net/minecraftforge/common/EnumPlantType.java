package net.minecraftforge.common;

import net.minecraftforge.common.util.EnumHelper;

public enum EnumPlantType
{
    Plains,
    Desert,
    Beach,
    Cave,
    Water,
    Nether,
    Crop;

    /**
     * Getting a custom {@link EnumPlantType}, or an existing one if it has the same name as that one. Your plant should implement {@link IPlantable}
     * and return this custom type in {@link IPlantable#getPlantType(net.minecraft.world.IBlockAccess, net.minecraft.util.BlockPos)}.
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
}
