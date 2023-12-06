/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Extension interface for {@link KeyMapping}.
 */
public interface IForgeKeyMapping {
    private KeyMapping self() {
        return (KeyMapping)this;
    }

    @NotNull InputConstants.Key getKey();

    /**
     * {@return true if the key conflict context and modifier are active and the keyCode matches this binding, false otherwise}
     */
    default boolean isActiveAndMatches(InputConstants.Key keyCode) {
        return keyCode != InputConstants.UNKNOWN && keyCode.equals(getKey()) && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    default void setToDefault() {
        setKeyModifierAndCode(getDefaultKeyModifier(), self().getDefaultKey());
    }

    void setKeyConflictContext(IKeyConflictContext keyConflictContext);

    IKeyConflictContext getKeyConflictContext();

    KeyModifier getDefaultKeyModifier();

    KeyModifier getKeyModifier();

    void setKeyModifierAndCode(KeyModifier keyModifier, InputConstants.Key keyCode);

    default boolean isConflictContextAndModifierActive() {
        return getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    /**
     * Returns true when one of the bindings' key codes conflicts with the other's modifier.
     */
    default boolean hasKeyModifierConflict(KeyMapping other) {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext())) {
            if (getKeyModifier().matches(other.getKey()) || other.getKeyModifier().matches(getKey()))
                return true;
        }
        return false;
    }
}
