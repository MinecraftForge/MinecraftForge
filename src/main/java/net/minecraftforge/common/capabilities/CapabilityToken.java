package net.minecraftforge.common.capabilities;

public abstract class CapabilityToken<T> {
    protected final String getType() {
        throw new RuntimeException("This will be implemented by a transformer");
    }

    @Override
    public String toString() {
        return "CapabilityToken[" + getType() + "]";
    }
}
