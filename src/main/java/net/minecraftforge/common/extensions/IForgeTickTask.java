package net.minecraftforge.common.extensions;

public interface IForgeTickTask {
    public default boolean shouldRun() {
        return false;
    }
}
