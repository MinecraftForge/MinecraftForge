/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.List;
import java.util.stream.Collectors;

public class LoadingFailedException extends RuntimeException {
    private final List<ModLoadingException> loadingExceptions;

    public LoadingFailedException(final List<ModLoadingException> loadingExceptions) {
        this.loadingExceptions = loadingExceptions;
    }

    public List<ModLoadingException> getErrors() {
        return this.loadingExceptions;
    }

    @Override
    public String getMessage() {
        return "Loading errors encountered: " + this.loadingExceptions.stream().map(ModLoadingException::getMessage).collect(Collectors.joining(",\n\t", "[\n\t", "\n]"));
    }
}
