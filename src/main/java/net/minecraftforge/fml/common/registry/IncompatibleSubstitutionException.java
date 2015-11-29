package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;

public class IncompatibleSubstitutionException extends RuntimeException {
    public IncompatibleSubstitutionException(ResourceLocation fromName, Object replacement, Object original)
    {
        super(String.format("The substitute %s for %s (type %s) is type incompatible.", replacement.getClass().getName(), fromName, original.getClass().getName()));
    }

    private static final long serialVersionUID = 1L;

}
