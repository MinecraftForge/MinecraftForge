package net.minecraftforge.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

@Mod(modid = NBTShareTagItemTest.MOD_ID, name = "NBTShareTag Item Test", version = "1.0.0")
public class NBTShareTagItemTest
{
    public static final String MOD_ID = "nbtsharetagitemtest";
    private static final ResourceLocation itemName = new ResourceLocation(MOD_ID, "nbt_share_tag_item");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ShareTagItem item = new ShareTagItem();
        item.setRegistryName(itemName);
        GameRegistry.register(item);

        ItemStack crafted = new ItemStack(item);
        NBTTagCompound craftedNBT = new NBTTagCompound();
        craftedNBT.setBoolean("crafted", true);
        crafted.setTagCompound(craftedNBT);
        CraftingManager.getInstance().addShapelessRecipe(crafted, new ItemStack(Items.STICK));
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
        public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
        {
            ItemStack creativeMenuItem = new ItemStack(itemIn);
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
