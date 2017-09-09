package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

@Mod(modid = OnItemUseFirstTest.MODID, name = "OnItemUseFirstTest", version = "0.0.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class OnItemUseFirstTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "onitemusefirsttest";
    @SidedProxy
    public static CommonProxy proxy = null;

    public static abstract class CommonProxy
    {
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            ItemTest.instance = new ItemTest().setCreativeTab(CreativeTabs.MISC);
            event.getRegistry().register(ItemTest.instance);
        }
    }

    public static final class ServerProxy extends CommonProxy
    {
    }

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        public void registerItem(RegistryEvent.Register<Item> event)
        {
            super.registerItem(event);
            for (int i = 0; i < EnumActionResult.values().length; i++)
            {
                ModelLoader.setCustomModelResourceLocation(ItemTest.instance, i, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));
            }
        }
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        if (ENABLE)
        {
            proxy.registerItem(event);
        }
    }

    public static class ItemTest extends Item
    {
        static Item instance;
        public ItemTest()
        {
            setRegistryName(MODID, "test_item");
            setHasSubtypes(true);
        }
        @Nonnull
        @Override
        public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
        {
            ItemStack stack = player.getHeldItem(hand);
            EnumActionResult ret = EnumActionResult.values()[stack.getMetadata()];
            player.sendMessage(new TextComponentString("Called onItemUseFirst in thread " + Thread.currentThread().getName() + ", returns " + ret + " in hand " + hand.name()));
            return ret;
        }

        @Override
        public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
        {
            subItems.add(new ItemStack(itemIn, 1, 0));
            subItems.add(new ItemStack(itemIn, 1, 1));
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "OnItemUseFirst Returns: " + EnumActionResult.values()[stack.getMetadata()];
        }
    }
}

