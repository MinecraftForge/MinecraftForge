package net.minecraftforge.fmp.multipart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.block.BlockMultipartContainer;
import net.minecraftforge.fmp.block.TileMultipartContainer;
import net.minecraftforge.fmp.capabilities.CapabilityMultipartContainer;

/**
 * A general use multipart helper with methods for part addition and placement checking.
 *
 * @see IMultipart
 * @see Multipart
 * @see IMultipartContainer
 */
public class MultipartHelper
{

    /**
     * Checks whether or not the specified part can be added to the world.
     */
    public static boolean canAddPart(World world, BlockPos pos, IMultipart part)
    {
        IMultipartContainer container = getPartContainer(world, pos);
        if (container == null)
        {
            List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
            part.addCollisionBoxes(new AxisAlignedBB(0, 0, 0, 1, 1, 1), list, null);
            for (AxisAlignedBB bb : list)
            {
                if (!world.checkNoEntityCollision(bb.offset(pos.getX(), pos.getY(), pos.getZ())))
                {
                    return false;
                }
            }

            Collection<? extends IMultipart> parts = MultipartRegistry.convert(world, pos, true);
            if (parts != null && !parts.isEmpty())
            {
                TileMultipartContainer tmp = new TileMultipartContainer();
                for (IMultipart p : parts)
                {
                    tmp.getPartContainer().addPart(p, false, false, false, false, UUID.randomUUID());
                }
                return tmp.getPartContainer().canAddPart(part);
            }

            return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
        }
        return container.canAddPart(part);
    }

    /**
     * Checks whether or not the specified part can be replaced by another part to the world.
     */
    public static boolean canReplacePart(World world, BlockPos pos, IMultipart oldPart, IMultipart newPart)
    {
        IMultipartContainer container = getPartContainer(world, pos);
        if (container != null)
        {
            return container.canReplacePart(oldPart, newPart);
        }
        return false;
    }

    /**
     * Checks whether or not a part of the specified type can be replaced by another part to the world.
     */
    public static boolean canReplacePart(World world, BlockPos pos, String oldType, IMultipart newPart)
    {
        IMultipartContainer container = getPartContainer(world, pos);
        if (container != null)
        {
            IMultipart oldPart = null;
            for (IMultipart part : container.getParts())
            {
                if (part.getType().equals(oldType))
                {
                    oldPart = part;
                    break;
                }
            }
            return container.canReplacePart(oldPart, newPart);
        }
        return false;
    }

    /**
     * Adds a part at the specified location in the world.
     */
    public static void addPart(World world, BlockPos pos, IMultipart part)
    {
        addPart(world, pos, part, null);
    }

    /**
     * Adds a part at the specified location in the world, with the provided UUID.
     */
    public static void addPart(World world, BlockPos pos, IMultipart part, UUID id)
    {
        IMultipartContainer container = world.isRemote ? getPartContainer(world, pos) : getOrConvertPartContainer(world, pos, true);
        boolean newContainer = container == null;
        if (newContainer)
        {
            world.setBlockState(pos, ForgeMultipartModContainer.multipart.getDefaultState()
                    .withProperty(BlockMultipartContainer.PROPERTY_TICKING, part instanceof ITickable));
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileMultipartContainer)
            {
                container = ((TileMultipartContainer) te).getPartContainer();
            }
            else
            {
                world.removeTileEntity(pos);
                world.setTileEntity(pos, te = new TileMultipartContainer());
                container = ((TileMultipartContainer) te).getPartContainer();
            }
        }
        if (container.getPartFromID(id) != null)
        {
            return;
        }
        part.setContainer(container);
        if (id != null)
        {
            container.addPart(id, part);
        }
        else
        {
            container.addPart(part);
        }
        if (newContainer)
        {
            world.notifyLightSet(pos);
        }
    }

    /**
     * Checks if a part can be added. If so, it adds it to the world.
     */
    public static boolean addPartIfPossible(World world, BlockPos pos, IMultipart part)
    {
        if (!canAddPart(world, pos, part))
        {
            return false;
        }
        addPart(world, pos, part);
        return true;
    }

    /**
     * Gets the part container at the specified position. Doesn't handle block conversion.
     */
    public static IMultipartContainer getPartContainer(IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te.hasCapability(CapabilityMultipartContainer.MULTIPART_CONTAINER_CAPABILITY, null))
        {
            return te.getCapability(CapabilityMultipartContainer.MULTIPART_CONTAINER_CAPABILITY, null);
        }
        return null;
    }

    /**
     * Gets the part container at the specified position. Handles block conversion. If doConvert is true, the block is
     * converted into a multipart container. If not, it returns a dummy container with all the converted parts.
     */
    public static IMultipartContainer getOrConvertPartContainer(World world, BlockPos pos, boolean doConvert)
    {
        IMultipartContainer container = getPartContainer(world, pos);
        if (container != null)
        {
            return container;
        }

        Collection<? extends IMultipart> parts = MultipartRegistry.convert(world, pos, !doConvert);
        if (parts == null || parts.isEmpty())
        {
            return null;
        }

        if (doConvert)
        {
            TileEntity oldTile = world.getTileEntity(pos);
            world.setBlockState(pos, ForgeMultipartModContainer.multipart.getDefaultState());
            TileEntity tile = world.getTileEntity(pos);
            TileMultipartContainer te = null;
            if (tile == null || !(tile instanceof TileMultipartContainer))
            {
                world.setTileEntity(pos, te = new TileMultipartContainer());
            }
            else
            {
                te = (TileMultipartContainer) tile;
            }

            for (IMultipart part : parts)
            {
                te.getPartContainer().addPart(part, false, false, false, false, UUID.randomUUID());
            }
            for (IMultipart part : parts)
            {
                part.onConverted(oldTile);
            }

            return te.getPartContainer();
        }
        else
        {
            TileMultipartContainer te = new TileMultipartContainer();

            for (IMultipart part : parts)
            {
                te.getPartContainer().addPart(part, false, false, false, false, UUID.randomUUID());
            }

            return te.getPartContainer();
        }
    }

}
