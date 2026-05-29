/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml;

public interface I18NParser {
    String parseMessage(String i18nMessage, Object... args);
    String stripControlCodes(String toStrip);
}
