/*
 * Minecraft Forge
 * Copyright (c) 2017.
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

package net.minecraftforge.items.customslots;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Provides a way for mods to register equipment slots, and share them with other mods.
 * <p>
 * This class also manages storage for the slots (can be opted out).
 */
public class ExtensionSlotContainer implements IExtensionContainer, INBTSerializable<NBTTagCompound>
{
    ////////////////////////////////////////////////////////////
    // Global slot registry
    //

    public interface SlotFactory extends BiFunction<EntityLivingBase, IExtensionContainer, IExtensionSlot>
    {
    }

    public static class SlotEntry
    {
        final String id;
        final SlotFactory factory;

        public SlotEntry(String id, SlotFactory factory)
        {
            this.id = id;
            this.factory = factory;
        }
    }

    private static final Map<String, SlotEntry> REGISTRY = Maps.newHashMap();

    /**
     * Obtains a previously-registered slot.
     *
     * @param id The id of the slot.
     * @return A functional interface that can be used to create new slots
     */
    @Nullable
    public static SlotFactory get(String id)
    {
        return REGISTRY.get(id).factory;
    }

    /**
     * Registers a slot with a given id. If it already exists, it does nothing.
     *
     * @param id      Id to register with
     * @param factory An extension slot factory to use by the FlexibleExtensionContainer
     * @return
     */
    public static boolean tryRegister(String id, SlotFactory factory)
    {
        if (REGISTRY.containsKey(id))
            return false;

        REGISTRY.put(id, new SlotEntry(id, factory));
        return true;
    }

    ////////////////////////////////////////////////////////////
    // Capability support code
    //

    private static final ResourceLocation CAPABILITY_ID = new ResourceLocation("forge", "extra_slots");

    @CapabilityInject(ExtensionSlotContainer.class)
    public static Capability<ExtensionSlotContainer> CAPABILITY = null;

    public static void register()
    {
        // Internal capability, IStorage and default instances are meaningless.
        CapabilityManager.INSTANCE.register(ExtensionSlotContainer.class, new Capability.IStorage<ExtensionSlotContainer>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ExtensionSlotContainer> capability, ExtensionSlotContainer instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<ExtensionSlotContainer> capability, ExtensionSlotContainer instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, () -> null);

        MinecraftForge.EVENT_BUS.register(new EventHandlers());
    }

    public static ExtensionSlotContainer get(EntityLivingBase player)
    {
        return player.getCapability(CAPABILITY, null);
    }

    static class EventHandlers
    {
        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
        {
            if (!(event.getObject() instanceof EntityPlayer))
                return;

            event.addCapability(CAPABILITY_ID, new ICapabilitySerializable<NBTTagCompound>()
            {
                final ExtensionSlotContainer extensionContainer = new ExtensionSlotContainer((EntityPlayer) event.getObject(), REGISTRY);

                @Override
                public NBTTagCompound serializeNBT()
                {
                    return extensionContainer.serializeNBT();
                }

                @Override
                public void deserializeNBT(NBTTagCompound nbt)
                {
                    extensionContainer.deserializeNBT(nbt);
                }

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
                {
                    if (capability == CAPABILITY)
                        return true;
                    return false;
                }

                @Nullable
                @SuppressWarnings("unchecked")
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
                {
                    if (capability == CAPABILITY)
                        return (T) extensionContainer;
                    return null;
                }
            });
        }

        @SubscribeEvent
        public void entityTick(TickEvent.PlayerTickEvent event)
        {
            ExtensionSlotContainer instance = get(event.player);
            if (instance == null) return;
            instance.tickAllSlots();
        }
    }

    ////////////////////////////////////////////////////////////
    // Equipment container implementation
    //

    private final EntityLivingBase owner;
    private final ImmutableMap<String, IExtensionSlot> slotsMap;
    private final ImmutableList<IExtensionSlot> slots;

    private ExtensionSlotContainer(EntityLivingBase owner, Map<String, SlotEntry> slotEntries)
    {
        this.owner = owner;
        this.slotsMap = slotEntries.entrySet().stream()
                .map((e) -> Pair.of(e.getKey(), e.getValue().factory.apply(owner, this)))
                .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
        this.slots = ImmutableList.copyOf(this.slotsMap.values());
    }

    @Nonnull
    @Override
    public EntityLivingBase getOwner()
    {
        return owner;
    }

    @Nonnull
    @Override
    public ImmutableList<IExtensionSlot> getSlots()
    {
        return slots;
    }

    @Nullable
    @Override
    public IExtensionSlot getSlot(String slotId)
    {
        return slotsMap.get(slotId);
    }

    private void tickAllSlots()
    {
        for (IExtensionSlot slot : slots)
        {
            slot.onWornTick();
        }
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Map.Entry<String, IExtensionSlot> entry : slotsMap.entrySet())
        {
            String id = entry.getKey();
            IExtensionSlot slot = entry.getValue();
            if (slot.skipStorage()) continue;
            nbt.setTag(id.toString(), slot.serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        for (Map.Entry<String, IExtensionSlot> entry : slotsMap.entrySet())
        {
            String id = entry.getKey();
            IExtensionSlot slot = entry.getValue();
            if (slot.skipStorage()) continue;
            NBTTagCompound tag = nbt.getCompoundTag(id.toString());
            if (tag != null)
                slot.deserializeNBT(tag);
        }
    }
}
