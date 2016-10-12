package net.minecraftforge.fmp.item;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.RayTraceUtils;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.MultipartHelper;
import net.minecraftforge.fmp.network.MessageWrappedPartPlacement;

/**
 * Object that handles the placement (as multiparts) of things that don't have a special handler for them. This would be the case
 * of a block that can either be standalone (a block in itself) or a multipart.<br/>
 * Extended by {@link MicroContainerPlacementWrapper} to handle the placement of microblock containers.
 * 
 * @see IItemMultipartFactory
 * @see MicroContainerPlacementWrapper
 */
public class PartPlacementWrapper
{
    
    private static final Map<String, PartPlacementWrapper> wrappers = new HashMap<String, PartPlacementWrapper>();

    public static PartPlacementWrapper getWrapper(String handler)
    {
        return wrappers.get(handler);
    }

    protected final Predicate<ItemStack> match;
    protected final IItemMultipartFactory factory;
    private String identifier;

    public PartPlacementWrapper(Predicate<ItemStack> match, IItemMultipartFactory factory)
    {
        this.match = match;
        this.factory = factory;
    }

    public PartPlacementWrapper(final ItemStack match, IItemMultipartFactory factory)
    {
        this(new Predicate<ItemStack>()
        {
            @Override
            public boolean apply(ItemStack input)
            {
                return input.isItemEqual(match);
            }
        }, factory);
    }

    public void register(String identifier)
    {
        wrappers.put(this.identifier = identifier, this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected boolean place(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player)
    {
        if (!player.canPlayerEdit(pos, side, stack))
            return false;

        IMultipart part = factory.createPart(world, pos, side, hit, stack, player);
        if (part != null && MultipartHelper.canAddPart(world, pos, part))
        {
            if (!world.isRemote)
            {
                MultipartHelper.addPart(world, pos, part);
            }
            if (!player.capabilities.isCreativeMode)
            {
                consumeItem(stack);
            }
            playPlacementSound(world, pos, stack, player);
            return true;
        }
        return false;
    }

    protected boolean placeDefault(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        Block placedBlock = Block.getBlockFromItem(stack.getItem());
        if (placedBlock == null)
        {
            throw new IllegalStateException("For non-ItemBlocks you need to write your own default placement handler!");
        }

        if (!block.isReplaceable(world, pos))
        {
            return false;
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (world.canBlockBePlaced(placedBlock, pos, false, side, (Entity) null, stack))
        {
            if (world.isRemote)
            {
                return true;
            }

            int i = stack.getItem().getMetadata(stack.getMetadata());
            IBlockState iblockstate1 = placedBlock.onBlockPlaced(world, pos, side, (float) hit.xCoord, (float) hit.yCoord,
                    (float) hit.zCoord, i, player);

            if (((ItemBlock) stack.getItem()).placeBlockAt(stack, player, world, pos, side, (float) hit.xCoord, (float) hit.yCoord,
                    (float) hit.zCoord, iblockstate1))
            {
                playPlacementSound(world, pos, stack, player);
                if (!player.capabilities.isCreativeMode)
                {
                    consumeItem(stack);
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected void consumeItem(ItemStack stack)
    {
        stack.stackSize--;
    }

    protected void playPlacementSound(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        Block placedBlock = Block.getBlockFromItem(stack.getItem());
        if (placedBlock != null)
        {
            world.playSound(player, pos, placedBlock.getSoundType().getPlaceSound(), SoundCategory.BLOCKS,
                    (placedBlock.getSoundType().getVolume() + 1.0F) / 2.0F, placedBlock.getSoundType().getPitch() * 0.8F);
        }

        SoundType sound = getPlacementSound(stack);
        if (sound != null)
        {
            world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F,
                    sound.getPitch() * 0.8F);
        }
    }

    protected SoundType getPlacementSound(ItemStack stack)
    {
        return SoundType.GLASS;
    }

    protected boolean isValidPlacement(World world, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        EnumHand hand = event.getHand();
        if (hand == null)
        {
            return;
        }
        ItemStack stack = event.getEntityPlayer().getHeldItem(hand);
        if (stack == null || !match.apply(stack))
        {
            return;
        }

        event.setCanceled(true);

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing side = event.getFace();
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult mop = world.rayTraceBlocks(RayTraceUtils.getStart(player), RayTraceUtils.getEnd(player));
        Vec3d hit = mop.hitVec.subtract(new Vec3d(mop.getBlockPos()));

        if (doPlace(world, pos, side, hit, stack, player))
        {
            player.swingArm(hand);
            if (world.isRemote)
            {
                new MessageWrappedPartPlacement(identifier, hand).send();
            }
        }
    }

    public boolean doPlace(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player)
    {
        if (doPlaceAt(world, pos, side, hit, stack, player))
        {
            return true;
        }
        pos = pos.offset(side);
        return doPlaceAt(world, pos, side, hit, stack, player);
    }

    private boolean doPlaceAt(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (block.isReplaceable(world, pos) && placeDefault(world, pos, side, hit, stack, player))
        {
            return true;
        }
        return isValidPlacement(world, pos, side) && place(world, pos, side, hit, stack, player);
    }

}
