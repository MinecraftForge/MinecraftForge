/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.snapshots;

import com.mojang.logging.LogUtils;
import net.minecraft.CrashReport;
import net.minecraft.client.Options;
import org.slf4j.Logger;

public class ForgeSnapshotsMod
{
    public static final String BRANDING_NAME = "Froge";
    public static final String BRANDING_ID = "froge";
    private static final Logger LOGGER = LogUtils.getLogger();
    static boolean seenSnapshotWarning = false;

    public static void processOptions(Options.FieldAccess fieldAccess)
    {
        seenSnapshotWarning = fieldAccess.process("seenSnapshotWarning", seenSnapshotWarning);
    }

    public static void logStartupWarning()
    {
        LOGGER.warn("Froge is not officially supported. Bugs and instability are expected.");
    }

    public static void addCrashReportHeader(StringBuilder builder, CrashReport crashReport)
    {
        builder.append("---- Please note that Minecraft Forge DOES NOT support Froge builds. Bugs and instability are expected. ----\n");
    }
}
