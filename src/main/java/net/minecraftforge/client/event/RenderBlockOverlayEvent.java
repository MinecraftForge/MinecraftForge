package net.minecraftforge.client.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when a block's texture is going to be overlaid on the player's HUD. Cancel this event to prevent the overlay.
 */
@Cancelable
public class RenderBlockOverlayEvent extends Event {
    
    public static enum OverlayType {
        FIRE, BLOCK, WATER
    }
    
    /**
     * The player which the overlay will apply to
     */
    public final EntityPlayer player;
    public final float renderPartialTicks;
    /**
     * The type of overlay to occur
     */
    public final OverlayType overlayType;
    /**
     * If the overlay type is BLOCK, then this is the block which the overlay is getting it's icon from
     */
    public final IBlockState blockForOverlay;
    public final BlockPos blockPos;
    
    @Deprecated
    public RenderBlockOverlayEvent(EntityPlayer player, float renderPartialTicks, OverlayType type, Block block, int x, int y, int z)
    {
        this(player, renderPartialTicks, type, block.getDefaultState(), new BlockPos(x, y, z));
    }
    
    public RenderBlockOverlayEvent(EntityPlayer player, float renderPartialTicks, OverlayType type, IBlockState block, BlockPos blockPos)
    {
        this.player = player;
        this.renderPartialTicks = renderPartialTicks;
        this.overlayType = type;
        this.blockForOverlay = block;
        this.blockPos = blockPos;
        
    }

}
