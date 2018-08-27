/*
 * Minecraft Forge
 * Copyright (c) 2018.
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
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.ModThreadContext;
import net.minecraftforge.fml.common.event.ModLifecycleEvent;
import net.minecraftforge.fml.AutomaticEventSubscriber;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.language.ModFileScanData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        this.scanResults = modFileScanResults;
        triggerMap.put(ModLoadingStage.CONSTRUCT, dummy().andThen(this::beforeEvent).andThen(this::constructMod).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.PREINIT, dummy().andThen(this::beforeEvent).andThen(this::preinitMod).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.SIDEDINIT, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.INIT, dummy().andThen(this::beforeEvent).andThen(this::initMod).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.POSTINIT, dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
        triggerMap.put(ModLoadingStage.COMPLETE, dummy().andThen(this::beforeEvent).andThen(this::completeLoading).andThen(this::fireEvent).andThen(this::afterEvent));
        this.eventBus = IEventBus.create(this::onEventFailed);

        try
        {
            modClass = Class.forName(className, true, modClassLoader);
            LOGGER.error(LOADING,"Loaded modclass {} with {}", modClass.getName(), modClass.getClassLoader());
        }
        catch (Throwable e)
        {
            LOGGER.error(LOADING, "Failed to load class {}", className, e);
            throw new RuntimeException(e);
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
        modLoadingError.add(throwable);
    }

    private void beforeEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
        FMLModLoadingContext.get().setActiveContainer(this);
        ModThreadContext.get().setActiveContainer(this);
    }

    private void fireEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
        final ModLifecycleEvent event = lifecycleEvent.buildModEvent(this);
        LOGGER.debug(LOADING, "Firing event for modid {} : {} @ {}", this.getModId(), event, System.identityHashCode(event.getClass()));
        try
        {
            eventBus.post(event);
            LOGGER.debug(LOADING, "Fired event for modid {} : {}", this.getModId(), event);
        }
        catch (Throwable e)
        {
            LOGGER.error(LOADING,"Caught exception during event {} dispatch for modid {}", event, this.getModId(), e);
            modLoadingStage = ModLoadingStage.ERROR;
            modLoadingError.add(e);
        }
    }

    private void afterEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
        ModThreadContext.get().setActiveContainer(null);
        FMLModLoadingContext.get().setActiveContainer(null);
        if (getCurrentState() == ModLoadingStage.ERROR) {
            LOGGER.error(LOADING,"An error occurred while dispatching event {} to {}", lifecycleEvent.fromStage(), getModId());
        }
    }

    private void preinitMod(LifecycleEventProvider.LifecycleEvent lifecycleEvent)
    {
        AutomaticEventSubscriber.inject(this, this.scanResults, this.modClass.getClassLoader());
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
            modLoadingStage = ModLoadingStage.ERROR;
            modLoadingError.add(e);
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
}
