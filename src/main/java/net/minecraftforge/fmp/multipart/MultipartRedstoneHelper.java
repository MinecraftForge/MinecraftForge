package net.minecraftforge.fmp.multipart;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fmp.multipart.IRedstonePart.ISlottedRedstonePart;

/**
 * A general use multipart redstone helper, with getters for redstone signals and connection checking.
 *
 * @see IMultipart
 * @see IRedstonePart
 * @see ISlottedRedstonePart
 * @see IMultipartContainer
 */
public class MultipartRedstoneHelper
{
    
    public static boolean canConnectRedstone(IMultipartContainer container, EnumFacing side)
    {
        for (EnumFacing face : EnumFacing.VALUES)
        {
            if (face != side && face != side.getOpposite() && canConnectRedstone(container, side, face))
            {
                return true;
            }
        }
        return false;
    }

    public static int getWeakSignal(IMultipartContainer container, EnumFacing side)
    {
        int max = 0;
        for (EnumFacing face : EnumFacing.VALUES)
        {
            if (face != side && face != side.getOpposite())
            {
                max = Math.max(max, getWeakSignal(container, side, face));
            }
        }
        return max;
    }

    public static int getStrongSignal(IMultipartContainer container, EnumFacing side)
    {
        int max = 0;
        for (EnumFacing face : EnumFacing.VALUES)
        {
            if (face != side && face != side.getOpposite())
            {
                max = Math.max(max, getStrongSignal(container, side, face));
            }
        }
        return max;
    }

    public static boolean canConnectRedstone(IMultipartContainer container, EnumFacing side, EnumFacing face)
    {
        if (container == null)
            return false;

        IMultipart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).canConnectRedstone(side) : false;
        }
        part = container.getPartInSlot(PartSlot.getEdgeSlot(side, face));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).canConnectRedstone(side) : false;
        }
        part = container.getPartInSlot(PartSlot.getFaceSlot(face));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).canConnectRedstone(side) : false;
        }

        if (face == null)
        {
            part = container.getPartInSlot(PartSlot.CENTER);
            if (part instanceof IRedstonePart)
            {
                return ((IRedstonePart) part).canConnectRedstone(side);
            }
        }

        for (IMultipart p : container.getParts())
        {
            if ((!(p instanceof ISlottedRedstonePart) || ((ISlottedRedstonePart) p).getSlotMask().isEmpty()) && p instanceof IRedstonePart)
            {
                return ((IRedstonePart) p).canConnectRedstone(side);
            }
        }

        return false;
    }

    public static int getWeakSignal(IMultipartContainer container, EnumFacing side, EnumFacing face)
    {
        if (container == null)
        {
            return 0;
        }

        IMultipart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).getWeakSignal(side) : 0;
        }
        part = container.getPartInSlot(PartSlot.getEdgeSlot(side, face));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).getWeakSignal(side) : 0;
        }
        part = container.getPartInSlot(PartSlot.getFaceSlot(face));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).getWeakSignal(side) : 0;
        }

        int max = 0;

        if (face == null)
        {
            part = container.getPartInSlot(PartSlot.CENTER);
            if (part instanceof IRedstonePart)
            {
                max = ((IRedstonePart) part).getWeakSignal(side);
            }
        }

        for (IMultipart p : container.getParts())
        {
            if ((!(p instanceof ISlottedRedstonePart) || ((ISlottedRedstonePart) p).getSlotMask().isEmpty()) && p instanceof IRedstonePart)
            {
                max = Math.max(max, ((IRedstonePart) p).getWeakSignal(side));
            }
        }

        return max;
    }

    public static int getStrongSignal(IMultipartContainer container, EnumFacing side, EnumFacing face)
    {
        if (container == null)
        {
            return 0;
        }

        IMultipart part = container.getPartInSlot(PartSlot.getFaceSlot(side));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).getStrongSignal(side) : 0;
        }
        part = container.getPartInSlot(PartSlot.getEdgeSlot(side, face));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).getStrongSignal(side) : 0;
        }
        part = container.getPartInSlot(PartSlot.getFaceSlot(face));
        if (part != null)
        {
            return part instanceof IRedstonePart ? ((IRedstonePart) part).getStrongSignal(side) : 0;
        }

        int max = 0;

        if (face == null)
        {
            part = container.getPartInSlot(PartSlot.CENTER);
            if (part instanceof IRedstonePart)
            {
                max = ((IRedstonePart) part).getStrongSignal(side);
            }
        }

        for (IMultipart p : container.getParts())
        {
            if ((!(p instanceof ISlottedRedstonePart) || ((ISlottedRedstonePart) p).getSlotMask().isEmpty()) && p instanceof IRedstonePart)
            {
                max = Math.max(max, ((IRedstonePart) p).getStrongSignal(side));
            }
        }

        return max;
    }

}
