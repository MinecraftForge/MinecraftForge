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

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CustomFishingRodTest.MOD_ID)
public class CustomFishingRodTest
{

    public static final String MOD_ID = "custom_fishing_rod_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> FISHING_ROD = ITEMS.register("fishing_rod", () -> new FishingRodItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1)));

    public CustomFishingRodTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = MOD_ID)
    private static class ClientEvents
    {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent e)
        {
            e.enqueueWork(() -> ItemProperties.register(FISHING_ROD.get(), new ResourceLocation(MOD_ID, "cast"), (stack, level, entity, i) -> {
                if (entity != null)
                {
                    boolean inMainHand = entity.getMainHandItem() == stack;
                    boolean inOffHand = entity.getOffhandItem() == stack;
                    if (entity.getMainHandItem().is(FISHING_ROD.get()))
                    {
                        inOffHand = false;
                    }
                    if (inMainHand || inOffHand)
                    {
                        if (entity instanceof Player player && player.fishing != null)
                        {
                            return 1.0F;
                        }
                    }

                }
                return 0.0F;
            }));
        }

    }

}
