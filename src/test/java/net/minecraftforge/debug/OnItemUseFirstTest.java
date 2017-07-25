package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

@Mod(modid = OnItemUseFirstTest.MODID, name = "OnItemUseFirstTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class OnItemUseFirstTest
{
    public static final boolean ENABLE = false;
    public static final String MODID = "onitemusefirsttest";
    @SidedProxy
    public static CommonProxy proxy = null;

    public static abstract class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            ItemTest.instance = new ItemTest();
            GameRegistry.register(ItemTest.instance, new ResourceLocation("onitemusefirsttest", "test_item"));
        }
    }

    public static final class ServerProxy extends CommonProxy
    {
    }

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            ModelLoader.setCustomModelResourceLocation(ItemTest.instance, 0, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ItemTest.instance, 1, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            proxy.preInit(event);
        }
    }

    public static class ItemTest extends Item
    {
        static Item instance;
        @Nonnull
        @Override
        public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
        {
            ItemStack stack = player.getHeldItem(hand);
            player.sendMessage(new TextComponentString("Called onItemUseFirst in thread " + Thread.currentThread().getName() + " with meta " + stack.getMetadata() + " in hand " + hand.name()));
            if (stack.getMetadata() == 1 && world.isRemote)
            {
                return EnumActionResult.PASS;
            }
            return EnumActionResult.SUCCESS;
        }

        @Override
        public CreativeTabs getCreativeTab()
        {
            return CreativeTabs.MISC;
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
            return "OnItemUseFirst Test Item: " + stack.getMetadata();
        }
    }
}