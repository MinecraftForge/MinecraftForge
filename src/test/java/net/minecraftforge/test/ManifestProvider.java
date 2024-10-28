/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import com.google.common.hash.Hashing;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class ManifestProvider implements DataProvider {
    private final PackOutput output;
    private final String name;
    private final Manifest manifest;

    public ManifestProvider(PackOutput output, String name, Manifest manifest) {
        this.output = output;
        this.name = name;
        this.manifest = manifest;
    }

    @Override
    public String getName() {
        return "ManifestProvider[" + this.name + ']';
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            var target = this.output.getOutputFolder().resolve(JarFile.MANIFEST_NAME);
            try {
                if (this.manifest.getMainAttributes().getValue(Attributes.Name.MANIFEST_VERSION) == null)
                    this.manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

                var bos = new ByteArrayOutputStream();
                this.manifest.write(bos);
                var data = bos.toByteArray();
                @SuppressWarnings("deprecation")
                var hash = Hashing.sha1().hashBytes(data);
                cache.writeIfNeeded(target, data, hash);
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", target, ioexception);
            }
        }, Util.backgroundExecutor());
    }
}
