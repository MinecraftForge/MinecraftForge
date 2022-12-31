/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.item.ItemPickupAllowedEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.pickup.ItemPickupQuery;
import net.minecraftforge.items.pickup.ItemPickupReasons;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod(ItemPreventPickupTest.ID)
public class ItemPreventPickupTest
{
    public static final boolean ENABLE = false;
    public static final String ID = "item_prevent_pickup_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final RegistryObject<Item> DUMMY = ITEMS.register("dummy", () -> new Item(new Item.Properties()) {
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag)
        {
            super.appendHoverText(stack, level, list, flag);

            if(ENABLE)
            {
                list.add(Component.literal("Valid pickup states:"));
                list.add(Component.literal("Player: Only If Sneaking"));
                list.add(Component.literal("Zombie: If standing on stone"));
                list.add(Component.literal("Hopper: Only if stacked more than 1"));
            }
        }
    });

    public static final ItemPickupQuery IF_MORE_THAN_ONE = (item, stack, level, pos, collector, pickupReasons) -> stack.getCount() >= 2;

    public ItemPreventPickupTest()
    {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);

        // event used to add dummy item to MISC / UTILITIES tab
        modBus.addListener(this::onBuildCreativeModeTabContents);
        // event used to add tooltips, used to display how items are allowed to be picked up
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
        // event used to disable hopper pickup type for diamond items
        MinecraftForge.EVENT_BUS.addListener(this::onItemTossed);
        // event used to add item pickup query for dummy item (hopper pickup only)
        // high priority to ensure we add queries before they are tested against
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onItemPickupAllowed_Pre);
        // event used to forcibly allow/disallow items from being picked up, based on a few states & item types
        MinecraftForge.EVENT_BUS.addListener(this::onItemPickupAllowed);

    }

    private void onBuildCreativeModeTabContents(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(DUMMY);
    }

    private void onItemPickupAllowed_Pre(ItemPickupAllowedEvent event)
    {
        // only for new dummy item, and only add the queries once per item entity
        if(ENABLE && event.getItemStack().is(DUMMY.get()) && !event.getEntity().getPersistentData().getBoolean("ForgeInitItemPickupQuery"))
        {
            // hoppers only pickup if item is stacked more than 1
            if(event.hasPickupReason(ItemPickupReasons.HOPPER)) event.getEntity().addPickupQuery(IF_MORE_THAN_ONE);
            event.getEntity().getPersistentData().putBoolean("ForgeInitItemPickupQuery", true);
        }
    }

    private void onItemPickupAllowed(ItemPickupAllowedEvent event)
    {
        if(ENABLE)
        {
            // forcibly disallow picking up iron ingots
            if(event.getItemStack().is(Tags.Items.INGOTS_IRON)) event.setResult(Event.Result.DENY);
            else if(event.getItemStack().is(DUMMY.get()))
            {
                // only allow picking up dummy item if player is sneaking
                if(event.hasPickupReason(ItemPickupReasons.PLAYER) && event.getCollector() instanceof Player player) event.setResult(player.isShiftKeyDown() ? Event.Result.ALLOW : Event.Result.DENY);
                    // only allow picking up dummy item if zombie is standing on stone
                else if(event.hasPickupReason(ItemPickupReasons.LIVING_ENTITY) && event.getCollector() instanceof Zombie zombie) event.setResult(event.getLevel().getBlockState(zombie.blockPosition().below()).is(BlockTags.BASE_STONE_OVERWORLD) ? Event.Result.ALLOW : Event.Result.DENY);
            }
        }
    }

    private void onItemTossed(ItemTossEvent event)
    {
        // disallow hoppers picking up diamonds
        if(ENABLE && event.getEntity().getItem().is(Tags.Items.GEMS_DIAMOND)) event.getEntity().disablePickupReason(ItemPickupReasons.HOPPER);
    }

    private void onItemTooltip(ItemTooltipEvent event)
    {
        if(ENABLE)
        {
            // add tooltip to say how these items are/are not allowed to be picked up
            if(event.getItemStack().is(Tags.Items.GEMS_DIAMOND)) event.getToolTip().add(Component.literal("Is not allowed to be picked up by Hoppers"));
            if(event.getItemStack().is(Tags.Items.INGOTS_IRON)) event.getToolTip().add(Component.literal("Picking up is forcibly disallowed via event"));
        }
    }
}
