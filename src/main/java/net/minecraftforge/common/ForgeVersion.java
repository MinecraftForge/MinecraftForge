/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;
import static net.minecraftforge.common.ForgeVersion.Status.*;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class ForgeVersion
{
    //This number is incremented every time we remove deprecated code/major API changes, never reset
    public static final int majorVersion    = 11;
    //This number is incremented every minecraft release, never reset
    public static final int minorVersion    = 15;
    //This number is incremented every time a interface changes or new major feature is added, and reset every Minecraft version
    public static final int revisionVersion = 1;
    //This number is incremented every time Jenkins builds Forge, and never reset. Should always be 0 in the repo code.
    public static final int buildVersion    = 0;
    // This is the minecraft version we're building for - used in various places in Forge/FML code
    public static final String mcVersion = "1.8.9";
    // This is the MCP data version we're using
    public static final String mcpVersion = "9.19";
    @SuppressWarnings("unused")
    private static Status status = PENDING;
    @SuppressWarnings("unused")
    private static String target = null;

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
        PENDING,
        FAILED,
        UP_TO_DATE,
        OUTDATED,
        AHEAD,
        BETA,
        BETA_OUTDATED
    }

    public static class CheckResult
    {
        public final Status status;
        public final ComparableVersion target;
        public final Map<ComparableVersion, String> changes;
        public final String url;

        private CheckResult(Status status, ComparableVersion target, Map<ComparableVersion, String> changes, String url)
        {
            this.status = status;
            this.target = target;
            this.changes = changes == null ? null : Collections.unmodifiableMap(changes);
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
                    FMLLog.log("ForgeVersionCheck", Level.INFO, "Global Forge version check system disabeld, no futher processing.");
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
                        FMLLog.log("ForgeVersionCheck", Level.INFO, "[%s] Skipped version check", mod.getModId());
                    }
                }
            }

            private void process(ModContainer mod, URL url)
            {
                try
                {
                    FMLLog.log("ForgeVersionCheck", Level.INFO, "[%s] Starting version check at %s", mod.getModId(), url.toString());
                    Status status = PENDING;
                    ComparableVersion target = null;

                    InputStream con = url.openStream();
                    String data = new String(ByteStreams.toByteArray(con));
                    con.close();

                    FMLLog.log("ForgeVersionCheck", Level.DEBUG, "[%s] Received version check data:\n%s", mod.getModId(), data);


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

                    FMLLog.log("ForgeVersionCheck", Level.INFO, "[%s] Found status: %s Target: %s", mod.getModId(), status, target);

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
                    FMLLog.log("ForgeVersionCheck", Level.DEBUG, e, "Failed to process update information");
                    status = FAILED;
                }
            }
        }.start();
    }

    // Gather a list of mods that have opted in to this update system by providing a URL.
    // Small hack needed to support a interface change until we force a recompile.
    public static Map<ModContainer, URL> gatherMods()
    {
        Map<ModContainer, URL> ret = new HashMap<ModContainer, URL>();
        for (ModContainer mod : Loader.instance().getActiveModList())
        {
            URL url = null;
            try {
                url = mod.getUpdateUrl();
            } catch (AbstractMethodError abs) { } //TODO: Remove this in 1.8.8+?
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

