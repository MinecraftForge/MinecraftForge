/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.test;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Exports;
import java.lang.module.ModuleDescriptor.Opens;
import java.lang.module.ModuleDescriptor.Provides;
import java.lang.module.ModuleDescriptor.Requires;
import java.lang.module.ModuleDescriptor.Version;
import java.lang.reflect.AccessFlag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.google.common.hash.Hashing;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class ModuleProvider implements DataProvider {
    private final PackOutput output;
    private final ModuleDescriptor desc;

    public ModuleProvider(PackOutput output, ModuleDescriptor desc) {
        this.output = output;
        this.desc = desc;
    }

    @Override
    public String getName() {
        return "ModuleProvider[" + this.desc.name() + ']';
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            var target = this.output.getOutputFolder().resolve("module-info.dat");
            try {
                var data = writeModuleInfo();
                @SuppressWarnings("deprecation")
                var hash = Hashing.sha1().hashBytes(data);
                cache.writeIfNeeded(target, data, hash);
            } catch (IOException ioexception) {
                LOGGER.error("Failed to save file to {}", target, ioexception);
            }
        }, Util.backgroundExecutor());
    }

    private byte[] writeModuleInfo() {
        var writer = new ClassWriter(0);

        writer.visit(Opcodes.V9, Opcodes.ACC_MODULE, "module-info", null, null, null);

        var module = writer.visitModule(desc.name(), flags(desc.accessFlags()), version(desc.version(), desc.rawVersion()));

        desc.mainClass().ifPresent(module::visitMainClass);

        for (var pkg : sorted(desc.packages(), Function.identity()))
            module.visitPackage(binary(pkg));

        for (var req : sorted(desc.requires(), Requires::name))
            module.visitRequire(req.name(), flags(req.accessFlags()), version(req.compiledVersion(), req.rawCompiledVersion()));

        for (var exp : sorted(desc.exports(), Exports::source))
            module.visitExport(binary(exp.source()), flags(exp.accessFlags()), array(exp.targets()));

        for (var open : sorted(desc.opens(), Opens::source))
            module.visitOpen(binary(open.source()), flags(open.accessFlags()), array(open.targets()));

        for (var uses : sorted(desc.uses(), Function.identity()))
            module.visitUse(binary(uses));

        for (var provide : sorted(desc.provides(), Provides::service)) {
            var providers = new ArrayList<String>();
            for (var provider : provide.providers())
                providers.add(binary(provider));

            module.visitProvide(provide.service(), array(providers));
        }

        module.visitEnd();

        return writer.toByteArray();
    }

    private static int flags(Set<AccessFlag> flags) {
        int access = 0;
        for (var flag : flags)
            access |= flag.mask();
        return access;
    }

    private static String[] array(Collection<String> lst) {
        return lst.stream().toArray(String[]::new);
    }

    private static String binary(String cls) {
        return cls.replace('.', '/');
    }

    private static String version(Optional<Version> ver, Optional<String> str) {
        var version = ver.map(Version::toString).orElse(null);
        if (version == null)
            return str.orElse(null);
        return version;
    }

    private static <T> List<T> sorted(Collection<T> data, Function<T, String> toString) {
        var ret = new ArrayList<T>();
        ret.addAll(data);
        Collections.sort(ret, (a, b) -> toString.apply(a).compareTo(toString.apply(b)));
        return ret;
    }

}
