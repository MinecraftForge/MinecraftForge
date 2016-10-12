package net.minecraftforge.fmp.client.microblock;

import java.util.EnumSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fmp.microblock.IMicroMaterial;

/**
 * Interface that allows you to provide fully custom quads for a specific material.
 */
public interface IMicroModelProvider
{
    
    /**
     * Gets an {@link IBakedModel} with the quads for the specified material, with the specified size.
     */
    public IBakedModel provideMicroModel(IMicroMaterial material, AxisAlignedBB bounds, EnumSet<EnumFacing> hiddenFaces);

}
