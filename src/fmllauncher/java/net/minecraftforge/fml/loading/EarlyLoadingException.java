/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

    EarlyLoadingException(final String message, final Throwable originalException, List<ExceptionData> errorMessages) {
        super(message, originalException);
        this.errorMessages = errorMessages;
    }


}
