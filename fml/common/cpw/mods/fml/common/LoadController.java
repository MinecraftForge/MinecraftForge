package cpw.mods.fml.common;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String,EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, ModState> modStates = HashMultimap.create();
    private Multimap<ModContainer, Throwable> errors = HashMultimap.create();
    private Logger log;
    private Map<String, ModContainer> modList;
    
    public LoadController(Loader loader)
    {
        this.loader = loader;
        this.modList = loader.getIndexedModList();
        this.masterChannel = new EventBus("FMLMainChannel");
        this.masterChannel.register(this);
        this.log = Loader.log;
        
        Builder<String, EventBus> eventBus = ImmutableMap.builder();
        
        for (ModContainer mod : Loader.getModList())
        {
            EventBus bus = new EventBus(mod.getModId());
            boolean isActive = mod.registerBus(bus, this);
            if (isActive)
            {
                modStates.put(mod.getModId(), ModState.UNLOADED);
                eventBus.put(mod.getModId(), bus);
            }
            else
            {
                modStates.put(mod.getModId(), ModState.UNLOADED);
                modStates.put(mod.getModId(), ModState.DISABLED);
            }
        }
        
        eventChannels = eventBus.build();
        
        state = LoaderState.CONSTRUCTING;
    }

    public void runNextPhase(Object... eventData)
    {
        if (state.hasEvent())
        {
            masterChannel.post(state.getEvent(eventData));
        }
    }
    
    public void transition()
    {
        state = state.transition(!errors.isEmpty());
    }
    
    @Subscribe
    void propogateStateMessage(FMLStateEvent stateEvent)
    {
        for (Map.Entry<String,EventBus> entry : eventChannels.entrySet())
        {
            entry.getValue().post(stateEvent);
            if (errors.isEmpty())
            {
                modStates.put(entry.getKey(), stateEvent.getModState());
            }
            else
            {
                modStates.put(entry.getKey(), ModState.ERRORED);
            }
        }
    }

    public void errorOccurred(ModContainer modContainer, Throwable exception)
    {
        errors.put(modContainer, exception);
    }

    public void log(Level level, String message, Object... rest)
    {
        log.log(level, String.format(message,rest));
    }

    public void log(Level level, Throwable error, String message, Object... rest)
    {
        log.log(level, String.format(message,rest), error);
    }

    public void printModStates(StringBuilder ret)
    {
        for (String modId : modStates.keySet())
        {
            ModContainer mod = modList.get(modId);
            ret.append("\n\t").append(mod.getName()).append(" (").append(mod.getSource().getName()).append(") ");
            Joiner.on("->"). appendTo(ret, modStates.get(modId));
        }
    }
}
