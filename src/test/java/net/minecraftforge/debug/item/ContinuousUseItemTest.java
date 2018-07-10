/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ContinuousUseItemTest.MOD_ID, name = "Test for canContinueUsing", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class ContinuousUseItemTest
{
    static final String MOD_ID = "continuous_use_item_test";
    static final boolean ENABLED = false;

    @GameRegistry.ObjectHolder(TestItem.NAME)
    public static final Item TEST_ITEM = null;

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(
                    new TestItem().setRegistryName(MOD_ID, TestItem.NAME)
                                  .setUnlocalizedName(MOD_ID + "." + TestItem.NAME)
                                  .setCreativeTab(CreativeTabs.MISC)
            );
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation("minecraft:stick", "inventory"));
            }
        }
    }

    static class TestItem extends Item
    {
        static final String NAME = "test_item";

        TestItem()
        {
            maxStackSize = 1;
            setMaxDamage(60);
        }

        @Override
        public boolean hasEffect(ItemStack stack)
        {
            return true;
        }

        @Override
        public EnumAction getItemUseAction(ItemStack stack)
        {
            return EnumAction.BOW;
        }

        @Override
        public int getMaxItemUseDuration(ItemStack stack)
        {
            return 72000;
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }

        @Override
        public void onUsingTick(ItemStack stack, EntityLivingBase living, int count)
        {
            if (count % 10 == 0)
            {
                stack.damageItem(1, living);
            }
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
        {
            return slotChanged || oldStack.getItem() != newStack.getItem();
        }

        @Override
        public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack)
        {
            return oldStack.getItem() == newStack.getItem();
        }
    }
}
