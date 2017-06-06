package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

@Mod(modid = StoreInItemTest.MODID, name = "StoreInItemTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class StoreInItemTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "storeinitemtest";
    @SidedProxy
    public static CommonProxy proxy = null;

    public static abstract class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            ItemTest.instance = new ItemTest();
            GameRegistry.register(ItemTest.instance, new ResourceLocation(MODID, "test_item"));
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

        @Override
        public boolean canPutIntoItemInventory(ItemStack stack, ItemStack container)
        {
            return stack.getMetadata() == 0;
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
            return (stack.getMetadata() == 0 ? "Can" : "Can't") + " be put into item storage";
        }
    }
}