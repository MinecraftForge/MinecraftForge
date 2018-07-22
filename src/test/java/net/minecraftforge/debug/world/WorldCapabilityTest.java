/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.world;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple mod to test world capability, by adding timer logic and check for raining time.
 */
@Mod(modid = WorldCapabilityTest.MODID, name = "World Periodic Rain Check Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class WorldCapabilityTest
{
    public static final String MODID = "worldperiodicrainchecktest";
    static final boolean ENABLED = false;

    @CapabilityInject(IRainTimer.class)
    public static final Capability<IRainTimer> TIMER_CAP = null;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IRainTimer.class, new TimerStorage(), DefaultTimer::new);
        MinecraftForge.EVENT_BUS.register(new NormalEventHandler());
    }

    public static class NormalEventHandler
    {
        @SubscribeEvent
        public void attatchTimer(AttachCapabilitiesEvent<World> event)
        {
            if (!event.getObject().isRemote && !event.getObject().provider.isNether())
            {
                event.addCapability(new ResourceLocation(MODID, "rainTimer"), new RainTimerProvider());
            }
        }

        @SubscribeEvent
        public void onTick(TickEvent.WorldTickEvent event)
        {
            if (!ENABLED || event.world.isRemote)
            {
                return;
            }

            IRainTimer timer = event.world.getCapability(TIMER_CAP, null);

            if (timer == null)
            {
                return;
            }

            if (event.phase == TickEvent.Phase.END)
            {
                timer.onTick();
            }

            if (event.phase == TickEvent.Phase.START)
            {
                if (event.world.isRaining())
                {
                    if (timer.getDuration() == 0)
                    {
                        timer.refreshTimer(1000, 0);
                    }
                    else if (timer.isTimerReachedDuration())
                    {
                        event.world.provider.resetRainAndThunder();
                    }
                }
                else
                {
                    timer.refreshTimer(0, 0);
                }
            }
        }
    }

    public interface IRainTimer
    {
        public int getCurrentTime();

        public int getDuration();

        public void refreshTimer(int duration, int initial);

        public void resetTimer();

        public boolean isTimerReachedDuration();

        public void onTick();
    }

    public static class TimerStorage implements IStorage<IRainTimer>
    {
        @Override
        public NBTBase writeNBT(Capability<IRainTimer> capability, IRainTimer instance, EnumFacing side)
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("current", instance.getCurrentTime());
            compound.setInteger("duration", instance.getDuration());
            return compound;
        }

        @Override
        public void readNBT(Capability<IRainTimer> capability, IRainTimer instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagCompound data = (NBTTagCompound) nbt;
            instance.refreshTimer(data.getInteger("duration"), data.getInteger("current"));
        }
    }

    public static class DefaultTimer implements IRainTimer
    {
        private int duration, current;

        @Override
        public int getCurrentTime()
        {
            return this.current;
        }

        @Override
        public int getDuration()
        {
            return this.duration;
        }

        @Override
        public void refreshTimer(int duration, int initial)
        {
            this.duration = duration;
            this.current = initial;
        }

        @Override
        public void resetTimer()
        {
            this.current = 0;
        }

        @Override
        public boolean isTimerReachedDuration()
        {
            return this.current >= this.duration;
        }

        @Override
        public void onTick()
        {
            if (this.current < this.duration)
            {
                this.current++;
            }
        }
    }

    public static class RainTimerProvider implements ICapabilitySerializable<NBTTagCompound>
    {
        private IRainTimer timer = TIMER_CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == TIMER_CAP;
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return capability == TIMER_CAP ? TIMER_CAP.<T>cast(this.timer) : null;
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
