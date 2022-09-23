package net.minecraftforge.common.crafting;

import net.minecraft.resources.ResourceLocation;

public interface IRecipeWithCategory {
    @javax.annotation.Nullable
    ResourceLocation getCategory();
}
