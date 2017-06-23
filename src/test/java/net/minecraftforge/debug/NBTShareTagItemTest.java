package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import javax.annotation.Nullable;

@Mod(modid = NBTShareTagItemTest.MODID, name = "NBTShareTag Item Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class NBTShareTagItemTest
{
    public static final String MODID = "nbtsharetagitemtest";
    private static final ResourceLocation itemName = new ResourceLocation(MODID, "nbt_share_tag_item");
    @ObjectHolder("nbt_share_tag_item")
    public static final Item TEST_ITEM = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
        {
            ItemStack crafted = new ItemStack(TEST_ITEM);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("crafted", true);
            crafted.setTagCompound(nbt);

            GameRegistry.addShapelessRecipe(new ResourceLocation(MODID, "nbt_share"), null, crafted, Ingredient.func_193367_a(Items.STICK));
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ShareTagItem().setRegistryName(itemName));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation(itemName, "inventory"));
        }
    }

    public static class ShareTagItem extends Item
    {
        public ShareTagItem()
        {
            setCreativeTab(CreativeTabs.MISC);
        }

        @Nullable
        @Override
        public NBTTagCompound getNBTShareTag(ItemStack stack)
        {
            NBTTagCompound networkNBT = stack.getTagCompound();
            if (networkNBT == null)
                networkNBT = new NBTTagCompound();
            networkNBT.setBoolean("nbtShareTag", true);
            return networkNBT;
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
        {
            if (!this.isInCreativeTab(tab))
                return;
            ItemStack creativeMenuItem = new ItemStack(this);
            NBTTagCompound creativeMenuNBT = new NBTTagCompound();
            creativeMenuNBT.setBoolean("creativeMenu", true);
            creativeMenuItem.setTagCompound(creativeMenuNBT);
            subItems.add(creativeMenuItem);
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            NBTTagCompound tagCompound = stack.getTagCompound();
            String name = "NBTShareTagItem: ";
            if (tagCompound == null)
            {
                name += "From /give. ";
            }
            else
            {
                if (tagCompound.getBoolean("nbtShareTag"))
                    name += "Sent from server. ";

                if (tagCompound.getBoolean("creativeMenu"))
                    name += "From creative menu. ";

                if (tagCompound.getBoolean("crafted"))
                    name += "From crafting. ";
            }
            return name;
        }
    }
}
