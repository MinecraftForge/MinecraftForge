package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class AddReloadListenerEvent extends Event
{
   private final List<IFutureReloadListener> listeners = new ArrayList<>();

   public void addListener(IFutureReloadListener listener)
   {
      listeners.add(listener);
   }

   public List<IFutureReloadListener> getListeners()
   {
      return ImmutableList.copyOf(listeners);
   }
}
