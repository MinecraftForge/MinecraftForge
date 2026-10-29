/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.locale.Language;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkContext;
import java.util.Locale;

/// @deprecated This class is scheduled for removal in 26.4 due to a lack of adoption by mods, in order to ease porting.
///             Please reach out to the Forge team if you are relying on this and would like to see it remain in Forge.
///             We may keep it or add it back in a future version if there is enough demand.
@Deprecated(forRemoval = true, since = "26.4")
public class TextComponentHelper {
    private TextComponentHelper() {}

    /**
     * Detects when sending to a vanilla client and falls back to sending english,
     * since they don't have the lang data necessary to translate on the client.
     */
    public static MutableComponent createComponentTranslation(CommandSource source, final String translation, final Object... args) {
        if (isVanillaClient(source))
            return Component.literal(String.format(Locale.ENGLISH, Language.getInstance().getOrDefault(translation), args));
        return Component.translatable(translation, args);
    }

    private static boolean isVanillaClient(CommandSource sender) {
        if (sender instanceof ServerPlayer playerMP)
            return NetworkContext.get(playerMP.connection.getConnection()).getType() == ConnectionType.VANILLA;
        return false;
    }
}
