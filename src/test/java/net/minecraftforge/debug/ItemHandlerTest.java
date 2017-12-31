package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.logging.log4j.Logger;

import java.util.OptionalInt;

@Mod(modid = ItemHandlerTest.MODID, name = "ItemHandlerTest", version = "1.0")
public class ItemHandlerTest
{
    public static final String MODID = "itemhandlertest";
    public static final boolean ENABLE = true;
    public static Logger logger;

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(EventHandler.class);
        }
    }

    private static class EventHandler
    {

        @SubscribeEvent
        public static void onPlayerInteract(PlayerInteractEvent event)
        {
            EntityPlayer player = event.getEntityPlayer();
            World world = player.world;
            BlockPos hitpos = event.getPos();
            if (world.isRemote) return;
            ItemStack heldStack = player.getHeldItem(event.getHand());
            Item heldItem = heldStack.getItem();
            if (heldStack.isEmpty()) return;
            Block block = world.getBlockState(event.getPos()).getBlock();
            if (block == Blocks.BREWING_STAND)
            {
                IItemHandler handler = world.getTileEntity(hitpos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace());

                if (heldItem == Items.STICK)
                {
                    event.setCanceled(true);
                    ItemHandlerTest.logger.info("Found on Side: {} {} slots", event.getFace(), handler.size());
                }
                if (heldItem == Items.GLOWSTONE_DUST)
                {
                    event.setCanceled(true);
                    for (Item item : ForgeRegistries.ITEMS)
                    {
                        if (item == Items.ENCHANTED_BOOK) continue;
                        NonNullList<ItemStack> stacks = NonNullList.create();
                        item.getSubItems(item.getCreativeTab(), stacks);
                        for (ItemStack stack : stacks)
                        {
                            for (int i = 0; i < handler.size(); i++)
                            {
                                if (handler.isStackValidForSlot(i, stack))
                                    logger.info("stack with {} and meta {} and NBT {} is valid for slot {}", stack.getItem().getUnlocalizedName(), stack.getItemDamage(), stack.getTagCompound(), i);
                            }
                        }
                    }
                }
            }
            if (block == Blocks.CHEST)
            {
                IItemHandler chestInv = world.getTileEntity(hitpos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace());

                if (heldItem == Items.BLAZE_ROD)
                {
                    event.setCanceled(true);

                    chestInv.insert(OptionalInt.empty(), new ItemStack(Items.GLOWSTONE_DUST, 48), false);

                }
            }
        }
    }
}

