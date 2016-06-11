package net.minecraftforge.fmp.item;

import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.MultipartHelper;

/**
 * {@link ItemMultipart} is an {@link Item} that can handle multipart placement.<br/>
 * Implement {@link ItemMultipart#place(World, BlockPos, EnumFacing, Vec3, ItemStack, EntityPlayer)} and optionally
 * override {@link ItemMultipart#consumeItem(ItemStack)} and {@link ItemMultipart#getPlacementSound(ItemStack)} to place
 * your part in the world.
 */
public abstract class ItemMultipart extends Item implements IItemMultipartFactory
{
    
    @Override
    public abstract IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player);

    public boolean place(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player)
    {
        if (!player.canPlayerEdit(pos, side, stack))
        {
            return false;
        }

        IMultipart part = createPart(world, pos, side, hit, stack, player);

        if (part != null && MultipartHelper.canAddPart(world, pos, part))
        {
            if (!world.isRemote)
            {
                MultipartHelper.addPart(world, pos, part);
            }
            consumeItem(stack);

            SoundType sound = getPlacementSound(stack);
            if (sound != null)
            {
                world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
            }

            return true;
        }

        return false;
    }

    protected void consumeItem(ItemStack stack)
    {
        stack.stackSize--;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side,
            float hitX, float hitY, float hitZ)
    {
        Vec3d hit = new Vec3d(hitX, hitY, hitZ);
        double depth = ((hit.xCoord * 2 - 1) * side.getFrontOffsetX() + (hit.yCoord * 2 - 1) * side.getFrontOffsetY()
                + (hit.zCoord * 2 - 1) * side.getFrontOffsetZ());
        if (depth < 1 && place(world, pos, side, hit, stack, player))
        {
            return EnumActionResult.SUCCESS;
        }
        if (place(world, pos.offset(side), side.getOpposite(), hit, stack, player))
        {
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public SoundType getPlacementSound(ItemStack stack)
    {
        return SoundType.GLASS;
    }

    @Override
    public boolean canItemEditBlocks()
    {
        return true;
    }
    
    /**
     * Wrapper for {@link ItemMultipart} which takes an {@link IItemMultipartFactory} as an argument. Handy when working with lambdas.
     */
    public static class Simple extends ItemMultipart {

        private final IItemMultipartFactory factory;
        
        public Simple(IItemMultipartFactory factory)
        {
            this.factory = factory;
        }
        
        @Override
        public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player)
        {
            return factory.createPart(world, pos, side, hit, stack, player);
        }
        
    }

}
