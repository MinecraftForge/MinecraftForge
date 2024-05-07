/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface SimpleHandlerProtocol<BUF extends FriendlyByteBuf, BASE> extends IProtocol<SimpleHandlerFlow<BUF, BASE>, SimpleHandlerProtocol<BUF, BASE>>, SimpleConnection<Object> {
    SimpleHandlerFlow<BUF, BASE> flow(@Nullable PacketFlow flow);
}
