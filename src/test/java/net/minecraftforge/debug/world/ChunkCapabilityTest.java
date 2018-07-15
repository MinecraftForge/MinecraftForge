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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple mod to test chunk capabilities.
 * Use flint and steel to increase pollution in a chunk and saplings to decrease pollution in a chunk.
 */
@Mod(modid = ChunkCapabilityTest.MODID, name = "Chunk Capability Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class ChunkCapabilityTest
{
    public static final String MODID = "chunkcapabilitypollutiontest";
    public static final boolean ENABLE = false;

    public interface IPollution
    {
        int get();

        void set(int value, boolean markDirty);
    }

    public static class PollutionStorage implements Capability.IStorage<IPollution>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IPollution> capability, IPollution instance, EnumFacing side) {
            return new NBTTagInt(instance.get());
        }

        @Override
        public void readNBT(Capability<IPollution> capability, IPollution instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagInt)
            {
                // The state is being loaded and not updated. We set the value silently to avoid unnecessary dirty chunks
                instance.set(((NBTTagInt) nbt).getInt(), false);
            }
        }
    }

    public static class DefaultPollution implements IPollution
    {
        private int value;

        @Override
        public int get() {
            return value;
        }

        @Override
        public void set(int value, boolean markDirty) {
            this.value = value;
        }
    }

    /**
     * Marks the chunk as dirty when the value changes.
     * Cannot be the default implementation because it requires a chunk in the constructor
     */
    public static class SafePollution extends DefaultPollution
    {
        private final Chunk chunk;

        public SafePollution(Chunk chunk)
        {
            this.chunk = chunk;
        }

        @Override
        public void set(int value, boolean markDirty) {
            super.set(value, markDirty);
            if (markDirty) {
                chunk.markDirty();
            }
        }
    }

    public static class PollutionProvider implements ICapabilitySerializable<NBTBase>
    {
        private final IPollution pollution;

        public PollutionProvider(Chunk chunk)
        {
            pollution = new SafePollution(chunk);
        }

        @Override
        public NBTBase serializeNBT() {
            return POLLUTION_CAPABILITY.getStorage().writeNBT(POLLUTION_CAPABILITY, pollution, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            POLLUTION_CAPABILITY.getStorage().readNBT(POLLUTION_CAPABILITY, pollution, null, nbt);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == POLLUTION_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == POLLUTION_CAPABILITY ? POLLUTION_CAPABILITY.cast(pollution) : null;
        }
    }

    public static class EventBusHandler
    {
        @SubscribeEvent
        public void onAttachChunkCapabilities(AttachCapabilitiesEvent<Chunk> event)
        {
            event.addCapability(new ResourceLocation(MODID, "pollution"), new PollutionProvider(event.getObject()));
        }

        @SubscribeEvent
        public void onUseItem(PlayerInteractEvent.RightClickBlock event)
        {
            if (!event.getWorld().isRemote) {
                ItemStack stack = event.getEntityPlayer().getHeldItem(event.getHand());
                int delta = 0;
                if (stack.getItem() == Items.FLINT_AND_STEEL)
                {
                    delta = 1;
                }
                else if (stack.getItem() == Item.getItemFromBlock(Blocks.SAPLING))
                {
                    delta = -1;
                }

                if (delta != 0)
                {
                    Chunk chunk = event.getWorld().getChunkFromBlockCoords(event.getPos());
                    IPollution pollution = chunk.getCapability(POLLUTION_CAPABILITY, null);
                    pollution.set(pollution.get() + delta, true);

                    event.getEntityPlayer().sendStatusMessage(new TextComponentString("Chunk pollution: " + pollution.get()), true);
                }
            }
        }
    }

    @CapabilityInject(IPollution.class)
    public static final Capability<IPollution> POLLUTION_CAPABILITY = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE)
        {
            CapabilityManager.INSTANCE.register(IPollution.class, new PollutionStorage(), DefaultPollution::new);
            MinecraftForge.EVENT_BUS.register(new EventBusHandler());
        }
    }

}
