/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ServiceRunner;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.api.distmarker.Dist;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/**
 * This is required by FMLLoader because ILaunchHandlerService doesn't have the context we need.
 * I need to clean this up to make proper api. But that involves editing ModLauncher itself which i'm not gunna do right now.
 *
 * So until that happens, guess this is public api.
 */
public abstract class CommonLaunchHandler implements ILaunchHandlerService {
    protected static final Logger LOGGER = LogUtils.getLogger();

    protected final LaunchType type;
    private final String prefix;

    protected CommonLaunchHandler(LaunchType type, String prefix) {
        this.type = type;
        this.prefix = prefix;
    }

    @Override public String name() { return this.prefix + this.type.name(); }
    public Dist getDist() { return this.type.dist(); }
    public boolean isData() { return this.type.data(); }
    public boolean isProduction() { return false; }
    public abstract String getNaming();

    public abstract List<Path> getMinecraftPaths();

    protected String[] preLaunch(String[] arguments, ModuleLayer layer) {
        URI uri;
        try (var reader = layer.configuration().findModule("net.minecraftforge.fmlloader").orElseThrow().reference().open()) {
            uri = reader.find("log4j2.xml").orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Force the log4j2 configuration to be loaded from fmlloader
        Configurator.reconfigure(ConfigurationFactory.getInstance().getConfiguration(LoggerContext.getContext(), ConfigurationSource.fromUri(uri)));

        return arguments;
    }

    @Override
    public ServiceRunner launchService(final String[] arguments, final ModuleLayer gameLayer) {
        FMLLoader.beforeStart(gameLayer);
        return makeService(arguments, gameLayer);
    }

    protected ServiceRunner makeService(final String[] arguments, final ModuleLayer gameLayer) {
        return () -> runTarget(this.type.module(), this.type.main(), arguments, gameLayer);
    }

    protected record LaunchType(String name, String module, String main, Dist dist, boolean data) {};
    protected static final LaunchType CLIENT          = new LaunchType("client", "minecraft", "net.minecraft.client.main.Main", Dist.CLIENT, false);
    protected static final LaunchType DATA            = new LaunchType("data",   "minecraft", "net.minecraft.data.Main", Dist.CLIENT, true);
    protected static final LaunchType SERVER          = new LaunchType("server", "minecraft", "net.minecraft.server.Main", Dist.DEDICATED_SERVER, false);
    protected static final LaunchType SERVER_GAMETEST = new LaunchType("server_gametest", "net.minecraftforge.forge", "net.minecraftforge.gametest.GameTestMain", Dist.DEDICATED_SERVER, false);

    protected void runTarget(String module, String target, final String[] arguments, final ModuleLayer layer) throws Throwable {
        var mod = layer.findModule(module).orElse(null);
        if (mod == null) throw new IllegalStateException("Could not find module " + module);
        var cls = Class.forName(mod, target);
        if (cls == null) throw new IllegalStateException("Could not find class " + target + " in module " + module);
        var mtd = cls.getMethod("main", String[].class);
        if (mtd == null) throw new IllegalStateException("Class " + target + " in module " + module + " does not have a main(String[]) method");
        mtd.invoke(null, (Object)arguments);
    }

    protected static Path getPathFromResource(String resource) {
        return getPathFromResource(resource, ForgeDevLaunchHandler.class.getClassLoader());
    }

    protected static Path getPathFromResource(String resource, ClassLoader cl) {
        var url = cl.getResource(resource);
        if (url == null)
            throw new IllegalStateException("Could not find " + resource + " in classloader " + cl);

        var str = url.toString();
        int len = resource.length();
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            str = url.getFile();
            len += 2;
        }
        str = str.substring(0, str.length() - len);
        var path = Path.of(URI.create(str));
        return path;
    }
}
