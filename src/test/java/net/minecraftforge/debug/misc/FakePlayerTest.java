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

package net.minecraftforge.debug.misc;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("fake_player_test")
public class FakePlayerTest
{
    public static final boolean ENABLE = true;

    public FakePlayerTest()
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("fakeplayer")
                //Tests fake player creation & interaction with other entities in world
                .then(Commands.literal("attack")
                        .then(Commands.argument("target", EntityArgument.entity())
                        .executes(context -> {
                            FakePlayerFactory.getMinecraft(context.getSource().getLevel()).attack(EntityArgument.getEntity(context, "target"));
                            return 1;
                        }))
                )
                //Makes sure fake player does not crash when its `connection` is accessed [#7604]
                .then(Commands.literal("opencontainer")
                        .executes(context -> {
                            ServerPlayer fakePlayer = FakePlayerFactory.getMinecraft(context.getSource().getLevel());
                            InteractionHand hand = InteractionHand.MAIN_HAND;
                            ItemStack stack = Items.WRITABLE_BOOK.getDefaultInstance();
                            fakePlayer.setItemInHand(hand, stack);
                            fakePlayer.openItemGui(stack, hand);
                            return 1;
                        })
                ));
    }
}
