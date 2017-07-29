/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLLoadEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
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
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.common.eventbus.SubscriberExceptionContext;

import javax.annotation.Nullable;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String,EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, ModState> modStates = ArrayListMultimap.create();
    private Multimap<String, Throwable> errors = ArrayListMultimap.create();
    private Map<String, String> modNames = Maps.newHashMap();
    private List<ModContainer> activeModList = Lists.newArrayList();
    private ModContainer activeContainer;
    private BiMap<ModContainer, Object> modObjectList;
    private ListMultimap<String, ModContainer> packageOwners;

    public LoadController(Loader loader)
    {
        this.loader = loader;
        this.masterChannel = new EventBus(new SubscriberExceptionHandler()
        {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context)
            {
                FMLLog.log.error("Could not dispatch event: {} to {}", context.getSubscriberMethod(), exception);
            }
        });
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

        for (final ModContainer mod : loader.getModList())
        {
            //Create mod logger, and make the EventBus logger a child of it.
            EventBus bus = new EventBus(new SubscriberExceptionHandler()
            {
                @Override
                public void handleException(final Throwable exception, final SubscriberExceptionContext context)
                {
                    LoadController.this.errorOccurred(mod, exception);
                }
            });

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
                LogManager.getLogger(mod.getModId()).warn("Mod {} has been disabled through configuration", mod.getModId());
                modStates.put(mod.getModId(), ModState.UNLOADED);
                modStates.put(mod.getModId(), ModState.DISABLED);
            }
            modNames.put(mod.getModId(), mod.getName());
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
        if (FMLCommonHandler.instance().isDisplayCloseRequested())
        {
            FMLLog.log.info("The game window is being closed by the player, exiting.");
            FMLCommonHandler.instance().exitJava(0, false);
        }

        LoaderState oldState = state;
        state = state.transition(!errors.isEmpty());
        if (state != desiredState && !forceState)
        {
            Entry<String, Throwable> toThrow = null;
            FMLLog.log.fatal("Fatal errors were detected during the transition from {} to {}. Loading cannot continue", oldState, desiredState);
            StringBuilder sb = new StringBuilder();
            printModStates(sb);
            FMLLog.log.fatal(sb.toString());
            if (errors.size()>0)
            {
                FMLLog.log.fatal("The following problems were captured during this phase");
                for (Entry<String, Throwable> error : errors.entries())
                {
                    String modId = error.getKey();
                    String modName = modNames.get(modId);
                    FMLLog.log.error("Caught exception from {} ({})", modId, error.getValue());
                    if (error.getValue() instanceof IFMLHandledException)
                    {
                        toThrow = error;
                    }
                    else if (toThrow == null)
                    {
                        toThrow = error;
                    }
                }
            }
            else
            {
                FMLLog.log.fatal("The ForgeModLoader state engine has become corrupted. Probably, a state was missed by and invalid modification to a base class" +
                        "ForgeModLoader depends on. This is a critical error and not recoverable. Investigate any modifications to base classes outside of" +
                        "ForgeModLoader, especially Optifine, to see if there are fixes available.");
                throw new RuntimeException("The ForgeModLoader state engine is invalid");
            }
            if (toThrow != null)
            {
                String modId = toThrow.getKey();
                String modName = modNames.get(modId);
                String errMsg = String.format("Caught exception from %s (%s)", modName, modId);
                throw new LoaderExceptionModCrash(errMsg, toThrow.getValue());
            }
        }
        else if (state != desiredState && forceState)
        {
            FMLLog.log.info("The state engine was in incorrect state {} and forced into state {}. Errors may have been discarded.", state, desiredState);
            forceState(desiredState);
        }

    }

    @Nullable
    public ModContainer activeContainer()
    {
        return activeContainer != null ? activeContainer : findActiveContainerFromStack();
    }

    void forceActiveContainer(@Nullable ModContainer container)
    {
        activeContainer = container;
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
        Collection<String> requirements =  mc.getRequirements().stream().map(ArtifactVersion::getLabel).collect(Collectors.toCollection(HashSet::new));
        for (ArtifactVersion av : mc.getDependencies())
        {
            if (av.getLabel()!= null && requirements.contains(av.getLabel()) && modStates.containsEntry(av.getLabel(),ModState.ERRORED))
            {
                LogManager.getLogger(modId).error("Skipping event {} and marking errored mod {} since required dependency {} has errored", stateEvent.getEventType(), modId, av.getLabel());
                modStates.put(modId, ModState.ERRORED);
                return;
            }
        }
        activeContainer = mc;
        stateEvent.applyModContainer(mc);
        ThreadContext.put("mod", modId);
        LogManager.getLogger(modId).trace("Sending event {} to mod {}", stateEvent.getEventType(), modId);
        eventChannels.get(modId).post(stateEvent);
        LogManager.getLogger(modId).trace("Sent event {} to mod {}", stateEvent.getEventType(), modId);
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
        ImmutableBiMap.Builder<ModContainer, Object> builder = ImmutableBiMap.builder();
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
                FMLLog.log.fatal("There is a severe problem with {} - it appears not to have constructed correctly", mc.getModId());
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
            errors.put(modContainer.getModId(), exception.getCause());
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

        List<ModStateData> data = loader.getModList().stream().map(mc -> new ModStateData(
            modStates.get(mc.getModId()).stream().map(ModState::getMarker).reduce("", (a, b) -> a + b),
            mc.getModId(),
            mc.getVersion(),
            mc.getSource().getName(),
            mc.getSigningCertificate() != null ? CertificateHelper.getFingerprint(mc.getSigningCertificate()) : "None"
        )).collect(Collectors.toList());

        ModStateData header = new ModStateData("State", "ID", "Version", "Source", "Signature");
        ModStateData widths = data.stream().reduce(header, (acc, m) -> new ModStateData(
            m.state.length() > acc.state.length() ? m.state : acc.state,
            m.id.length() > acc.id.length() ? m.id : acc.id,
            m.version.length() > acc.version.length() ? m.version : acc.version,
            m.source.length() > acc.source.length() ? m.source : acc.source,
            m.signature.length() > acc.signature.length() ? m.signature : acc.signature
        ));

        String baseFormat = "| %%-%ds | %%-%ds | %%-%ds | %%-%ds |";
        if (widths.signature.length() > header.signature.length())
        {
            baseFormat += " %%-%ds |";
        }
        String format = String.format(baseFormat,
            widths.state.length(),
            widths.id.length(),
            widths.version.length(),
            widths.source.length(),
            widths.signature.length());
        String separator = String.format(format,
            StringUtils.leftPad("", widths.state.length(), '-'),
            StringUtils.leftPad("", widths.id.length(), '-'),
            StringUtils.leftPad("", widths.version.length(), '-'),
            StringUtils.leftPad("", widths.source.length(), '-'),
            StringUtils.leftPad("", widths.signature.length(), '-'));
        ret.append("\n");
        ret.append("\n\t");
        ret.append(header.format(format));
        ret.append("\n\t");
        ret.append(separator);
        for (ModStateData mod : data)
        {
            ret.append("\n\t");
            ret.append(mod.format(format));
        }
        ret.append("\n");
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
            FMLLog.log.error("An unexpected exception", e);
            throw new LoaderException(e);
        }
    }

    public BiMap<ModContainer, Object> getModObjectList()
    {
        if (modObjectList == null)
        {
            FMLLog.log.fatal("Detected an attempt by a mod {} to perform game activity during mod construction. This is a serious programming error.", activeContainer);
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

    @Nullable
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

    private static class ModStateData
    {
        private String state;
        private String id;
        private String version;
        private String source;
        private String signature;

        private ModStateData(String state, String id, String version, String source, String signature)
        {
            this.state = state;
            this.id = id;
            this.version = version;
            this.source = source;
            this.signature = signature;
        }

        private String format(String format)
        {
            return String.format(format, state, id, version, source, signature);
        }
    }
}
