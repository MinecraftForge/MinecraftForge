package cpw.mods.fml.common;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String,EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, ModState> modStates = ArrayListMultimap.create();
    private Multimap<String, Throwable> errors = ArrayListMultimap.create();
    private Map<String, ModContainer> modList;
    private List<ModContainer> activeModList = Lists.newArrayList();
    private String activeContainer;
    private BiMap<ModContainer, Object> modObjectList;

    public LoadController(Loader loader)
    {
        this.loader = loader;
        this.masterChannel = new EventBus("FMLMainChannel");
        this.masterChannel.register(this);

        state = LoaderState.NOINIT;


    }

    @Subscribe
    public void buildModList(FMLLoadEvent event)
    {
        this.modList = loader.getIndexedModList();
        Builder<String, EventBus> eventBus = ImmutableMap.builder();

        for (ModContainer mod : loader.getModList())
        {
            EventBus bus = new EventBus(mod.getModId());
            boolean isActive = mod.registerBus(bus, this);
            if (isActive)
            {
                FMLLog.fine("Activating mod %s", mod.getModId());
                activeModList.add(mod);
                modStates.put(mod.getModId(), ModState.UNLOADED);
                eventBus.put(mod.getModId(), bus);
            }
            else
            {
                FMLLog.warning("Mod %s has been disabled through configuration", mod.getModId());
                modStates.put(mod.getModId(), ModState.UNLOADED);
                modStates.put(mod.getModId(), ModState.DISABLED);
            }
        }

        eventChannels = eventBus.build();
    }

    public void distributeStateMessage(LoaderState state, Object... eventData)
    {
        if (state.hasEvent())
        {
            masterChannel.post(state.getEvent(eventData));
        }
    }

    public void transition(LoaderState desiredState)
    {
        LoaderState oldState = state;
        state = state.transition(!errors.isEmpty());
        if (state != desiredState)
        {
            FMLLog.severe("Fatal errors were detected during the transition from %s to %s. Loading cannot continue", oldState, desiredState);
            StringBuilder sb = new StringBuilder();
            printModStates(sb);
            FMLLog.severe(sb.toString());
            FMLLog.severe("The following problems were captured during this phase");
            for (Entry<String, Throwable> error : errors.entries())
            {
                FMLLog.log(Level.SEVERE, error.getValue(), "Caught exception from %s", error.getKey());
            }

            // Throw embedding the first error (usually the only one)
            throw new LoaderException(errors.values().iterator().next());
        }
    }

    public ModContainer activeContainer()
    {
        return activeContainer!=null ? modList.get(activeContainer) : null;
    }

    @Subscribe
    public void propogateStateMessage(FMLStateEvent stateEvent)
    {
        if (stateEvent instanceof FMLPreInitializationEvent)
        {
            modObjectList = buildModObjectList();
        }
        for (Map.Entry<String,EventBus> entry : eventChannels.entrySet())
        {
            activeContainer = entry.getKey();
            stateEvent.applyModContainer(activeContainer());
            FMLLog.finer("Posting state event %s to mod %s", stateEvent, entry.getKey());
            entry.getValue().post(stateEvent);
            FMLLog.finer("State event %s delivered to mod %s", stateEvent, entry.getKey());
            activeContainer = null;
            if (!errors.containsKey(entry.getKey()))
            {
                modStates.put(entry.getKey(), stateEvent.getModState());
            }
            else
            {
                modStates.put(entry.getKey(), ModState.ERRORED);
            }
        }
    }

    public ImmutableBiMap<ModContainer, Object> buildModObjectList()
    {
        ImmutableBiMap.Builder<ModContainer, Object> builder = ImmutableBiMap.<ModContainer, Object>builder();
        for (ModContainer mc : activeModList)
        {
            if (!mc.isImmutable() && mc.getMod()!=null)
            {
                builder.put(mc, mc.getMod());
            }
            if (mc.getMod()==null && !mc.isImmutable() && state!=LoaderState.CONSTRUCTING)
            {
                FMLLog.severe("There is a severe problem with %s - it appears not to have constructed correctly", mc.getModId());
                if (state != LoaderState.CONSTRUCTING)
                {
                    this.errorOccurred(mc, new RuntimeException());
                }
            }
        }
        return builder.build();
    }

    public void errorOccurred(ModContainer modContainer, Throwable exception)
    {
        errors.put(modContainer.getModId(), exception);
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

    public List<ModContainer> getActiveModList()
    {
        return activeModList;
    }

    public ModState getModState(ModContainer selectedMod)
    {
        return Iterables.getLast(modStates.get(selectedMod.getModId()), ModState.AVAILABLE);
    }

    public void distributeStateMessage(Class<?> customEvent)
    {
        try
        {
            masterChannel.post(customEvent.newInstance());
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "An unexpected exception");
            throw new LoaderException(e);
        }
    }

    public BiMap<ModContainer, Object> getModObjectList()
    {
        if (modObjectList == null)
        {
            FMLLog.severe("Detected an attempt by a mod %s to perform game activity during mod construction. This is a serious programming error.", activeContainer);
            return buildModObjectList();
        }
        return ImmutableBiMap.copyOf(modObjectList);
    }

    public boolean isInState(LoaderState state)
    {
        return this.state == state;
    }
}
