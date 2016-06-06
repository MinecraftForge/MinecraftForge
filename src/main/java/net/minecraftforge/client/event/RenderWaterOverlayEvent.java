package net.minecraftforge.client.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when fluid is rendered. It is used to get rid of the flowing water texture on custom glass or transparent blocks.
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 *
 * {@link #blockAccess} is the blockAccess this block is from.
 * {@link #pos} is the position of the block being rendered.
 * {@link #state} is the blockstate of the block being rendered.
 * {@link #renderOverlay} is whether the water overlay should be rendered (fixes flowing water texture on glass) or not.
 */
public class RenderWaterOverlayEvent extends Event
{
    private final IBlockAccess blockAccess;
    private final BlockPos pos;
    private final IBlockState state;

    private boolean renderOverlay;

    public RenderWaterOverlayEvent(IBlockAccess blockAccess, BlockPos pos, IBlockState state, boolean renderOverlay)
    {
        this.blockAccess = blockAccess;
        this.pos = pos;
        this.state = state;
        this.renderOverlay = renderOverlay;
    }

    public IBlockAccess getBlockAccess()
    {
        return blockAccess;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public IBlockState getState()
    {
        return state;
    }

    public boolean getRenderOverlay()
    {
        return renderOverlay;
    }

    public void setRenderOverlay(boolean renderOverlay)
    {
        this.renderOverlay = renderOverlay;
    }
}
