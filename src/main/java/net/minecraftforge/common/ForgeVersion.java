/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common;

import static net.minecraftforge.common.ForgeVersion.Status.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

import javax.annotation.Nullable;

public class ForgeVersion
{
    // This is Forge's Mod Id, used for the ForgeModContainer and resource locations
    public static final String MOD_ID = "forge";
    //This number is incremented every time we remove deprecated code/major API changes, never reset
    public static final int majorVersion    = 14;
    //This number is incremented every minecraft release, never reset
    public static final int minorVersion    = 23;
    //This number is incremented every time a interface changes or new major feature is added, and reset every Minecraft version
    public static final int revisionVersion = 4;
    //This number is incremented every time Jenkins builds Forge, and never reset. Should always be 0 in the repo code.
    public static final int buildVersion    = 0;
    // This is the minecraft version we're building for - used in various places in Forge/FML code
    public static final String mcVersion = "1.12.2";
    // This is the MCP data version we're using
    public static final String mcpVersion = "9.42";
    @SuppressWarnings("unused")
    private static Status status = PENDING;
    @SuppressWarnings("unused")
    private static String target = null;

    private static final Logger log = LogManager.getLogger(MOD_ID + ".VersionCheck");

    private static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);

    public static int getMajorVersion()
    {
        return majorVersion;
    }

    public static int getMinorVersion()
    {
        return minorVersion;
    }

    public static int getRevisionVersion()
    {
        return revisionVersion;
    }

    public static int getBuildVersion()
    {
        return buildVersion;
    }

    public static Status getStatus()
    {
        return getResult(ForgeModContainer.getInstance()).status;
    }

    @Nullable
    public static String getTarget()
    {
        CheckResult res = getResult(ForgeModContainer.getInstance());
        return res.target != null ? res.target.toString() : null;
    }

    public static String getVersion()
    {
        return String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
    }

    public static enum Status
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
        new Thread("Forge Version Check")
        {
            @Override
            public void run()
            {
                if (!ForgeModContainer.getConfig().get(ForgeModContainer.VERSION_CHECK_CAT, "Global", true).getBoolean())
                {
                    log.info("Global Forge version check system disabled, no further processing.");
                    return;
                }

                for (Entry<ModContainer, URL> entry : gatherMods().entrySet())
                {
                    ModContainer mod = entry.getKey();
                    if (ForgeModContainer.getConfig().get(ForgeModContainer.VERSION_CHECK_CAT, mod.getModId(), true).getBoolean())
                    {
                        process(mod, entry.getValue());
                    }
                    else
                    {
                        log.info("[{}] Skipped version check", mod.getModId());
                    }
                }
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

            private void process(ModContainer mod, URL url)
            {
                try
                {
                    log.info("[{}] Starting version check at {}", mod.getModId(), url.toString());
                    Status status = PENDING;
                    ComparableVersion target = null;

                    InputStream con = openUrlStream(url);
                    String data = new String(ByteStreams.toByteArray(con), "UTF-8");
                    con.close();

                    log.debug("[{}] Received version check data:\n{}", mod.getModId(), data);


                    @SuppressWarnings("unchecked")
                    Map<String, Object> json = new Gson().fromJson(data, Map.class);
                    @SuppressWarnings("unchecked")
                    Map<String, String> promos = (Map<String, String>)json.get("promos");
                    String display_url = (String)json.get("homepage");

                    String rec = promos.get(MinecraftForge.MC_VERSION + "-recommended");
                    String lat = promos.get(MinecraftForge.MC_VERSION + "-latest");
                    ComparableVersion current = new ComparableVersion(mod.getVersion());

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
                        {
                            status = BETA_OUTDATED;
                            target = latest;
                        }
                        else
                            status = BETA;
                    }
                    else
                        status = BETA;

                    log.info("[{}] Found status: {} Target: {}", mod.getModId(), status, target);

                    Map<ComparableVersion, String> changes = new LinkedHashMap<ComparableVersion, String>();
                    @SuppressWarnings("unchecked")
                    Map<String, String> tmp = (Map<String, String>)json.get(MinecraftForge.MC_VERSION);
                    if (tmp != null)
                    {
                        List<ComparableVersion> ordered = new ArrayList<ComparableVersion>();
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
                    if (mod instanceof InjectedModContainer)
                        mod = ((InjectedModContainer)mod).wrappedContainer;
                    results.put(mod, new CheckResult(status, target, changes, display_url));
                }
                catch (Exception e)
                {
                    log.debug("Failed to process update information", e);
                    status = FAILED;
                }
            }
        }.start();
    }

    // Gather a list of mods that have opted in to this update system by providing a URL.
    public static Map<ModContainer, URL> gatherMods()
    {
        Map<ModContainer, URL> ret = new HashMap<ModContainer, URL>();
        for (ModContainer mod : Loader.instance().getActiveModList())
        {
            URL url = mod.getUpdateUrl();
            if (url != null)
                ret.put(mod, url);
        }
        return ret;
    }

    private static Map<ModContainer, CheckResult> results = new ConcurrentHashMap<ModContainer, CheckResult>();
    private static final CheckResult PENDING_CHECK = new CheckResult(PENDING, null, null, null);

    public static CheckResult getResult(ModContainer mod)
    {
        if (mod == null) return PENDING_CHECK;
        if (mod instanceof InjectedModContainer)
            mod = ((InjectedModContainer)mod).wrappedContainer;
        CheckResult ret = results.get(mod);
        return ret == null ? PENDING_CHECK : ret;
    }
}

