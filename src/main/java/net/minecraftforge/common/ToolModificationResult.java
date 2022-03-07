/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import com.google.common.base.Objects;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Holds a {@link #toolModifiedState() tool-modified state} and whether the modification {@link #passed()} or {@link #failed()}.
 * <p>
 * If the tool modification result has a fail state, the tool-modified state is always an empty optional.
 * <p>
 * If the tool modification result has a pass state, an empty tool-modified state signifies a fallback on the default behavior.
 */
public final class ToolModificationResult
{
    private static final ToolModificationResult FAIL_RESULT = new ToolModificationResult(Type.FAIL, null);
    private static final ToolModificationResult EMPTY_PASS_RESULT = new ToolModificationResult(Type.PASS, null);
    private final Type type;
    @Nullable
    private final BlockState toolModifiedState;

    private ToolModificationResult(Type type, @Nullable BlockState toolModifiedState)
    {
        this.type = type;
        this.toolModifiedState = toolModifiedState;
    }

    /**
     * Signifies that this action failed and should be canceled immediately.
     *
     * @return An immutable tool modification result that signifies a fail state
     */
    @NotNull
    public static ToolModificationResult fail()
    {
        return FAIL_RESULT;
    }

    /**
     * Signifies that the default behavior should be invoked.
     *
     * @return An immutable tool modification result that signifies a fallback on default behavior
     */
    @NotNull
    public static ToolModificationResult passEmpty()
    {
        return EMPTY_PASS_RESULT;
    }

    /**
     * Signifies that action should be taken with the given tool-modified state.
     *
     * @param toolModifiedState The tool-modified state to complete this action with, never null
     * @return An immutable tool modification result that signifies a pass with the given tool-modified state
     */

    public static ToolModificationResult pass(@NotNull BlockState toolModifiedState)
    {
        return new ToolModificationResult(Type.PASS, toolModifiedState);
    }

    public boolean passed()
    {
        return this.type == Type.PASS;
    }

    public boolean failed()
    {
        return this.type == Type.FAIL;
    }

    public Type type()
    {
        return type;
    }

    /**
     * If this tool modification result has a {@link #failed() fail state}, this return value is always an empty optional.
     * <p>
     * If this tool modification result has a {@link #passed() pass state}, an empty optional signifies a fallback on the default behavior.
     *
     * @return an optional tool-modified state
     */
    @NotNull
    public Optional<BlockState> toolModifiedState()
    {
        return Optional.ofNullable(toolModifiedState);
    }

    @Override
    public String toString()
    {
        return "ToolModificationResult{" +
                "type=" + type +
                ", toolModifiedState=" + toolModifiedState +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ToolModificationResult that = (ToolModificationResult) o;
        return type == that.type && Objects.equal(toolModifiedState, that.toolModifiedState);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(type, toolModifiedState);
    }

    public enum Type
    {
        PASS,
        FAIL
    }
}
