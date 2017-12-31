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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class VanillaEquipment implements IExtensionContainer
{
    public static final String HEAD = EntityEquipmentSlot.HEAD.getName();
    public static final String CHEST = EntityEquipmentSlot.CHEST.getName();
    public static final String LEGS = EntityEquipmentSlot.LEGS.getName();
    public static final String FEET = EntityEquipmentSlot.FEET.getName();
    public static final String OFFHAND = EntityEquipmentSlot.OFFHAND.getName();
    public static final String MAINHAND = EntityEquipmentSlot.MAINHAND.getName();

    public static void register()
    {
        registerCapability();
        registerVanillaSlots();
    }

    ////////////////////////////////////////////////////////////
    // Capability support code
    //

    private static final ResourceLocation CAPABILITY_ID = new ResourceLocation("forge", "vanilla_slots");

    @CapabilityInject(VanillaEquipment.class)
    public static Capability<VanillaEquipment> CAPABILITY = null;

    public static void registerCapability()
    {
        // Internal capability, IStorage and default instances are meaningless.
        CapabilityManager.INSTANCE.register(VanillaEquipment.class, new Capability.IStorage<VanillaEquipment>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<VanillaEquipment> capability, VanillaEquipment instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<VanillaEquipment> capability, VanillaEquipment instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, () -> null);

        MinecraftForge.EVENT_BUS.register(new VanillaEquipment.EventHandlers());
    }

    public static VanillaEquipment get(EntityPlayer player)
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

            // No serialization needed
            event.addCapability(CAPABILITY_ID, new ICapabilityProvider()
            {
                final VanillaEquipment extensionContainer = new VanillaEquipment((EntityPlayer) event.getObject());

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
    }

    ////////////////////////////////////////////////////////////
    // Equipment container implementation
    //

    static void registerVanillaSlots()
    {
        registerVanillaSlot(HEAD, EntityEquipmentSlot.HEAD);
        registerVanillaSlot(CHEST, EntityEquipmentSlot.CHEST);
        registerVanillaSlot(LEGS, EntityEquipmentSlot.LEGS);
        registerVanillaSlot(FEET, EntityEquipmentSlot.FEET);
        registerVanillaSlot(OFFHAND, EntityEquipmentSlot.OFFHAND);
        registerVanillaSlot(MAINHAND, EntityEquipmentSlot.MAINHAND);
    }

    private static void registerVanillaSlot(final String slot, final EntityEquipmentSlot source)
    {
        ExtensionSlotContainer.tryRegister(slot,
                (entity, container) -> new Slot(container, slot, source));
    }

    private final EntityLivingBase owner;
    private ImmutableList<IExtensionSlot> slots = null;

    public VanillaEquipment(EntityLivingBase owner)
    {
        this.owner = owner;

    }

    @Nonnull
    @Override
    public ImmutableList<IExtensionSlot> getSlots()
    {
        if (slots == null)
        {
            IExtensionContainer container = ExtensionSlotContainer.get(owner);
            this.slots = ImmutableList.of(
                    container.getSlot(HEAD),
                    container.getSlot(CHEST),
                    container.getSlot(LEGS),
                    container.getSlot(FEET),
                    container.getSlot(OFFHAND),
                    container.getSlot(MAINHAND)
            );
        }
        return slots;
    }

    @Nullable
    @Override
    public IExtensionSlot getSlot(String slotId)
    {
        // Ensure we have loaded the list from the extension container
        getSlots();
        if (HEAD.equals(slotId)) return slots.get(0);
        if (CHEST.equals(slotId)) return slots.get(1);
        if (LEGS.equals(slotId)) return slots.get(2);
        if (FEET.equals(slotId)) return slots.get(3);
        if (OFFHAND.equals(slotId)) return slots.get(4);
        if (MAINHAND.equals(slotId)) return slots.get(5);
        return null;
    }

    @Nonnull
    @Override
    public EntityLivingBase getOwner()
    {
        return owner;
    }

    private static class Slot implements IExtensionSlot
    {
        private final String id;
        private final EntityEquipmentSlot slot;
        private final IExtensionContainer container;

        private Slot(IExtensionContainer container, String id, EntityEquipmentSlot slot)
        {
            this.id = id;
            this.slot = slot;
            this.container = container;
        }

        @Nonnull
        @Override
        public IExtensionContainer getContainer()
        {
            return container;
        }

        @Override
        public String getId()
        {
            return id;
        }

        @Nonnull
        @Override
        public String getType()
        {
            return id;
        }

        @Nonnull
        @Override
        public ItemStack getContents()
        {
            return container.getOwner().getItemStackFromSlot(slot);
        }

        @Override
        public void setContents(@Nonnull ItemStack stack)
        {
            container.getOwner().setItemStackToSlot(slot, stack);
        }

        @Override
        public boolean canEquip(@Nonnull ItemStack stack)
        {
            if (stack.getItem().isValidArmor(stack, slot, container.getOwner()))
                return true;
            return IExtensionSlot.super.canEquip(stack);
        }

        // Vanilla equipment slots already do their own saving,
        // so storing in the FlexibleExtensionContainer is not needed.
        @Override
        public boolean skipStorage()
        {
            return true;
        }
    }
}
