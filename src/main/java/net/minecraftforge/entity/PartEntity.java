/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;

public abstract class PartEntity<T extends Entity> extends Entity {
    private final T parent;

    public PartEntity(T parent) {
        super(parent.getType(), parent.level);
        this.parent = parent;
    }

    public T getParent() {
        return parent;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }
}
