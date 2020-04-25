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

package net.minecraftforge.fml;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraftforge.fml.VersionChecker.Status.*;

public class VersionChecker
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);

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
        @Nonnull
        public final Status status;
        @Nullable
        public final ComparableVersion target;
        @Nullable
        public final Map<ComparableVersion, String> changes;
        @Nullable
        public final String url;

        private CheckResult(@Nonnull Status status, @Nullable ComparableVersion target, @Nullable Map<ComparableVersion, String> changes, @Nullable String url)
        {
            this.status = status;
            this.target = target;
            this.changes = changes == null ? Collections.<ComparableVersion, String>emptyMap() : Collections.unmodifiableMap(changes);
            this.url = url;
        }
    }

    public static void startVersionCheck()
    {
        new Thread("Forge Version Check")
        {
            @Override
            public void run()
            {
                if (!FMLConfig.runVersionCheck())
                {
                    LOGGER.info("Global Forge version check system disabled, no further processing.");
                    return;
                }

                gatherMods().forEach(this::process);
            }

            /**
             * Opens stream for given URL while following redirects
             */
            private InputStream openUrlStream(URL url) throws IOException
            {
                URL currentUrl = url;
                for (int redirects = 0; redirects < MAX_HTTP_REDIRECTS; redirects++)
                {
                    URLConnection c = currentUrl.openConnection();
                    if (c instanceof HttpURLConnection)
                    {
                        HttpURLConnection huc = (HttpURLConnection) c;
                        huc.setInstanceFollowRedirects(false);
                        int responseCode = huc.getResponseCode();
                        if (responseCode >= 300 && responseCode <= 399)
                        {
                            try
                            {
                                String loc = huc.getHeaderField("Location");
                                currentUrl = new URL(currentUrl, loc);
                                continue;
                            }
                            finally
                            {
                                huc.disconnect();
                            }
                        }
                    }

                    return c.getInputStream();
                }
                throw new IOException("Too many redirects while trying to fetch " + url);
            }

            @SuppressWarnings("UnstableApiUsage")
            private void process(IModInfo mod)
            {
                Status status = PENDING;
                ComparableVersion target = null;
                Map<ComparableVersion, String> changes = null;
                String display_url = null;
                try
                {
                    URL url = mod.getUpdateURL();
                    LOGGER.info("[{}] Starting version check at {}", mod.getModId(), url.toString());

                    InputStream con = openUrlStream(url);
                    String data = new String(ByteStreams.toByteArray(con), StandardCharsets.UTF_8);
                    con.close();

                    LOGGER.debug("[{}] Received version check data:\n{}", mod.getModId(), data);


                    @SuppressWarnings("unchecked")
                    Map<String, Object> json = new Gson().fromJson(data, Map.class);
                    @SuppressWarnings("unchecked")
                    Map<String, String> promos = (Map<String, String>)json.get("promos");
                    display_url = (String)json.get("homepage");

                    String mcVersion = MCPVersion.getMCVersion();
                    String rec = promos.get(mcVersion + "-recommended");
                    String lat = promos.get(mcVersion + "-latest");
                    ComparableVersion current = new ComparableVersion(mod.getVersion().toString());

                    if (rec != null)
                    {
                        ComparableVersion recommended = new ComparableVersion(rec);
                        int diff = recommended.compareTo(current);

                        if (diff == 0)
                            status = UP_TO_DATE;
                        else if (diff < 0)
                        {
                            status = AHEAD;
                            if (lat != null)
                            {
                                ComparableVersion latest = new ComparableVersion(lat);
                                if (current.compareTo(latest) < 0)
                                {
                                    status = OUTDATED;
                                    target = latest;
                                }
                            }
                        }
                        else
                        {
                            status = OUTDATED;
                            target = recommended;
                        }
                    }
                    else if (lat != null)
                    {
                        ComparableVersion latest = new ComparableVersion(lat);
                        if (current.compareTo(latest) < 0)
                            status = BETA_OUTDATED;
                        else
                            status = BETA;
                        target = latest;
                    }
                    else
                        status = BETA;

                    LOGGER.info("[{}] Found status: {} Current: {} Target: {}", mod.getModId(), status, current, target);

                    changes = new LinkedHashMap<>();
                    @SuppressWarnings("unchecked")
                    Map<String, String> tmp = (Map<String, String>)json.get(mcVersion);
                    if (tmp != null)
                    {
                        List<ComparableVersion> ordered = new ArrayList<>();
                        for (String key : tmp.keySet())
                        {
                            ComparableVersion ver = new ComparableVersion(key);
                            if (ver.compareTo(current) > 0 && (target == null || ver.compareTo(target) < 1))
                            {
                                ordered.add(ver);
                            }
                        }
                        Collections.sort(ordered);

                        for (ComparableVersion ver : ordered)
                        {
                            changes.put(ver, tmp.get(ver.toString()));
                        }
                    }
                }
                catch (Exception e)
                {
                    LOGGER.warn("Failed to process update information", e);
                    status = FAILED;
                }
                results.put(mod, new CheckResult(status, target, changes, display_url));
            }
        }.start();
    }

    // Gather a list of mods that have opted in to this update system by providing a URL.
    private static List<IModInfo> gatherMods()
    {
        List<IModInfo> ret = new LinkedList<>();
        for (ModInfo info : ModList.get().getMods()) {
            URL url = info.getUpdateURL();
            if (url != null)
                ret.add(info);
        }
        return ret;
    }

    private static Map<IModInfo, CheckResult> results = new ConcurrentHashMap<>();
    private static final CheckResult PENDING_CHECK = new CheckResult(PENDING, null, null, null);

    public static CheckResult getResult(IModInfo mod)
    {
        return results.getOrDefault(mod, PENDING_CHECK);
    }

}
