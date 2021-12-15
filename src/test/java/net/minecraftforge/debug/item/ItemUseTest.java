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

package net.minecraftforge.debug.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.ItemUseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("item_use_event")
@Mod.EventBusSubscriber
/**
 * The ItemUseTest will allow Wooden Pickaxes to place torches as if they were a torch item, taking durability damage in the process.
 * The use of the pickaxe should be completely seamless - as if you were physically holding a torch item, including all block-interactions.
 * The pickaxe will not take durability damage in creative.
 */
public class ItemUseTest
{

    @SubscribeEvent
    public static void itemUse(ItemUseEvent event)
    {
        UseOnContext ctx = event.getContext();
        if (ctx.getItemInHand().getItem() == Items.WOODEN_PICKAXE)
        {
            if (Items.TORCH.useOn(ctx).consumesAction())
            {
                ctx.getItemInHand().grow(1); // Grow call is because the torch will shrink the stack. The "clean" way is to make a new context.
                ctx.getPlayer().getItemInHand(ctx.getHand()).hurtAndBreak((int) 1, ctx.getPlayer(),
                        p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }
}
