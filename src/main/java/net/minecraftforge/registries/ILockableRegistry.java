package net.minecraftforge.registries;

public interface ILockableRegistry {

    //Lock a registry to disable any direct Register calls.
    // All future calls must be done via Forge registry methods not vanilla
    void lock();
}
