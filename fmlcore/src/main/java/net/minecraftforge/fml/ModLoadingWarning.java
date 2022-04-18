/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import com.google.common.collect.Streams;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ModLoadingWarning
{
    /**
     * Mod Info for mod with warning
     */
    private final IModInfo modInfo;
    /**
     * The stage where this warning was encountered
     */
    private final ModLoadingStage warningStage;

    /**
     * I18N message to use for display
     */
    private final String i18nMessage;

    /**
     * Context for message display
     */
    private final List<Object> context;

    public ModLoadingWarning(final IModInfo modInfo, final ModLoadingStage warningStage, final String i18nMessage, Object... context) {
        this.modInfo = modInfo;
        this.warningStage = warningStage;
        this.i18nMessage = i18nMessage;
        this.context = Arrays.asList(context);
    }

    public String formatToString() {
        return Bindings.getMessageParser().get().parseMessage(i18nMessage, Streams.concat(Stream.of(modInfo, warningStage), context.stream()).toArray());
    }
}
