package net.minecraft.src.forge;

public class MinecraftForgeServer {
    
    /**
     * Register a new chat handler. Will only be called on server.
     * @param handler The Handler to be registered
     */
    public static void registerChatHandler(IChatHandler handler)
    {
        ForgeHooksServer.chatHandlers.add(handler);
    }
}
