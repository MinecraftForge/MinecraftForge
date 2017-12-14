package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@Mod(modid = ItemClientTagTest.MOD_ID, name = "Item Share Tag Creative Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class ItemClientTagTest
{
    public static final String MOD_ID = "share_tag_creative_test";

    private static final ResourceLocation ITEM_NAME = new ResourceLocation(MOD_ID, "share_tag_item");

    @GameRegistry.ObjectHolder("share_tag_item")
    private static final Item TEST_ITEM = null;

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ItemClientTag().setRegistryName(ITEM_NAME));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation(ITEM_NAME, "inventory"));
        }
    }

    public static class ItemClientTag extends Item
    {
        private ItemClientTag()
        {
            setCreativeTab(CreativeTabs.MISC);
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            ItemStack heldItem = playerIn.getHeldItem(handIn);
            if(!worldIn.isRemote)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("full_tag", true);
                tag.setString("extra_data", "This is more data");
                heldItem.setTagCompound(tag);
                return new ActionResult<>(EnumActionResult.SUCCESS, heldItem.copy());
            }
            return new ActionResult<>(EnumActionResult.PASS, heldItem);
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
        {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag != null && tag.hasKey("full_tag", Constants.NBT.TAG_BYTE))
            {
                if(tag.getBoolean("full_tag"))
                {
                    tooltip.add("You should only see this message");
                    tooltip.add("if you are on an integrated server");
                    tooltip.add("or you are in creative mode");
                }
                else
                {
                    tooltip.add("You should only see this message");
                    tooltip.add("if you are on a server and you are");
                    tooltip.add("in survival mode");
                }

                tooltip.add(TextFormatting.RED + "Full Tag: " + tag.getBoolean("full_tag"));
                tooltip.add(TextFormatting.RED + "Extra data: " + tag.getString("extra_data"));
            }
        }

        @Nullable
        @Override
        public NBTTagCompound getNBTShareTag(ItemStack stack)
        {
            NBTTagCompound tag = stack.getTagCompound();
            if(tag != null)
            {
                NBTTagCompound clientTag = tag.copy();
                clientTag.setBoolean("full_tag", false);
                clientTag.removeTag("extra_data");
                return clientTag;
            }
            return tag;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Share Tag Item";
        }
    }
}
