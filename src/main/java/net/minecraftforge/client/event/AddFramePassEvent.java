/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class AddFramePassEvent extends Event {
	public final FrameGraphBuilder builder;
	public final LevelTargetBundle bundle;
	public AddFramePassEvent(FrameGraphBuilder builder, LevelTargetBundle bundle) {
		this.builder = builder;
		this.bundle = bundle;
	}
	
	public FramePass createPass(ResourceLocation rl) {
		return builder.addPass(rl.toString());
	}
	
	public FramePass createPass(ResourceLocation rl, String other, Order order) {
		return builder.addOrderedPass(rl, other, order.ordinal());
	}
	
	public static enum Order {
		BEFORE,
		AFTER
	}
}