package cpw.mods.fml.common;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

/**
 * The state enum used to help track state progression for the loader
 * @author cpw
 *
 */
public enum LoaderState
{
    NOINIT("Uninitialized",null),
    LOADING("Loading",null),
    CONSTRUCTING("Constructing mods",FMLConstructionEvent.class),
    PREINITIALIZATION("Pre-initializing mods", FMLPreInitializationEvent.class),
    INITIALIZATION("Initializing mods", FMLInitializationEvent.class),
    POSTINITIALIZATION("Post-initializing mods", FMLPostInitializationEvent.class),
    LOADCOMPLETE("Mod loading complete", null),
    ERRORED("Mod Loading errored",null);

    private Class<? extends FMLStateEvent> eventClass;
    private String name;

    private LoaderState(String name, Class<? extends FMLStateEvent> event)
    {
        this.name = name;
        this.eventClass = event;
    }

    public LoaderState transition(boolean errored)
    {
        if (errored)
        {
            return ERRORED;
        }
        return values()[ordinal() < values().length ? ordinal()+1 : ordinal()];
    }

    public boolean hasEvent()
    {
        return eventClass != null;
    }
    
    public FMLStateEvent getEvent(Object... eventData)
    {
        try
        {
            return eventClass.getConstructor(Object[].class).newInstance((Object)eventData);
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }
    public enum ModState
    {
        UNLOADED("Unloaded"),
        LOADED("Loaded"),
        CONSTRUCTED("Constructed"),
        PREINITIALIZED("Pre-initialized"),
        INITIALIZED("Initialized"),
        POSTINITIALIZED("Post-initialized"),
        AVAILABLE("Available"),
        DISABLED("Disabled"),
        ERRORED("Errored");
        
        private String label;

        private ModState(String label)
        {
            this.label = label;
        }

        public String toString()
        {
            return this.label;
        }
    }
}