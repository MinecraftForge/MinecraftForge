package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = StoreInItemTest.MODID, name = "StoreInItemTest", version = "0.0.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class StoreInItemTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "storeinitemtest";
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
            ModelLoader.setCustomModelResourceLocation(ItemTest.instance, 0, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ItemTest.instance, 1, new ModelResourceLocation(new ResourceLocation(MODID, "test_item"), "inventory"));
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

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
        {
            if (isInCreativeTab(tab))
            {
                subItems.add(new ItemStack(this, 1, 0));
                subItems.add(new ItemStack(this, 1, 1));
            }
        }

        @Override
        public boolean canBePutIntoItemInventory(ItemStack stack, String location)
        {
            return stack.getMetadata()==0;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            if (stack.getMetadata()==0)
            {
                return "Can be put into an item inventory";
            }
            else
            {
                return "Can't be put into an item inventory";
            }
        }
    }
}