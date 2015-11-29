package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;

public class ExistingSubstitutionException extends Exception {
    public ExistingSubstitutionException(ResourceLocation fromName, Object toReplace) {
    }

    private static final long serialVersionUID = 1L;

}
