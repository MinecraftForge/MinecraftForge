/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.util;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public interface INBTBuilder {
    default Builder nbt() {
        return new Builder();
    }

    public static class Builder implements INBTBuilder {
        private final CompoundTag tag = new CompoundTag();

        public CompoundTag build() {
            return tag;
        }

        public Builder tag(String key, Tag value) {
            tag.put(key, value);
            return this;
        }

        public Builder putByte(String key, byte value) {
           tag.putByte(key, value);
           return this;
        }

        public Builder putShort(String key, short value) {
            tag.putShort(key, value);
            return this;
        }

        public Builder putInt(String key, int value) {
            tag.putInt(key, value);
            return this;
        }

        public Builder putLong(String key, long value) {
            tag.putLong(key, value);
            return this;
        }

        public Builder putFloat(String key, float value) {
            tag.putFloat(key, value);
            return this;
        }

        public Builder putDouble(String key, double value) {
            tag.putDouble(key, value);
            return this;
        }

        public Builder putByteArray(String key, byte... value) {
            tag.putByteArray(key, value);
            return this;
        }

        public Builder putByteArray(String key, List<Byte> value) {
            tag.putByteArray(key, value);
            return this;
        }

        public Builder putIntArray(String key, int... value) {
            tag.putIntArray(key, value);
            return this;
        }

        public Builder putIntArray(String key, List<Integer> value) {
            tag.putIntArray(key, value);
            return this;
        }

        public Builder putLongArray(String key, long... value) {
            tag.putLongArray(key, value);
            return this;
        }

        public Builder putLongArray(String key, List<Long> value) {
            tag.putLongArray(key, value);
            return this;
        }

        public Builder put(String key, boolean value) {
            tag.putBoolean(key, value);
            return this;
        }

        public Builder put(String key, String value) {
            tag.putString(key, value);
            return this;
        }

        public Builder put(String key, UUID value) {
            tag.putUUID(key, value);
            return this;
        }
    }
}
