/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This test mod provides two items for testing the Forge onStopUsing hook. Both items attempt to create an item that increases FOV and allows creative flight when used
 * <ul>
 *   <li>{@code stop_using_item:bad_scope}: Implements the item without the onStopUsing to demonstrate the problem.
 *       Should see that when selecting another hotbar slot or dropping the item, the FOV is not properly reverted and you remain flying.
 *   </li>
 *   <li>{@code stop_using_item:good_scope}: Implements the item with onStopUsing to test that the hook hook works.
 *       Should see that when selecting another hotbar slot or dropping the item, the FOV is properly reverted and you stop flying.
 *   </li>
 * </ul>
 */
@Mod(StopUsingItemTest.MODID)
public class StopUsingItemTest
{
	protected static final String MODID = "stop_using_item";
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	/** Current FOV change, consumed by the event.
	 * Good enough for a test mod as we only need one copy for the client player, in a real mod you probably want to reset this on world exit. */
	private static float fovChange = 1.0f;

	public StopUsingItemTest()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(modEventBus);
		modEventBus.addListener(this::addCreative);
		MinecraftForge.EVENT_BUS.addListener(this::onVanillaEvent);
	}

	/** Attempt at a "reverse scope" that also makes you fly without using the Forge method. Will not remove the speed if you scroll away or swap items */
	public static RegistryObject<Item> BAD = ITEMS.register("bad_scope", () -> new InvertedTelescope(new Item.Properties())
	{
		@Override
		public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living)
		{
			removeFov(living);
			return stack;
		}

		@Override
		public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int count)
		{
			removeFov(living);
		}
	});

	/** Successful "scope item" using the Forge method, all cases of stopping using the item will stop the FOV change */
	public static RegistryObject<Item> GOOD = ITEMS.register("good_scope", () -> new InvertedTelescope(new Item.Properties())
	{
		@Override
		public void onStopUsing(ItemStack stack, LivingEntity living, int count)
		{
			removeFov(living);
		}
	});

	private void addCreative(BuildCreativeModeTabContentsEvent event)
	{
		if (event.getTabKey() == CreativeModeTabs.COMBAT)
		{
			event.accept(BAD);
			event.accept(GOOD);
		}
	}

	private void onVanillaEvent(VanillaGameEvent event)
	{
		if (event.getVanillaEvent() == GameEvent.ITEM_INTERACT_FINISH && event.getCause() instanceof LivingEntity living && living.isUsingItem() && living.getUseItem().is(BAD.get()))
			InvertedTelescope.removeFov(living);
	}

	private static abstract class InvertedTelescope extends Item
	{
		public InvertedTelescope(Properties props)
		{
			super(props);
		}

		@Override
		public int getUseDuration(ItemStack stack)
		{
			return 72000;
		}

		@Override
		public UseAnim getUseAnimation(ItemStack stack)
		{
			return UseAnim.EAT;
		}

		@Override
		public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
		{
			player.startUsingItem(hand);
			player.getAbilities().mayfly = true;
			if (player.level().isClientSide)
				fovChange = 10f;
			return InteractionResultHolder.consume(player.getItemInHand(hand));
		}

		public static void removeFov(LivingEntity living)
		{
			if (living.level().isClientSide)
				fovChange = 1f;
			if (living instanceof Player player)
			{
				player.getAbilities().mayfly = player.isCreative();
				if (!player.isCreative())
					player.getAbilities().flying = false;
			}
		}
	}

	@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Bus.FORGE)
	public static class ClientEvents
	{
		@SubscribeEvent
		static void computeFovModifier(ComputeFovModifierEvent event)
		{
			event.setNewFovModifier(event.getFovModifier() * fovChange);
		}
	}
}
