package net.minecraftforge.fmp.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fmp.multipart.IMultipart;

/**
 * Creates a new {@link IMultipart} from some placement data. Used by {@link ItemMultipart.Simple} and {@link PartPlacementWrapper}.
 * 
 * @see ItemMultipart.Simple
 * @see PartPlacementWrapper
 */
public interface IItemMultipartFactory
{
    
    /**
     * Creates a part from the placement data.
     */
    public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player);

}