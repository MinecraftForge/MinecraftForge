/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraftforge.common.util.TextTable;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLLoadEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.FMLThrowingEventBus;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.FormattedMessage;

import com.google.common.base.Throwables;
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
import com.google.common.collect.MultimapBuilder;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.annotation.Nullable;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String, EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, ModState> modStates = MultimapBuilder.hashKeys().enumSetValues(ModState.class).build();
    private List<ModContainer> activeModList = Lists.newArrayList();
    private ModContainer activeContainer;
    private BiMap<ModContainer, Object> modObjectList;
    private ListMultimap<String, ModContainer> packageOwners;

    public LoadController(Loader loader)
    {
        this.loader = loader;
        this.masterChannel = new FMLThrowingEventBus((exception, context) -> {
            Throwables.throwIfUnchecked(exception);
            // should not happen, but add some extra context for checked exceptions
            Method method = context.getSubscriberMethod();
            String parameterNames = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(", "));
            String message = "Exception thrown during LoadController." + method.getName() + '(' + parameterNames + ')';
            throw new LoaderExceptionModCrash(message, exception);
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
        eventChannels = ImmutableMap.copyOf(temporary);
        modStates.put(modId, ModState.DISABLED);
        modObjectList.remove(mod);
        activeModList.remove(mod);
    }

    @Subscribe
    public void buildModList(FMLLoadEvent event)
    {
        Builder<String, EventBus> eventBus = ImmutableMap.builder();

        for (final ModContainer mod : loader.getModList())
        {
            EventBus bus = new FMLThrowingEventBus((exception, context) -> this.errorOccurred(mod, exception));

            boolean isActive = mod.registerBus(bus, this);
            if (isActive)
            {
                activeModList.add(mod);
                modStates.put(mod.getModId(), ModState.LOADED);
                eventBus.put(mod.getModId(), bus);
                FMLCommonHandler.instance().addModToResourcePack(mod);
            }
            else
            {
                FMLLog.log.warn("Mod {} has been disabled through configuration", mod.getModId());
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
        if (FMLCommonHandler.instance().isDisplayCloseRequested())
        {
            FMLLog.log.info("The game window is being closed by the player, exiting.");
            FMLCommonHandler.instance().exitJava(0, false);
        }

        LoaderState oldState = state;
        state = state.transition(false);
        if (state != desiredState)
        {
            if (!forceState)
            {
                FormattedMessage message = new FormattedMessage("A fatal error occurred during the state transition from {} to {}. State became {} instead. Loading cannot continue.", oldState, desiredState, state);
                throw new LoaderException(message.getFormattedMessage());
            }
            else
            {
                FMLLog.log.info("The state engine was in incorrect state {} and forced into state {}. Errors may have been discarded.", state, desiredState);
                forceState(desiredState);
            }
        }
    }

    @Deprecated // TODO remove in 1.13
    public void checkErrorsAfterAvailable()
    {
    }

    @Deprecated // TODO remove in 1.13
    public void checkErrors()
    {
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
        Collection<String> requirements = mc.getRequirements().stream().map(ArtifactVersion::getLabel).collect(Collectors.toCollection(HashSet::new));
        for (ArtifactVersion av : mc.getDependencies())
        {
            if (av.getLabel() != null && requirements.contains(av.getLabel()) && modStates.containsEntry(av.getLabel(), ModState.ERRORED))
            {
                FMLLog.log.error("Skipping event {} and marking errored mod {} since required dependency {} has errored", stateEvent.getEventType(), modId, av.getLabel());
                modStates.put(modId, ModState.ERRORED);
                return;
            }
        }
        activeContainer = mc;
        stateEvent.applyModContainer(mc);
        ThreadContext.put("mod", modId);
        FMLLog.log.trace("Sending event {} to mod {}", stateEvent.getEventType(), modId);
        eventChannels.get(modId).post(stateEvent);
        FMLLog.log.trace("Sent event {} to mod {}", stateEvent.getEventType(), modId);
        ThreadContext.remove("mod");
        activeContainer = null;
        if (stateEvent instanceof FMLStateEvent)
        {
            modStates.put(modId, ((FMLStateEvent) stateEvent).getModState());
        }
    }

    public ImmutableBiMap<ModContainer, Object> buildModObjectList()
    {
        ImmutableBiMap.Builder<ModContainer, Object> builder = ImmutableBiMap.builder();
        for (ModContainer mc : activeModList)
        {
            if (!mc.isImmutable() && mc.getMod() != null)
            {
                builder.put(mc, mc.getMod());
                List<String> packages = mc.getOwnedPackages();
                for (String pkg : packages)
                {
                    packageOwners.put(pkg, mc);
                }
            }
            if (mc.getMod() == null && !mc.isImmutable() && state != LoaderState.CONSTRUCTING)
            {
                FormattedMessage message = new FormattedMessage("There is a severe problem with {} ({}) - it appears not to have constructed correctly", mc.getName(), mc.getModId());
                this.errorOccurred(mc, new RuntimeException(message.getFormattedMessage()));
            }
        }
        return builder.build();
    }

    public void errorOccurred(ModContainer modContainer, Throwable exception)
    {
        String modId = modContainer.getModId();
        String modName = modContainer.getName();
        modStates.put(modId, ModState.ERRORED);
        if (exception instanceof InvocationTargetException)
        {
            exception = exception.getCause();
        }
        if (exception instanceof LoaderException) // avoid wrapping loader exceptions multiple times
        {
            throw (LoaderException) exception;
        }
        FormattedMessage message = new FormattedMessage("Caught exception from {} ({})", modName, modId);
        throw new LoaderExceptionModCrash(message.getFormattedMessage(), exception);
    }

    public void printModStates(StringBuilder ret)
    {
        ret.append("\n\tStates:");
        for (ModState state : ModState.values())
            ret.append(" '").append(state.getMarker()).append("' = ").append(state.toString());

        TextTable table = new TextTable(Lists.newArrayList(
            TextTable.column("State"),
            TextTable.column("ID"),
            TextTable.column("Version"),
            TextTable.column("Source"),
            TextTable.column("Signature"))
        );
        for (ModContainer mc : loader.getModList())
        {
            table.add(
                modStates.get(mc.getModId()).stream().map(ModState::getMarker).reduce("", (a, b) -> a + b),
                mc.getModId(),
                mc.getVersion(),
                mc.getSource().getName(),
                mc.getSigningCertificate() != null ? CertificateHelper.getFingerprint(mc.getSigningCertificate()) : "None"
            );
        }

        ret.append("\n");
        ret.append("\n\t");
        table.append(ret, "\n\t");
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
        Object eventInstance;
        try
        {
            eventInstance = customEvent.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new LoaderException("Failed to create new event instance for " + customEvent.getName(), e);
        }
        masterChannel.post(eventInstance);
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

    boolean hasReachedState(LoaderState state)
    {
        return this.state.ordinal() >= state.ordinal() && this.state != LoaderState.ERRORED;
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
            String pkg = c.getName().substring(0, idx);
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
