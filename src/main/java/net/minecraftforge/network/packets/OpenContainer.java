/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import org.jetbrains.annotations.ApiStatus;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.network.CustomPayloadEvent;

// TODO: Reevaluate if this is needed This is the same as ClientboundOpenScreenPacket packet but allows for additional data
public class OpenContainer {
    private final int id;
    private final int windowId;
    private final Component name;
    private final FriendlyByteBuf additionalData;

    /** Use NetworkHooks.openScreen */
    @ApiStatus.Internal
    @SuppressWarnings("deprecation")
    public OpenContainer(MenuType<?> id, int windowId, Component name, FriendlyByteBuf additionalData) {
        this(BuiltInRegistries.MENU.getId(id), windowId, name, additionalData);
    }

    private OpenContainer(int id, int windowId, Component name, FriendlyByteBuf additionalData) {
        this.id = id;
        this.windowId = windowId;
        this.name = name;
        this.additionalData = additionalData;
    }

    public static void encode(OpenContainer msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.id);
        buf.writeVarInt(msg.windowId);
        buf.writeComponent(msg.name);
        msg.additionalData.markReaderIndex();
        buf.writeByteArray(msg.additionalData.readByteArray());
        msg.additionalData.resetReaderIndex();
    }

    public static OpenContainer decode(FriendlyByteBuf buf) {
        return new OpenContainer(buf.readVarInt(), buf.readVarInt(), buf.readComponent(), new FriendlyByteBuf(Unpooled.wrappedBuffer(buf.readByteArray(32600))));
    }

    @SuppressWarnings("unchecked")
    public static void handle(OpenContainer msg, CustomPayloadEvent.Context ctx) {
        try {
            var mc = Minecraft.getInstance();
            var inv = mc.player.getInventory();
            var factory = MenuScreens.getScreenFactory(msg.getType(), mc, msg.getWindowId(), msg.getName());
            factory.ifPresent(f -> {
                var c = msg.getType().create(msg.getWindowId(), inv, msg.getAdditionalData());

                var s = ((MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>)f).create(c, inv, msg.getName());
                mc.player.containerMenu = s.getMenu();
                mc.setScreen(s);
            });
        } finally {
            msg.getAdditionalData().release();
        }
    }

    @SuppressWarnings("deprecation")
    public final MenuType<?> getType() {
        return BuiltInRegistries.MENU.byId(this.id);
    }

    public int getWindowId() {
        return windowId;
    }

    public Component getName() {
        return name;
    }

    public FriendlyByteBuf getAdditionalData() {
        return additionalData;
    }
}
