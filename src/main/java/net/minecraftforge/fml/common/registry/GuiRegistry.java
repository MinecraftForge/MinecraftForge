package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.gui.GuiProvider;

public class GuiRegistry {

    private static GuiRegistry INSTANCE = new GuiRegistry();
    private FMLControlledNamespacedRegistry registry = PersistentRegistryManager.createRegistry(new ResourceLocation("forge:guis"), GuiProvider.class, new ResourceLocation("forge:unknown_gui"), 0, 5000, true, null, null, null);

    private GuiRegistry() { }

    public static GuiRegistry instance()
    {
        return INSTANCE;
    }

    public static FMLControlledNamespacedRegistry getRegistry()
    {
        return INSTANCE.registry;
    }

}
