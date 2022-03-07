/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import com.google.common.base.Objects;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a {@link #toolModifiedState() tool-modified state} and whether the modification {@link #passed()} or {@link #failed()}.
 * <p>
 * If the tool modification result has a fail state, the tool-modified state is always null.
 * <p>
 * If the tool modification result has a pass state, a null tool-modified state signifies a fallback on the default behavior.
 */
public final class ToolModificationResult
{
    private static final ToolModificationResult FAIL_RESULT = new ToolModificationResult(Type.FAIL, null);
    private static final ToolModificationResult NULL_PASS_RESULT = new ToolModificationResult(Type.PASS, null);
    private final Type type;
    @Nullable
    private final BlockState toolModifiedState;

    private ToolModificationResult(Type type, @Nullable BlockState toolModifiedState)
    {
        this.type = type;
        this.toolModifiedState = toolModifiedState;
    }

    public static ToolModificationResult fail()
    {
        return FAIL_RESULT;
    }

    public static ToolModificationResult pass(@Nullable BlockState toolModifiedState)
    {
        return toolModifiedState == null ? NULL_PASS_RESULT : new ToolModificationResult(Type.PASS, toolModifiedState);
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
     * If this tool modification result has a {@link #failed() fail state}, this return value is always null.
     * <p>
     * If this tool modification result has a {@link #passed() pass state}, a null return value signifies a fallback on the default behavior.
     *
     * @return the nullable tool-modified state
     */
    @Nullable
    public BlockState toolModifiedState()
    {
        return toolModifiedState;
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
