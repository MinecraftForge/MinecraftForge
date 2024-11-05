/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.modules.closedtest;

import java.lang.module.ModuleDescriptor;
import java.util.jar.Manifest;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.debug.modules.closed.api.PublicUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.test.ManifestProvider;
import net.minecraftforge.test.ModuleProvider;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@Mod(ClosedTestsMod.MODID)
@GameTestHolder("forge.module.closed")
public class ClosedTestsMod extends BaseTestMod {
    public static final String MODID = "closed_module_test";

    public ClosedTestsMod(FMLJavaModLoadingContext context) {
        super(context);
    }

    @SubscribeEvent
    public void runData(GatherDataEvent event) {
        var out = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(true, new ModuleProvider(out, module()));
        event.getGenerator().addProvider(true, new ManifestProvider(out, MODID, manifest()));
    }

    private ModuleDescriptor module() {
        return ModuleDescriptor.newOpenModule(getClass().getPackageName())
            .requires("net.minecraftforge.eventbus")
            .requires("net.minecraftforge.fmlcore")
            .requires("net.minecraftforge.forge")
            .requires("net.minecraftforge.javafmlmod")
            .requires("net.minecraftforge.debug.modules.closed")
            .build();
    }

    private Manifest manifest() {
        var ret = new Manifest();
        // Add-Opens is respected by FMLModContainer, it should give us access to closed packages
        ret.getMainAttributes().putValue("Add-Opens", "net.minecraftforge.debug.modules.closed/net.minecraftforge.debug.modules.closed.internala" );
        return ret;
    }

    /*
     * This attempted to access a class and method that IS exported by ClosedMod
     * Should succeed with no issues
     */
    @GameTest(template = "forge:empty3x3x3")
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
    @GameTest(template = "forge:empty3x3x3")
    public static void can_reflect_opened_internal(GameTestHelper helper) throws ReflectiveOperationException {
        try {
            var cls = Class.forName("net.minecraftforge.debug.modules.closed.internala.InternalA");
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
    @GameTest(template = "forge:empty3x3x3")
    public static void cant_reflect_internal(GameTestHelper helper) throws ReflectiveOperationException {
        try {
            var cls = Class.forName("net.minecraftforge.debug.modules.closed.internalb.InternalB");
            var method = cls.getDeclaredMethod("internalMethod");
            method.invoke(null);
            helper.fail("Invoked internal method without error");
        } catch (IllegalAccessException e) {
            helper.succeed();
        }
    }
}
