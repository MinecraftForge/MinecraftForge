package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebugInfoEvent extends Event 
{
    public Minecraft mc;
    
    public DebugInfoEvent(Minecraft mc)
    {
        this.mc = mc;
    }

    /**
     * This event fires when toggling {@code Minecraft.gameSettings.showDebugInfo}.
     * Cancel this event to prevent {@code Minecraft.gameSettings.showDebugInfo} from being set to true.
     *
     * @author bspkrs
     */
    @Cancelable
    public static class ToggleShowDebugInfoEvent extends DebugInfoEvent
    {
        public ToggleShowDebugInfoEvent(Minecraft mc)
        {
            super(mc);
        }
    }

    /**
     * This event fires when toggling {@code Minecraft.gameSettings.showDebugProfilerChart}.
     * Cancel this event to prevent {@code Minecraft.gameSettings.showDebugProfilerChart} from being set to true.
     *
     * @author bspkrs
     */
    @Cancelable
    public static class ToggleShowDebugProfilerChartEvent extends DebugInfoEvent
    {
        public ToggleShowDebugProfilerChartEvent(Minecraft mc)
        {
            super(mc);
        }
    }
    
    /**
     * This event fires when the textual debug info is about to be displayed.
     * Cancel this event to override the debug info or to prevent it from showing. 
     * 
     * @author bspkrs
     */
    @Cancelable
    public static class DisplayDebugInfoEvent extends DebugInfoEvent
    {
        public DisplayDebugInfoEvent(Minecraft mc)
        {
            super(mc);
        }
    }
    
    /**
     * This event fires when the graphical profiler info is about to be displayed.
     * Cancel this event to override the profiler info or to prevent it from showing.
     * 
     * @author bspkrs
     */
    @Cancelable
    public static class DisplayDebugProfilerChartEvent extends DebugInfoEvent
    {
        public DisplayDebugProfilerChartEvent(Minecraft mc)
        {
            super(mc);
        }
    }
}
