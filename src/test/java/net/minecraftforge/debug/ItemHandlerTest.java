package net.minecraftforge.debug;

import com.google.common.collect.Range;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IExtractionManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerIterator;
import net.minecraftforge.items.InsertTransaction;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.CombinedWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

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

    public static class ExampleExtractionManager implements IExtractionManager
    {
        public NonNullList<ItemStack> stacks = NonNullList.create();
        public int slotExtractedFrom;

        @Override
        public int extract(@Nonnull ItemStack stack)
        {
            if (stack.getCount() == 1)
                return 1;
            else return stack.getCount() / 2;
        }

        @Override
        public void extractedStack(@Nonnull ItemStack stack, int slot)
        {
            for (ItemStack stack1 : stacks)
            {
                if (stack1.isEmpty()) continue;
                if (ItemHandlerHelper.canItemStacksStack(stack, stack1))
                {

                    int free = stack1.getMaxStackSize() - stack1.getCount();
                    stack1.grow(Math.min(free, stack.getCount()));
                    stack.shrink(Math.min(free, stack.getCount()));
                }
            }
            if (!stack.isEmpty())
                stacks.add(stack);
            slotExtractedFrom = slot;
        }

        @Override
        public boolean satisfied()
        {
            return stacks.size() >= 10;//lets only have 10 stacks
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

                if (heldItem == Items.STICK)
                {
                    event.setCanceled(true);
                    player.sendMessage(new TextComponentString(Float.toString(chestInv.calcRedStoneFromInventory(Range.all(), 100, false))));
                    player.sendMessage(new TextComponentString(Float.toString(chestInv.calcRedStoneFromInventory(Range.singleton(1), 100, false))));
                    player.sendMessage(new TextComponentString(Float.toString(chestInv0to9.calcRedStoneFromInventory(Range.all(), 100, false))));
                    player.sendMessage(new TextComponentString(Float.toString(chestInv0to9.calcRedStoneFromInventory(Range.all(), 100, true))));
                }

                if (heldItem == Items.BLAZE_ROD)
                {
                    event.setCanceled(true);

                    InsertTransaction transaction = chestInv19to27.insert(Range.singleton(0), new ItemStack(Items.GLOWSTONE_DUST, 48), false);
                    logger.info(transaction.getInsertedStack());
                    logger.info(transaction.getLeftoverStack());
                }
                if (heldItem == Items.GLOWSTONE_DUST)
                {
                    event.setCanceled(true);

                    IItemHandlerIterator iterator = chestInv.itemHandlerIterator();
                    while (iterator.hasNext())
                    {
                        ItemStack stack = iterator.next();
                        if (!stack.isEmpty())
                        {
                            logger.info(stack + " at index " + Integer.toString(iterator.previousIndex()));
                        }
                    }
                    logger.info("complete itr");
                }
                if (heldItem == Item.getItemFromBlock(Blocks.COBBLESTONE))
                {

                    event.setCanceled(true);

                    ExampleExtractionManager extractionManager = new ExampleExtractionManager();

                    combined.MultiExtract(TileEntityFurnace::isItemFuel, Range.all(), extractionManager, false);

                    logger.info("end of inv");
                    for (ItemStack stack : extractionManager.stacks)
                    {
                        logger.info(stack.toString());
                    }
                    logger.info(Integer.toString(extractionManager.slotExtractedFrom));
                }
            }
        }
    }
}
