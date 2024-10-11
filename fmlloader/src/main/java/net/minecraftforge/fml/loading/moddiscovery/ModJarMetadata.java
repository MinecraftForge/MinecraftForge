/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import cpw.mods.jarhandling.JarMetadata;
import net.minecraftforge.forgespi.locating.IModFile;
import java.lang.module.ModuleDescriptor;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ModJarMetadata implements JarMetadata {
    private static final String AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";
    private IModFile modFile;
    private String name;
    private String version;
    private ModuleDescriptor descriptor;

    ModJarMetadata() { }

    public void setModFile(IModFile file) {
        this.modFile = file;
        var mods = this.modFile.getModFileInfo().getMods();

        if (!mods.isEmpty()) {
            var main = mods.get(0);
            this.name = main.getModId();
            this.version = main.getVersion().toString();
        }

        var jar = file.getSecureJar();
        var aname = jar.moduleDataProvider().getManifest().getMainAttributes().getValue(AUTOMATIC_MODULE_NAME);
        if (aname != null)
            this.name = aname;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public ModuleDescriptor descriptor() {
        if (descriptor != null)
            return descriptor;

        var bld = ModuleDescriptor.newAutomaticModule(name())
            .version(version())
            .packages(modFile.getSecureJar().getPackages());

        for (var provider : modFile.getSecureJar().getProviders()) {
            if (provider.providers().isEmpty())
                continue;

            bld.provides(provider.serviceName(), provider.providers());
        }

        descriptor = bld.build();
        return descriptor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ModJarMetadata) obj;
        return Objects.equals(this.modFile, that.modFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modFile);
    }

    @Override
    public String toString() {
        return "ModJarMetadata[" +"modFile=" + modFile + ']';
    }
}
