package net.minecraftforge.fmp.multipart;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fmp.capabilities.ISlottedCapabilityProvider;

/**
 * Enumeration of all the slots a multipart can occupy inside a block.
 *
 * @see IMultipart
 * @see ISlottedPart
 * @see ISlotOccludingPart
 * @see ISlottedCapabilityProvider
 * @see IMultipartContainer
 */
public enum PartSlot implements IStringSerializable
{
    
    DOWN(EnumFacing.DOWN),
    UP(EnumFacing.UP),
    NORTH(EnumFacing.NORTH),
    SOUTH(EnumFacing.SOUTH),
    WEST(EnumFacing.WEST),
    EAST(EnumFacing.EAST),
    CENTER(null),
    EDGE_XNN(EnumFacing.DOWN, EnumFacing.NORTH),
    EDGE_XNP(EnumFacing.DOWN, EnumFacing.SOUTH),
    EDGE_XPN(EnumFacing.UP, EnumFacing.NORTH),
    EDGE_XPP(EnumFacing.UP, EnumFacing.SOUTH),
    EDGE_NYN(EnumFacing.WEST, EnumFacing.NORTH),
    EDGE_NYP(EnumFacing.WEST, EnumFacing.SOUTH),
    EDGE_PYN(EnumFacing.EAST, EnumFacing.NORTH),
    EDGE_PYP(EnumFacing.EAST, EnumFacing.SOUTH),
    EDGE_NNZ(EnumFacing.WEST, EnumFacing.DOWN),
    EDGE_NPZ(EnumFacing.WEST, EnumFacing.UP),
    EDGE_PNZ(EnumFacing.EAST, EnumFacing.DOWN),
    EDGE_PPZ(EnumFacing.EAST, EnumFacing.UP),
    CORNER_NNN(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.NORTH),
    CORNER_NNP(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.SOUTH),
    CORNER_NPN(EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH),
    CORNER_NPP(EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH),
    CORNER_PNN(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.NORTH),
    CORNER_PNP(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.SOUTH),
    CORNER_PPN(EnumFacing.EAST, EnumFacing.UP, EnumFacing.NORTH),
    CORNER_PPP(EnumFacing.EAST, EnumFacing.UP, EnumFacing.SOUTH);

    public static final PartSlot[] VALUES = values();
    public static final PartSlot[] FACES = { DOWN, UP, NORTH, SOUTH, WEST, EAST };
    public static final PartSlot[] EDGES = { EDGE_XNN, EDGE_XNP, EDGE_XPN, EDGE_XPP, EDGE_NYN, EDGE_NYP, EDGE_PYN, EDGE_PYP,
            EDGE_NNZ, EDGE_NPZ, EDGE_PNZ, EDGE_PPZ };
    public static final PartSlot[] CORNERS = { CORNER_NNN, CORNER_NNP, CORNER_NPN, CORNER_NPP, CORNER_PNN, CORNER_PNP,
            CORNER_PPN, CORNER_PPP };

    public static PartSlot getFaceSlot(EnumFacing facing)
    {
        if (facing == null)
        {
            return CENTER;
        }
        return VALUES[facing.ordinal()];
    }

    public static PartSlot getEdgeSlot(EnumFacing facing1, EnumFacing facing2)
    {
        if (facing1 == null || facing2 == null)
            return null;
        if (facing1 == facing2 || facing1.getOpposite() == facing2)
        {
            throw new IllegalArgumentException("Tried to form an illegal edge between " + facing1 + " and " + facing2);
        }

        int x = facing1.getFrontOffsetX() + facing2.getFrontOffsetX();
        int y = facing1.getFrontOffsetY() + facing2.getFrontOffsetY();
        int z = facing1.getFrontOffsetZ() + facing2.getFrontOffsetZ();

        int edge = 0;
        if (x == 0)
        {
            edge = 0x0 + (y > 0 ? 0x2 : 0x0) + (z > 0 ? 0x1 : 0x0);
        }
        else if (y == 0)
        {
            edge = 0x4 + (x > 0 ? 0x2 : 0x0) + (z > 0 ? 0x1 : 0x0);
        }
        else if (z == 0)
        {
            edge = 0x8 + (x > 0 ? 0x2 : 0x0) + (y > 0 ? 0x1 : 0x0);
        }
        return EDGES[edge];
    }

