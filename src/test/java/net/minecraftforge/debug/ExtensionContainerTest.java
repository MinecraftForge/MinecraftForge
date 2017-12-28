package net.minecraftforge.debug;

import com.google.common.collect.ImmutableList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.customslots.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber
@Mod(modid = "extension_container_test", name = "Extension Container Test", version = "1.0.0")
public class ExtensionContainerTest
{
    private static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            ExtensionContainer.register();
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ExtensionSlotItemTest()
                .setRegistryName("slot_item_test").setUnlocalizedName("forge.slot_item_test")
                .setMaxStackSize(1).setMaxDamage(50).setCreativeTab(CreativeTabs.MISC));
    }

    public static class ExtensionSlotItemTest extends Item implements IExtensionSlotItem
    {
        private boolean onWornTick_received = false;

        // Equivalent to ItemArmor#onItemRightclick
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            ItemStack heldItem = playerIn.getHeldItem(handIn);

            IExtensionSlot slot = ExtensionContainer.get(playerIn).getNeck();

            ItemStack existing = slot.getContents();

            if (existing.isEmpty())
            {
                slot.setContents(heldItem.copy());
                heldItem.setCount(0);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
            }
            else
            {
                // For testing purposes only. A real implementation wouldn't do this. :P
                ItemHandlerHelper.giveItemToPlayer(playerIn, existing);
                slot.setContents(ItemStack.EMPTY);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
                // End testing stuff.

                // In reality, you would do this:
                //return new ActionResult<>(EnumActionResult.FAIL, heldItem);
            }
        }

        @Override
        public void onEquipped(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot)
        {
            if (!onWornTick_received)
            {
                logger.debug("onWornTick Received!");
                onWornTick_received = true;
            }
        }

        @Override
        public boolean canUnequip(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot)
        {
            // Prevents unequipping from the GUI
            return false;
        }

        @Override
        public void onWornTick(@Nonnull ItemStack stack, @Nonnull IExtensionSlot slot)
        {
            if (!onWornTick_received)
            {
                logger.debug("onWornTick Received!");
                onWornTick_received = true;
            }
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
        {
            return new ICapabilityProvider()
            {
                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
                {
                    if (capability == CapabilityExtensionSlotItem.INSTANCE)
                        return true;
                    return false;
                }

                @SuppressWarnings("unchecked")
                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
                {
                    if (capability == CapabilityExtensionSlotItem.INSTANCE)
                        return (T)ExtensionSlotItemTest.this;
                    return null;
                }
            };
        }
    }

    public static class ExtensionContainer implements IExtensionContainer, INBTSerializable<NBTTagCompound>
    {
        ////////////////////////////////////////////////////////////
        // Capability support code
        //

        private static final ResourceLocation CAPABILITY_ID = new ResourceLocation("forge", "extension_container_test");

        @CapabilityInject(ExtensionContainer.class)
        public static Capability<ExtensionContainer> CAPABILITY = null;

        public static void register()
        {
            // Internal capability, IStorage and default instances are meaningless.
            CapabilityManager.INSTANCE.register(ExtensionContainer.class, new Capability.IStorage<ExtensionContainer>()
            {
                @Nullable
                @Override
                public NBTBase writeNBT(Capability<ExtensionContainer> capability, ExtensionContainer instance, EnumFacing side)
                {
                    return null;
                }

                @Override
                public void readNBT(Capability<ExtensionContainer> capability, ExtensionContainer instance, EnumFacing side, NBTBase nbt)
                {

                }
            }, () -> null);

            MinecraftForge.EVENT_BUS.register(new EventHandlers());
        }

        public static ExtensionContainer get(EntityPlayer player)
        {
            return player.getCapability(CAPABILITY, null);
        }

        static class EventHandlers
        {
            @SubscribeEvent
            public void attachCapabilities(AttachCapabilitiesEvent<Entity> event)
            {
                if (event.getObject() instanceof EntityPlayer)
                {
                    event.addCapability(CAPABILITY_ID, new ICapabilitySerializable<NBTTagCompound>()
                    {
                        final ExtensionContainer extensionContainer = new ExtensionContainer((EntityPlayer) event.getObject());

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
            }

            @SubscribeEvent
            public void entityTick(TickEvent.PlayerTickEvent event)
            {
                ExtensionContainer instance = get(event.player);
                if (instance == null) return;
                instance.tickAllSlots();
            }
        }

        ////////////////////////////////////////////////////////////
        // Equipment container implementation
        //

        public static final ResourceLocation RING = new ResourceLocation("forge", "ring");
        public static final ResourceLocation TRINKET = new ResourceLocation("forge", "trinket");
        public static final ResourceLocation NECK = new ResourceLocation("forge", "neck");
        public static final ResourceLocation BELT = new ResourceLocation("forge", "belt");
        public static final ResourceLocation WRISTS = new ResourceLocation("forge", "wrists");
        public static final ResourceLocation ANKLES = new ResourceLocation("forge", "ankles");

        private final EntityLivingBase owner;
        private final ItemStackHandler inventory = new ItemStackHandler(8);
        private final ExtensionSlotItemHandler ring1 = new ExtensionSlotItemHandler(this, RING, inventory, 0);
        private final ExtensionSlotItemHandler ring2 = new ExtensionSlotItemHandler(this, RING, inventory, 1);
        private final ExtensionSlotItemHandler trinket1 = new ExtensionSlotItemHandler(this, TRINKET, inventory, 2);
        private final ExtensionSlotItemHandler trinket2 = new ExtensionSlotItemHandler(this, TRINKET, inventory, 3);
        private final ExtensionSlotItemHandler neck = new ExtensionSlotItemHandler(this, NECK, inventory, 4);
        private final ExtensionSlotItemHandler belt = new ExtensionSlotItemHandler(this, BELT, inventory, 5);
        private final ExtensionSlotItemHandler wrists = new ExtensionSlotItemHandler(this, WRISTS, inventory, 6);
        private final ExtensionSlotItemHandler ankles = new ExtensionSlotItemHandler(this, ANKLES, inventory, 7);
        private final ImmutableList<IExtensionSlot> slots = ImmutableList.of(
                ring1, ring2, trinket1, trinket2,
                neck, belt, wrists, ankles
        );

        private ExtensionContainer(EntityLivingBase owner)
        {
            this.owner = owner;
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

        @Nonnull
        public IExtensionSlot getRing1()
        {
            return ring1;
        }

        @Nonnull
        public IExtensionSlot getRing2()
        {
            return ring2;
        }

        @Nonnull
        public IExtensionSlot getTrinket1()
        {
            return trinket1;
        }

        @Nonnull
        public IExtensionSlot getTrinket2()
        {
            return trinket2;
        }

        @Nonnull
        public IExtensionSlot getNeck()
        {
            return neck;
        }

        @Nonnull
        public IExtensionSlot getBelt()
        {
            return belt;
        }

        @Nonnull
        public IExtensionSlot getWrists()
        {
            return wrists;
        }

        @Nonnull
        public IExtensionSlot getAnkles()
        {
            return ankles;
        }

        private void tickAllSlots()
        {
            for (IExtensionSlot slot : slots)
            {
                ((ExtensionSlotItemHandler) slot).onWornTick();
            }
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return inventory.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            inventory.deserializeNBT(nbt);
        }
    }
}