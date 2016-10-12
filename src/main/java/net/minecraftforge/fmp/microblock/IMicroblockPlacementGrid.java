package net.minecraftforge.fmp.microblock;

import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents a grid for {@link IMicroblock} placement.
 *
 * @see IMicroblock
 * @see MicroblockClass
 */
@SideOnly(Side.CLIENT)
public interface IMicroblockPlacementGrid
{
    
    public void renderGrid();

    public void glTransform(RayTraceResult hit);

}
