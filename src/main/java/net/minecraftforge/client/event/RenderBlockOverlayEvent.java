package net.minecraftforge.client.event;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

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
    public final Block blockForOverlay;
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    
    public RenderBlockOverlayEvent(EntityPlayer player, float renderPartialTicks, OverlayType type, Block block, int blockX, int blockY, int blockZ)
    {
        this.player = player;
        this.renderPartialTicks = renderPartialTicks;
        this.overlayType = type;
        if (this.overlayType == OverlayType.BLOCK)
            this.blockForOverlay = block;
        else
            this.blockForOverlay = null;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        
    }

}
