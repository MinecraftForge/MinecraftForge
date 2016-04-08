package net.minecraftforge.client.settings;

import net.minecraft.client.settings.KeyBinding;

/**
 * Defines the context that a {@link KeyBinding} is used.
 * Key conflicts occur when a {@link KeyBinding} has the same {@link IKeyConflictContext} and has conflicting modifiers and keyCodes.
 */
public interface IKeyConflictContext {
    /**
     * @return true if conditions are met to activate {@link KeyBinding}s with this context
     */
    boolean isActive();

    /**
     * @return true if the other context can have {@link KeyBinding} conflicts with this one.
     * This will be called on both contexts to check for conflicts.
     */
    boolean conflicts(IKeyConflictContext other);
}
