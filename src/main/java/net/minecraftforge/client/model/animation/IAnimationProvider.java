package net.minecraftforge.client.model.animation;

/**
 * Something that can provide the Animation State Machine, for example and Entity or a Block
 */
public interface IAnimationProvider
{
    public AnimationStateMachine asm();
}
