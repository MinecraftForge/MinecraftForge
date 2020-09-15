package net.minecraftforge.client.extensions;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;

import javax.annotation.Nullable;

public interface IForgeGeneratorType
{
    default boolean hasEditScreen()
    {
        return false;
    }

    @Nullable
    default BiomeGeneratorTypeScreens.IFactory getEditScreenFactory()
    {
        return null;
    }
}
