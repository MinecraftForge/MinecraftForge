package net.minecraftforge.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired whenever the client or server application is scheduled to be shut down.
 * This event is fired during the invocation of either {@link Minecraft#shutdown()} (window closed, quit game button pressed)
 * on the client or {@link MinecraftServer#initiateShutdown()} ({@link net.minecraft.command.server.CommandStop} run, window closed)
 * on the server. <br>
 * <br>
 * {@link #isClient} check whether the client or the server is shutting down.<br>
 * <br>
 * This event is {@link Cancelable} <br>
 * If the event is canceled the application will not close.<br>
 * <br>
 * This event does not have a result. {@link net.minecraftforge.fml.common.eventhandler.Event.HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 * <br>
 * Call {@link Minecraft#shutdown(boolean)} with true to ignore the firing of this event.
 */
@Cancelable
public class ApplicationCloseEvent extends Event
{
  private final boolean isClient;

  public ApplicationCloseEvent(boolean isClient)
  {
    this.isClient = isClient;
  }

  @Override
  public void setCanceled(boolean canceled)
  {
    super.setCanceled(canceled);
  }

  public boolean isClient()
  {
    return isClient;
  }
}
