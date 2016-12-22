package net.minecraftforge.test;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "customshieldstests", name = "CustomShieldsTest", version = "0.0.0")
public class CustomShieldsTest
{
    public static final boolean ENABLE = true;

    public static ItemCustomShield testShield = new ItemCustomShield();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            GameRegistry.register(testShield.setRegistryName("test_shield").setUnlocalizedName("test_shield"));
        }
    }

    private static class ItemCustomShield extends Item
    {

        public ItemCustomShield()
        {
            this.maxStackSize = 1;
            this.setCreativeTab(CreativeTabs.COMBAT);
            this.setMaxDamage(1424);
            this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter()
            {
                @SideOnly(Side.CLIENT)
                public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
                {
                    return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
                }
            });
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
        }

        public EnumAction getItemUseAction(ItemStack stack)
        {
            return EnumAction.BLOCK;
        }

        public int getMaxItemUseDuration(ItemStack stack)
        {
            return 72000;
        }

        public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn)
        {
            ItemStack itemstack = worldIn.getHeldItem(playerIn);
            worldIn.setActiveHand(playerIn);
            return new ActionResult(EnumActionResult.SUCCESS, itemstack);
        }

        @Override public boolean isShield(ItemStack stack)
        {
            return true;
        }
    }
}
