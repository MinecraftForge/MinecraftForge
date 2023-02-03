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
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.item.ItemPickupAllowedEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.pickup.ItemPickupPredicate;
import net.minecraftforge.items.pickup.ItemPickupReasons;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * The ItemPreventPickupTest is the test mod for testing the {@link ItemPickupReasons} system.
 * <p>When this mod is enabled the following is enabled:
 *
 * <p>This mod adds a new item to the game {@code item_prevent_pickup_test:dummy}
 * in which should only be picked up under the following circumstances
 * <ul>
 *     <li>If Player is sneaking</li>
 *     <li>If Zombie is standing on a Stone block</li>
 *     <li>If Hopper is trying to pick up and it is stacked to 2 or more</li>
 * </ul>
 *
 * <p>Items tagged with {@link Tags.Items#INGOTS_IRON} should be forcibly disallowed from being picked up
 * via the {@link ItemPickupAllowedEvent}.
 *
 * <p>Items tagged with {@link Tags.Items#GEMS_DIAMOND} should not be allowed to be picked up {@link ItemPickupReasons#HOPPER} reasoning's.
 *
 * <p><i>All of the above tests & requirements are provided in game on each of the items tooltips.</i>
 * <p>
 * <p>Additional test which has to be done manually, is to validate that the existing vanilla way to disable item pickup
 * with commands / nbt still works as intended.
 * <p>Using the following command should spawn a {@link Items#STONE_AXE} with the nbt data to mark is a not being able to be picked up.
 * (2 blocks above the command sender)
 * <p>{@code /summon minecraft:item ~ ~2 ~ {Item:{id:"minecraft:stone_axe",Count:1},PickupDelay:-32768}}
 * <p>The important part here is the {@code PickupDelay:-32768} property, this exact value marks the item as not being able to be picked up.
 */
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

            if (ENABLE)
            {
                list.add(Component.literal("Valid pickup states:"));
                list.add(Component.literal("Player: Only If Sneaking"));
                list.add(Component.literal("Zombie: If standing on stone"));
                list.add(Component.literal("Hopper: Only if stacked more than 1"));
            }
        }
    });

    // if pickup is for hoppers && trying to pickup 2 or more of dummy item
    public static final ItemPickupPredicate HOPPER_IF_DUMMY_MORE_THAN_ONE = (item, stack, level, pos, collector, pickupReasons) -> {
        // not dummy item or not for hopper pickup, return true, skip over to over queries & reasons
        // returning false here, would disallow the pickup, if it's not dummy or not for hoppers (& if dummy is not registered)
        if (!DUMMY.isPresent() || !pickupReasons.contains(ItemPickupReasons.HOPPER) || !stack.is(DUMMY.get())) return true;
        // require 2 or more for when hopper is picking up dummy item
        return stack.getCount() >= 2;
    };

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
        // event used to forcibly allow/disallow items from being picked up, based on a few states & item types
        MinecraftForge.EVENT_BUS.addListener(this::onItemPickupAllowed);

    }

    private void onBuildCreativeModeTabContents(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(DUMMY);
    }

    private void onItemPickupAllowed(ItemPickupAllowedEvent event)
    {
        if (ENABLE)
        {
            var stack = event.getEntity().getItem();

            // register predicate for hopper picking up dummy item
            event.getEntity().addPickupPredicate(HOPPER_IF_DUMMY_MORE_THAN_ONE);

            // forcibly disallow picking up iron ingots
            if (stack.is(Tags.Items.INGOTS_IRON)) event.setResult(Event.Result.DENY);
            else if (stack.is(DUMMY.get()))
            {
                // only allow picking up dummy item if player is sneaking
                if (event.hasPickupReason(ItemPickupReasons.PLAYER) && event.getCollector() instanceof Player player)
                    event.setResult(player.isShiftKeyDown() ? Event.Result.ALLOW : Event.Result.DENY);
                // only allow picking up dummy item if zombie is standing on stone
                else if (event.hasPickupReason(ItemPickupReasons.LIVING_ENTITY) && event.getCollector() instanceof Zombie zombie)
                    event.setResult(event.getEntity().level.getBlockState(zombie.blockPosition().below()).is(BlockTags.BASE_STONE_OVERWORLD) ? Event.Result.ALLOW : Event.Result.DENY);
            }
        }
    }

    private void onItemTossed(ItemTossEvent event)
    {
        // disallow hoppers picking up diamonds
        if (ENABLE && event.getEntity().getItem().is(Tags.Items.GEMS_DIAMOND)) event.getEntity().disablePickupReason(ItemPickupReasons.HOPPER);
    }

    private void onItemTooltip(ItemTooltipEvent event)
    {
        if (ENABLE)
        {
            // add tooltip to say how these items are/are not allowed to be picked up
            if (event.getItemStack().is(Tags.Items.GEMS_DIAMOND)) event.getToolTip().add(Component.literal("Is not allowed to be picked up by Hoppers"));
            if (event.getItemStack().is(Tags.Items.INGOTS_IRON)) event.getToolTip().add(Component.literal("Picking up is forcibly disallowed via event"));
        }
    }
}
