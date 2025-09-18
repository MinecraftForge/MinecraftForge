/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import net.minecraftforge.fml.common.asm.CapabilityTokenSubclass;
import org.objectweb.asm.Type;

import java.lang.constant.ClassDesc;

/**
 * Inspired by {@link com.google.common.reflect.TypeToken TypeToken}, use a subclass to capture
 * generic types. Then uses {@link CapabilityTokenSubclass a transformer}
 * to convert that generic into a string returned by {@link #getType}
 * This allows us to know the generic type, without having a hard reference to the
 * class.
 *
 * Example usage:
 * <pre>{@code
 *    public static Capability<IDataHolder> DATA_HOLDER_CAPABILITY
 *    		= CapabilityManager.get(new CapabilityToken<>(){});
 * }</pre>
 *
 */
public abstract class CapabilityToken<T> {
    protected final String getType() {
        throw new RuntimeException("This will be implemented by a transformer");
    }

    /**
     * @deprecated Use {@link #of(Class)} or {@link #of(ClassDesc)} instead
     */
    @Deprecated(forRemoval = true, since = "1.21.8")
    public CapabilityToken() {}

    @Override
    public String toString() {
        return "CapabilityToken[" + getType() + ']';
    }

    public static <T> CapabilityToken<T> of(Class<T> clazz) {
        return new CapabilityTokenDesc<>(Type.getInternalName(clazz));
    }

    public static <T> CapabilityToken<T> of(ClassDesc classDesc) {
        var descriptorString = classDesc.descriptorString();
        return new CapabilityTokenDesc<>(descriptorString.substring(1, descriptorString.length() - 1));
    }

}
