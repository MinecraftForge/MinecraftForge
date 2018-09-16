/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VersionChecker
{
    public enum Status
    {
        PENDING(),
        FAILED(),
        UP_TO_DATE(),
        OUTDATED(3, true),
        AHEAD(),
        BETA(),
        BETA_OUTDATED(6, true);

        final int sheetOffset;
        final boolean draw, animated;

        Status()
        {
            this(0, false, false);
        }

        Status(int sheetOffset)
        {
            this(sheetOffset, true, false);
        }

        Status(int sheetOffset, boolean animated)
        {
            this(sheetOffset, true, animated);
        }

        Status(int sheetOffset, boolean draw, boolean animated)
        {
            this.sheetOffset = sheetOffset;
            this.draw = draw;
            this.animated = animated;
        }

        public int getSheetOffset()
        {
            return sheetOffset;
        }

        public boolean shouldDraw()
        {
            return draw;
        }

        public boolean isAnimated()
        {
            return animated;
        }

    }

    public static class CheckResult
    {
        public final Status status;
        @Nullable
        public final ComparableVersion target;
        public final Map<ComparableVersion, String> changes;
        @Nullable
        public final String url;

        private CheckResult(Status status, @Nullable ComparableVersion target, @Nullable Map<ComparableVersion, String> changes, @Nullable String url)
        {
            this.status = status;
            this.target = target;
            this.changes = changes == null ? Collections.<ComparableVersion, String>emptyMap() : Collections.unmodifiableMap(changes);
            this.url = url;
        }
    }

    public static void startVersionCheck()
    {
    }

    // Gather a list of mods that have opted in to this update system by providing a URL.
    public static Map<ModContainer, URL> gatherMods()
    {
        Map<ModContainer, URL> ret = new HashMap<>();
        return ret;
    }

    private static Map<ModContainer, CheckResult> results = new ConcurrentHashMap<>();

    public static CheckResult getResult(ModInfo mod)
    {
        return new CheckResult(Status.PENDING, null, null, null);
    }

}
