/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLLoadEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.functions.ArtifactVersionNameFunction;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.ThreadContext;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String,EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, ModState> modStates = ArrayListMultimap.create();
    private Multimap<String, Throwable> errors = ArrayListMultimap.create();
    private List<ModContainer> activeModList = Lists.newArrayList();
    private ModContainer activeContainer;
    private BiMap<ModContainer, Object> modObjectList;
    private ListMultimap<String, ModContainer> packageOwners;

    public LoadController(Loader loader)
    {
        this.loader = loader;
        this.masterChannel = new EventBus("FMLMainChannel");
        this.masterChannel.register(this);

        state = LoaderState.NOINIT;
        packageOwners = ArrayListMultimap.create();

    }

    void disableMod(ModContainer mod)
    {
        HashMap<String, EventBus> temporary = Maps.newHashMap(eventChannels);
        String modId = mod.getModId();
        EventBus bus = temporary.remove(modId);
        bus.post(new FMLModDisabledEvent());
        if (errors.get(modId).isEmpty())
        {
            eventChannels = ImmutableMap.copyOf(temporary);
            modStates.put(modId, ModState.DISABLED);
            modObjectList.remove(mod);
            activeModList.remove(mod);
        }
    }
    @Subscribe
    public void buildModList(FMLLoadEvent event)
    {
        Builder<String, EventBus> eventBus = ImmutableMap.builder();

        for (ModContainer mod : loader.getModList())
        {
            //Create mod logger, and make the EventBus logger a child of it.
            EventBus bus = new EventBus(mod.getModId());
            boolean isActive = mod.registerBus(bus, this);
            if (isActive)
            {
                activeModList.add(mod);
                modStates.put(mod.getModId(), ModState.UNLOADED);
                eventBus.put(mod.getModId(), bus);
                FMLCommonHandler.instance().addModToResourcePack(mod);
            }
            else
            {
                FMLLog.log(mod.getModId(), Level.WARN, "Mod %s has been disabled through configuration", mod.getModId());
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

    public void transition(LoaderState desiredState, boolean forceState)
    {
        LoaderState oldState = state;
        state = state.transition(!errors.isEmpty());
        if (state != desiredState && !forceState)
        {
            Throwable toThrow = null;
            FMLLog.severe("Fatal errors were detected during the transition from %s to %s. Loading cannot continue", oldState, desiredState);
            StringBuilder sb = new StringBuilder();
            printModStates(sb);
            FMLLog.severe("%s", sb.toString());
            if (errors.size()>0)
            {
                FMLLog.severe("The following problems were captured during this phase");
                for (Entry<String, Throwable> error : errors.entries())
                {
                    FMLLog.log(Level.ERROR, error.getValue(), "Caught exception from %s", error.getKey());
                    if (error.getValue() instanceof IFMLHandledException)
                    {
                        toThrow = error.getValue();
                    }
                    else if (toThrow == null)
                    {
                        toThrow = error.getValue();
                    }
                }
            }
            else
            {
                FMLLog.severe("The ForgeModLoader state engine has become corrupted. Probably, a state was missed by and invalid modification to a base class" +
                		"ForgeModLoader depends on. This is a critical error and not recoverable. Investigate any modifications to base classes outside of" +
                		"ForgeModLoader, especially Optifine, to see if there are fixes available.");
                throw new RuntimeException("The ForgeModLoader state engine is invalid");
            }
            if (toThrow != null && toThrow instanceof RuntimeException)
            {
                throw (RuntimeException)toThrow;
            }
            else
            {
                throw new LoaderException(toThrow);
            }
        }
        else if (state != desiredState && forceState)
        {
            FMLLog.info("The state engine was in incorrect state %s and forced into state %s. Errors may have been discarded.", state, desiredState);
            forceState(desiredState);
        }

    }

    public ModContainer activeContainer()
    {
        return activeContainer != null ? activeContainer : findActiveContainerFromStack();
    }

    @Subscribe
    public void propogateStateMessage(FMLEvent stateEvent)
    {
        if (stateEvent instanceof FMLPreInitializationEvent)
        {
            modObjectList = buildModObjectList();
        }
        ProgressBar bar = ProgressManager.push(stateEvent.description(), activeModList.size(), true);
        for (ModContainer mc : activeModList)
        {
            bar.step(mc.getName());
            sendEventToModContainer(stateEvent, mc);
        }
        ProgressManager.pop(bar);
    }

    private void sendEventToModContainer(FMLEvent stateEvent, ModContainer mc)
    {
        String modId = mc.getModId();
        Collection<String> requirements =  Collections2.transform(mc.getRequirements(),new ArtifactVersionNameFunction());
        for (ArtifactVersion av : mc.getDependencies())
        {
            if (av.getLabel()!= null && requirements.contains(av.getLabel()) && modStates.containsEntry(av.getLabel(),ModState.ERRORED))
            {
                FMLLog.log(modId, Level.ERROR, "Skipping event %s and marking errored mod %s since required dependency %s has errored", stateEvent.getEventType(), modId, av.getLabel());
                modStates.put(modId, ModState.ERRORED);
                return;
            }
        }
        activeContainer = mc;
        stateEvent.applyModContainer(activeContainer());
        ThreadContext.put("mod", modId);
        FMLLog.log(modId, Level.TRACE, "Sending event %s to mod %s", stateEvent.getEventType(), modId);
        eventChannels.get(modId).post(stateEvent);
        FMLLog.log(modId, Level.TRACE, "Sent event %s to mod %s", stateEvent.getEventType(), modId);
        ThreadContext.remove("mod");
        activeContainer = null;
        if (stateEvent instanceof FMLStateEvent)
        {
            if (!errors.containsKey(modId))
            {
                modStates.put(modId, ((FMLStateEvent)stateEvent).getModState());
            }
            else
            {
                modStates.put(modId, ModState.ERRORED);
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
                List<String> packages = mc.getOwnedPackages();
                for (String pkg : packages)
                {
                    packageOwners.put(pkg, mc);
                }
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
        if (exception instanceof InvocationTargetException)
        {
            errors.put(modContainer.getModId(), ((InvocationTargetException)exception).getCause());
        }
        else
        {
            errors.put(modContainer.getModId(), exception);
        }
    }

    public void printModStates(StringBuilder ret)
    {
        ret.append("\n\tStates:");
        for (ModState state : ModState.values())
            ret.append(" '").append(state.getMarker()).append("' = ").append(state.toString());

        for (ModContainer mc : loader.getModList())
        {
            ret.append("\n\t");
            for (ModState state : modStates.get(mc.getModId()))
                ret.append(state.getMarker());

            ret.append("\t").append(mc.getModId()).append("{").append(mc.getVersion()).append("} [").append(mc.getName()).append("] (").append(mc.getSource().getName()).append(") ");
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
            FMLLog.log(Level.ERROR, e, "An unexpected exception");
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

	boolean hasReachedState(LoaderState state) {
		return this.state.ordinal()>=state.ordinal() && this.state!=LoaderState.ERRORED;
	}

    void forceState(LoaderState newState)
    {
        this.state = newState;
    }

    private ModContainer findActiveContainerFromStack()
    {
        for (Class<?> c : getCallingStack())
        {
            int idx = c.getName().lastIndexOf('.');
            if (idx == -1)
            {
                continue;
            }
            String pkg = c.getName().substring(0,idx);
            if (packageOwners.containsKey(pkg))
            {
                return packageOwners.get(pkg).get(0);
            }
        }

        return null;
    }
    private FMLSecurityManager accessibleManager = new FMLSecurityManager();

    class FMLSecurityManager extends SecurityManager
    {
        Class<?>[] getStackClasses()
        {
            return getClassContext();
        }
    }

    Class<?>[] getCallingStack()
    {
        return accessibleManager.getStackClasses();
    }

    LoaderState getState()
    {
        return state;
    }
}
