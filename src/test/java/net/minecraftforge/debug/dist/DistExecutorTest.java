/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.dist;

import net.minecraft.client.Minecraft;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@Mod(DistExecutorTest.MOD_ID)
@GameTestNamespace("forge")
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
