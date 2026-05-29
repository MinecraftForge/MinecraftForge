/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.modules.closedtest;

import java.lang.module.ModuleDescriptor;
import java.util.jar.Manifest;

import net.minecraftsingularity.data.event.GatherDataEvent;
import net.minecraftsingularity.debug.modules.closed.api.PublicUtils;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;
import net.minecraftsingularity.test.ManifestProvider;
import net.minecraftsingularity.test.ModuleProvider;
import net.minecraft.gametest.framework.GameTestHelper;

@Mod(ClosedTestsMod.MODID)
@GameTestNamespace("singularity")
public class ClosedTestsMod extends BaseTestMod {
    public static final String MODID = "closed_module_test";

    public ClosedTestsMod(FMLJavaModLoadingContext context) {
        super(context, false, false);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
    }

    public void gatherData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new ModuleProvider(out, module()));
        event.getGenerator().addProvider(true, new ManifestProvider(out, MODID, manifest()));
    }

    private ModuleDescriptor module() {
        return ModuleDescriptor.newOpenModule(getClass().getPackageName())
            .requires("net.minecraftsingularity.eventbus")
            .requires("net.minecraftsingularity.fmlcore")
            .requires("net.minecraftsingularity.singularity")
            .requires("net.minecraftsingularity.javafmlmod")
            .requires("net.minecraftsingularity.debug.modules.closed")
            .build();
    }

    private Manifest manifest() {
        var ret = new Manifest();
        // Add-Opens is respected by FMLModContainer, it should give us access to closed packages
        ret.getMainAttributes().putValue("Add-Opens", "net.minecraftsingularity.debug.modules.closed/net.minecraftsingularity.debug.modules.closed.internala" );
        return ret;
    }

    /*
     * This attempted to access a class and method that IS exported by ClosedMod
     * Should succeed with no issues
     */
    @GameTest
    public static void can_reflect_exported(GameTestHelper helper) throws ReflectiveOperationException {
        var method = PublicUtils.class.getDeclaredMethod("publicMethod");
        method.invoke(null);
        helper.succeed();
    }

    /*
     * This is opened by us having an Add-Opens entry in ClosedTest's manifest
     * It is NOT exported/opened by ClosedMod
     * Should succeed with no exceptions
     */
    @GameTest
    public static void can_reflect_opened_internal(GameTestHelper helper) throws ReflectiveOperationException {
        try {
            var cls = Class.forName("net.minecraftsingularity.debug.modules.closed.internala.InternalA");
            var method = cls.getDeclaredMethod("internalMethod");
            method.invoke(null);
            helper.succeed();
        } catch (IllegalAccessException e) {
            helper.fail("Failed to invoke internal method: " + e.getMessage());
        }
    }

    /*
     * This is NOT opened by us, and is NOT exported by ClosedTest
     * This should error with IllegalAccessException because java is enforcing access control.
     */
    @GameTest
    public static void cant_reflect_internal(GameTestHelper helper) throws ReflectiveOperationException {
        try {
            var cls = Class.forName("net.minecraftsingularity.debug.modules.closed.internalb.InternalB");
            var method = cls.getDeclaredMethod("internalMethod");
            method.invoke(null);
            helper.fail("Invoked internal method without error");
        } catch (IllegalAccessException e) {
            helper.succeed();
        }
    }
}
