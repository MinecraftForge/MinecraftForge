package net.minecraftforge.test;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Simple mod to test world capability, by adding timer logic and check for raining time. */
@Mod(modid=WorldCapabilityTimerTest.MODID, name="World Periodic Check Test", version="0.0.0")
public class WorldCapabilityTimerTest {
    public static final String MODID = "worldperiodicchecktest";

    @CapabilityInject(IWorldTimer.class)
    public static final Capability<IWorldTimer> TIMER_CAP = null;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IWorldTimer.class, new TimerStorage(), DefaultTimer.class);
        MinecraftForge.EVENT_BUS.register(new NormalEventHandler());
    }

    public static class NormalEventHandler {
        @SubscribeEvent
        public void attatchTimer(AttachCapabilitiesEvent.World event)
        {
            event.addCapability(new ResourceLocation(MODID, "worldTimer"), new WorldTimerProvider());
        }

        @SubscribeEvent
        public void onTick(TickEvent.WorldTickEvent event)
        {
            if(event.world.isRemote)
                return;

            IWorldTimer timer = event.world.getCapability(TIMER_CAP, null);

            if(event.phase == TickEvent.Phase.END)
                timer.onTick();

            if(event.phase == TickEvent.Phase.START){
                if(!event.world.provider.getHasNoSky() && event.world.isRaining())
                {
                    if(!timer.timerIDs().contains("rainStopper"))
                        timer.addTimer("rainStopper", 1000);
                    else if(timer.isTimerReachedDuration("rainStopper")) {
                        event.world.provider.resetRainAndThunder();
                    }
                } else
                    timer.removeTimer("rainStopper");
            }
        }
    }

    public interface IWorldTimer {
        public Set<String> timerIDs();
        public int getCurrentTime(String timerID);
        public int getDuration(String timerID);

        public void addTimer(String timerID, int duration);
        public void refreshTimer(String timerID, int duration, int initial);
        public void removeTimer(String timerID);

        public void resetTimer(String timerID);
        public boolean isTimerReachedDuration(String timerID);

        public void onTick();
    }

    public static class TimerStorage implements IStorage<IWorldTimer> {
        @Override
        public NBTBase writeNBT(Capability<IWorldTimer> capability, IWorldTimer instance, EnumFacing side) {
            NBTTagCompound data = new NBTTagCompound();
            for(String timerID : instance.timerIDs())
                data.setIntArray(timerID, new int[] {instance.getCurrentTime(timerID), instance.getDuration(timerID)});
            return data;
        }

        @Override
        public void readNBT(Capability<IWorldTimer> capability, IWorldTimer instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound data = (NBTTagCompound) nbt;
            for(String timerID : data.getKeySet()) {
                int[] array = data.getIntArray(timerID);
                instance.refreshTimer(timerID, array[1], array[0]);
            }
        }
    }

    public static class DefaultTimer implements IWorldTimer {
        private Map<String, Integer> currentMap = Maps.newHashMap();
        private Map<String, Integer> durationMap = Maps.newHashMap();

        @Override
        public Set<String> timerIDs() { return currentMap.keySet(); }
        @Override
        public int getCurrentTime(String timerID) { return currentMap.get(timerID); }
        @Override
        public int getDuration(String timerID) { return durationMap.get(timerID); }

        @Override
        public void addTimer(String timerID, int duration)
        {
            currentMap.put(timerID, 0);
            durationMap.put(timerID, duration);
        }
        @Override
        public void refreshTimer(String timerID, int duration, int initial)
        {
            currentMap.put(timerID, initial);
            durationMap.put(timerID, duration);
        }
        @Override
        public void removeTimer(String timerID)
        {
            currentMap.remove(timerID);
            durationMap.remove(timerID);
        }

        @Override
        public void resetTimer(String timerID)
        {
            currentMap.put(timerID, 0);
        }
        @Override
        public boolean isTimerReachedDuration(String timerID)
        {
            return this.getCurrentTime(timerID) >= this.getDuration(timerID);
        }

        @Override
        public void onTick()
        {
            for(String timerID : currentMap.keySet()) {
                if(!this.isTimerReachedDuration(timerID))
                    currentMap.put(timerID, currentMap.get(timerID) + 1);
            }
        }
    }

    public static class WorldTimerProvider implements ICapabilitySerializable<NBTTagCompound> {
        private IWorldTimer timer = TIMER_CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == TIMER_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            return capability == TIMER_CAP? TIMER_CAP.<T>cast(this.timer) : null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return (NBTTagCompound) TIMER_CAP.writeNBT(this.timer, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            TIMER_CAP.readNBT(this.timer, null, nbt);
        }
    }
}