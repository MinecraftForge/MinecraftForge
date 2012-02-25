/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

/** This interface is to be implemented by a Block class.  Allows a block
 * type to perform rendering in both render passes, in case some parts of the
 * block are solid and others are transparent.
 */
public interface IMultipassRender
{
    /** Returns true when the block has things to render in this render
     * pass.
     */
    public boolean canRenderInPass(int pass);
}
