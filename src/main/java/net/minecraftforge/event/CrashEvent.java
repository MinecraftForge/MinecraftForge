package net.minecraftforge.event;

import javax.annotation.Nonnull;

import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
/**
 * CrashEvent is fired whenever a new {@link CrashReport} is constructed.<br>
 * <br>
 * Be careful not to raise any exception when subscribing to this event<br>
 * in order to prevent recursive calls.<br>
 * <br>
 * {@link #report} contains the instance of CrashReport just generated.<br>
 * <br>
 * This event is not {@link Cancelable}. <br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * <br>
 **/
public class CrashEvent extends net.minecraftforge.fml.common.eventhandler.Event 
{
    private final CrashReport report;
    public CrashEvent(@Nonnull CrashReport report)
    {
        this.report = report;
    }
    public CrashReport getReport() { return this.report; }
}
