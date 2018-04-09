package net.minecraftforge.common.brewing;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

/**
 * A few static utilities related to brewing stand fuels.
 */
public final class BrewingFuelCapabilities
{
    private BrewingFuelCapabilities()
    {
    }

    @CapabilityInject(BrewingFuel.class)
    private static Capability<BrewingFuel> BREWING_FUEL_CAPABILITY;

    /**
     * Accesses the brewing fuel capability instance.
     *
     * @return The brewing fuel capability instance
     */
    public static Capability<BrewingFuel> getBrewingFuelCapability() {
        return BREWING_FUEL_CAPABILITY;
    }

    /**
     * Reserved for {@link net.minecraftforge.common.ForgeModContainer}. Modders
     * do not call this!
     */
    public static void register()
    {
        // We need <BrewingFuel> or the double lambda cracks
        CapabilityManager.INSTANCE.<BrewingFuel>register(BrewingFuel.class,
                new Capability.IStorage<BrewingFuel>()
                {
                    @Nullable
                    @Override
                    public NBTBase writeNBT(Capability<BrewingFuel> capability, BrewingFuel instance, EnumFacing side)
                    {
                        return null;
                    }

                    @Override
                    public void readNBT(Capability<BrewingFuel> capability, BrewingFuel instance, EnumFacing side, NBTBase nbt)
                    {
                    }
                },
                () -> () -> 0
        );
    }
}
