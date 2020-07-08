package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * The main ResourceManager is recreated on each reload, through {@link DataPackRegistries}'s creation.
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
public class AddReloadListenerEvent extends Event
{
    private final List<IFutureReloadListener> listeners = new ArrayList<>();
    private final DataPackRegistries dataPackRegistries;
    
    public AddReloadListenerEvent(DataPackRegistries dataPackRegistries)
    {
        this.dataPackRegistries = dataPackRegistries;
    }
    
   /**
    * @param listener the listener to add to the ResourceManager on reload
    */
   public void addListener(IFutureReloadListener listener)
   {
      listeners.add(listener);
   }

   public List<IFutureReloadListener> getListeners()
   {
      return ImmutableList.copyOf(listeners);
   }
    
    public DataPackRegistries getDataPackRegistries()
    {
        return dataPackRegistries;
    }
}
