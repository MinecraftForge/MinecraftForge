/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;

/**
 * Thrown during early loading phase, and collected by the LoadingModList for handoff to the client
 * or server.
 */
public class EarlyLoadingException extends RuntimeException {
    public static class ExceptionData {
        private final IModInfo modInfo;
        private final String i18message;
        private final Object[] args;

        public ExceptionData(final String message, Object... args) {
            this(message, null, args);
        }

        public ExceptionData(final String message, final IModInfo modInfo, Object... args) {
            this.i18message = message;
            this.modInfo = modInfo;
            this.args = args;
        }

        public String getI18message() {
            return i18message;
        }

        public Object[] getArgs() {
            return args;
        }

        public IModInfo getModInfo() {
            return modInfo;
        }
    }
    private final List<ExceptionData> errorMessages;

    public List<ExceptionData> getAllData() {
        return errorMessages;
    }

    public EarlyLoadingException(final String message, final Throwable originalException, List<ExceptionData> errorMessages) {
        super(message, originalException);
        this.errorMessages = errorMessages;
    }


}
