package net.minecraftforge.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.customslots.CapabilityExtensionSlotItem;
import net.minecraftforge.items.customslots.IExtensionSlot;
import net.minecraftforge.items.customslots.IExtensionSlotItem;
import net.minecraftforge.items.customslots.VanillaEquipment;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber
@Mod(modid = "slot_registry_test", name = "Slot Registry Test", version = "1.0.0")
public class ExtensionSlotRegistryTest
{
    private static final boolean ENABLED = true;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(new ExtensionSlotItemTest()
                    .setRegistryName("flex_slot_item_test").setUnlocalizedName("forge.slot_registry_test_item")
                    .setMaxStackSize(1).setMaxDamage(50).setCreativeTab(CreativeTabs.MISC));
        }
    }

    public static class ExtensionSlotItemTest extends Item implements IExtensionSlotItem
    {
        private boolean onWornTick_received = false;

        // Equivalent to ItemArmor#onItemRightclick
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            ItemStack heldItem = playerIn.getHeldItem(handIn);

            IExtensionSlot slot = VanillaEquipment.get(playerIn).getSlot(VanillaEquipment.HEAD);

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
            logger.debug("onEquipped Received!");
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

}