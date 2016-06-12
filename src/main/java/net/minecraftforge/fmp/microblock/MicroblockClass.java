package net.minecraftforge.fmp.microblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fmp.multipart.IPartFactory.IAdvancedPartFactory;

/**
 * Class that represents a type of microblock, such as a face microblock (cover, panel...) or a fence.
 */
public abstract class MicroblockClass implements IAdvancedPartFactory
{
    
    /**
     * Gets a unique identifier for this microblock class. Used as the multipart ID for the microblock.
     */
    public abstract ResourceLocation getType();

    /**
     * Gets the localized name of a microblock made of the specified material.
     */
    public abstract String getLocalizedName(IMicroMaterial material, int size);

    /**
     * Creates a stack with the specified material, size and stacksize.
     */
    public abstract ItemStack createStack(IMicroMaterial material, int size, int stackSize);

    /**
     * Gets a placement handler for a microblock of this type.
     */
    public abstract MicroblockPlacement getPlacement(World world, BlockPos pos, IMicroMaterial material, int size, RayTraceResult hit,
            EntityPlayer player);

    /**
     * Gets the placement grid of this microblock type.
     */
    public abstract IMicroblockPlacementGrid getPlacementGrid();

    /**
     * Creates a new instance of the microblock. For more advanced functionality, override
     * {@link MicroblockClass#createPart(ResourceLocation, PacketBuffer)} and
     * {@link MicroblockClass#createPart(ResourceLocation, NBTTagCompound)}.
     */
    public abstract IMicroblock create(boolean client);

    @Override
    public IMicroblock createPart(ResourceLocation type, PacketBuffer buf)
    {
        if (type.equals(getType()))
        {
            IMicroblock part = create(false);
            part.readUpdatePacket(buf);
            return part;
        }
        return null;
    }

    @Override
    public IMicroblock createPart(ResourceLocation type, NBTTagCompound tag)
    {
        if (type.equals(getType()))
        {
            IMicroblock part = create(false);
            part.readFromNBT(tag);
            return part;
        }
        return null;
    }

}
