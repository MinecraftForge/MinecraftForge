/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.dist;

import net.minecraft.client.Minecraft;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftsingularity.api.distmarker.Dist;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.fml.loading.FMLEnvironment;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

@Mod(DistExecutorTest.MOD_ID)
@GameTestNamespace("singularity")
public class DistExecutorTest extends BaseTestMod {
    static final String MOD_ID = "dist_executor";

    public DistExecutorTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void preventClientOnServer(GameTestHelper helper) {
        try {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft.getInstance().getWindow();
            }
        } catch (Throwable throwable) {
            helper.fail("Client class was loaded on server! " + throwable);
        }

        helper.succeed();
    }
}
