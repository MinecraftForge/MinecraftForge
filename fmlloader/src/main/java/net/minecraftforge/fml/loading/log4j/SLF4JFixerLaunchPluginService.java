/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.log4j;

import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class SLF4JFixerLaunchPluginService implements ILaunchPluginService {
    private static final EnumSet<Phase> NO_PHASES = EnumSet.noneOf(Phase.class);

    @Override
    public String name() {
        return "slf4jfixer";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return NO_PHASES;
    }

    @Override
    public void initializeLaunch(ITransformerLoader transformerLoader, NamedPath[] specialPaths) {
        Thread curThread = Thread.currentThread();
        ClassLoader contextClassLoader = curThread.getContextClassLoader();

        // Set the CCL of the current thread to MC-BOOTSTRAP ModuleClassLoader
        curThread.setContextClassLoader(this.getClass().getClassLoader());

        // Force SLF4J to bind the service providers while we manually set the context classloader to be correct
        LoggerFactory.getILoggerFactory();

        // Set CCL back to TransformingClassLoader
        curThread.setContextClassLoader(contextClassLoader);
    }
}
