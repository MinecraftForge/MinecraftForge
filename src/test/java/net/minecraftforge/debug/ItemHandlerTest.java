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
import net.minecraftforge.items.IItemHandlerObserver;
import net.minecraftforge.items.wrapper.CombinedWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

@Mod(modid = ItemHandlerTest.MODID)
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

                if (heldItem  == Items.STICK){
                    event.setCanceled(true);
                    ItemHandlerTest.logger.info("Found on Side: {} {} slots", event.getFace(), handler.size());
                }
                if (heldItem == Items.GLOWSTONE_DUST){
                    event.setCanceled(true);
                    for (Item item : ForgeRegistries.ITEMS){
                        if (item == Items.ENCHANTED_BOOK) continue;
                        NonNullList<ItemStack> stacks = NonNullList.create();
                        item.getSubItems(item.getCreativeTab(), stacks);
                        for (ItemStack stack : stacks){
                            for (int i = 0; i < handler.size(); i++)
                            {
                                if (handler.isStackValidForSlot(stack, i))
                                    logger.info("stack with {} and meta {} and NBT {} is valid for slot {}", stack.getItem().getUnlocalizedName(), stack.getItemDamage(), stack.getTagCompound(), i);
                            }
                        }
                    }
                }
            }
            if (block == Blocks.CHEST)
            {


                IItemHandler playerinv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                IItemHandler chestInv = world.getTileEntity(hitpos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace());
                IItemHandler chestInv0to9 = new RangedWrapper(chestInv, 0, 9);
                IItemHandler chestInv10to18 = new RangedWrapper(chestInv, 10, 18);
                IItemHandler chestInv19to27 = new RangedWrapper(chestInv, 19, 27);
                IItemHandler combined = new CombinedWrapper(chestInv0to9, chestInv10to18, chestInv19to27);

                if (heldItem == Items.BLAZE_ROD)
                {
                    event.setCanceled(true);

                    chestInv19to27.insert(OptionalInt.of(0), new ItemStack(Items.GLOWSTONE_DUST, 48), false);

                }
                if (heldItem == Items.STICK){
                    event.setCanceled(true);
                    IItemHandlerObserver observer = new IItemHandlerObserver()
                    {
                        @Override
                        public void onStackInserted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot)
                        {
                            logger.info("in {} was a stack inserted into slot {} tha old stack was {} and the new one is {}", handler, slot, oldStack, newStack);
                        }

                        @Override
                        public void onStackExtracted(IItemHandler handler, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, int slot)
                        {
                            logger.info("in {} was a stack extracted from slot {} tha old stack was {} and the new one is {}", handler, slot, oldStack, newStack);
                        }
                    };
                    chestInv.addObserver(observer);

                }
                if (heldItem == Items.GLOWSTONE_DUST)
                {
                    event.setCanceled(true);

                    for (ItemStack stack : chestInv)
                    {
                        if (!stack.isEmpty())
                        {
                            logger.info(stack);
                        }
                    }
                    logger.info("complete itr");
                }
            }
        }
    }
}
