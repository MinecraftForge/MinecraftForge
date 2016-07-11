package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.gui.capability.IGuiProvider;

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

    public static void register(IGuiProvider guiHandler)
    {
        INSTANCE.registry.register(new GuiProvider(guiHandler));
    }

    public static class GuiProvider extends IForgeRegistryEntry.Impl<GuiProvider> {

        private IGuiProvider instance;

        public GuiProvider(IGuiProvider instance)
        {
            setRegistryName(instance.getGuiIdentifier());
            this.instance = instance;
        }

        public GuiProvider(ResourceLocation regName, IGuiProvider instance)
        {
            setRegistryName(regName);
            this.instance = instance;
        }

        public IGuiProvider getInstance()
        {
            return this.instance;
        }


    }
}
