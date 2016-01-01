package net.minecraftforge.client.model.animation;

import net.minecraftforge.common.model.animation.IAnimationStateMachine;


/**
 * Something that can provide the Animation State Machine, for example and Entity or a Block
 */
public interface IAnimationProvider
{
    public IAnimationStateMachine asm();
}
