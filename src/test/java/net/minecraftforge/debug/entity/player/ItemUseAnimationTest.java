/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.entity.player;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.extensions.IForgeArmPose;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ItemUseAnimationTest.MOD_ID)
public class ItemUseAnimationTest
{

    public static final String MOD_ID = "item_use_animation_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    private static final RegistryObject<Item> THING = ITEMS.register("thing", () -> new ThingItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).food(new FoodProperties.Builder().nutrition(4).build())));

    private static final UseAnim SWING = UseAnim.create("SWING");

    public ItemUseAnimationTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ClientRegistry.registerUseAnimation(SWING, new IForgeArmPose()
        {
            @Override
            public boolean isPoseTwoHanded()
            {
                return false;
            }

            @Override
            public <T extends LivingEntity> void applyTransform(HumanoidModel<T> model, T entity)
            {
                model.rightArm.xRot = (float) (Math.random() * Math.PI * 2);
            }
        });
    }

    private static class ThingItem extends Item
    {

        public ThingItem(Item.Properties props)
        {
            super(props);
        }

        @Override
        public UseAnim getUseAnimation(ItemStack stack)
        {
            return SWING;
        }

    }
}
