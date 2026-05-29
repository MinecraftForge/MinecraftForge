/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading.moddiscovery;

import net.minecraftsingularity.singularityspi.language.IModFileInfo;
import net.minecraftsingularity.singularityspi.locating.ModFileLoadingException;

import java.util.Locale;
import java.util.Optional;

public class InvalidModFileException extends ModFileLoadingException {
    private static final long serialVersionUID = 1230464325917450374L;
    private final IModFileInfo modFileInfo;

    public InvalidModFileException(String message, IModFileInfo modFileInfo) {
        super(String.format(Locale.ROOT, "%s (%s)", message, Optional.ofNullable(modFileInfo).map(mf->mf.getFile().getFileName()).orElse("MISSING FILE NAME")));
        this.modFileInfo = modFileInfo;
    }

    public IModFileInfo getBrokenFile() {
        return modFileInfo;
    }
}
