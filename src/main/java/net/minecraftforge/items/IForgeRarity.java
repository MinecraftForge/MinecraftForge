package net.minecraftforge.items;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.ApiStatus;

public interface IForgeRarity {
    ChatFormatting color();


    @ApiStatus.Internal
    default boolean isModded() {
        return getClass() != Rarity.class;
    }
}
