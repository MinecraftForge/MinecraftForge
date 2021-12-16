package net.minecraftforge.client.animation;

public record AnimationKey<T>(T defaultValue)
{
    public static final AnimationKey<Float> YAW = new AnimationKey<>(0F);
    public static final AnimationKey<Float> PITCH = new AnimationKey<>(0F);
    public static final AnimationKey<Float> DELTA_BODY_YAW = new AnimationKey<>(0F);
    public static final AnimationKey<Float> MOVEMENT_TICKS = new AnimationKey<>(0F);
    public static final AnimationKey<Float> MOVEMENT_SPEED = new AnimationKey<>(0F);
    public static final AnimationKey<Float> BOB_TICKS = new AnimationKey<>(0F);
}
