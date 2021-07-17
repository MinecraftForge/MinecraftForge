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

package net.minecraftforge.server.command;

import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fmllegacy.network.ConnectionType;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class TextComponentHelper
{
    private TextComponentHelper() {}

    /**
     * Detects when sending to a vanilla client and falls back to sending english,
     * since they don't have the lang data necessary to translate on the client.
     */
    public static BaseComponent createComponentTranslation(CommandSource source, final String translation, final Object... args)
    {
        if (isVanillaClient(source))
        {
            return new TextComponent(String.format(Language.getInstance().getOrDefault(translation), args));
        }
        return new TranslatableComponent(translation, args);
    }

    private static boolean isVanillaClient(CommandSource sender)
    {
        if (sender instanceof ServerPlayer)
        {
            ServerPlayer playerMP = (ServerPlayer) sender;
            ServerGamePacketListenerImpl channel = playerMP.connection;
            return NetworkHooks.getConnectionType(()->channel.connection) == ConnectionType.VANILLA;
        }
        return false;
    }
}
