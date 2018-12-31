package net.minecraftforge.fml.language;

public interface ILifecycleEvent<R extends ILifecycleEvent<?>> {
    @SuppressWarnings("unchecked")
    default R concrete() {
        return (R) this;
    }
}

