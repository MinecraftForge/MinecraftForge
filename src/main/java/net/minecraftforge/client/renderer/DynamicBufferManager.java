/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.event.RegisterRenderTypesEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;

final class DynamicBufferManager
{
    final Map<ResourceLocation, RenderTypeGroup> registeredTypes;
    final Map<RenderType, BufferBuilder> registeredBuffers;
    final BufferBuilder defaultBuilder;
    
    public DynamicBufferManager(
        Function<RenderType, BufferBuilder> fixedBuffer,
        Map<ResourceLocation, RenderTypeGroup> initial)
    {
        this.registeredTypes = new HashMap<>(initial);
        this.registeredBuffers = new HashMap<>();
        this.defaultBuilder = new BufferBuilder(256);
        
        ModLoader.get().postEventWithWrapInModOrder(
            new RegisterRenderTypesEvent.Named(registeredTypes),
            (mc, e) -> ModLoadingContext.get().setActiveContainer(mc),
            (mc, e) -> ModLoadingContext.get().setActiveContainer(null));
        
        for (final var entry : registeredTypes.entrySet())
        {
            final var value = entry.getValue();
            registeredBuffers.put(value.entity(), new BufferBuilder(value.entity().bufferSize()));
            registeredBuffers.put(value.entityFabulous(), new BufferBuilder(value.entityFabulous().bufferSize()));
        }
        
        registeredBuffers.put(Sheets.solidBlockSheet(), fixedBuffer.apply(RenderType.solid()));
        registeredBuffers.put(Sheets.cutoutBlockSheet(), fixedBuffer.apply(RenderType.cutout()));
        registeredBuffers.put(Sheets.bannerSheet(), fixedBuffer.apply(RenderType.cutoutMipped()));
        registeredBuffers.put(Sheets.translucentCullBlockSheet(), fixedBuffer.apply(RenderType.translucent()));
        registerHelper(registeredBuffers, Sheets.shieldSheet());
        registerHelper(registeredBuffers, Sheets.bedSheet());
        registerHelper(registeredBuffers, Sheets.shulkerBoxSheet());
        registerHelper(registeredBuffers, Sheets.signSheet());
        registerHelper(registeredBuffers, Sheets.hangingSignSheet());
        registerHelper(registeredBuffers, Sheets.chestSheet());
        registerHelper(registeredBuffers, RenderType.translucentNoCrumbling());
        registerHelper(registeredBuffers, RenderType.armorGlint());
        registerHelper(registeredBuffers, RenderType.armorEntityGlint());
        registerHelper(registeredBuffers, RenderType.glint());
        registerHelper(registeredBuffers, RenderType.glintDirect());
        registerHelper(registeredBuffers, RenderType.glintTranslucent());
        registerHelper(registeredBuffers, RenderType.entityGlint());
        registerHelper(registeredBuffers, RenderType.entityGlintDirect());
        registerHelper(registeredBuffers, RenderType.waterMask());
        ModelBakery.DESTROY_TYPES.forEach((p_173062_) -> {
           registerHelper(registeredBuffers, p_173062_);
        });
    }

    private static void registerHelper(Map<RenderType, BufferBuilder> map, RenderType renderType)
    {
        map.put(renderType, new BufferBuilder(renderType.bufferSize()));
    }
    
    public BufferBuilder get(RenderType renderType)
    {
        return registeredBuffers.get(renderType);
    }
    
    public BufferBuilder getOrDefault(RenderType renderType)
    {
        return registeredBuffers.getOrDefault(renderType, defaultBuilder);
    }
    
    public RenderTypeGroup getGroup(ResourceLocation location)
    {
        return registeredTypes.getOrDefault(location, RenderTypeGroup.EMPTY);
    }
}
