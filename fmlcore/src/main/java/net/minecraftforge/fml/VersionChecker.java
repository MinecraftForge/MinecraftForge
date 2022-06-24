/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import com.google.gson.Gson;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static net.minecraftforge.fml.VersionChecker.Status.*;

public class VersionChecker
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);
    private static final int HTTP_TIMEOUT_SECS = Integer.getInteger("http.timeoutSecs", 15);

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

    public record CheckResult(VersionChecker.Status status, ComparableVersion target, Map<ComparableVersion, String> changes, String url) {}

    public static void startVersionCheck()
    {
        new Thread("Forge Version Check")
        {
            private HttpClient client;

            @Override
            public void run()
            {
                if (!FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.VERSION_CHECK))
                {
                    LOGGER.info("Global Forge version check system disabled, no further processing.");
                    return;
                }

                client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(HTTP_TIMEOUT_SECS)).build();
                gatherMods().forEach(this::process);
            }

            /**
             * Returns the response body as a String for the given URL while following redirects
             */
            private String openUrlString(URL url, IModInfo mod) throws IOException, URISyntaxException, InterruptedException {
                URL currentUrl = url;

                final StringBuilder sb = new StringBuilder();
                sb.append("Java-http-client/").append(System.getProperty("java.version")).append(' ');
                sb.append("MinecraftForge/").append(FMLLoader.versionInfo().mcAndForgeVersion()).append(' ');
                sb.append(mod.getModId()).append('/').append(mod.getVersion());
                final String userAgent = sb.toString();

                for (int redirects = 0; redirects < MAX_HTTP_REDIRECTS; redirects++)
                {
                    var request = HttpRequest.newBuilder()
                            .uri(currentUrl.toURI())
                            .timeout(Duration.ofSeconds(HTTP_TIMEOUT_SECS))
                            .setHeader("Accept-Encoding", "gzip")
                            .setHeader("User-Agent", userAgent)
                            .GET()
                            .build();

                    final HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                    int responseCode = response.statusCode();
                    if (responseCode >= 300 && responseCode <= 399)
                    {
                        String newLocation = response.headers().firstValue("Location")
                                .orElseThrow(() -> new IOException("Got a 3xx response code but Location header was null while trying to fetch " + url));
                        currentUrl = new URL(currentUrl, newLocation);
                        continue;
                    }

                    final boolean isGzipEncoded = response.headers().firstValue("Content-Encoding").orElse("").equals("gzip");

                    final String bodyStr;
                    try (InputStream inStream = isGzipEncoded ? new GZIPInputStream(response.body()) : response.body())
                    {
                        try (var bufferedReader = new BufferedReader(new InputStreamReader(inStream)))
                        {
                            bodyStr = bufferedReader.lines().collect(Collectors.joining("\n"));
                        }
                    }
                    return bodyStr;
                }
                throw new IOException("Too many redirects while trying to fetch " + url);
            }

            private void process(IModInfo mod)
            {
                Status status = PENDING;
                ComparableVersion target = null;
                Map<ComparableVersion, String> changes = null;
                String display_url = null;
                try
                {
                    if (mod.getUpdateURL().isEmpty()) return;
                    URL url = mod.getUpdateURL().get();
                    LOGGER.info("[{}] Starting version check at {}", mod.getModId(), url.toString());

                    String data = openUrlString(url, mod);

                    LOGGER.debug("[{}] Received version check data:\n{}", mod.getModId(), data);


                    @SuppressWarnings("unchecked")
                    Map<String, Object> json = new Gson().fromJson(data, Map.class);
                    @SuppressWarnings("unchecked")
                    Map<String, String> promos = (Map<String, String>)json.get("promos");
                    display_url = (String)json.get("homepage");

                    var mcVersion = FMLLoader.versionInfo().mcVersion();
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
        for (IModInfo info : ModList.get().getMods()) {
            if (info.getUpdateURL().isPresent())
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
