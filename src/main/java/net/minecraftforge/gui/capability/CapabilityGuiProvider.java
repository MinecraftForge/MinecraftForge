package net.minecraftforge.gui.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.gui.capability.impl.GuiProviderBase;

import java.util.concurrent.Callable;

public class CapabilityGuiProvider
{

    @CapabilityInject(IGuiProvider.class)
    public final static Capability<IGuiProvider> GUI_PROVIDER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IGuiProvider.class, new Capability.IStorage<IGuiProvider>()
        {
            @Override
            public NBTBase writeNBT(Capability<IGuiProvider> capability, IGuiProvider instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IGuiProvider> capability, IGuiProvider instance, EnumFacing side, NBTBase nbt) { }
        }, new Callable<IGuiProvider>()
        {
            @Override
            public IGuiProvider call() throws Exception
            {
                return new GuiProviderBase();
            }
        });
    }
}
