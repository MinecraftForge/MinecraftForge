package net.minecraftforge.common.capabilities;

final class CapabilityTokenDesc<T> extends CapabilityToken<T> {
    final String internalName;

    CapabilityTokenDesc(String internalName) {
        this.internalName = internalName;
    }

    @Override
    public String toString() {
        return "CapabilityToken[" + internalName + ']';
    }
}
