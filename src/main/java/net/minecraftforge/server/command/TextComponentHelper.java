/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.ConnectionType;
import net.minecraftforge.fml.network.NetworkHooks;

public class TextComponentHelper
{
    private TextComponentHelper() {}

    /**
     * Detects when sending to a vanilla client and falls back to sending english,
     * since they don't have the lang data necessary to translate on the client.
     */
    public static TextComponent createComponentTranslation(ICommandSource source, final String translation, final Object... args)
    {
        if (isVanillaClient(source))
        {
            return new StringTextComponent(String.format(LanguageMap.getInstance().getOrDefault(translation), args));
        }
        return new TranslationTextComponent(translation, args);
    }

    private static boolean isVanillaClient(ICommandSource sender)
    {
        if (sender instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity playerMP = (ServerPlayerEntity) sender;
            ServerPlayNetHandler channel = playerMP.connection;
            return NetworkHooks.getConnectionType(()->channel.connection) == ConnectionType.VANILLA;
        }
        return false;
    }
}
