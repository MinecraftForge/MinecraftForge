/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterEntitySpectatorShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("shader_fix_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShaderFixTest
{
    @SubscribeEvent
    public static void onRegisterEntitySpectatorShaders(final RegisterEntitySpectatorShadersEvent event)
    {
        event.register(EntityType.PLAYER, new ResourceLocation("shaders/post/desaturate.json"));
    }
}