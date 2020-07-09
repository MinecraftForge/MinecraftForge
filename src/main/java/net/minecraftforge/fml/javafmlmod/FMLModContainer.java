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

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.EventBusErrorMessage;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.AutomaticEventSubscriber;
import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraftforge.fml.Logging.LOADING;

public class FMLModContainer extends ModContainer
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ModFileScanData scanResults;
    private final IEventBus eventBus;
    private Object modInstance;
    private final Class<?> modClass;

    public FMLModContainer(IModInfo info, String className, ClassLoader modClassLoader, ModFileScanData modFileScanResults)
    {
        super(info);
        LOGGER.debug(LOADING,"Creating FMLModContainer instance for {} with classLoader {} & {}", className, modClassLoader, getClass().getClassLoader());
        this.scanResults = modFileScanResults;
        triggerMap.put(ModLoadingStage.CONSTRUCT, dummy().andThen(this::beforeEvent).andThen(this::constructMod).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.CREATE_REGISTRIES, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.LOAD_REGISTRIES, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.COMMON_SETUP, dummy().andThen(this::beforeEvent).andThen(this::preinitMod).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.SIDED_SETUP, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.ENQUEUE_IMC, dummy().andThen(this::beforeEvent).andThen(this::initMod).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.PROCESS_IMC, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.COMPLETE, dummy().andThen(this::beforeEvent).andThen(this::completeLoading).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.GATHERDATA, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        this.eventBus = BusBuilder.builder().setExceptionHandler(this::onEventFailed).setTrackPhases(false).build();
        this.configHandler = Optional.of(event -> this.eventBus.post(event));
        final FMLJavaModLoadingContext contextExtension = new FMLJavaModLoadingContext(this);
        this.contextExtension = () -> contextExtension;
        try
        {
            modClass = Class.forName(className, true, modClassLoader);
            LOGGER.debug(LOADING,"Loaded modclass {} with {}", modClass.getName(), modClass.getClassLoader());
        }
        catch (Throwable e)
        {
            LOGGER.error(LOADING, "Failed to load class {}", className, e);
            throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
        }
    }

    private void completeLoading(LifecycleEventProvider.LifecycleEvent lifecycleEvent)
    {

    }

    private void initMod(LifecycleEventProvider.LifecycleEvent lifecycleEvent)
    {

    }

    private Consumer<LifecycleEventProvider.LifecycleEvent> dummy() { return (s) -> {}; }

    private void onEventFailed(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable)
    {
        LOGGER.error(new EventBusErrorMessage(event, i, iEventListeners, throwable));
    }

    private void beforeEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
    }

    private void fireEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
        final Event event = lifecycleEvent.getOrBuildEvent(this);
        LOGGER.debug(LOADING, "Firing event for modid {} : {}", this.getModId(), event);
        try
        {
            eventBus.post(event);
            LOGGER.debug(LOADING, "Fired event for modid {} : {}", this.getModId(), event);
        }
        catch (Throwable e)
        {
            LOGGER.error(LOADING,"Caught exception during event {} dispatch for modid {}", event, this.getModId(), e);
            throw new ModLoadingException(modInfo, lifecycleEvent.fromStage(), "fml.modloading.errorduringevent", e);
        }
    }

    private void afterEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
        if (getCurrentState() == ModLoadingStage.ERROR) {
            LOGGER.error(LOADING,"An error occurred while dispatching event {} to {}", lifecycleEvent.fromStage(), getModId());
        }
    }

    private void preinitMod(LifecycleEventProvider.LifecycleEvent lifecycleEvent)
    {
    }

    private void constructMod(LifecycleEventProvider.LifecycleEvent event)
    {
        try
        {
            LOGGER.debug(LOADING, "Loading mod instance {} of type {}", getModId(), modClass.getName());
            this.modInstance = modClass.newInstance();
            LOGGER.debug(LOADING, "Loaded mod instance {} of type {}", getModId(), modClass.getName());
        }
        catch (Throwable e)
        {
            LOGGER.error(LOADING,"Failed to create mod instance. ModID: {}, class {}", getModId(), modClass.getName(), e);
            throw new ModLoadingException(modInfo, event.fromStage(), "fml.modloading.failedtoloadmod", e, modClass);
        }
        try {
            LOGGER.debug(LOADING, "Injecting Automatic event subscribers for {}", getModId());
            AutomaticEventSubscriber.inject(this, this.scanResults, this.modClass.getClassLoader());
            LOGGER.debug(LOADING, "Completed Automatic event subscribers for {}", getModId());
        } catch (Throwable e) {
            LOGGER.error(LOADING,"Failed to register automatic subscribers. ModID: {}, class {}", getModId(), modClass.getName(), e);
            throw new ModLoadingException(modInfo, event.fromStage(), "fml.modloading.failedtoloadmod", e, modClass);
        }
    }

    @Override
    public boolean matches(Object mod)
    {
        return mod == modInstance;
    }

    @Override
    public Object getMod()
    {
        return modInstance;
    }

    public IEventBus getEventBus()
    {
        return this.eventBus;
    }

    @Override
    protected void acceptEvent(final Event e) {
        this.eventBus.post(e);
    }
}
