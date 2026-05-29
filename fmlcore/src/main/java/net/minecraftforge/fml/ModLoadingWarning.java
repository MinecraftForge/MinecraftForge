/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

import net.minecraftsingularity.singularityspi.language.IModInfo;

import java.util.List;
import java.util.stream.Stream;

/**
 * @param modInfo Mod info for mod with warning
 * @param warningStage The stage where this warning was encountered
 * @param i18nMessage I18N message to use for display
 * @param context Context for message display
 */
public record ModLoadingWarning(IModInfo modInfo, ModLoadingStage warningStage, String i18nMessage, List<Object> context) {
    public ModLoadingWarning(final IModInfo modInfo, final ModLoadingStage warningStage, final String i18nMessage, Object... context) {
        this(modInfo, warningStage, i18nMessage, List.of(context));
    }

    public ModLoadingWarning {
        context = List.copyOf(context);
    }

    public String formatToString() {
        return Bindings.getMessageParser().get().parseMessage(i18nMessage, Stream.concat(Stream.of(modInfo, warningStage), context.stream()).toArray());
    }
}
