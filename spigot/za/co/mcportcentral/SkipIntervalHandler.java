package za.co.mcportcentral;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.server.MinecraftServer;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class SkipIntervalHandler implements IScheduledTickHandler
{
    public static ArrayList<SkipIntervalHandler> handlers = new ArrayList<SkipIntervalHandler>();
    
    private ITickHandler wrapped;
    public String configString;
    public SkipIntervalHandler(ITickHandler handler)
    {
        this.wrapped=handler;
        configString = "tick-intervals." + handler.getClass().getName();
        handlers.add(this);
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (nextTickSpacing() > 10 && MCPCConfig.Setting.tickIntervalLogging.getValue())
        {
            MinecraftServer.getServer().logInfo("Ticking: " + wrapped.getClass().getSimpleName() + " at " + System.currentTimeMillis() + " Interval: "+ nextTickSpacing());
        }
        wrapped.tickStart(type, tickData);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        wrapped.tickEnd(type, tickData);
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return wrapped.ticks();
    }

    @Override
    public String getLabel()
    {
        return wrapped.getLabel();
    }

    @Override
    public int nextTickSpacing()
    {
        return za.co.mcportcentral.MCPCConfig.getInt(configString, 1);
    }

}