    public static PartSlot getCornerSlot(EnumFacing facing1, EnumFacing facing2, EnumFacing facing3)
    {
        if (facing1 == null || facing2 == null || facing3 == null)
            return null;
        if (facing1 == facing2 || facing1.getOpposite() == facing2)
        {
            throw new IllegalArgumentException("Tried to form an illegal corner between " + facing1 + " and " + facing2 + " " + facing3);
        }
        if (facing2 == facing3 || facing2.getOpposite() == facing3)
        {
            throw new IllegalArgumentException("Tried to form an illegal corner between " + facing1 + " and " + facing2 + " " + facing3);
        }
        if (facing1 == facing3 || facing1.getOpposite() == facing3)
        {
            throw new IllegalArgumentException("Tried to form an illegal corner between " + facing1 + " and " + facing2 + " " + facing3);
        }

        int x = (facing1.getFrontOffsetX() + facing2.getFrontOffsetX() + facing3.getFrontOffsetX() + 1) / 2;
        int y = (facing1.getFrontOffsetY() + facing2.getFrontOffsetY() + facing3.getFrontOffsetY() + 1) / 2;
        int z = (facing1.getFrontOffsetZ() + facing2.getFrontOffsetZ() + facing3.getFrontOffsetZ() + 1) / 2;

        return CORNERS[(x << 2) | (y << 1) | (z << 0)];
    }

    public final EnumFacing f1, f2, f3;

    private PartSlot(EnumFacing f1)
    {
        this.f1 = f1;
        this.f2 = this.f3 = null;
    }

    private PartSlot(EnumFacing f1, EnumFacing f2)
    {
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = null;
    }

    private PartSlot(EnumFacing f1, EnumFacing f2, EnumFacing f3)
    {
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
    }

    public String getUnlocalizedName()
    {
        return "partslot." + name().toLowerCase();
    }

    public String getLocalizedName()
    {
        return I18n.translateToLocal(getUnlocalizedName());
    }

    @Override
    public String getName()
    {
        return getLocalizedName();
    }

    public boolean matches(EnumFacing f1)
    {
        return this.f1 == f1 || this.f2 == f1 || this.f3 == f1;
    }

    public boolean matches(EnumFacing f1, EnumFacing f2)
    {
        return (this.f1 == f1 && this.f2 == f2) || (this.f1 == f2 && this.f2 == f1) || (this.f1 == f1 && this.f3 == f2)
                || (this.f1 == f2 && this.f3 == f1) || (this.f2 == f1 && this.f3 == f2) || (this.f2 == f2 && this.f3 == f1);
    }

    public boolean matches(EnumFacing f1, EnumFacing f2, EnumFacing f3)
    {
        return (this.f1 == f1 && this.f2 == f2 && this.f3 == f3) || (this.f1 == f2 && this.f2 == f1 && this.f3 == f3)
                || (this.f1 == f1 && this.f2 == f3 && this.f3 == f2) || (this.f1 == f3 && this.f2 == f2 && this.f3 == f1);
    }

    public PartSlot getOpposite(Axis axis)
    {
        if (f2 != null)
        {
            if (f3 != null)
            {
                return getCornerSlot(f1.getAxis() == axis ? f1.getOpposite() : f1,
                    f2.getAxis() == axis ? f2.getOpposite() : f2, f3.getAxis() == axis ? f3.getOpposite() : f3);
            }
            return getEdgeSlot(f1.getAxis() == axis ? f1.getOpposite() : f1, f2.getAxis() == axis ? f2.getOpposite() : f2);
        }
        if (f1 == null)
        {
            return CENTER;
        }
        return getFaceSlot(f1.getAxis() == axis ? f1.getOpposite() : f1);
    }

}
