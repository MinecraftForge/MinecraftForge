/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@Mod("menu_screen_event_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MenuScreenEventTest
{

	@ObjectHolder("menu_screen_event_test:my_item")
	public static MyItem MY_ITEM;
	
	@ObjectHolder("menu_screen_event_test:my_menu")
	public static MenuType<MyMenu> MY_MENU_TYPE;

	@SubscribeEvent
	public static void itemRegistry(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new MyItem(new Item.Properties()).setRegistryName("menu_screen_event_test", "my_item"));
	}
	
	@SubscribeEvent
	public static void menuTypeRegistry(RegistryEvent.Register<MenuType<?>> event)
	{
		event.getRegistry().register(new MenuType<>(MyMenu::new).setRegistryName("menu_screen_event_test", "my_menu"));
	}

	@SubscribeEvent
	public static void registerMenuScreen(RegisterMenuScreensEvent event)
	{
		event.register(MY_MENU_TYPE, MyMenuScreen::new);
	}

	private static class MyItem extends Item
	{

		public MyItem(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
		{
			final ItemStack stack = player.getItemInHand(hand);
			
			if(!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
			{
				NetworkHooks.openGui(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, unused) -> {
					return new MyMenu(containerId, playerInventory);
				}, new TextComponent("My Menu")));
			}
			
			return InteractionResultHolder.success(stack);
		}
	}
	
	private static class MyMenu extends AbstractContainerMenu
	{

		protected MyMenu(int containerId, Inventory playerInventory)
		{
			super(MY_MENU_TYPE, containerId);
		}

		@Override
		public boolean stillValid(Player player)
		{
			return true;
		}
	}

	private static class MyMenuScreen extends AbstractContainerScreen<MyMenu>
	{

		public MyMenuScreen(MyMenu menu, Inventory playerInventory, Component title)
		{
			super(menu, playerInventory, title);
		}

		@Override
		protected void renderBg(PoseStack poseStack, float mouseX, int mouseY, int partialTicks)
		{
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/container/crafting_table.png"));
			blit(poseStack, leftPos, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight);
		}
	}
}
