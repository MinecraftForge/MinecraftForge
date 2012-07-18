package net.minecraft.src.forge.idrequest;

public interface IIDCallback {
    /**
     * Called by Forge to register your thing.
     * @param name The name of the thing to register
     * @param id The ID to use
     */
    public void register(String name, int id);
    
    /**
     * Called by Forge to unregister your thing.
     * @param name The name of the thing to unregister
     * @param id The thing's current ID
     */
    public void unregister(String name, int id);
}
